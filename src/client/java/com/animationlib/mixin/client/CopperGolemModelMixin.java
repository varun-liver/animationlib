package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.golem.CopperGolemModel;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CopperGolemModel overrides setupAnim and plays its own keyframe animations there, after super, so
// the bones ("head" and the arms are nested under "body") are applied at the tail of that override.
@Mixin(CopperGolemModel.class)
abstract class CopperGolemModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(CopperGolemRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "CopperGolemModel");
    }
}
