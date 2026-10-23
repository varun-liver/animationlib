package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CreeperModel overrides setupAnim and sets its head and leg swing there, after super, so the bones
// are applied at the tail of that override rather than of Model's.
@Mixin(CreeperModel.class)
abstract class CreeperModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(CreeperRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "CreeperModel");
    }
}
