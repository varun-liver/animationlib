package com.animationlib.client;

import com.animationlib.api.Vec3f;
import com.animationlib.impl.AnimationPlayerImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;

/**
 * Animates the item an entity is holding, separately from the arm that holds it. Called with the
 * pose stack already in the item's own space (right before the item is submitted), so rotation
 * pivots on the item's origin and position is along the item's own axes. Bones: {@code item}
 * (either hand), then {@code item_left} / {@code item_right} (the physical hand holding it).
 */
public final class ItemBoneApplier {
    private ItemBoneApplier() {
    }

    public static void apply(PoseStack poseStack, Integer entityId, HumanoidArm arm) {
        if (entityId == null) {
            return;
        }

        AnimationPlayerImpl.sample(entityId).ifPresent(sample -> {
            applyBone(poseStack, sample, "item");
            applyBone(poseStack, sample, arm == HumanoidArm.LEFT ? "item_left" : "item_right");
        });
    }

    private static void applyBone(PoseStack poseStack, AnimationPlayerImpl.Sample sample, String bone) {
        // Position is in model pixels like every other bone; the pose stack here is in blocks.
        Vec3f position = sample.position(bone);
        if (position != null) {
            poseStack.translate(position.x() / 16f, position.y() / 16f, position.z() / 16f);
        }

        Vec3f rotation = sample.rotation(bone);
        if (rotation != null) {
            poseStack.mulPose(new Matrix4f().rotationXYZ(
                    (float) Math.toRadians(rotation.x()),
                    (float) Math.toRadians(rotation.y()),
                    (float) Math.toRadians(rotation.z())));
        }
    }
}
