package io.github.palexdev.mfxeffects.enums;

import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;

public enum RippleState {
    /**
     * Indicates that the generator is inactive, meaning that there's no animation playing, or
     * that the 'effect end' animations are playing
     */
    INACTIVE,

    /**
     * Indicates that the ripple effect has been generated and now the generator is waiting for its release,
     * see {@link RippleGenerator#release()}
     */
    WAITING_FOR_CLICK
}
