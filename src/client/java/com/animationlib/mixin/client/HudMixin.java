package com.animationlib.mixin.client;

import com.animationlib.impl.AnimationPlayerImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// While the local player is playing an animation with hideInventory, the hotbar's items (and the
// held item's name above it) aren't drawn.
@Mixin(Hud.class)
abstract class HudMixin {
    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    private void animationlib$hideHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (animationlib$inventoryHidden()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void animationlib$hideSelectedItemName(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (animationlib$inventoryHidden()) {
            ci.cancel();
        }
    }

    private static boolean animationlib$inventoryHidden() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && AnimationPlayerImpl.sample(player.getId())
                .map(AnimationPlayerImpl.Sample::hideInventory).orElse(false);
    }
}
