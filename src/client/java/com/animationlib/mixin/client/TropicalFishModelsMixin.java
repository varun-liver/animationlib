package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.animal.fish.TropicalFishSmallModel;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Both tropical fish shapes override setupAnim and wag their tail there, after super, so the bones
// are applied at the tail of each override rather than of Model's.
@Mixin({TropicalFishLargeModel.class, TropicalFishSmallModel.class})
abstract class TropicalFishModelsMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/TropicalFishRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(TropicalFishRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "TropicalFishModel");
    }
}
