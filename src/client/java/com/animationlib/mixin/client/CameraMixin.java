package com.animationlib.mixin.client;

import com.animationlib.api.Vec3f;
import com.animationlib.client.BoneLookAt;
import com.animationlib.impl.AnimationPlayerImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Camera.class)
abstract class CameraMixin {
    // The camera type from before an animation with forceThirdPerson switched it to third
    // person, so it can be restored once that animation stops. Null when nothing is forced.
    private static CameraType animationlib$previousCameraType;

    @Shadow
    public abstract Entity entity();

    @Shadow
    public abstract Vec3 position();

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Inject(method = "update", at = @At("TAIL"))
    private void animationlib$applyCameraAnimation(DeltaTracker deltaTracker, CallbackInfo ci) {
        Entity entity = this.entity();
        Optional<AnimationPlayerImpl.Sample> sample =
                entity == null ? Optional.empty() : AnimationPlayerImpl.sample(entity.getId());

        animationlib$updateForcedThirdPerson(sample.map(AnimationPlayerImpl.Sample::forceThirdPerson).orElse(false));

        boolean hasFollowTarget = sample.map(s -> s.cameraFollowEntityId() != null && s.cameraFollowBone() != null)
                .orElse(false);
        boolean hasOwnCameraBone = sample.map(s -> s.data().bones().containsKey("camera")).orElse(false);
        if (!hasFollowTarget && !hasOwnCameraBone) {
            return;
        }

        AnimationPlayerImpl.Sample s = sample.get();

        // Rotation is absolute, not additive: it fully owns the look direction (paired with
        // MouseHandlerMixin freezing player input) rather than nudging whatever direction the
        // player happened to be facing. cameraFollowsEntity's target bone takes priority: aim at
        // its actual position (captured from the real render-time pose by ModelMixin, one frame
        // stale) rather than copying its rotation values. If it's configured but no live position
        // is available for that bone right now, this falls back to this animation's own "camera"
        // bone rotation, if any.
        Vec3 lookAt = hasFollowTarget ? BoneLookAt.getPosition(s.cameraFollowEntityId(), s.cameraFollowBone()) : null;
        if (lookAt != null) {
            // lookAt is camera-relative (captured from the same render-time PoseStack used to
            // draw the target), so it already IS the direction vector from camera to target.
            double horizontalDist = Math.sqrt(lookAt.x * lookAt.x + lookAt.z * lookAt.z);
            float pitch = (float) -Math.toDegrees(Math.atan2(lookAt.y, horizontalDist));
            float yaw = (float) (Math.toDegrees(Math.atan2(lookAt.z, lookAt.x)) - 90.0);
            this.setRotation(yaw, pitch);
        } else {
            Vec3f rotation = s.rotation("camera");
            if (rotation != null) {
                this.setRotation(rotation.y(), rotation.x());
            }
        }

        // Position always comes from this animation's own "camera" bone. The camera already
        // tracks the player's body position every frame (set earlier in this same update()
        // call); add the bone's own position from the JSON directly on top of that, rather than
        // transforming it through the camera's rotation the way Camera#move does.
        Vec3f position = s.position("camera");
        if (position != null) {
            Vec3 base = this.position();
            this.setPosition(base.x + position.x(), base.y + position.y(), base.z + position.z());
        }
    }

    private static void animationlib$updateForcedThirdPerson(boolean force) {
        Options options = Minecraft.getInstance().options;
        if (force) {
            if (animationlib$previousCameraType == null) {
                animationlib$previousCameraType = options.getCameraType();
                options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
        } else if (animationlib$previousCameraType != null) {
            options.setCameraType(animationlib$previousCameraType);
            animationlib$previousCameraType = null;
        }
    }
}
