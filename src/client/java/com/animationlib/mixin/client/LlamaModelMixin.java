package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// LlamaModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The baby llama and trader llama render through the same model.
@Mixin(LlamaModel.class)
abstract class LlamaModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/LlamaRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(LlamaRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "LlamaModel");
    }
}
