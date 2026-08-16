package com.animationlib.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * A named, reusable handle to an animation resource. Create one with {@link #init}, tweak its
 * public fields, and call {@link #play(Entity)} on it whenever you want to trigger it. Plays on
 * any entity, as long as its bones (e.g. {@code left_arm}) match names actually present on that
 * entity's model. Works on either the logical client (only meaningful for the client's own
 * player) or the logical server (any entity):
 * <pre>{@code
 * public static final Animation WALK = Animation.init("mymod:walk");
 * WALK.cameraFollowsEntity(player, "body");
 * ...
 * WALK.play(player);
 * }</pre>
 */
public final class Animation {
    private final Identifier id;

    private Entity cameraFollowEntity;
    private String cameraFollowBone;

    /**
     * When {@code true} (the default), playing this animation forces the client into
     * third-person view for its duration, restoring whatever camera type was active before once
     * it stops.
     */
    public boolean forceThirdPerson = false;

    private Animation(Identifier id) {
        this.id = id;
    }

    /**
     * While this animation plays, locks the camera onto {@code bone} of {@code entity} (which
     * doesn't need to be the same entity this animation is played on) instead of onto this
     * animation's own {@code camera} bone, if any. {@code entity} only has visible rotation data
     * for {@code bone} at a given moment if it's itself currently playing some animation that
     * animates that bone; if not, this falls back to this animation's own {@code camera} bone
     * rotation, if it has one. Either way, this animation's own {@code camera} bone position
     * still applies, and mouse look is frozen for the duration, like a cutscene.
     *
     * <p>Pass {@code null} for both arguments to clear the target and go back to the default
     * behavior (this animation's own {@code camera} bone, if any).
     */
    public void cameraFollowsEntity(Entity entity, String bone) {
        this.cameraFollowEntity = entity;
        this.cameraFollowBone = bone;
    }

    public static Animation init(Identifier id) {
        return new Animation(id);
    }

    public static Animation init(String namespace, String path) {
        return init(Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static Animation init(String id) {
        return init(Identifier.parse(id));
    }

    public Identifier id() {
        return id;
    }

    public void play(Entity entity) {
        Integer followId = cameraFollowEntity != null ? cameraFollowEntity.getId() : null;
        AnimationPlayer.play(entity, id, followId, cameraFollowBone, forceThirdPerson);
    }

    public void stop(Entity entity) {
        AnimationPlayer.stop(entity);
    }

    public boolean isPlaying(Entity entity) {
        return AnimationPlayer.isPlaying(entity, id);
    }
}
