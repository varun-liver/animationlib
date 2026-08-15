package com.animationlib.mixin.client;

import com.animationlib.client.RenderStateEntityId;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// createRenderState(T, float) is final on EntityRenderer, so this fires for every entity of
// every type, unlike setupAnim which each model subclass overrides independently.
@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("RETURN"))
    private void animationlib$tagEntityId(T entity, float partialTick, CallbackInfoReturnable<S> cir) {
        ((FabricRenderState) cir.getReturnValue()).setData(RenderStateEntityId.KEY, entity.getId());
    }
}
