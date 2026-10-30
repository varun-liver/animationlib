package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.sniffer.SnifferModel;
import net.minecraft.client.renderer.entity.state.SnifferRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SnifferModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The snifflet model extends it.
@Mixin(SnifferModel.class)
abstract class SnifferModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SnifferRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(SnifferRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SnifferModel");
    }
}
