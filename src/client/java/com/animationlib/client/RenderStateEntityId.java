package com.animationlib.client;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

/**
 * Not every {@link net.minecraft.client.renderer.entity.state.EntityRenderState} subclass carries
 * its own entity id (only {@code AvatarRenderState} does, for players). {@link
 * com.animationlib.mixin.client.EntityRendererMixin} stashes it here for every entity type, via
 * Fabric's render-state extra data mechanism, so model mixins can look up which entity they're
 * rendering regardless of what kind of entity it is.
 */
public final class RenderStateEntityId {
    public static final RenderStateDataKey<Integer> KEY = RenderStateDataKey.create(() -> "animationlib:entity_id");

    private RenderStateEntityId() {
    }
}
