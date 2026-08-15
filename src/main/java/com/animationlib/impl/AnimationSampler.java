package com.animationlib.impl;

import com.animationlib.api.BoneChannel;
import com.animationlib.api.Keyframe;
import com.animationlib.api.Vec3f;

import java.util.List;

/** Linearly interpolates a {@link BoneChannel} at a given time. "step" keyframes hold instead of blending. */
final class AnimationSampler {
    private AnimationSampler() {
    }

    static Vec3f sample(BoneChannel channel, double time) {
        if (channel == null) {
            return null;
        }

        List<Keyframe> keyframes = channel.keyframes();
        if (keyframes.isEmpty()) {
            return null;
        }
        if (time <= keyframes.get(0).time()) {
            return keyframes.get(0).value();
        }

        Keyframe last = keyframes.get(keyframes.size() - 1);
        if (time >= last.time()) {
            return last.value();
        }

        for (int i = 0; i < keyframes.size() - 1; i++) {
            Keyframe a = keyframes.get(i);
            Keyframe b = keyframes.get(i + 1);
            if (time < b.time()) {
                if ("step".equals(a.lerpMode())) {
                    return a.post();
                }
                double span = b.time() - a.time();
                float t = span <= 0 ? 0f : (float) ((time - a.time()) / span);
                return lerp(a.post(), b.pre(), t);
            }
        }

        return last.value();
    }

    private static Vec3f lerp(Vec3f a, Vec3f b, float t) {
        return new Vec3f(
                a.x() + (b.x() - a.x()) * t,
                a.y() + (b.y() - a.y()) * t,
                a.z() + (b.z() - a.z()) * t
        );
    }
}
