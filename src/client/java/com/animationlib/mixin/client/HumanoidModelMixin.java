package com.animationlib.mixin.client;

import com.animationlib.api.Vec3f;
import com.animationlib.client.BoneLookAt;
import com.animationlib.client.MissingBoneWarnings;
import com.animationlib.client.ModelBones;
import com.animationlib.client.RenderStateEntityId;
import com.animationlib.impl.AnimationPlayerImpl;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.model.monster.piglin.AbstractPiglinModel;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Targets HumanoidModel rather than a specific entity's model class: every vanilla humanoid-
// shaped entity (zombies, skeletons, villagers, piglins, etc.), not just the player, renders
// through this same model class, just parameterized by its own render state type, so this
// covers all of them for free. Bones are matched by name directly against the model's part tree
// (e.g. "left_arm", the model's own part name) rather than a fixed list, so an animation applies
// to whatever bone names actually exist on whatever entity it's played on.
@Mixin(HumanoidModel.class)
abstract class HumanoidModelMixin<T extends HumanoidRenderState> {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void animationlib$applyAnimation(T state, CallbackInfo ci) {
        // These override setupAnim and reapply their pose after this runs; they have their own mixins.
        if ((Object) this instanceof SkeletonModel || (Object) this instanceof EndermanModel
                || (Object) this instanceof AbstractPiglinModel) {
            return;
        }

        Integer entityId = ((FabricRenderState) state).getData(RenderStateEntityId.KEY);
        // Handed off to ModelMixin, which captures this same model instance's render-time bone
        // positions for Animation#cameraFollowsEntity moments later, once renderToBuffer runs.
        BoneLookAt.beginModel(entityId);
        if (entityId == null) {
            return;
        }

        ModelPart root = ((Model<?>) (Object) this).root();
        AnimationPlayerImpl.sample(entityId).ifPresent(sample ->
                sample.data().bones().keySet().forEach(bone -> {
                    if (bone.equals("camera") || bone.equals("item")
                            || bone.equals("item_left") || bone.equals("item_right")) {
                        return;
                    }
                    ModelPart part = ModelBones.find(root, bone);
                    if (part != null) {
                        animationlib$applyBone(part, sample, bone);
                    } else {
                        MissingBoneWarnings.warnOnce(entityId, bone, "HumanoidModel");
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
