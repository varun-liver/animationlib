package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.chicken.ChickenModel;
import net.minecraft.client.model.animal.chicken.AdultChickenModel;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// The baby chicken model uses ChickenModel.setupAnim directly. The adult overrides it again and
// has its own mixin, so it is skipped here to not apply twice.
@Mixin(ChickenModel.class)
abstract class ChickenModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(ChickenRenderState state, CallbackInfo ci) {
        if ((Object) this instanceof AdultChickenModel) {
            return;
        }

        ModelBoneApplier.apply((Model<?>) (Object) this, state, "ChickenModel");
    }
}
