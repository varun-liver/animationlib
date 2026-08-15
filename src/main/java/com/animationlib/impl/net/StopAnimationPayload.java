package com.animationlib.impl.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Serverbound, it's a stop request from a client for its own player; {@code entityId} is ignored
 * and re-derived from the sender. Clientbound, it's the resulting broadcast telling a client to
 * stop whatever animation is playing on the player identified by {@code entityId}.
 */
public record StopAnimationPayload(int entityId) implements CustomPacketPayload {
    public static final Type<StopAnimationPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("animationlib", "stop_animation"));

    public static final StreamCodec<ByteBuf, StopAnimationPayload> CODEC =
            ByteBufCodecs.VAR_INT.map(StopAnimationPayload::new, StopAnimationPayload::entityId);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
