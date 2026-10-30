package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// PiglinModel sets the arm poses (admiring, crossbow, holding a weapon) after AbstractPiglinModel's
// setupAnim has run, so the animation is applied here at the tail instead. This covers the piglin,
// piglin brute and the baby piglin.
@Mixin(PiglinModel.class)
abstract class PiglinModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(PiglinRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "PiglinModel");
    }
}
