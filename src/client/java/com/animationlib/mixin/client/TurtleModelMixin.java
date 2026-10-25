package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.turtle.AdultTurtleModel;
import net.minecraft.client.model.animal.turtle.TurtleModel;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TurtleModel is a QuadrupedModel that rewrites the leg rotations after calling super, so
// QuadrupedModelMixin skips it and the animation is applied here at the tail instead. The adult
// model overrides this again and has its own mixin, so it is skipped here to not apply twice.
@Mixin(TurtleModel.class)
abstract class TurtleModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/TurtleRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(TurtleRenderState state, CallbackInfo ci) {
        if ((Object) this instanceof AdultTurtleModel) {
            return;
        }

        ModelBoneApplier.apply((Model<?>) (Object) this, state, "TurtleModel");
    }
}
