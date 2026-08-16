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
                             String cameraFollowBone, boolean forceThirdPerson) {
        AnimationRegistry.get(animationId).ifPresentOrElse(
                data -> {
                    PLAYING.put(entityId, new Playing(animationId, data, cameraFollowEntityId, cameraFollowBone,
                            forceThirdPerson, System.currentTimeMillis()));
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
        for (Playing playing : PLAYING.values()) {
            if (playing.cameraFollowBone() != null
                    && playing.cameraFollowEntityId() != null
                    && playing.cameraFollowEntityId() == entityId) {
                bones.add(playing.cameraFollowBone());
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

        return Optional.of(new Sample(playing.data, playing.cameraFollowEntityId, playing.cameraFollowBone,
                playing.forceThirdPerson, time));
    }

    private record Playing(Identifier id, AnimationData data, Integer cameraFollowEntityId, String cameraFollowBone,
                            boolean forceThirdPerson, long startMillis) {
    }

    public record Sample(AnimationData data, Integer cameraFollowEntityId, String cameraFollowBone,
                          boolean forceThirdPerson, double time) {
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
