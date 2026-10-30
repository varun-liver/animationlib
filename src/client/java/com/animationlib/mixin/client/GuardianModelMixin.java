package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.guardian.GuardianModel;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// GuardianModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// Guardian and elder guardian render with it.
@Mixin(GuardianModel.class)
abstract class GuardianModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/GuardianRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(GuardianRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "GuardianModel");
    }
}
