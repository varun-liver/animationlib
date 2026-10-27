package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// BeeModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The adult and baby bee models extend it without overriding setupAnim.
@Mixin(BeeModel.class)
abstract class BeeModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BeeRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(BeeRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "BeeModel");
    }
}
