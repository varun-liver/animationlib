package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.strider.StriderModel;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// StriderModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The adult and baby strider models extend it without overriding setupAnim.
@Mixin(StriderModel.class)
abstract class StriderModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/StriderRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(StriderRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "StriderModel");
    }
}
