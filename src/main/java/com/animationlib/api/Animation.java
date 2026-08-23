package com.animationlib.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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

    private Identifier soundId;
    private Player soundPlayer;

    private Animation(Identifier id) {
        this.id = id;
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
     * Plays {@code soundId} once at {@code player}'s position each time this animation starts.
     * Everyone in earshot hears it, not just {@code player}. Pass {@code null} for either
     * argument to clear the track.
     *
     * <p>The id must name a registered {@code SoundEvent}: a vanilla one such as
     * {@code "minecraft:entity.zombie.ambient"}, or your own, declared in
     * {@code assets/<modid>/sounds.json} with the {@code .ogg} under
     * {@code assets/<modid>/sounds/} and registered as a {@code SoundEvent}.
     *
     * <p>Because this needs a {@code Player} in hand, it can't be set on a {@code static final}
     * handle at class-load time the way {@link #cameraFollowsPlayer} and {@link #forceThirdPerson}
     * can — call it at the point you play the animation:
     * <pre>{@code
     * Example.ANIMATION.audioTrack("modid:whoosh", player);
     * Example.ANIMATION.play(zombie);
     * }</pre>
     *
     * <p>Fire-and-forget: {@link #stop} does not cut the sound off, and a looping animation does
     * not re-trigger it on each cycle.
     */
    public Animation audioTrack(String soundId, Player player) {
        boolean cleared = soundId == null || player == null;
        this.soundId = cleared ? null : Identifier.parse(soundId);
        this.soundPlayer = cleared ? null : player;
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

    public Identifier id() {
        return id;
    }

    public void play(Entity entity) {
        AnimationPlayer.play(entity, id, cameraFollowId(entity), cameraFollowBone, forceThirdPerson,
                soundId, soundPlayer != null ? soundPlayer.getId() : null);
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
