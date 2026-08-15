package com.animationlib.impl;

import com.animationlib.api.AnimationData;
import com.animationlib.api.BoneAnimation;
import com.animationlib.api.BoneChannel;
import com.animationlib.api.Keyframe;
import com.animationlib.api.Vec3f;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Loads Blockbench/Bedrock-format animation JSON from
 * {@code assets/<namespace>/animationlib/animations/*.json} on every resource reload.
 */
public final class AnimationLoader implements SimpleSynchronousResourceReloadListener {
    public static final AnimationLoader INSTANCE = new AnimationLoader();

    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final Identifier ID = Identifier.fromNamespaceAndPath("animationlib", "animations");
    private static final String DIRECTORY = "animationlib/animations";
    private static final Gson GSON = new Gson();

    private static volatile Map<Identifier, AnimationData> animations = Map.of();

    private AnimationLoader() {
    }

    public static Optional<AnimationData> get(Identifier id) {
        return Optional.ofNullable(animations.get(id));
    }

    public static Collection<Identifier> ids() {
        return animations.keySet();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<Identifier, AnimationData> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry :
                manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json")).entrySet()) {
            Identifier resourceId = entry.getKey();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root == null || !root.has("animations")) {
                    continue;
                }

                for (Map.Entry<String, JsonElement> animEntry : root.getAsJsonObject("animations").entrySet()) {
                    String name = stripAnimationPrefix(animEntry.getKey());
                    Identifier animId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), name);
                    loaded.put(animId, parseAnimation(name, animEntry.getValue().getAsJsonObject()));
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load animation file {}", resourceId, e);
            }
        }

        animations = Map.copyOf(loaded);
        LOGGER.info("animationlib: loaded {} animation(s)", animations.size());
    }

    private static String stripAnimationPrefix(String key) {
        return key.startsWith("animation.") ? key.substring("animation.".length()) : key;
    }

    private static AnimationData parseAnimation(String name, JsonObject json) {
        boolean loop = json.has("loop") && json.get("loop").getAsBoolean();
        double length = json.has("animation_length") ? json.get("animation_length").getAsDouble() : 0.0;

        Map<String, BoneAnimation> bones = new LinkedHashMap<>();
        if (json.has("bones")) {
            for (Map.Entry<String, JsonElement> boneEntry : json.getAsJsonObject("bones").entrySet()) {
                JsonObject boneJson = boneEntry.getValue().getAsJsonObject();
                bones.put(boneEntry.getKey(), new BoneAnimation(
                        parseChannel(boneJson.get("rotation")),
                        parseChannel(boneJson.get("position")),
                        parseChannel(boneJson.get("scale"))
                ));
            }
        }

        return new AnimationData(name, loop, length, Map.copyOf(bones));
    }

    // A channel is either a constant [x, y, z] array, or an object mapping timestamp -> keyframe value.
    private static BoneChannel parseChannel(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonArray()) {
            return BoneChannel.constant(parseVec3(element.getAsJsonArray()));
        }

        List<Keyframe> keyframes = new ArrayList<>();
        for (Map.Entry<String, JsonElement> kfEntry : element.getAsJsonObject().entrySet()) {
            keyframes.add(parseKeyframe(Double.parseDouble(kfEntry.getKey()), kfEntry.getValue()));
        }
        keyframes.sort(Comparator.comparingDouble(Keyframe::time));
        return new BoneChannel(List.copyOf(keyframes));
    }

    // A keyframe value is either a plain [x, y, z] array, or {"pre": [...], "post": [...], "lerp_mode": "..."}.
    private static Keyframe parseKeyframe(double time, JsonElement value) {
        if (value.isJsonArray()) {
            Vec3f vec = parseVec3(value.getAsJsonArray());
            return new Keyframe(time, vec, vec, "linear");
        }

        JsonObject obj = value.getAsJsonObject();
        JsonElement postEl = obj.has("post") ? obj.get("post") : obj.get("pre");
        JsonElement preEl = obj.has("pre") ? obj.get("pre") : postEl;
        Vec3f post = parseVec3(postEl.getAsJsonArray());
        Vec3f pre = preEl != null ? parseVec3(preEl.getAsJsonArray()) : post;
        String lerpMode = obj.has("lerp_mode") ? obj.get("lerp_mode").getAsString() : "linear";
        return new Keyframe(time, pre, post, lerpMode);
    }

    private static Vec3f parseVec3(JsonArray array) {
        return new Vec3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }
}
