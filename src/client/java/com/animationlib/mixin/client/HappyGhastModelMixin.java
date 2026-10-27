package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.ghast.HappyGhastModel;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// HappyGhastModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(HappyGhastModel.class)
abstract class HappyGhastModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HappyGhastRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(HappyGhastRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "HappyGhastModel");
    }
}
