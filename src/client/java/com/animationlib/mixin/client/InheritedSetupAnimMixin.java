package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SulfurCubeModel (and SmallSulfurCubeModel, and the inner layer's own SulfurCubeModel instance)
// and SlimeModel inherit setupAnim straight from Model without overriding it, so there is nothing
// on those classes to inject into. This hooks Model#setupAnim instead and bails out for every
// other model. That's only safe because these models set no pose after super.setupAnim; a model
// that does needs its own mixin on its own setupAnim, like TadpoleModelMixin.
@Mixin(Model.class)
abstract class InheritedSetupAnimMixin<S> {
    @Inject(method = "setupAnim(Ljava/lang/Object;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(S state, CallbackInfo ci) {
        Object self = this;
        if (self instanceof SulfurCubeModel) {
            ModelBoneApplier.apply((Model<?>) self, state, "SulfurCubeModel");
        } else if (self instanceof SlimeModel) {
            ModelBoneApplier.apply((Model<?>) self, state, "SlimeModel");
        }
    }
}
