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

// While the local player is playing an animation with hideInventory, everything the game draws
// around the hotbar is skipped: the hotbar itself (bar, selection, offhand slot, items, attack
// indicator), the held item's name, health/hunger/armor, vehicle health, the XP/jump bar and the
// spectator hotbar. They're all drawn from this one method.
@Mixin(Hud.class)
abstract class HudMixin {
    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void animationlib$hideHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && AnimationPlayerImpl.sample(player.getId())
                .map(AnimationPlayerImpl.Sample::hideInventory).orElse(false)) {
            ci.cancel();
        }
    }
}
