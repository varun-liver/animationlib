package com.animationlib.mixin.client;

import com.animationlib.client.BossBarImages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// A boss bar with an image (BossBar#bossbar) draws that instead of the vanilla bar sprites; the
// title above it is still drawn by the overlay.
@Mixin(BossHealthOverlay.class)
abstract class BossHealthOverlayMixin {
    @Inject(method = "extractBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/world/BossEvent;)V",
            at = @At("HEAD"), cancellable = true)
    private void animationlib$drawImage(GuiGraphicsExtractor graphics, int x, int y, BossEvent event,
                                        CallbackInfo ci) {
        if (BossBarImages.draw(graphics, x, y, event, Minecraft.getInstance().getResourceManager())) {
            ci.cancel();
        }
    }
}
