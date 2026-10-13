package com.animationlib.client;

import com.animationlib.api.ActionBar;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class ActionBarDisplay implements ActionBar.Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final String FILE_NAME = "animationlib_actionbars.json";

    // Timed message from ActionBar#actionbar; takes priority over the sticky ones while it lasts.
    private Component message;
    private int ticksLeft;

    // ActionBar#actionBarStart messages by id, in start order; the most recent one is shown.
    private final Map<String, Component> sticky = new LinkedHashMap<>();

    // Ids ended with savedToDisk, for the world they were loaded from.
    private Path completedFile;
    private Set<String> completed = new HashSet<>();

    static void init() {
        ActionBarDisplay display = new ActionBarDisplay();
        ActionBar.setHandler(display);
        ClientTickEvents.END_CLIENT_TICK.register(display::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> display.reset());
    }

    @Override
    public void show(String text, int seconds, String id, boolean savedToDisk) {
        if (savedToDisk) {
            if (!loadCompleted().add(id)) {
                return;
            }
            saveCompleted();
        }
        message = Component.literal(text);
        ticksLeft = Math.max(0, seconds) * 20;
        refresh(Minecraft.getInstance());
    }

    @Override
    public void start(String id, String text) {
        if (loadCompleted().contains(id)) {
            return;
        }
        sticky.remove(id);
        sticky.put(id, Component.literal(text));
        refresh(Minecraft.getInstance());
    }

    @Override
    public void end(String id, boolean savedToDisk) {
        sticky.remove(id);
        if (savedToDisk && loadCompleted().add(id)) {
            saveCompleted();
        }
        Minecraft mc = Minecraft.getInstance();
        if (sticky.isEmpty() && ticksLeft <= 0 && mc.gui != null) {
            mc.gui.hud.setOverlayMessage(Component.empty(), false);
        } else {
            refresh(mc);
        }
    }

    private void reset() {
        sticky.clear();
        message = null;
        ticksLeft = 0;
        completedFile = null;
        completed = new HashSet<>();
    }

    // The vanilla overlay message only lasts ~3s, so keep refreshing it until it should go away.
    private void tick(Minecraft mc) {
        if (ticksLeft > 0) {
            ticksLeft--;
        }
        refresh(mc);
    }

    private void refresh(Minecraft mc) {
        if (mc.gui == null) {
            return;
        }
        Component shown = null;
        if (ticksLeft > 0 && message != null) {
            shown = message;
        } else {
            for (Component stickyMessage : sticky.values()) {
                shown = stickyMessage;
            }
        }
        if (shown != null) {
            mc.gui.hud.setOverlayMessage(shown, false);
        }
    }

    // Singleplayer keeps the file in the world folder. A multiplayer server's world isn't on this
    // machine, so it gets a file per server address in the game's config folder instead.
    private static Path completedPath(Minecraft mc) {
        if (mc.getSingleplayerServer() != null) {
            return mc.getSingleplayerServer().getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);
        }
        if (mc.getCurrentServer() != null) {
            String server = mc.getCurrentServer().ip.replaceAll("[^A-Za-z0-9._-]", "_");
            return FabricLoader.getInstance().getConfigDir().resolve("animationlib").resolve("actionbars")
                    .resolve(server + ".json");
        }
        return null;
    }

    private Set<String> loadCompleted() {
        Path path = completedPath(Minecraft.getInstance());
        if (path == null) {
            return completed;
        }
        if (!path.equals(completedFile)) {
            completedFile = path;
            completed = new HashSet<>();
            if (Files.exists(path)) {
                try {
                    JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8))
                            .getAsJsonObject();
                    if (root.has("completed")) {
                        root.getAsJsonArray("completed").forEach(e -> completed.add(e.getAsString()));
                    }
                } catch (IOException | RuntimeException e) {
                    LOGGER.error("animationlib: failed to read {}", path, e);
                }
            }
        }
        return completed;
    }

    private void saveCompleted() {
        if (completedFile == null) {
            return;
        }
        JsonArray ids = new JsonArray();
        completed.stream().sorted().forEach(ids::add);
        JsonObject root = new JsonObject();
        root.add("completed", ids);
        try {
            Files.createDirectories(completedFile.getParent());
            Files.writeString(completedFile, new GsonBuilder().setPrettyPrinting().create().toJson(root),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("animationlib: failed to write {}", completedFile, e);
        }
    }
}
