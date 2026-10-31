package com.animationlib.impl;

import com.animationlib.impl.net.BossBarImagePayload;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps one {@link ServerBossEvent} per id passed to {@code BossBar.bossbar}: fills it from its
 * entity's health and shows it to whoever is tracking the entity, every server tick.
 *
 * <p>Bars are saved to {@code animationlib_bossbars.json} in the world folder whenever one is
 * added or removed, and loaded again when the world starts, so they survive quitting the game. A
 * saved bar only remembers its entity's uuid: it is hidden while that entity isn't loaded (a
 * player who is offline, an unloaded chunk) and shows again as soon as it is found. A bar is
 * deleted for good when its entity dies or is discarded.
 */
public final class BossBarManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final String FILE_NAME = "animationlib_bossbars.json";
    private static final int RESOLVE_INTERVAL_TICKS = 10;
    private static final Gson GSON = new Gson();

    private static final class Bar {
        final UUID entityUuid;
        final ServerBossEvent event;
        BossEvent.BossBarColor color;
        String title;
        // Drawn instead of the vanilla bar texture, or null.
        Identifier image;
        // Null while the entity isn't loaded.
        LivingEntity entity;

        Bar(UUID entityUuid, BossEvent.BossBarColor color, String title, Identifier image) {
            this.entityUuid = entityUuid;
            this.color = color;
            this.title = title;
            this.image = image;
            this.event = new ServerBossEvent(UUID.randomUUID(), Component.literal(title), color,
                    BossEvent.BossBarOverlay.PROGRESS);
        }
    }

    // By bar id. Only touched on the server thread.
    private static final Map<String, Bar> BARS = new LinkedHashMap<>();
    private static MinecraftServer server;
    private static int ticks;

    private BossBarManager() {
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(started -> {
            server = started;
            load();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(stopping -> save());
        ServerLifecycleEvents.SERVER_STOPPED.register(stopped -> {
            BARS.values().forEach(bar -> bar.event.removeAllPlayers());
            BARS.clear();
            server = null;
        });
        ServerTickEvents.END_SERVER_TICK.register(ticking -> tick());
    }

    /** The id a bar gets when none is given: its entity's uuid, so there is one per entity. */
    public static String defaultId(LivingEntity entity) {
        return entity.getUUID().toString();
    }

    public static Set<String> ids() {
        return Set.copyOf(BARS.keySet());
    }

    public static void show(String id, BossEvent.BossBarColor color, String title, Identifier image,
                            LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (server == null) {
            server = level.getServer();
        }

        Bar bar = BARS.get(id);
        if (bar == null || !bar.entityUuid.equals(entity.getUUID())) {
            // A new bar, or the id moved to another entity: the old bar's viewers lose it.
            if (bar != null) {
                bar.event.removeAllPlayers();
            }
            bar = new Bar(entity.getUUID(), color, title, image);
            BARS.put(id, bar);
        }
        bar.color = color;
        bar.title = title;
        if (!Objects.equals(bar.image, image)) {
            bar.image = image;
            for (ServerPlayer player : bar.event.getPlayers()) {
                sendImage(bar, player);
            }
        }
        bar.event.setColor(color);
        bar.event.setName(Component.literal(title));
        bar.entity = entity;
        update(bar);
        save();
    }

    /** Returns whether a bar with that id existed. */
    public static boolean remove(String id) {
        Bar bar = BARS.remove(id);
        if (bar != null) {
            bar.event.removeAllPlayers();
            save();
        }
        return bar != null;
    }

    private static void tick() {
        boolean changed = false;
        boolean resolve = ++ticks % RESOLVE_INTERVAL_TICKS == 0;
        Iterator<Bar> bars = BARS.values().iterator();
        while (bars.hasNext()) {
            Bar bar = bars.next();
            if (bar.entity != null) {
                Entity.RemovalReason reason = bar.entity.getRemovalReason();
                if (reason != null ? reason.shouldDestroy() : !bar.entity.isAlive()) {
                    bar.event.removeAllPlayers();
                    bars.remove();
                    changed = true;
                } else if (reason != null) {
                    // Unloaded with its chunk or player, or changed dimension: hidden until found again.
                    bar.event.removeAllPlayers();
                    bar.entity = null;
                } else {
                    update(bar);
                }
            } else if (resolve) {
                bar.entity = find(bar.entityUuid);
                if (bar.entity != null) {
                    update(bar);
                }
            }
        }
        if (changed) {
            save();
        }
    }

    // The loaded, living entity with this uuid, in any dimension, or null.
    private static LivingEntity find(UUID uuid) {
        if (server == null) {
            return null;
        }
        Entity found = server.getPlayerList().getPlayer(uuid);
        if (found == null) {
            found = server.overworld().getEntityInAnyDimension(uuid);
        }
        return found instanceof LivingEntity living && living.isAlive() && !living.isRemoved() ? living : null;
    }

    private static void update(Bar bar) {
        LivingEntity entity = bar.entity;
        bar.event.setProgress(Mth.clamp(entity.getHealth() / entity.getMaxHealth(), 0.0f, 1.0f));

        Set<ServerPlayer> viewers = new HashSet<>(PlayerLookup.tracking(entity));
        if (entity instanceof ServerPlayer self) {
            viewers.add(self);
        }
        for (ServerPlayer player : Set.copyOf(bar.event.getPlayers())) {
            if (!viewers.contains(player)) {
                bar.event.removePlayer(player);
            }
        }
        for (ServerPlayer player : viewers) {
            // The image is sent once per viewer, as the bar first reaches them.
            boolean added = !bar.event.getPlayers().contains(player);
            bar.event.addPlayer(player);
            if (added) {
                sendImage(bar, player);
            }
        }
    }

    private static void sendImage(Bar bar, ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, BossBarImagePayload.TYPE)) {
            ServerPlayNetworking.send(player, BossBarImagePayload.of(bar.event.getId(), bar.image));
        }
    }

    private static Path file() {
        return server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);
    }

    private static void save() {
        if (server == null) {
            return;
        }
        JsonArray array = new JsonArray();
        BARS.forEach((id, bar) -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", id);
            json.addProperty("entity", bar.entityUuid.toString());
            json.addProperty("color", bar.color.getName());
            json.addProperty("title", bar.title);
            if (bar.image != null) {
                json.addProperty("image", bar.image.toString());
            }
            array.add(json);
        });
        JsonObject root = new JsonObject();
        root.add("bars", array);
        try {
            Files.writeString(file(), GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("animationlib: failed to write {}", file(), e);
        }
    }

    private static void load() {
        BARS.clear();
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            for (JsonElement element : root.getAsJsonArray("bars")) {
                JsonObject json = element.getAsJsonObject();
                BossEvent.BossBarColor color = BossEvent.BossBarColor.valueOf(
                        json.get("color").getAsString().toUpperCase(java.util.Locale.ROOT));
                Identifier image = json.has("image") ? Identifier.tryParse(json.get("image").getAsString()) : null;
                BARS.put(json.get("id").getAsString(), new Bar(UUID.fromString(json.get("entity").getAsString()),
                        color, json.get("title").getAsString(), image));
            }
        } catch (Exception e) {
            LOGGER.error("animationlib: failed to read {}", path, e);
        }
    }
}
