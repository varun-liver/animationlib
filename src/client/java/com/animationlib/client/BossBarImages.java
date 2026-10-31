package com.animationlib.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The image each boss bar draws instead of the vanilla bar texture (see {@code BossBar#bossbar}),
 * from {@code assets/<namespace>/animationlib/bossbar/<path>.png}, or from layers
 * {@code <path>layer0.png}, {@code <path>layer1.png}, ... drawn on top of each other, layer 0 at the
 * bottom. The server tells a client which image goes with which bar;
 * {@code BossHealthOverlayMixin} then calls {@link #draw}.
 */
public final class BossBarImages implements SimpleSynchronousResourceReloadListener {
    public static final BossBarImages INSTANCE = new BossBarImages();

    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final Identifier ID = Identifier.fromNamespaceAndPath("animationlib", "bossbar_images");
    private static final String DIRECTORY = "animationlib/bossbar/";
    private static final int BAR_WIDTH = 182;

    private record Image(Identifier texture, int width, int height) {
    }

    // Bar (boss event) uuid -> image id, as sent by the server.
    private static final Map<UUID, Identifier> BARS = new HashMap<>();
    // Image id -> its layers in draw order, or empty if it doesn't exist. Cleared on resource reload.
    private static final Map<Identifier, List<Image>> IMAGES = new HashMap<>();

    private BossBarImages() {
    }

    public static void set(UUID barId, Identifier image) {
        if (image == null) {
            BARS.remove(barId);
        } else {
            BARS.put(barId, image);
        }
    }

    public static void clear() {
        BARS.clear();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        IMAGES.clear();
    }

    /** Draws the bar's image and returns true, or returns false if it has none (so vanilla draws it). */
    public static boolean draw(GuiGraphicsExtractor graphics, int x, int y, BossEvent event,
                               ResourceManager manager) {
        Identifier id = BARS.get(event.getId());
        if (id == null) {
            return false;
        }
        List<Image> layers = IMAGES.computeIfAbsent(id, key -> load(manager, key));
        if (layers.isEmpty()) {
            return false;
        }

        int fill = Mth.lerpDiscrete(event.getProgress(), 0, BAR_WIDTH);
        for (Image layer : layers) {
            drawLayer(graphics, x, y, layer, fill);
        }
        return true;
    }

    private static void drawLayer(GuiGraphicsExtractor graphics, int x, int y, Image image, int fill) {
        int height = Math.max(1, Math.round(BAR_WIDTH * (float) image.height() / image.width()));
        // The left part of the image at full brightness is the fill, the rest of it dimmed is the empty
        // bar. They sit side by side rather than overlapping, so draw order can't hide the fill.
        int fillSource = Math.round(fill * (float) image.width() / BAR_WIDTH);
        if (fill > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, image.texture(), x, y, 0.0f, 0.0f, fill, height,
                    Math.max(1, fillSource), image.height(), image.width(), image.height(), 0xFFFFFFFF);
        }
        if (fill < BAR_WIDTH) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, image.texture(), x + fill, y, (float) fillSource, 0.0f,
                    BAR_WIDTH - fill, height, Math.max(1, image.width() - fillSource), image.height(),
                    image.width(), image.height(), 0xFF606060);
        }
    }

    // The layers <path>layer0.png, <path>layer1.png, ... up to the first missing number, or else just
    // <path>.png. Empty if neither exists.
    private static List<Image> load(ResourceManager manager, Identifier id) {
        List<Image> layers = new ArrayList<>();
        for (int layer = 0; ; layer++) {
            Image image = read(manager, id, id.getPath() + "layer" + layer, false);
            if (image == null) {
                break;
            }
            layers.add(image);
        }
        if (layers.isEmpty()) {
            Image image = read(manager, id, id.getPath(), true);
            if (image != null) {
                layers.add(image);
            }
        }
        return List.copyOf(layers);
    }

    private static Image read(ResourceManager manager, Identifier id, String path, boolean warnIfMissing) {
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), DIRECTORY + path + ".png");
        Optional<Resource> resource = manager.getResource(texture);
        if (resource.isEmpty()) {
            if (warnIfMissing) {
                LOGGER.warn("animationlib: no boss bar image {} (expected at assets/{}/{}, or as layers {}layer0.png ...)",
                        id, texture.getNamespace(), texture.getPath(), id.getPath());
            }
            return null;
        }
        try (InputStream stream = resource.get().open(); NativeImage pixels = NativeImage.read(stream)) {
            return new Image(texture, pixels.getWidth(), pixels.getHeight());
        } catch (IOException e) {
            LOGGER.warn("animationlib: could not read boss bar image {}", texture, e);
            return null;
        }
    }
}
