package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.feline.AdultFelineModel;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Cats and ocelots both render through AdultFelineModel; it sets every part's pose in its own
// setupAnim, so the animation is applied at its tail.
@Mixin(AdultFelineModel.class)
abstract class AdultFelineModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/FelineRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(FelineRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AdultFelineModel");
    }
}
