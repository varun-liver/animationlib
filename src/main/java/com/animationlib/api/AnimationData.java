package com.animationlib.api;

import java.util.Map;

/**
 * A parsed animation: its loop flag, length in seconds, per-bone keyframe channels, whether the
 * {@code camera} bone's position is relative to the player ({@code true}) or absolute world
 * coordinates ({@code false}), and whether playing it forces third-person view
 * ({@code force_third_person} in the JSON), and whether it hides the player's inventory
 * ({@code hide_inventory} in the JSON). {@code cameraFollowsPlayer}, if not {@code null}, is the
 * bone of the animated entity the camera locks onto ({@code camera_follows_player} in the JSON, e.g.
 * {@code "right_arm"}), like {@link Animation#cameraFollowsPlayer}. {@code sound}, if not
 * {@code null}, is played every time the animation starts ({@code sound} in the JSON, e.g.
 * {@code "modid:whoosh"} for {@code assets/modid/animationlib/sounds/whoosh.ogg}).
 */
public record AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones,
                            boolean relative, boolean forceThirdPerson, boolean hideInventory,
                            String cameraFollowsPlayer, String sound) {
    public AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones) {
        this(name, loop, length, bones, true, false, false, null, null);
    }
}
