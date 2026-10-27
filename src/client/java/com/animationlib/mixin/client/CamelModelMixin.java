package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CamelModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// The baby camel model extends it without overriding setupAnim.
@Mixin(CamelModel.class)
abstract class CamelModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CamelRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(CamelRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "CamelModel");
    }
}
