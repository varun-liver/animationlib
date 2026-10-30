package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.piglin.AbstractPiglinModel;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AbstractPiglinModel is a HumanoidModel, but its own setupAnim poses the ears after calling super,
// which would overwrite an ear animated from HumanoidModelMixin, so that mixin skips piglins and
// the animation is applied here at the tail instead. This covers the zombified piglin, which does
// not override setupAnim again. PiglinModel poses the arms after this runs, so it has its own hook.
@Mixin(AbstractPiglinModel.class)
abstract class AbstractPiglinModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(HumanoidRenderState state, CallbackInfo ci) {
        if ((Object) this instanceof PiglinModel) {
            return;
        }
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AbstractPiglinModel");
    }
}
