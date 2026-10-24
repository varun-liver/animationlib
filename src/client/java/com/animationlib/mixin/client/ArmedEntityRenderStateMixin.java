package com.animationlib.mixin.client;

import com.animationlib.client.HeldItemOverride;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Third-person held items, for the player and every other armed mob: swaps in the item a playing
 * animation's {@code item} asks for, right after vanilla filled in the real held items.
 */
@Mixin(ArmedEntityRenderState.class)
abstract class ArmedEntityRenderStateMixin {
    @Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"))
    private static void animationlib$overrideHeldItem(LivingEntity entity, ArmedEntityRenderState state,
            ItemModelResolver resolver, float partialTicks, CallbackInfo ci) {
        ItemStack stack = HeldItemOverride.stackFor(entity.getId());
        if (stack == null) {
            return;
        }

        if (state.mainArm == HumanoidArm.LEFT) {
            resolver.updateForLiving(state.leftHandItemState, stack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
            state.leftHandItemStack = stack;
        } else {
            resolver.updateForLiving(state.rightHandItemState, stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
            state.rightHandItemStack = stack;
        }
    }
}
