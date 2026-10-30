package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SquidModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// Squid and glow squid render with it; the baby squid model extends it without overriding setupAnim.
@Mixin(SquidModel.class)
abstract class SquidModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SquidRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(SquidRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SquidModel");
    }
}
