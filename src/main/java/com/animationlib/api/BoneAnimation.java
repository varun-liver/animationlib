package com.animationlib.api;

/** The animated channels for a single bone. Any channel is {@code null} if the bone doesn't animate it. */
public record BoneAnimation(BoneChannel rotation, BoneChannel position, BoneChannel scale) {
}
