package com.animationlib.api;

/**
 * A single keyframe on a {@link BoneChannel}. {@code pre} and {@code post} differ only
 * when the source data defines a discontinuity (a "step") at this time; otherwise both
 * equal the keyframe's value.
 */
public record Keyframe(double time, Vec3f pre, Vec3f post, String lerpMode) {
    public Vec3f value() {
        return post;
    }
}
