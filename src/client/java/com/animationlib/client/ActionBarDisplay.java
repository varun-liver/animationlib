package com.animationlib.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

final class ActionBarDisplay {
    private static Component message;
    private static int ticksLeft;

    private ActionBarDisplay() {
    }

    static void init() {
        com.animationlib.api.ActionBar.setHandler(ActionBarDisplay::show);
        ClientTickEvents.END_CLIENT_TICK.register(ActionBarDisplay::tick);
    }

    private static void show(String text, int seconds) {
        message = Component.literal(text);
        ticksLeft = Math.max(0, seconds) * 20;
        apply(Minecraft.getInstance());
    }

    // The vanilla overlay message only lasts ~3s, so keep refreshing it until our time is up.
    private static void tick(Minecraft mc) {
        if (ticksLeft > 0) {
            ticksLeft--;
            apply(mc);
        }
    }

    private static void apply(Minecraft mc) {
        if (ticksLeft > 0 && message != null && mc.gui != null) {
            mc.gui.hud.setOverlayMessage(message, false);
        }
    }
}
