package com.animationlib.api;

/** Base class of the unchecked exceptions animationlib throws. */
public class AnimationException extends RuntimeException {
    public AnimationException(String message) {
        super(message);
    }

    public AnimationException(String message, Throwable cause) {
        super(message, cause);
    }
}
