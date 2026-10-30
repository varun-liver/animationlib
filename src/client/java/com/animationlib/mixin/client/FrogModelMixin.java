package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.frog.FrogModel;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// FrogModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(FrogModel.class)
abstract class FrogModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/FrogRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(FrogRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "FrogModel");
    }
}
