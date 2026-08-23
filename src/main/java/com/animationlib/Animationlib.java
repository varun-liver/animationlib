package com.animationlib;

import com.animationlib.api.AnimationPlayer;
import com.animationlib.impl.net.PlayAnimationPayload;
import com.animationlib.impl.net.StopAnimationPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class Animationlib implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(PlayAnimationPayload.TYPE, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayAnimationPayload.TYPE, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StopAnimationPayload.TYPE, StopAnimationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StopAnimationPayload.TYPE, StopAnimationPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.TYPE, (payload, context) -> {
            Entity target = resolveRequestedEntity(context.player(), payload.entityId());
            AnimationPlayer.play(target, payload.animationId(), payload.resolvedCameraFollowEntityId(),
                    payload.resolvedCameraFollowBone(), payload.forceThirdPerson(), payload.resolvedSoundId(),
                    payload.resolvedSoundEntityId());
        });
        ServerPlayNetworking.registerGlobalReceiver(StopAnimationPayload.TYPE, (payload, context) ->
                AnimationPlayer.stop(resolveRequestedEntity(context.player(), payload.entityId())));
    }

    // Trusts the request's entityId (so a client can animate another entity, not just itself), but
    // only for entities the sender is actually tracking — i.e. ones already loaded on its client —
    // so it can't puppet something it can't even see. Anything else falls back to the sender.
    private static Entity resolveRequestedEntity(ServerPlayer sender, int requestedEntityId) {
        Entity requested = sender.level().getEntity(requestedEntityId);
        if (requested == null || requested == sender) {
            // A player never appears in its own tracking list, so self-requests skip that check.
            return sender;
        }
        return PlayerLookup.tracking(requested).contains(sender) ? requested : sender;
    }
}
