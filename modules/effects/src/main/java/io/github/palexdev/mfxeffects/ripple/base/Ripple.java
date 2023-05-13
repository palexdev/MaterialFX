package io.github.palexdev.mfxeffects.ripple.base;

import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.scene.shape.Shape;

/**
 * Public API for ripples that should be used by {@link RippleGenerator}.
 * <p></p>
 * In coordination with the new {@link MFXRippleGenerator}, the API has also been expanded to support
 * the 'dual-phase' ripple effect. Also, there's been a shift in responsibilities. The ripple can build any amount of
 * animations it needs, as long as it specifies the behavior for the two phases: 'press' and 'release'.
 * It's also worth mentioning that since the background animation for the generator may need to be 'interconnected' with
 * the other animations, it's the ripple's responsibility to build an animation for it and play/stop it.
 */
public interface Ripple<S extends Shape> {

    /**
     * @return the ripple's node
     */
    S toNode();

    /**
     * Should initialize the ripple if needed.
     */
    void init();

    /**
     * This is typically given by a {@link RippleGenerator} during the generation of a ripple.
     * Should be used to set the position of the ripple.
     */
    void position(double x, double y);

    /**
     * Should implement the logic to build/play the animations responsible for showing the ripple.
     */
    void playIn();

    /**
     * Should implement the logic to build/play the animations responsible for make the ripple disappear.
     */
    void playOut();
}
