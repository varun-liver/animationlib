package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.fish.PufferfishBigModel;
import net.minecraft.client.model.animal.fish.PufferfishMidModel;
import net.minecraft.client.model.animal.fish.PufferfishSmallModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// The three pufferfish sizes each override setupAnim and set their fin and eye pose there, after
// super, so the bones are applied at the tail of each override rather than of Model's.
@Mixin({PufferfishBigModel.class, PufferfishMidModel.class, PufferfishSmallModel.class})
abstract class PufferfishModelsMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(EntityRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "PufferfishModel");
    }
}
