package com.animationlib.impl.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

/**
 * Clientbound: the boss bar whose {@link net.minecraft.world.BossEvent} has uuid {@code barId}
 * draws {@code image} (an {@code assets/<namespace>/animationlib/bossbar/<path>.png} id) instead
 * of the vanilla bar texture. {@code image} is {@code ""} to go back to the vanilla texture.
 */
public record BossBarImagePayload(UUID barId, String image) implements CustomPacketPayload {
    public static final Type<BossBarImagePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("animationlib", "bossbar_image"));

    public static final StreamCodec<ByteBuf, BossBarImagePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, BossBarImagePayload::barId,
            ByteBufCodecs.STRING_UTF8, BossBarImagePayload::image,
            BossBarImagePayload::new
    );

    public static BossBarImagePayload of(UUID barId, Identifier image) {
        return new BossBarImagePayload(barId, image != null ? image.toString() : "");
    }

    public Identifier resolvedImage() {
        return image.isEmpty() ? null : Identifier.tryParse(image);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
