package com.animationlib.api;

import com.animationlib.impl.BossBarManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * Shows boss bars for entities, filled according to their health. Works on the logical server
 * (where a bar is shown to every player tracking its entity, and to the entity itself if it is a
 * player); on a client it does nothing.
 */
public final class BossBar {
    private BossBar() {
    }

    /**
     * Shows a boss bar named {@code id}, titled {@code title} in {@code color}, whose fill is the
     * entity's health as a fraction of its max health and is kept up to date every tick. The bar
     * disappears when the entity dies or is removed, or on {@link #remove(String)}. Calling this
     * again with the same id changes that bar's title, color and image (or moves it to another
     * entity) instead of adding a second bar. Different ids give separate bars, even on the same
     * entity.
     *
     * <p>{@code image} replaces the vanilla bar texture: the PNG at
     * {@code assets/<namespace>/animationlib/bossbar/<path>.png}, for the id {@code namespace:path}.
     * It is drawn 182 pixels wide (as tall as its proportions give), dimmed as the empty part of
     * the bar, and at full brightness for the filled part. A client that has no such image shows
     * the vanilla bar.
     *
     * @param image  the image's id, or {@code null} for the vanilla bar texture
     * @param entity a living entity, since only those have health
     */
    public static void bossbar(String id, BossEvent.BossBarColor color, String title, Identifier image,
                               LivingEntity entity) {
        BossBarManager.show(id, color, title, image, entity);
    }

    /** Like {@link #bossbar(String, BossEvent.BossBarColor, String, Identifier, LivingEntity)}, with the vanilla bar texture. */
    public static void bossbar(String id, BossEvent.BossBarColor color, String title, LivingEntity entity) {
        bossbar(id, color, title, null, entity);
    }

    /** Like {@link #bossbar(String, BossEvent.BossBarColor, String, LivingEntity)}, with one bar per entity. */
    public static void bossbar(BossEvent.BossBarColor color, String title, LivingEntity entity) {
        bossbar(BossBarManager.defaultId(entity), color, title, entity);
    }

    /** Removes the boss bar with this id, if any. Returns whether there was one. */
    public static boolean remove(String id) {
        return BossBarManager.remove(id);
    }

    /** Removes the bar {@link #bossbar(BossEvent.BossBarColor, String, LivingEntity)} showed for {@code entity}. */
    public static void remove(LivingEntity entity) {
        remove(BossBarManager.defaultId(entity));
    }
}
