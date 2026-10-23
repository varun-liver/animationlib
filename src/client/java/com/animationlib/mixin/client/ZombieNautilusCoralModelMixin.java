package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.nautilus.ZombieNautilusCoralModel;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ZombieNautilusCoralModel overrides setupAnim again, after NautilusModel's, so the bones are applied
// at the tail of this override. NautilusModelMixin skips this class to avoid applying them twice.
@Mixin(ZombieNautilusCoralModel.class)
abstract class ZombieNautilusCoralModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/NautilusRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(NautilusRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "ZombieNautilusCoralModel");
    }
}
