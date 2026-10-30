package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AbstractEquineModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
// Horse, skeleton horse, zombie horse, donkey and mule (and their babies) all extend it without
// overriding setupAnim. The saddle model overrides it, so it picks the animation up via super.
@Mixin(AbstractEquineModel.class)
abstract class AbstractEquineModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EquineRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EquineRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AbstractEquineModel");
    }
}
