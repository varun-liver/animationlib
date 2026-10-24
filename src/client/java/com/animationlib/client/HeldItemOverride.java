package com.animationlib.client;

import com.animationlib.impl.AnimationPlayerImpl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** The item an entity's playing animation wants shown in its main hand, if any. */
public final class HeldItemOverride {
    private HeldItemOverride() {
    }

    /** A fresh stack of the playing animation's {@code item}, or {@code null} if it has none. */
    public static ItemStack stackFor(int entityId) {
        Identifier id = AnimationPlayerImpl.sample(entityId).map(AnimationPlayerImpl.Sample::item).orElse(null);
        if (id == null) {
            return null;
        }

        return BuiltInRegistries.ITEM.getOptional(id).map(ItemStack::new).orElseGet(() -> {
            MissingBoneWarnings.warnOnceItem(id);
            return null;
        });
    }
}
