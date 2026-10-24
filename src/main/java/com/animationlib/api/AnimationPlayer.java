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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
    private AnimationPlayer() {
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson) {
        play(entity, animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson, null);
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, Identifier sound) {
        play(entity, animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson, false, sound);
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, boolean hideInventory,
                             Identifier sound) {
        play(entity, animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson, hideInventory,
                sound, null);
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, boolean hideInventory,
                             Identifier sound, Identifier item) {
        play(entity, animationId, cameraFollowEntityId, cameraFollowBone, forceThirdPerson, hideInventory,
                sound, item, null);
    }

    public static void play(Entity entity, Identifier animationId, Integer cameraFollowEntityId,
                             String cameraFollowBone, boolean forceThirdPerson, boolean hideInventory,
                             Identifier sound, Identifier item, Integer itemNum) {
        if (entity.level().isClientSide()) {
            AnimationPlayerImpl.sendToServer(PlayAnimationPayload.of(entity.getId(), animationId,
                    cameraFollowEntityId, cameraFollowBone, forceThirdPerson, hideInventory, sound, item, itemNum));
            return;
        }

        Identifier shownItem = item != null ? item : itemFromSlot(entity, animationId, itemNum);
        AnimationPlayerImpl.play(entity.getId(), animationId, cameraFollowEntityId, cameraFollowBone,
                forceThirdPerson, hideInventory, shownItem);
        broadcast(entity, PlayAnimationPayload.of(entity.getId(), animationId, cameraFollowEntityId,
                cameraFollowBone, forceThirdPerson, hideInventory, sound, shownItem, null));
    }

    // Only the server knows a player's whole inventory (clients are only told what others hold in
    // their hands), so the slot is resolved here and what's broadcast is just the resulting item.
    // An explicit item, here or in the animation's own "item" field, wins over any slot; an empty
    // or out-of-range slot, or an entity with no inventory, leaves the real held item alone.
    private static Identifier itemFromSlot(Entity entity, Identifier animationId, Integer itemNum) {
        var data = AnimationRegistry.get(animationId);
        if (data.map(d -> d.item() != null).orElse(false) || !(entity instanceof Player player)) {
            return null;
        }

        Integer slot = itemNum != null ? itemNum : data.map(AnimationData::itemNum).orElse(null);
        if (slot == null || slot < 0 || slot >= player.getInventory().getContainerSize()) {
            return null;
        }

        ItemStack stack = player.getInventory().getItem(slot);
        return stack.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(stack.getItem());
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
