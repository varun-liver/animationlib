package com.animationlib.mixin.client;

import com.animationlib.client.ModelBoneApplier;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ParrotModel does not share a base class with the humanoid or quadruped models, so it has its own hook.
@Mixin(ParrotModel.class)
abstract class ParrotModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ParrotRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(ParrotRenderState state, CallbackInfo ci) {
        ModelBoneApplier.apply((Model<?>) (Object) this, state, "ParrotModel");
    }
}
