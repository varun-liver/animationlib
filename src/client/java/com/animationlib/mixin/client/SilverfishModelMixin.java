package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.silverfish.SilverfishModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SilverfishModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(SilverfishModel.class)
abstract class SilverfishModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EntityRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SilverfishModel");
    }
}
