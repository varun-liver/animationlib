package com.animationlib.api;

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

    public static void play(Entity entity, String animationId, String sound) {
        AnimationPlayer.play(entity, Identifier.parse(animationId), null, null, false,
                sound != null ? Identifier.parse(sound) : null);
    }
}
