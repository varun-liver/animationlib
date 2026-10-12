package com.animationlib.api;

import java.util.function.BiConsumer;

/**
 * Shows text in the action bar slot above the hotbar for a number of seconds. Client-side only;
 * does nothing when called where no client is running.
 */
public final class ActionBar {
    private static BiConsumer<String, Integer> handler;

    private ActionBar() {
    }

    public static void actionbar(String text, int seconds) {
        if (handler != null) {
            handler.accept(text, seconds);
        }
    }

    /** Installed by the client initializer. */
    public static void setHandler(BiConsumer<String, Integer> handler) {
        ActionBar.handler = handler;
    }
}
