package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AllayModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(AllayModel.class)
abstract class AllayModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AllayRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(AllayRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AllayModel");
    }
}
