package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// EnderDragonModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(EnderDragonModel.class)
abstract class EnderDragonModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EnderDragonRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "EnderDragonModel");
    }
}
