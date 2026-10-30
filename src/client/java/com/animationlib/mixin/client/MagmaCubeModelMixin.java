package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.slime.MagmaCubeModel;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// MagmaCubeModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(MagmaCubeModel.class)
abstract class MagmaCubeModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SlimeRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(SlimeRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "MagmaCubeModel");
    }
}
