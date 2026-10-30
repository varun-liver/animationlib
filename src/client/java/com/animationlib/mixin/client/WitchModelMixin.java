package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.witch.WitchModel;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// WitchModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(WitchModel.class)
abstract class WitchModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/WitchRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(WitchRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "WitchModel");
    }
}
