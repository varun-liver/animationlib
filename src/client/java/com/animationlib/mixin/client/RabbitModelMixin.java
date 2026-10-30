package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.rabbit.RabbitModel;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// RabbitModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The adult and baby rabbit models extend it without overriding setupAnim.
@Mixin(RabbitModel.class)
abstract class RabbitModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/RabbitRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(RabbitRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "RabbitModel");
    }
}
