package io.github.palexdev.mfxcomponents.skins.base;

/**
 * Defines common API for popup' skins that support open/close animations.
 */
public interface IMFXPopupSkin {

    /**
     * Plays the open animation.
     */
    void animateIn();

    /**
     * Plays the close animation.
     */
    void animateOut();
}
