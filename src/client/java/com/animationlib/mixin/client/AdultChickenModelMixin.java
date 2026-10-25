package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.chicken.AdultChickenModel;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AdultChickenModel moves parts after ChickenModel.setupAnim returns, so the adult applies here
// at the tail instead of in ChickenModelMixin (which the cold chicken model inherits this from).
@Mixin(AdultChickenModel.class)
abstract class AdultChickenModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(ChickenRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AdultChickenModel");
    }
}
