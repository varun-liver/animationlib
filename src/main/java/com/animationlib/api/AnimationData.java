package com.animationlib.api;

import java.util.Map;

/**
 * A parsed animation: its loop flag, length in seconds, per-bone keyframe channels, whether the
 * {@code camera} bone's position is relative to the player ({@code true}) or absolute world
 * coordinates ({@code false}), and whether playing it forces third-person view
 * ({@code force_third_person} in the JSON).
 */
public record AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones,
                            boolean relative, boolean forceThirdPerson) {
    public AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones) {
        this(name, loop, length, bones, true, false);
    }
}
