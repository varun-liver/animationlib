package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.phantom.PhantomModel;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PhantomModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(PhantomModel.class)
abstract class PhantomModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PhantomRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(PhantomRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "PhantomModel");
    }
}
