package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CreakingModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(CreakingModel.class)
abstract class CreakingModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CreakingRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(CreakingRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "CreakingModel");
    }
}
