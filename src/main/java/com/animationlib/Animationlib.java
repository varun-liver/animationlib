package com.animationlib;

import com.animationlib.api.AnimationPlayer;
import com.animationlib.impl.net.PlayAnimationPayload;
import com.animationlib.impl.net.StopAnimationPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Animationlib implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(PlayAnimationPayload.TYPE, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayAnimationPayload.TYPE, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StopAnimationPayload.TYPE, StopAnimationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StopAnimationPayload.TYPE, StopAnimationPayload.CODEC);

        // A client's play/stop request re-derives the target from the sender rather than
        // trusting the payload's entityId, so one player can't puppet another's animation.
        ServerPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayer.play(context.player(), payload.animationId(), payload.resolvedCameraFollowEntityId(),
                        payload.resolvedCameraFollowBone(), payload.forceThirdPerson()));
        ServerPlayNetworking.registerGlobalReceiver(StopAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayer.stop(context.player()));
    }
}
