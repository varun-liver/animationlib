package com.animationlib.api;

import java.util.List;

/** The keyframes for one transform channel (rotation, position, or scale) of a bone. */
public record BoneChannel(List<Keyframe> keyframes) {
    public static BoneChannel constant(Vec3f value) {
        return new BoneChannel(List.of(new Keyframe(0.0, value, value, "linear")));
    }
}
