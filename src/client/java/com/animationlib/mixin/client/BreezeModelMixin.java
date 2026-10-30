package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.breeze.BreezeModel;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// BreezeModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(BreezeModel.class)
abstract class BreezeModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BreezeRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(BreezeRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "BreezeModel");
    }
}
