package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.ghast.GhastModel;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// GhastModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(GhastModel.class)
abstract class GhastModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/GhastRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(GhastRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "GhastModel");
    }
}
