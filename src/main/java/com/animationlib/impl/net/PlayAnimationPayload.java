package com.animationlib.impl.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Serverbound, it's a play request from a client for its own player; {@code entityId} is ignored
 * and re-derived from the sender. Clientbound, it's the resulting broadcast telling a client to
 * start playing {@code animationId} on the player identified by {@code entityId}.
 *
 * <p>{@code cameraFollowEntityId} is {@code -1} and {@code cameraFollowBone} is {@code ""} when
 * no camera-follow target is set; use {@link #resolvedCameraFollowEntityId()} and
 * {@link #resolvedCameraFollowBone()} to read them back as nullable values.
 */
public record PlayAnimationPayload(int entityId, Identifier animationId, int cameraFollowEntityId,
                                    String cameraFollowBone, boolean forceThirdPerson) implements CustomPacketPayload {
    public static final Type<PlayAnimationPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("animationlib", "play_animation"));

    public static final StreamCodec<ByteBuf, PlayAnimationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayAnimationPayload::entityId,
            Identifier.STREAM_CODEC, PlayAnimationPayload::animationId,
            ByteBufCodecs.VAR_INT, PlayAnimationPayload::cameraFollowEntityId,
            ByteBufCodecs.STRING_UTF8, PlayAnimationPayload::cameraFollowBone,
            ByteBufCodecs.BOOL, PlayAnimationPayload::forceThirdPerson,
            PlayAnimationPayload::new
    );

    public static PlayAnimationPayload of(int entityId, Identifier animationId, Integer cameraFollowEntityId,
                                           String cameraFollowBone, boolean forceThirdPerson) {
        return new PlayAnimationPayload(entityId, animationId,
                cameraFollowEntityId != null ? cameraFollowEntityId : -1,
                cameraFollowBone != null ? cameraFollowBone : "",
                forceThirdPerson);
    }

    public Integer resolvedCameraFollowEntityId() {
        return cameraFollowEntityId == -1 ? null : cameraFollowEntityId;
    }

    public String resolvedCameraFollowBone() {
        return cameraFollowBone.isEmpty() ? null : cameraFollowBone;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
