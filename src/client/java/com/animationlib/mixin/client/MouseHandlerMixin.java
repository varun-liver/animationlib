package com.animationlib.mixin.client;

import com.animationlib.impl.AnimationPlayerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Freezes mouse look while the local player is playing an animation that either defines a
// "camera" bone or has a cameraFollowsEntity target, so its scripted (or followed) rotation
// isn't fought by the player's own mouse input.
@Mixin(MouseHandler.class)
abstract class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void animationlib$lockCameraDuringAnimation(double partialTick, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        boolean lockCamera = AnimationPlayerImpl.sample(player.getId())
                .map(sample -> sample.data().bones().containsKey("camera")
                        || (sample.cameraFollowEntityId() != null && sample.cameraFollowBone() != null))
                .orElse(false);
        if (lockCamera) {
            ci.cancel();
        }
    }
}
