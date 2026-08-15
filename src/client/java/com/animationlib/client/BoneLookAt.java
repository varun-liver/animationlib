package com.animationlib.client;

import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Bridges model-render-time bone position capture to camera-time lookup, for
 * {@code Animation#cameraFollowsEntity}'s look-at target.
 *
 * <p>{@link com.animationlib.mixin.client.HumanoidModelMixin} hands off which entity is about to
 * render via {@link #beginModel}; {@link com.animationlib.mixin.client.ModelMixin} then captures
 * that entity's tracked bone positions from the real render-time pose transform, moments later
 * for the same model instance. Rendering is single-threaded and sequential per entity, so this
 * simple static handoff is safe. Positions are one render frame stale by the time
 * {@code CameraMixin} reads them (it runs earlier in the frame than model rendering), which is
 * imperceptible for a look-at effect.
 */
public final class BoneLookAt {
    private static Integer currentEntityId;
    private static final Map<BoneKey, Vec3> POSITIONS = new ConcurrentHashMap<>();

    private BoneLookAt() {
    }

    public static void beginModel(Integer entityId) {
        currentEntityId = entityId;
    }

    public static Integer currentEntityId() {
        return currentEntityId;
    }

    public static void setPosition(int entityId, String bone, Vec3 position) {
        POSITIONS.put(new BoneKey(entityId, bone), position);
    }

    public static Vec3 getPosition(int entityId, String bone) {
        return POSITIONS.get(new BoneKey(entityId, bone));
    }

    private record BoneKey(int entityId, String bone) {
    }
}
