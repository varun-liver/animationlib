package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.axolotl.AdultAxolotlModel;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AdultAxolotlModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(AdultAxolotlModel.class)
abstract class AdultAxolotlModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AxolotlRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(AxolotlRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AdultAxolotlModel");
    }
}
