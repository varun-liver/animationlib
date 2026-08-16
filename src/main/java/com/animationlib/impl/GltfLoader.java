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
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads animations from glTF 2.0 files: JSON {@code .gltf} (Blender's "glTF Separate" with a
 * sibling {@code .bin}, or "glTF Embedded" with base64 buffers) and binary {@code .glb}
 * ("glTF Binary", a small header plus a JSON chunk and an optional BIN chunk in one file). Only
 * animation data is used: translation ("position"), rotation, and scale channels targeting a
 * named node become that node's name's bone channels, same as the Bedrock/Blockbench format.
 * Rotation quaternions are converted to Euler XYZ degrees at load time. Translation is assumed to
 * be authored at "1 Blender unit = 1 Minecraft block" and is scaled by 16 to match the 1/16-block
 * units the rest of this library (and ModelPart) uses elsewhere; if positions come out 16x too
 * big or too small, that's the constant to adjust. Morph-target ("weights") channels, sparse
 * accessors, and non-FLOAT component types aren't supported.
 */
final class GltfLoader {
    private static final int COMPONENT_TYPE_FLOAT = 5126;
    private static final int GLB_MAGIC = 0x46546C67; // "glTF", little-endian
    private static final int GLB_CHUNK_JSON = 0x4E4F534A; // "JSON", little-endian
    private static final int GLB_CHUNK_BIN = 0x004E4942; // "BIN\0", little-endian
    private static final Gson GSON = new Gson();

    private GltfLoader() {
    }

    static Map<String, AnimationData> load(ResourceManager manager, Identifier resourceId, JsonObject root)
            throws IOException {
        return load(manager, resourceId, root, null);
    }

    static Map<String, AnimationData> loadBinary(ResourceManager manager, Identifier resourceId, byte[] glb)
            throws IOException {
        Glb parsed = parseGlb(glb);
        return load(manager, resourceId, parsed.json(), parsed.binChunk());
    }

    // embeddedBuffer is the .glb's BIN chunk, used for any buffer with no "uri" (the normal case
    // for a Blender-exported .glb, which packs everything into that one chunk). Null for a plain
    // .gltf file, where every buffer must have a "uri" (data: or a sibling resource).
    private static Map<String, AnimationData> load(ResourceManager manager, Identifier resourceId, JsonObject root,
                                                     byte[] embeddedBuffer) throws IOException {
        JsonArray animations = root.getAsJsonArray("animations");
        if (animations == null) {
            return Map.of();
        }

        byte[][] buffers = readBuffers(manager, resourceId, root.getAsJsonArray("buffers"), embeddedBuffer);
        JsonArray bufferViews = root.getAsJsonArray("bufferViews");
        JsonArray accessors = root.getAsJsonArray("accessors");
        JsonArray nodes = root.getAsJsonArray("nodes");

        Map<String, AnimationData> result = new LinkedHashMap<>();
        for (int i = 0; i < animations.size(); i++) {
            JsonObject animJson = animations.get(i).getAsJsonObject();
            String name = stripAnimationPrefix(animJson.has("name") ? animJson.get("name").getAsString() : "gltf_" + i);
            AnimationData data = parseAnimation(name, animJson, buffers, bufferViews, accessors, nodes);
            if (data != null) {
                result.put(name, data);
            }
        }
        return result;
    }

    private record Glb(JsonObject json, byte[] binChunk) {
    }

    private static Glb parseGlb(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        if (buf.remaining() < 12 || buf.getInt() != GLB_MAGIC) {
            throw new IOException("Not a valid .glb file (bad header)");
        }
        buf.getInt(); // version, unused
        buf.getInt(); // total declared length, unused

        JsonObject json = null;
        byte[] binChunk = null;
        while (buf.remaining() >= 8) {
            int chunkLength = buf.getInt();
            int chunkType = buf.getInt();
            byte[] chunkData = new byte[chunkLength];
            buf.get(chunkData);
            if (chunkType == GLB_CHUNK_JSON) {
                json = GSON.fromJson(new String(chunkData, StandardCharsets.UTF_8), JsonObject.class);
            } else if (chunkType == GLB_CHUNK_BIN) {
                binChunk = chunkData;
            }
        }
        if (json == null) {
            throw new IOException(".glb file has no JSON chunk");
        }
        return new Glb(json, binChunk);
    }

