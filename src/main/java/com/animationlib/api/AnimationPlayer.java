package com.animationlib.api;

import com.animationlib.impl.AnimationPlayerImpl;
import com.animationlib.impl.net.PlayAnimationPayload;
import com.animationlib.impl.net.StopAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plays a loaded animation on an entity. While playing, its bones (matched by name against
 * whatever the entity's model actually has, e.g. {@code head}, {@code left_arm}) are applied to
 * the entity's rendered model each frame, and its {@code camera} bone is applied to the viewing
 * client's own camera if the entity being animated is that client's own player.
 *
 * <p>Works from either side. Called on the logical server, with any entity, it updates
 * server-side state directly and broadcasts the change to everyone tracking that entity (plus
 * the entity itself, if it's a {@code ServerPlayer}, since a player doesn't track itself). Called
 * on the logical client, it sends a request to the server; the server always applies that request
 * to the sender's own player, regardless of which entity was passed, since a client has no
 * authority to trigger animations on other entities.
 */
public final class AnimationPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");

    private AnimationPlayer() {
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson) {
        play(entity, animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson, null, null);
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, Identifier soundId,
                             Integer soundEntityId) {
        if (entity.level().isClientSide()) {
            AnimationPlayerImpl.sendToServer(PlayAnimationPayload.of(entity.getId(), animationId,
                    cameraFollowEntityId, cameraFollowBone, forceThirdPerson, soundId, soundEntityId));
            return;
        }

        AnimationPlayerImpl.play(entity.getId(), animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson);
        playSound(entity, soundId, soundEntityId);
        broadcast(entity, PlayAnimationPayload.of(entity.getId(), animationId, cameraFollowEntityId,
                cameraFollowBone, forceThirdPerson, soundId, soundEntityId));
    }

    // Server-side only: Level#playSound already sends vanilla's own sound packet to every nearby
    // client, so playing it again in the clientbound receiver would double it up for the players
    // who can hear it. Passing null as the excluded player means nobody is skipped.
    //
    // The sound is positioned at the audioTrack player rather than at the animated entity, so it
    // is emitted from whoever triggered it even when the animation itself is playing on something
    // else. That player is looked up on the server rather than trusted from the payload object.
    private static void playSound(Entity animated, Identifier soundId, Integer soundEntityId) {
        if (soundId == null || soundEntityId == null) {
            return;
        }
        Entity source = animated.level().getEntity(soundEntityId);
        if (source == null) {
            LOGGER.warn("animationlib: audioTrack player {} not found on the server", soundEntityId);
            return;
        }
        BuiltInRegistries.SOUND_EVENT.getOptional(soundId).ifPresentOrElse(
                sound -> source.level().playSound(null, source.getX(), source.getY(), source.getZ(),
                        sound, SoundSource.PLAYERS, 1.0f, 1.0f),
                () -> LOGGER.warn("animationlib: unknown sound {}", soundId));
    }

    public static void stop(Entity entity) {
        if (entity.level().isClientSide()) {
            AnimationPlayerImpl.sendToServer(new StopAnimationPayload(entity.getId()));
            return;
        }

        AnimationPlayerImpl.stop(entity.getId());
        broadcast(entity, new StopAnimationPayload(entity.getId()));
    }

    public static boolean isPlaying(Entity entity) {
        return AnimationPlayerImpl.isPlaying(entity.getId());
    }

    public static boolean isPlaying(Entity entity, Identifier animationId) {
        return AnimationPlayerImpl.isPlaying(entity.getId(), animationId);
    }

    private static void broadcast(Entity entity, CustomPacketPayload payload) {
        if (entity instanceof ServerPlayer self) {
            ServerPlayNetworking.send(self, payload);
        }
        for (ServerPlayer observer : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(observer, payload);
        }
    }
}
