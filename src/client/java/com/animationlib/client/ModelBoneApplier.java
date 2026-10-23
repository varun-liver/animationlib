package com.animationlib.client;

import com.animationlib.api.Vec3f;
import com.animationlib.impl.AnimationPlayerImpl;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;

/**
 * Applies whichever animation is playing on the entity a render state belongs to onto a model's
 * parts, by name: the same thing HumanoidModelMixin and QuadrupedModelMixin do for their model
 * classes, shared so a model that only needs a hook in the right place doesn't repeat it. The
 * {@code camera} bone is skipped here and handled by CameraMixin.
 */
public final class ModelBoneApplier {
    private ModelBoneApplier() {
    }

    /** Call at the very end of the model's {@code setupAnim}, after it has set its own pose. */
    public static void apply(Model<?> model, Object state, String modelName) {
        Integer entityId = ((FabricRenderState) state).getData(RenderStateEntityId.KEY);
        // Handed off to ModelMixin, which captures this same model instance's render-time bone
        // positions for Animation#cameraFollowsEntity moments later, once renderToBuffer runs.
        BoneLookAt.beginModel(entityId);
        if (entityId == null) {
            return;
        }

        ModelPart root = model.root();
        AnimationPlayerImpl.sample(entityId).ifPresent(sample ->
                sample.data().bones().keySet().forEach(bone -> {
                    if (bone.equals("camera")) {
                        return;
                    }
                    if (root.hasChild(bone)) {
                        applyBone(root.getChild(bone), sample, bone);
                    } else {
                        MissingBoneWarnings.warnOnce(entityId, bone, modelName);
                    }
                }));
    }

    private static void applyBone(ModelPart part, AnimationPlayerImpl.Sample sample, String bone) {
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
