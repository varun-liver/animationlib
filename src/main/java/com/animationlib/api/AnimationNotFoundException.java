package com.animationlib.api;

import net.minecraft.resources.Identifier;

/** Thrown by {@link AnimationRegistry#require} when no animation is loaded under an id. */
public class AnimationNotFoundException extends AnimationException {
    private final Identifier id;

    public AnimationNotFoundException(Identifier id) {
        super("No animation is loaded with id " + id);
        this.id = id;
    }

    public Identifier id() {
        return id;
    }
}
