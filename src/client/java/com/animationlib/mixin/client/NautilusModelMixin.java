package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.monster.nautilus.ZombieNautilusCoralModel;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// NautilusModel overrides setupAnim; its armor and saddle models, and the zombie nautilus, inherit
// it. The bones ("shell" and "body" are nested under a part named "root") are applied at the tail.
// The coral zombie nautilus overrides it again and has its own mixin, so it is skipped here: its
// override calls this one first, and applying twice would double every offset.
@Mixin(NautilusModel.class)
abstract class NautilusModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/NautilusRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(NautilusRenderState state, CallbackInfo ci) {
        if ((Object) this instanceof ZombieNautilusCoralModel) {
            return;
        }

        ModelBoneApplier.apply((Model<?>) (Object) this, state, "NautilusModel");
    }
}
