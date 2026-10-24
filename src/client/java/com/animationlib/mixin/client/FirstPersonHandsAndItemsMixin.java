package com.animationlib.mixin.client;

import com.animationlib.client.HeldItemOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** First-person counterpart of {@link ArmedEntityRenderStateMixin}, for the local player's own hand. */
@Mixin(FirstPersonHandsAndItems.class)
abstract class FirstPersonHandsAndItemsMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void animationlib$overrideHeldItem(LocalPlayer player, float partialTicks,
            FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        ItemStack stack = HeldItemOverride.stackFor(player.getId());
        if (stack == null) {
            return;
        }

        ItemDisplayContext context = player.getMainArm() == HumanoidArm.LEFT
                ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        state.mainHandItem = stack;
        state.mainHandRenderState.clear();
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.mainHandRenderState, stack, context,
                player.level(), player, player.getId() + context.ordinal());
    }
}
