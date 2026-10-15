package com.animationlib.impl;

import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * Bridge from the common {@code AudioTrack} API to the client's sound playback. The client sets
 * the handlers on startup; without them (dedicated server) playing or stopping a sound by name
 * does nothing.
 */
public final class LocalAudio {
    private static volatile Consumer<Identifier> player = id -> { };
    private static volatile Consumer<Identifier> stopper = id -> { };

    private LocalAudio() {
    }

    public static void setHandlers(Consumer<Identifier> play, Consumer<Identifier> stop) {
        player = play;
        stopper = stop;
    }

    public static void play(Identifier sound) {
        player.accept(sound);
    }

    public static void stop(Identifier sound) {
        stopper.accept(sound);
    }
}
