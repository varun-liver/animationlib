package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.hoglin.HoglinModel;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// HoglinModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// Hoglin and zoglin render with it; the baby hoglin model extends it without overriding setupAnim.
@Mixin(HoglinModel.class)
abstract class HoglinModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HoglinRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(HoglinRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "HoglinModel");
    }
}
