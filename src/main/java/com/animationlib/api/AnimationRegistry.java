package com.animationlib.api;

import com.animationlib.impl.AnimationLoader;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Optional;

/**
 * Read-only access to animations loaded from {@code assets/<namespace>/animationlib/animations/},
 * populated automatically on resource reload. Supported formats are Blockbench/Bedrock
 * {@code .json} and glTF 2.0 {@code .gltf}/{@code .glb}.
 *
 * <p>An animation's id is <em>not</em> its filename: the namespace comes from the file's asset
 * path, and the path comes from the animation's own name inside the file, lowercased with anything
 * outside {@code [a-z0-9/._-]} replaced by {@code _}. So a Blender action {@code headAction}
 * exported into {@code assets/mymod/animationlib/animations/pig.glb} is {@code mymod:headaction},
 * and one file can define several animations.
 */
public final class AnimationRegistry {
    private AnimationRegistry() {
    }

    public static Optional<AnimationData> get(Identifier id) {
        return AnimationLoader.get(id);
    }

    public static Collection<Identifier> ids() {
        return AnimationLoader.ids();
    }
}
