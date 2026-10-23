package com.animationlib.mixin.client;

import com.animationlib.client.BoneLookAt;
import com.animationlib.client.ModelBones;
import com.animationlib.impl.AnimationPlayerImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

// renderToBuffer is final on Model, so this fires for every entity's model right after its own
// setupAnim already ran (HumanoidModelMixin handed off which entity via BoneLookAt.beginModel).
// The captured position reuses the real render-time PoseStack, so it reflects the model's actual
// resolved pose (including whatever this entity's own animation did to that bone) without this
// mod needing to separately reconstruct the entity's world/mirror/pivot transform itself.
@Mixin(Model.class)
abstract class ModelMixin {
    @Shadow
    public abstract ModelPart root();

    @Inject(method = "renderToBuffer", at = @At("HEAD"))
    private void animationlib$captureBonePositions(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                                     int packedOverlay, int color, CallbackInfo ci) {
        Integer entityId = BoneLookAt.currentEntityId();
        if (entityId == null) {
            return;
        }

        Set<String> tracked = AnimationPlayerImpl.trackedBonesFor(entityId);
        if (tracked.isEmpty()) {
            return;
        }

        ModelPart root = this.root();
        for (String bone : tracked) {
            // Every part from the root's child down to the bone, so a nested bone's position
            // includes the transforms of the parts it hangs off.
            List<ModelPart> path = ModelBones.path(root, bone);
            if (path.isEmpty()) {
                continue;
            }

            PoseStack local = new PoseStack();
            local.last().pose().set(poseStack.last().pose());
            path.forEach(part -> part.translateAndRotate(local));
            Vector3f translation = local.last().pose().getTranslation(new Vector3f());
            BoneLookAt.setPosition(entityId, bone, new Vec3(translation.x(), translation.y(), translation.z()));
        }
    }
}
