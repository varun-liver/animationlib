package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.turtle.AdultTurtleModel;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AdultTurtleModel moves a part and its visibility after TurtleModel.setupAnim returns, so the
// adult applies here at the tail instead of in TurtleModelMixin.
@Mixin(AdultTurtleModel.class)
abstract class AdultTurtleModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/TurtleRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(TurtleRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "AdultTurtleModel");
    }
}
