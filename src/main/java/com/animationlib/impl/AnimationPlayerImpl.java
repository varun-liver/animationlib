package com.animationlib.impl;

import com.animationlib.api.AnimationData;
import com.animationlib.api.AnimationRegistry;
import com.animationlib.api.BoneAnimation;
import com.animationlib.api.Vec3f;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/** Tracks which animation each entity is currently playing, keyed by entity id. */
public final class AnimationPlayerImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final Map<Integer, Playing> PLAYING = new ConcurrentHashMap<>();

    // Set by AnimationlibClient on the logical client to ClientPlayNetworking::send. Stays a
    // no-op on a dedicated server, which never needs to send a play/stop request anywhere.
    private static Consumer<CustomPacketPayload> clientSender = payload -> { };

    private AnimationPlayerImpl() {
    }

    public static void setClientSender(Consumer<CustomPacketPayload> sender) {
        clientSender = sender;
    }

    public static void sendToServer(CustomPacketPayload payload) {
        clientSender.accept(payload);
    }

    public static void play(int entityId, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, boolean hideInventory,
                             Identifier item) {
        AnimationRegistry.get(animationId).ifPresentOrElse(
                data -> {
                    PLAYING.put(entityId, new Playing(animationId, data, cameraFollowEntityId, cameraFollowBone,
                            forceThirdPerson, hideInventory, item, System.currentTimeMillis()));
                    LOGGER.info("animationlib: playing {} on entity {} (bones: {})", animationId, entityId,
                            data.bones().keySet());
                },
                () -> LOGGER.warn("animationlib: unknown animation {}", animationId)
        );
    }

    public static void stop(int entityId) {
        PLAYING.remove(entityId);
    }

    public static boolean isPlaying(int entityId) {
        return PLAYING.containsKey(entityId);
    }

    public static boolean isPlaying(int entityId, Identifier animationId) {
        Playing playing = PLAYING.get(entityId);
        return playing != null && playing.id.equals(animationId);
    }

    /** Which bone names, if any, some currently-playing animation wants to look at on this entity. */
    public static Set<String> trackedBonesFor(int entityId) {
        Set<String> bones = new HashSet<>();
        for (Map.Entry<Integer, Playing> entry : PLAYING.entrySet()) {
            Playing playing = entry.getValue();
            String bone = playing.followBone();
            Integer followId = playing.followEntityId(entry.getKey());
            if (bone != null && followId != null && followId == entityId) {
                bones.add(bone);
            }
        }
        return bones;
    }

    public static Optional<Sample> sample(int entityId) {
        Playing playing = PLAYING.get(entityId);
        if (playing == null) {
            return Optional.empty();
        }

        double time = (System.currentTimeMillis() - playing.startMillis) / 1000.0;
        double length = playing.data.length();
        if (length > 0 && time >= length) {
            if (playing.data.loop()) {
                time %= length;
            } else {
                PLAYING.remove(entityId);
                return Optional.empty();
            }
        }

        return Optional.of(new Sample(playing.data, playing.followEntityId(entityId), playing.followBone(),
                playing.forceThirdPerson || playing.data.forceThirdPerson(),
                playing.hideInventory || playing.data.hideInventory(), playing.item(), time));
    }

    private record Playing(Identifier id, AnimationData data, Integer cameraFollowEntityId, String cameraFollowBone,
                            boolean forceThirdPerson, boolean hideInventory, Identifier itemOverride,
                            long startMillis) {
        // An item set in code (Animation#item) wins over the animation's own "item" field.
        Identifier item() {
            return itemOverride != null ? itemOverride
                    : data.item() != null ? Identifier.tryParse(data.item()) : null;
        }

        // A camera target set in code (cameraFollowsPlayer/cameraFollowsEntity) wins; otherwise the
        // animation's own camera_follows_player, which always targets the entity it plays on.
        String followBone() {
            return cameraFollowBone != null ? cameraFollowBone : data.cameraFollowsPlayer();
        }

        Integer followEntityId(int playedOnEntityId) {
            if (cameraFollowBone != null) {
                return cameraFollowEntityId;
            }
            return data.cameraFollowsPlayer() != null ? Integer.valueOf(playedOnEntityId) : null;
        }
    }

    public record Sample(AnimationData data, Integer cameraFollowEntityId, String cameraFollowBone,
                          boolean forceThirdPerson, boolean hideInventory, Identifier item, double time) {
        public Vec3f rotation(String bone) {
            return channel(bone, BoneAnimation::rotation);
        }

        public Vec3f position(String bone) {
            return channel(bone, BoneAnimation::position);
        }

        private Vec3f channel(String bone, Function<BoneAnimation, com.animationlib.api.BoneChannel> selector) {
            BoneAnimation boneAnimation = data.bones().get(bone);
            return boneAnimation == null ? null : AnimationSampler.sample(selector.apply(boneAnimation), time);
        }
    }
}
