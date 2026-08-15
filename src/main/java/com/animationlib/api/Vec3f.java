package com.animationlib.api;

/** A rotation/position/scale triple, as used by Blockbench/Bedrock animation keyframes. */
public record Vec3f(float x, float y, float z) {
    public static final Vec3f ZERO = new Vec3f(0, 0, 0);
}
