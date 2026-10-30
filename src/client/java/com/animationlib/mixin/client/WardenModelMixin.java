package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.warden.WardenModel;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// WardenModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(WardenModel.class)
abstract class WardenModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/WardenRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(WardenRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "WardenModel");
    }
}
