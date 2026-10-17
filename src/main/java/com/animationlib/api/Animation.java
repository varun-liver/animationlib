package com.animationlib.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A named, reusable handle to an animation resource: configured once, then played as often as you
 * like.
 *
 * <p>Drop the animation file in {@code resources/assets/<modid>/animationlib/animations/} (see
 * {@link AnimationRegistry} for the supported formats and how ids are derived from them), then
 * declare a handle for it and set up whatever options it needs:
 * <pre>{@code
 * public final class Example {
 *     public static final Animation ANIMATION = Animation.init("modid:animation")
 *             .cameraFollowsPlayer("right_arm")
 *             .forceThirdPerson(true);
 * }
 *
 * ...
 *
 * Example.ANIMATION.play(player);
 * }</pre>
 *
 * <p>Every option setter returns {@code this}, so they can either chain as above or stand on their
 * own ({@code Example.ANIMATION.forceThirdPerson(true);}) — including later, to reconfigure a
 * handle between plays.
 *
 * <p>Plays on any entity, as long as its bones (e.g. {@code left_arm}) match names actually present
 * on that entity's model. Works on either the logical client (only meaningful for the client's own
 * player) or the logical server (any entity).
 */
public final class Animation {
    private final Identifier id;

    // Which entity's bone the camera should look at: an explicit one (cameraFollowsEntity), or
    // whichever entity this animation is played on (cameraFollowsPlayer), or neither.
    private Entity cameraFollowEntity;
    private boolean cameraFollowsPlayedEntity;
    private String cameraFollowBone;

    private boolean forceThirdPerson;
    private boolean hideInventory;

    // Every handle made through init(), by id (the latest one wins), so /animation can play an
    // animation with the options its handle was configured with.
    private static final Map<Identifier, Animation> HANDLES = new ConcurrentHashMap<>();

    private Animation(Identifier id) {
        this.id = id;
    }

    public static Animation init(Identifier id) {
        Animation animation = new Animation(id);
        HANDLES.put(id, animation);
        return animation;
    }

    /**
     * The handle most recently created with {@code init} for {@code id}, if any. A handle only
     * exists once the class declaring it has been loaded.
     */
    public static Optional<Animation> registered(Identifier id) {
        return Optional.ofNullable(HANDLES.get(id));
    }

    /** The ids of every handle created so far with {@code init}. */
    public static Set<Identifier> registeredIds() {
        return Set.copyOf(HANDLES.keySet());
    }

    /**
     * A new handle with the same id and options, that isn't registered: changing it leaves this
     * one (and what {@link #registered} returns) untouched.
     */
    public Animation copy() {
        Animation copy = new Animation(id);
        copy.cameraFollowEntity = cameraFollowEntity;
        copy.cameraFollowsPlayedEntity = cameraFollowsPlayedEntity;
        copy.cameraFollowBone = cameraFollowBone;
        copy.forceThirdPerson = forceThirdPerson;
        copy.hideInventory = hideInventory;
        return copy;
    }

    public static Animation init(String namespace, String path) {
        return init(Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static Animation init(String id) {
        return init(Identifier.parse(id));
    }

    /**
     * While this animation plays, locks the camera onto {@code bone} of the entity it's being
     * played on, instead of onto this animation's own {@code camera} bone, if any. Since the
     * camera only ever follows the viewing client's own player, that entity is the player
     * whenever this does anything at all — which is what lets it be configured up front, with no
     * entity in hand.
     *
     * <p>This animation's own {@code camera} bone position still applies on top, and mouse look is
     * frozen for the duration, like a cutscene. Pass {@code null} to clear the target and go back
     * to the default behavior (this animation's own {@code camera} bone, if any).
     */
    public Animation cameraFollowsPlayer(String bone) {
        this.cameraFollowEntity = null;
        this.cameraFollowsPlayedEntity = bone != null;
        this.cameraFollowBone = bone;
        return this;
    }

    /**
     * Like {@link #cameraFollowsPlayer}, but aims the camera at {@code bone} of some other
     * {@code entity} entirely, which doesn't need to be the one this animation is played on.
     *
     * <p>{@code entity} only has visible rotation data for {@code bone} at a given moment if it's
     * itself currently playing some animation that animates that bone; if not, this falls back to
     * this animation's own {@code camera} bone rotation, if it has one.
     *
     * <p>Pass {@code null} for both arguments to clear the target.
     */
    public Animation cameraFollowsEntity(Entity entity, String bone) {
        this.cameraFollowEntity = entity;
        this.cameraFollowsPlayedEntity = false;
        this.cameraFollowBone = bone;
        return this;
    }
    /**
     * When {@code true}, playing this animation forces the client into third-person view for its
     * duration, restoring whatever camera type was active before once it stops. Defaults to
     * {@code false}.
     */
    public Animation forceThirdPerson(boolean force) {
        this.forceThirdPerson = force;
        return this;
    }


    /**
     * When {@code true}, playing this animation hides the player's inventory for its duration: the
     * inventory screen can't be opened (and is closed if already open), and the hotbar is not
     * drawn, nor are the health, hunger, armor and XP bars around it. Defaults to {@code false}.
     */
    public Animation hideInventory(boolean hide) {
        this.hideInventory = hide;
        return this;
    }

    public Identifier id() {
        return id;
    }

    public void play(Entity entity) {
        AnimationPlayer.play(entity, id, cameraFollowId(entity), cameraFollowBone, forceThirdPerson,
                hideInventory, null);
    }

    public void stop(Entity entity) {
        AnimationPlayer.stop(entity);
    }

    public boolean isPlaying(Entity entity) {
        return AnimationPlayer.isPlaying(entity, id);
    }

    private Integer cameraFollowId(Entity playedOn) {
        if (cameraFollowEntity != null) {
            return cameraFollowEntity.getId();
        }
        return cameraFollowsPlayedEntity ? playedOn.getId() : null;
    }
}