    private static String stripAnimationPrefix(String name) {
        return name.startsWith("animation.") ? name.substring("animation.".length()) : name;
    }

    private static byte[][] readBuffers(ResourceManager manager, Identifier resourceId, JsonArray buffersJson,
                                         byte[] embeddedBuffer) throws IOException {
        if (buffersJson == null) {
            return new byte[0][];
        }
        byte[][] buffers = new byte[buffersJson.size()][];
        for (int i = 0; i < buffersJson.size(); i++) {
            buffers[i] = readBuffer(manager, resourceId, buffersJson.get(i).getAsJsonObject(), embeddedBuffer);
        }
        return buffers;
    }

    private static byte[] readBuffer(ResourceManager manager, Identifier resourceId, JsonObject bufferJson,
                                      byte[] embeddedBuffer) throws IOException {
        if (!bufferJson.has("uri")) {
            // The normal case for a .glb: this buffer's data lives in the container's BIN chunk.
            if (embeddedBuffer == null) {
                throw new IOException("Buffer has no \"uri\" and no embedded .glb BIN chunk is available");
            }
            return embeddedBuffer;
        }

        String uri = bufferJson.get("uri").getAsString();
        if (uri.startsWith("data:")) {
            int comma = uri.indexOf(',');
            return Base64.getDecoder().decode(uri.substring(comma + 1));
        }

        // External .bin (Blender's default "glTF Separate" export): resolve as a sibling
        // resource next to the .gltf file, same namespace and directory.
        String path = resourceId.getPath();
        String dir = path.substring(0, path.lastIndexOf('/') + 1);
        String decodedUri = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        Identifier bufferId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), dir + decodedUri);
        try (InputStream in = manager.open(bufferId)) {
            return in.readAllBytes();
        }
    }

    private static AnimationData parseAnimation(String name, JsonObject animJson, byte[][] buffers,
                                                  JsonArray bufferViews, JsonArray accessors, JsonArray nodes) {
        JsonArray samplers = animJson.getAsJsonArray("samplers");
        JsonArray channels = animJson.getAsJsonArray("channels");
        if (samplers == null || channels == null || nodes == null) {
            return null;
        }

        // [rotation, position, scale] per bone name, matching BoneAnimation's field order.
        Map<String, BoneChannel[]> bones = new LinkedHashMap<>();
        double length = 0.0;

        for (JsonElement channelEl : channels) {
            JsonObject channel = channelEl.getAsJsonObject();
            JsonObject target = channel.getAsJsonObject("target");
            if (!target.has("node")) {
                continue;
            }
            String path = target.get("path").getAsString();
            int slot = switch (path) {
                case "rotation" -> 0;
                case "translation" -> 1;
                case "scale" -> 2;
                default -> -1;
            };
            if (slot < 0) {
                continue;
            }

            JsonObject node = nodes.get(target.get("node").getAsInt()).getAsJsonObject();
            if (!node.has("name")) {
                continue;
            }
            String bone = node.get("name").getAsString();

            JsonObject sampler = samplers.get(channel.get("sampler").getAsInt()).getAsJsonObject();
            String interpolation = sampler.has("interpolation") ? sampler.get("interpolation").getAsString() : "LINEAR";
            float[][] times = readAccessor(buffers, bufferViews, accessors, sampler.get("input").getAsInt());
            float[][] values = readAccessor(buffers, bufferViews, accessors, sampler.get("output").getAsInt());

            BoneChannel boneChannel = buildChannel(path, interpolation, times, values);
            if (boneChannel == null) {
                continue;
            }
            for (Keyframe kf : boneChannel.keyframes()) {
                length = Math.max(length, kf.time());
            }
            bones.computeIfAbsent(bone, b -> new BoneChannel[3])[slot] = boneChannel;
        }

        if (bones.isEmpty()) {
            return null;
        }

        Map<String, BoneAnimation> boneAnimations = new LinkedHashMap<>();
        for (Map.Entry<String, BoneChannel[]> entry : bones.entrySet()) {
            BoneChannel[] c = entry.getValue();
            boneAnimations.put(entry.getKey(), new BoneAnimation(c[0], c[1], c[2]));
        }

        return new AnimationData(name, false, length, Map.copyOf(boneAnimations));
    }

    private static BoneChannel buildChannel(String path, String interpolation, float[][] times, float[][] values) {
        boolean cubicSpline = "CUBICSPLINE".equals(interpolation);
        // CUBICSPLINE's tangents aren't evaluated; only the middle (value) triple is used, so a
        // cubic-spline channel plays back as if it were linear.
        String lerpMode = "STEP".equals(interpolation) ? "step" : "linear";
        boolean isRotation = path.equals("rotation");

        List<Keyframe> keyframes = new ArrayList<>();
        for (int i = 0; i < times.length; i++) {
            int valueIndex = cubicSpline ? i * 3 + 1 : i;
            if (valueIndex >= values.length) {
                break;
            }
            float[] raw = values[valueIndex];
            Vec3f value = isRotation ? quaternionToEuler(raw) : translationOrScale(path, raw);
            keyframes.add(new Keyframe(times[i][0], value, value, lerpMode));
        }
        if (keyframes.isEmpty()) {
            return null;
        }
        keyframes.sort(Comparator.comparingDouble(Keyframe::time));
        return new BoneChannel(List.copyOf(keyframes));
    }

    private static Vec3f translationOrScale(String path, float[] raw) {
        if (path.equals("translation")) {
            return new Vec3f(raw[0] * 16f, raw[1] * 16f, raw[2] * 16f);
        }
        return new Vec3f(raw[0], raw[1], raw[2]);
    }

    private static Vec3f quaternionToEuler(float[] xyzw) {
        Vector3f euler = new Quaternionf(xyzw[0], xyzw[1], xyzw[2], xyzw[3]).getEulerAnglesXYZ(new Vector3f());
        return new Vec3f(
                (float) Math.toDegrees(euler.x()),
                (float) Math.toDegrees(euler.y()),
                (float) Math.toDegrees(euler.z())
        );
    }

    // Decodes a glTF accessor into `count` rows of componentCount floats each. Only FLOAT
    // component-typed, non-sparse accessors are supported (what Blender's exporter emits for
    // animation sampler input/output).
    private static float[][] readAccessor(byte[][] buffers, JsonArray bufferViews, JsonArray accessors, int index) {
        JsonObject accessor = accessors.get(index).getAsJsonObject();
        int componentType = accessor.get("componentType").getAsInt();
        if (componentType != COMPONENT_TYPE_FLOAT) {
            throw new IllegalArgumentException("Unsupported accessor componentType " + componentType + " (only FLOAT is supported)");
        }

        int componentCount = switch (accessor.get("type").getAsString()) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            default -> throw new IllegalArgumentException("Unsupported accessor type " + accessor.get("type"));
        };
        int count = accessor.get("count").getAsInt();
        int accessorByteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;

        JsonObject bufferView = bufferViews.get(accessor.get("bufferView").getAsInt()).getAsJsonObject();
        int bufferIndex = bufferView.get("buffer").getAsInt();
        int viewByteOffset = bufferView.has("byteOffset") ? bufferView.get("byteOffset").getAsInt() : 0;
        int stride = bufferView.has("byteStride") ? bufferView.get("byteStride").getAsInt() : componentCount * 4;

        ByteBuffer buffer = ByteBuffer.wrap(buffers[bufferIndex]).order(ByteOrder.LITTLE_ENDIAN);
        int base = viewByteOffset + accessorByteOffset;
        float[][] result = new float[count][componentCount];
        for (int i = 0; i < count; i++) {
            int rowOffset = base + i * stride;
            for (int c = 0; c < componentCount; c++) {
                result[i][c] = buffer.getFloat(rowOffset + c * 4);
            }
        }
        return result;
    }
}
