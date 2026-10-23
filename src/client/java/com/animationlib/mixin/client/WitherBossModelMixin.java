package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.wither.WitherBossModel;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// WitherBossModel overrides setupAnim and sets its head and ribcage pose there, after super, so the
// bones are applied at the tail of that override rather than of Model's.
@Mixin(WitherBossModel.class)
abstract class WitherBossModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/WitherRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(WitherRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "WitherBossModel");
    }
}
