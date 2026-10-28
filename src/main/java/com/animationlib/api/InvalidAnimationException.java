package com.animationlib.api;

/**
 * Thrown for an animation setting that can't be used: a bad option passed to {@link Animation}, or
 * a field in an animation file with an invalid value (which makes the loader skip that file and
 * log this message).
 */
public class InvalidAnimationException extends AnimationException {
    public InvalidAnimationException(String message) {
        super(message);
    }
}
