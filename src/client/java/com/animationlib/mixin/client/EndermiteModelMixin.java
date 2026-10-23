package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.endermite.EndermiteModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// EndermiteModel overrides setupAnim and wiggles its segments there, after super, so the bones
// ("segment0" to "segment3") have to be applied at the tail of that override rather than of Model's.
@Mixin(EndermiteModel.class)
abstract class EndermiteModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EntityRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "EndermiteModel");
    }
}
