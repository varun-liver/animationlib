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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Loads animations from {@code assets/<namespace>/animationlib/animations/} on every resource
 * reload: Blockbench/Bedrock-format {@code .json}, and glTF 2.0 {@code .gltf}/{@code .glb} (see
 * {@link GltfLoader}).
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

        for (Map.Entry<Identifier, Resource> entry : manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json")
                || id.getPath().endsWith(".gltf") || id.getPath().endsWith(".glb")).entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                String path = resourceId.getPath();
                Map<String, AnimationData> parsed;
                if (path.endsWith(".gltf")) {
                    parsed = readGltf(manager, resourceId, entry.getValue());
                } else if (path.endsWith(".glb")) {
                    parsed = readGlb(manager, resourceId, entry.getValue());
                } else {
                    parsed = readBedrock(entry.getValue());
                }
                for (Map.Entry<String, AnimationData> parsedEntry : parsed.entrySet()) {
                    Identifier animId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(),
                            sanitizeName(parsedEntry.getKey()));
                    loaded.put(animId, parsedEntry.getValue());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load animation file {}", resourceId, e);
            }
        }

        animations = Map.copyOf(loaded);
        LOGGER.info("animationlib: loaded {} animation(s)", animations.size());
    }

    private static Map<String, AnimationData> readBedrock(Resource resource) throws Exception {
        Map<String, AnimationData> result = new LinkedHashMap<>();
        try (BufferedReader reader = resource.openAsReader()) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("animations")) {
                return result;
            }
            for (Map.Entry<String, JsonElement> animEntry : root.getAsJsonObject("animations").entrySet()) {
                String name = stripAnimationPrefix(animEntry.getKey());
                result.put(name, parseAnimation(name, animEntry.getValue().getAsJsonObject()));
            }
        }
        return result;
    }

    private static Map<String, AnimationData> readGltf(ResourceManager manager, Identifier resourceId,
                                                         Resource resource) throws Exception {
        JsonObject root;
        try (BufferedReader reader = resource.openAsReader()) {
            root = GSON.fromJson(reader, JsonObject.class);
        }
        return root == null ? Map.of() : GltfLoader.load(manager, resourceId, root);
    }

    private static Map<String, AnimationData> readGlb(ResourceManager manager, Identifier resourceId,
                                                        Resource resource) throws Exception {
        byte[] data;
        try (InputStream in = resource.open()) {
            data = in.readAllBytes();
        }
        return GltfLoader.loadBinary(manager, resourceId, data);
    }

    private static String stripAnimationPrefix(String key) {
        return key.startsWith("animation.") ? key.substring("animation.".length()) : key;
    }

    // Identifier paths only allow [a-z0-9/._-]; glTF/Blender action names are often mixed-case
    // (e.g. "headAction") or contain spaces, so this maps anything else to '_'.
    private static String sanitizeName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
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
