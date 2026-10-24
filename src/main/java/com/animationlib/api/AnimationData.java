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
 * {@code "modid:whoosh"} for {@code assets/modid/animationlib/sounds/whoosh.ogg}). {@code item}, if
 * not {@code null}, is the id of the item shown in the animated entity's main hand while the
 * animation plays ({@code item} in the JSON, e.g. {@code "minecraft:diamond_sword"}), like
 * {@link Animation#item}. {@code itemNum}, if not {@code null}, is the inventory slot whose item is
 * shown instead ({@code item_num} in the JSON, 0-8 the hotbar, 9-35 the rest of the inventory);
 * ignored when {@code item} is set, like {@link Animation#itemNum}.
 */
public record AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones,
                            boolean relative, boolean forceThirdPerson, boolean hideInventory,
                            String cameraFollowsPlayer, String sound, String item,
                            Integer itemNum) {
    public AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones) {
        this(name, loop, length, bones, true, false, false, null, null, null, null);
    }
}
