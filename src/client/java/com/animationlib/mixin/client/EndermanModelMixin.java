package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// EndermanModel is a HumanoidModel that rewrites its head, arms and legs after calling super, so
// HumanoidModelMixin skips it and the animation is applied here at the tail instead.
@Mixin(EndermanModel.class)
abstract class EndermanModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EndermanRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EndermanRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "EndermanModel");
    }
}
