package com.animationlib.api;

import com.animationlib.impl.AnimationLoader;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Optional;

/**
 * Read-only access to animations loaded from {@code assets/<namespace>/animationlib/animations/*.json}
 * (Blockbench/Bedrock-format animation files). Populated automatically on resource reload.
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
