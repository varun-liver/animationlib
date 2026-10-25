package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.dolphin.DolphinModel;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// DolphinModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The baby dolphin model extends it without overriding setupAnim.
@Mixin(DolphinModel.class)
abstract class DolphinModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/DolphinRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(DolphinRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "DolphinModel");
    }
}
