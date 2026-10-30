package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.armadillo.ArmadilloModel;
import net.minecraft.client.renderer.entity.state.ArmadilloRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ArmadilloModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The adult and baby armadillo models extend it without overriding setupAnim.
@Mixin(ArmadilloModel.class)
abstract class ArmadilloModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ArmadilloRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(ArmadilloRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "ArmadilloModel");
    }
}
