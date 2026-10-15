package com.animationlib.api;

import com.animationlib.impl.LocalAudio;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Plays an animation together with a sound. The sound is an {@code .ogg} file in
 * {@code assets/<modid>/animationlib/sounds/}, named without its extension:
 * {@code assets/modid/animationlib/sounds/whoosh.ogg} is {@code "modid:whoosh"}. It is not a
 * registered {@code SoundEvent}, so no {@code sounds.json} is needed.
 *
 * <pre>{@code
 * AudioTrack.play(zombie, "modid:animation", "modid:whoosh");
 * }</pre>
 *
 * <p>The sound plays once, on every client tracking the entity, at the entity's position when the
 * animation starts. Mono files are positional; stereo files play flat. {@code .mp4} and other
 * formats are not supported. It is fire-and-forget: {@link Animation#stop} does not cut it off.
 *
 * <p>Like {@link AnimationPlayer#play}, called on the client it asks the server to apply it to the
 * sender's own player.
 */
public final class AudioTrack {
    private AudioTrack() {
    }

    /**
     * Plays a sound by name, on its own with no animation. {@code audioName} is the sound file's
     * name without its extension, in the same form as above: {@code "modid:whoosh"} for
     * {@code assets/modid/animationlib/sounds/whoosh.ogg}.
     *
     * <p>Client only, and only for the local player: it plays flat at full volume, with no
     * position, and nothing is sent over the network. Does nothing on a dedicated server.
     * Call it again to layer another copy; {@link #stop(String)} cuts off every copy.
     */
    public static void play(String audioName) {
        LocalAudio.play(Identifier.parse(audioName));
    }

    /** Cuts off every copy of {@code audioName} still playing from {@link #play(String)}. */
    public static void stop(String audioName) {
        LocalAudio.stop(Identifier.parse(audioName));
    }

    public static void play(Entity entity, String animationId, String sound) {
        AnimationPlayer.play(entity, Identifier.parse(animationId), null, null, false,
                sound != null ? Identifier.parse(sound) : null);
    }
}
