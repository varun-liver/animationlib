package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.fish.SalmonModel;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SalmonModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(SalmonModel.class)
abstract class SalmonModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SalmonRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(SalmonRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SalmonModel");
    }
}
