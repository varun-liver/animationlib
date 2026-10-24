package com.animationlib.mixin.client;

import com.animationlib.client.ItemBoneApplier;
import com.animationlib.client.RenderStateEntityId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Third-person held items, for the player and every other armed mob. Injecting here rather than in
 * PlayerItemInHandLayer also covers it, since that layer calls through to this method.
 */
@Mixin(ItemInHandLayer.class)
abstract class ItemInHandLayerMixin {
    @Inject(method = "submitArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void animationlib$animateHeldItem(ArmedEntityRenderState state, ItemStackRenderState itemState,
            ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light,
            CallbackInfo ci) {
        ItemBoneApplier.apply(poseStack, ((FabricRenderState) state).getData(RenderStateEntityId.KEY), arm);
    }
}
