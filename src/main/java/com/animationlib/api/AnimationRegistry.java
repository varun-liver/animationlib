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
 * <p>For a glTF file, the id is its filename: {@code assets/mymod/animationlib/animations/pig.glb}
 * is {@code mymod:pig} (a subfolder becomes part of the path, {@code mymod:mobs/pig}), and all of
 * the file's animations (Blender actions) are merged into that one. File names must be lowercase,
 * as Minecraft ignores resource files with capitals or spaces in their names.
 *
 * <p>For a Bedrock/Blockbench {@code .json} file the id is <em>not</em> its filename: the
 * namespace comes from the file's asset path, and the path comes from each animation's own name
 * inside the file, lowercased with anything outside {@code [a-z0-9/._-]} replaced by {@code _},
 * and one file can define several animations.
 */
public final class AnimationRegistry {
    private AnimationRegistry() {
    }

    public static Optional<AnimationData> get(Identifier id) {
        return AnimationLoader.get(id);
    }

    /** Like {@link #get}, but throws instead of returning an empty Optional. */
    public static AnimationData require(Identifier id) {
        return get(id).orElseThrow(() -> new AnimationNotFoundException(id));
    }

    public static Collection<Identifier> ids() {
        return AnimationLoader.ids();
    }
}
