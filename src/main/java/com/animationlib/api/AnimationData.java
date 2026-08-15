package com.animationlib.api;

import java.util.Map;

/** A parsed animation: its loop flag, length in seconds, and per-bone keyframe channels. */
public record AnimationData(String name, boolean loop, double length, Map<String, BoneAnimation> bones) {
}
