package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SkeletonModel is a HumanoidModel, but its own setupAnim sets the arm poses (bow, holding items)
// after calling super, which would overwrite an arm animated from HumanoidModelMixin, so that
// mixin skips skeletons and the animation is applied here at the tail instead. This also covers
// the stray, parched, wither skeleton and bogged, which share the model; the bogged's own override
// only changes visibility after calling this one.
@Mixin(SkeletonModel.class)
abstract class SkeletonModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(SkeletonRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "SkeletonModel");
    }
}
