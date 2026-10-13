package com.animationlib.api;

/**
 * Shows text in the action bar slot above the hotbar. Client-side only; every method does nothing
 * where no client is running.
 */
public final class ActionBar {
    /** Implemented by the client initializer. */
    public interface Handler {
        void show(String text, int seconds, String id, boolean savedToDisk);

        void start(String id, String text);

        void end(String id, boolean savedToDisk);
    }

    private static Handler handler;

    private ActionBar() {
    }

    /** Shows {@code text} for {@code seconds} seconds. */
    public static void actionbar(String text, int seconds) {
        actionbar(text, seconds, null, false);
    }

    /**
     * Like {@link #actionbar(String, int)}, but when {@code savedToDisk} is true the id is written
     * to the world's json file as soon as the text is shown, so it only ever shows once per world,
     * even after restarting the game. {@code id} is only needed (and only used) when
     * {@code savedToDisk} is true.
     *
     * @throws IllegalArgumentException if {@code savedToDisk} is true and {@code id} is null
     */
    public static void actionbar(String text, int seconds, String id, boolean savedToDisk) {
        if (savedToDisk && id == null) {
            throw new IllegalArgumentException("id is required when savedToDisk is true");
        }
        if (handler != null) {
            handler.show(text, seconds, id, savedToDisk);
        }
    }

    /**
     * Keeps {@code text} on screen until {@link #actionBarEnd} is called with the same {@code id}.
     * Does nothing if that id was already ended with {@code savedToDisk = true} in this world.
     * Calling it again with the same id replaces the text. Cleared when you leave the world.
     */
    public static void actionBarStart(String id, String text) {
        if (handler != null) {
            handler.start(id, text);
        }
    }

    /**
     * Removes the text started with {@code id}. If {@code savedToDisk} is true, the id is written to
     * a json file in the world folder so {@link #actionBarStart} won't show it again, even after
     * restarting the game.
     */
    public static void actionBarEnd(String id, boolean savedToDisk) {
        if (handler != null) {
            handler.end(id, savedToDisk);
        }
    }

    /** Installed by the client initializer. */
    public static void setHandler(Handler handler) {
        ActionBar.handler = handler;
    }
}
