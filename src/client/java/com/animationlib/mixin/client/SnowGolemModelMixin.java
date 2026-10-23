package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SnowGolemModel overrides setupAnim and sets its head and arm pose there, after super, so the bones
// are applied at the tail of that override rather than of Model's.
@Mixin(SnowGolemModel.class)
abstract class SnowGolemModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(LivingEntityRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SnowGolemModel");
    }
}
