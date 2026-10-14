package com.animationlib.client;

import com.animationlib.impl.AnimationLoader;
import com.animationlib.impl.AnimationPlayerImpl;
import com.animationlib.impl.net.PlayAnimationPayload;
import com.animationlib.impl.net.StopAnimationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class AnimationlibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(AnimationLoader.INSTANCE);

        AnimationPlayerImpl.setClientSender(ClientPlayNetworking::send);
        ActionBarDisplay.init();
        AnimationCommand.init();

        AnimationSounds.init();

        ClientPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.TYPE, (payload, context) -> {
            AnimationPlayerImpl.play(payload.entityId(), payload.animationId(),
                    payload.resolvedCameraFollowEntityId(), payload.resolvedCameraFollowBone(),
                    payload.forceThirdPerson());
            // An explicit sound from AudioTrack wins over the animation's own "sound" field.
            Identifier sound = payload.resolvedSound();
            if (sound == null) {
                sound = AnimationLoader.get(payload.animationId())
                        .map(data -> data.sound() != null ? Identifier.tryParse(data.sound()) : null)
                        .orElse(null);
            }
            if (sound != null) {
                AnimationSounds.play(payload.entityId(), sound);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(StopAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayerImpl.stop(payload.entityId()));
    }
}
