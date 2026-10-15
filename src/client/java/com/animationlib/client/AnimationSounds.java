package com.animationlib.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Plays {@code .ogg} files from {@code assets/<namespace>/animationlib/sounds/} straight through
 * OpenAL, at an entity's position. They are not registered {@code SoundEvent}s, so they bypass
 * {@code sounds.json}. Mono files are positional; stereo files play flat.
 */
final class AnimationSounds {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final String DIRECTORY = "animationlib/sounds/";
    private static final float MAX_DISTANCE = 16.0f;

    // entityId is LOCAL for sounds played by name, which no animation stop should touch.
    private static final int LOCAL = -1;

    private record Playing(int entityId, Identifier sound, int source) {
    }

    private static final Map<Identifier, Integer> BUFFERS = new HashMap<>();
    private static final List<Playing> PLAYING = new ArrayList<>();

    private AnimationSounds() {
    }

    static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> reap());
    }

    static void play(int entityId, Identifier sound) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(entityId);
        if (entity == null) {
            return;
        }

        // A new animation on the same entity replaces the old one, sound included.
        stop(entityId);

        start(mc, entityId, sound, entity);
    }

    /** Plays {@code sound} flat, at full volume, for the local player only. */
    static void playLocal(Identifier sound) {
        start(Minecraft.getInstance(), LOCAL, sound, null);
    }

    /** Cuts off every sound of this name started by {@link #playLocal}. */
    static void stopLocal(Identifier sound) {
        Iterator<Playing> it = PLAYING.iterator();
        while (it.hasNext()) {
            Playing playing = it.next();
            if (playing.entityId() == LOCAL && playing.sound().equals(sound)) {
                AL10.alSourceStop(playing.source());
                AL10.alDeleteSources(playing.source());
                it.remove();
            }
        }
    }

    // entity == null plays relative to the listener, with no distance falloff.
    private static void start(Minecraft mc, int entityId, Identifier sound, Entity entity) {
        if (sound.getPath().endsWith(".mp4") || sound.getPath().endsWith(".mp3")) {
            LOGGER.warn("animationlib: {} is not supported, only .ogg files can be played", sound);
            return;
        }
        Integer buffer = buffer(mc, sound);
        if (buffer == null) {
            return;
        }

        float volume = (float) (mc.options.getSoundSourceOptionInstance(SoundSource.MASTER).get()
                * mc.options.getSoundSourceOptionInstance(SoundSource.PLAYERS).get());
        int source = AL10.alGenSources();
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_GAIN, volume);
        if (entity != null) {
            AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 1.0f);
            AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, MAX_DISTANCE);
            AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, 1.0f);
            AL10.alSource3f(source, AL10.AL_POSITION, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
        } else {
            AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
            AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, 0.0f);
            AL10.alSource3f(source, AL10.AL_POSITION, 0.0f, 0.0f, 0.0f);
        }
        AL10.alSourcePlay(source);
        PLAYING.add(new Playing(entityId, sound, source));
    }

    /** Cuts off any sound still playing for {@code entityId}'s animation. */
    static void stop(int entityId) {
        Iterator<Playing> it = PLAYING.iterator();
        while (it.hasNext()) {
            Playing playing = it.next();
            if (playing.entityId() == entityId) {
                AL10.alSourceStop(playing.source());
                AL10.alDeleteSources(playing.source());
                it.remove();
            }
        }
    }

    private static Integer buffer(Minecraft mc, Identifier sound) {
        Integer cached = BUFFERS.get(sound);
        if (cached != null) {
            return cached;
        }
        Identifier file = Identifier.fromNamespaceAndPath(sound.getNamespace(), DIRECTORY + sound.getPath()
                + (sound.getPath().endsWith(".ogg") ? "" : ".ogg"));
        Resource resource = mc.getResourceManager().getResource(file).orElse(null);
        if (resource == null) {
            LOGGER.warn("animationlib: sound file {} not found", file);
            return null;
        }

        ByteBuffer encoded = null;
        ShortBuffer pcm = null;
        try (InputStream in = resource.open(); MemoryStack stack = MemoryStack.stackPush()) {
            byte[] bytes = in.readAllBytes();
            encoded = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();
            // Decode into a buffer we allocate ourselves; stb_vorbis_decode_memory returns memory
            // from stb's own allocator, which is not safe to release with memFree.
            IntBuffer error = stack.mallocInt(1);
            long handle = STBVorbis.stb_vorbis_open_memory(encoded, error, null);
            if (handle == 0L) {
                LOGGER.warn("animationlib: could not decode {} (stb_vorbis error {})", file, error.get(0));
                return null;
            }
            int buffer;
            try {
                STBVorbisInfo info = STBVorbisInfo.malloc(stack);
                STBVorbis.stb_vorbis_get_info(handle, info);
                int channels = info.channels();
                int samples = STBVorbis.stb_vorbis_stream_length_in_samples(handle);
                pcm = MemoryUtil.memAllocShort(samples * channels);
                int read = STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, pcm);
                pcm.limit(read * channels);
                int format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
                buffer = AL10.alGenBuffers();
                AL10.alBufferData(buffer, format, pcm, info.sample_rate());
            } finally {
                STBVorbis.stb_vorbis_close(handle);
            }
            BUFFERS.put(sound, buffer);
            return buffer;
        } catch (Exception e) {
            LOGGER.error("animationlib: failed to load sound {}", file, e);
            return null;
        } finally {
            if (encoded != null) {
                MemoryUtil.memFree(encoded);
            }
            if (pcm != null) {
                MemoryUtil.memFree(pcm);
            }
        }
    }

    private static void reap() {
        Iterator<Playing> it = PLAYING.iterator();
        while (it.hasNext()) {
            int source = it.next().source();
            if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
                AL10.alDeleteSources(source);
                it.remove();
            }
        }
    }
}
