package com.animationlib.mixin.client;

import com.animationlib.api.Vec3f;
import com.animationlib.impl.AnimationPlayerImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The local player's own head is never rendered in first person, and the visible hand/arm is
 * drawn here rather than through {@link net.minecraft.client.model.player.PlayerModel} (see
 * {@link PlayerModelMixin}) - so arm animations need a separate hook to be visible in first
 * person. Only rotation is applied (not position): this renderer works in a different coordinate
 * space/scale than the player model's bones, so a position offset tuned for the model would be
 * wildly wrong here.
 */
@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererMixin {
    @Inject(method = "renderPlayerArm", at = @At("HEAD"))
    private void animationlib$pushBareArm(PoseStack poseStack, SubmitNodeCollector collector, int light,
            float partialTicks, float equipProgress, HumanoidArm arm, CallbackInfo ci) {
        animationlib$pushArmPose(poseStack, arm);
    }

    @Inject(method = "renderPlayerArm", at = @At("RETURN"))
    private void animationlib$popBareArm(PoseStack poseStack, SubmitNodeCollector collector, int light,
            float partialTicks, float equipProgress, HumanoidArm arm, CallbackInfo ci) {
        poseStack.popPose();
    }

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void animationlib$pushItemArm(AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress,
            PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        animationlib$pushArmPose(poseStack, arm);
    }

    @Inject(method = "submitArmWithItem", at = @At("RETURN"))
    private void animationlib$popItemArm(AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress,
            PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        poseStack.popPose();
    }

    // Always pairs with a popPose() in the matching @At("RETURN") injector above, even when there's
    // no local player or no animation playing, so the stack stays balanced on every code path.
    private static void animationlib$pushArmPose(PoseStack poseStack, HumanoidArm arm) {
        poseStack.pushPose();

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        AnimationPlayerImpl.sample(player.getId()).ifPresent(sample -> {
            String bone = arm == HumanoidArm.LEFT ? "arm_left" : "arm_right";
            Vec3f rotation = sample.rotation(bone);
            if (rotation != null) {
                poseStack.mulPose(new Quaternionf().rotationXYZ(
                        (float) Math.toRadians(rotation.x()),
                        (float) Math.toRadians(rotation.y()),
                        (float) Math.toRadians(rotation.z())));
            }
        });
    }
}
