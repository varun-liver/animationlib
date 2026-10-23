package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// IronGolemModel overrides setupAnim and sets its arm and leg swing there, after super, so the bones
// are applied at the tail of that override rather than of Model's.
@Mixin(IronGolemModel.class)
abstract class IronGolemModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/IronGolemRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(IronGolemRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "IronGolemModel");
    }
}
