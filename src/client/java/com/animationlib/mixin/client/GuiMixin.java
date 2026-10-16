package com.animationlib.mixin.client;

import com.animationlib.impl.AnimationPlayerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// While the local player is playing an animation with hideInventory, the player's inventory
// screen (survival or creative) can't be opened.
@Mixin(Gui.class)
abstract class GuiMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void animationlib$blockInventoryScreen(Screen screen, CallbackInfo ci) {
        if (animationlib$isInventoryScreen(screen) && animationlib$inventoryHidden()) {
            ci.cancel();
        }
    }

    // Also closes the inventory if it was already open when the animation started.
    @Inject(method = "tick", at = @At("HEAD"))
    private void animationlib$closeInventoryScreen(CallbackInfo ci) {
        Gui self = (Gui) (Object) this;
        if (animationlib$isInventoryScreen(self.screen()) && animationlib$inventoryHidden()) {
            self.setScreen(null);
        }
    }

    private static boolean animationlib$isInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    private static boolean animationlib$inventoryHidden() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && AnimationPlayerImpl.sample(player.getId())
                .map(AnimationPlayerImpl.Sample::hideInventory).orElse(false);
    }
}
