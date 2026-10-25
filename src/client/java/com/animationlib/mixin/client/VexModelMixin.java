package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// VexModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(VexModel.class)
abstract class VexModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/VexRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(VexRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "VexModel");
    }
}
