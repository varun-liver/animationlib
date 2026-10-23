package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.shulker.ShulkerModel;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ShulkerModel overrides setupAnim and sets its lid and head pose there, after super, so the bones
// ("base", "lid", "head") have to be applied at the tail of that override rather than of Model's.
@Mixin(ShulkerModel.class)
abstract class ShulkerModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ShulkerRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(ShulkerRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "ShulkerModel");
    }
}
