package com.animationlib.mixin.client;

import com.animationlib.api.Vec3f;
import com.animationlib.client.BoneLookAt;
import com.animationlib.client.MissingBoneWarnings;
import com.animationlib.client.RenderStateEntityId;
import com.animationlib.impl.AnimationPlayerImpl;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Targets QuadrupedModel rather than PigModel specifically: pigs and other four-legged mobs that
// don't override setupAnim render through this same shared model class, so this covers all of
// them for free, the same way HumanoidModelMixin covers humanoid-shaped mobs.
@Mixin(QuadrupedModel.class)
abstract class QuadrupedModelMixin<T extends LivingEntityRenderState> {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(T state, CallbackInfo ci) {
        Integer entityId = ((FabricRenderState) state).getData(RenderStateEntityId.KEY);
        BoneLookAt.beginModel(entityId);
        if (entityId == null) {
            return;
        }

        ModelPart root = ((Model<?>) (Object) this).root();
        AnimationPlayerImpl.sample(entityId).ifPresent(sample ->
                sample.data().bones().keySet().forEach(bone -> {
                    if (bone.equals("camera")) {
                        return;
                    }
                    if (root.hasChild(bone)) {
                        animationlib$applyBone(root.getChild(bone), sample, bone);
                    } else {
                        MissingBoneWarnings.warnOnce(entityId, bone, "QuadrupedModel");
                    }
                }));
    }

    private static void animationlib$applyBone(ModelPart part, AnimationPlayerImpl.Sample sample, String bone) {
        Vec3f rotation = sample.rotation(bone);
        if (rotation != null) {
            part.offsetRotation(new Vector3f(
                    (float) Math.toRadians(rotation.x()),
                    (float) Math.toRadians(rotation.y()),
                    (float) Math.toRadians(rotation.z())
            ));
        }

        Vec3f position = sample.position(bone);
        if (position != null) {
            part.offsetPos(new Vector3f(position.x(), position.y(), position.z()));
        }
    }
}
