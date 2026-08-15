package com.animationlib.client;

import com.animationlib.impl.AnimationLoader;
import com.animationlib.impl.AnimationPlayerImpl;
import com.animationlib.impl.net.PlayAnimationPayload;
import com.animationlib.impl.net.StopAnimationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class AnimationlibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(AnimationLoader.INSTANCE);

        AnimationPlayerImpl.setClientSender(ClientPlayNetworking::send);

        ClientPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayerImpl.play(payload.entityId(), payload.animationId(),
                        payload.resolvedCameraFollowEntityId(), payload.resolvedCameraFollowBone(),
                        payload.forceThirdPerson()));
        ClientPlayNetworking.registerGlobalReceiver(StopAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayerImpl.stop(payload.entityId()));
    }
}
