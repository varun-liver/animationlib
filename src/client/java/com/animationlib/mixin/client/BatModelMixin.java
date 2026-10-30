package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ambient.BatModel;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// BatModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(BatModel.class)
abstract class BatModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BatRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(BatRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "BatModel");
    }
}
