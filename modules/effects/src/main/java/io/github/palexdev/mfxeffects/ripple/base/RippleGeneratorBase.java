package io.github.palexdev.mfxeffects.ripple.base;

import io.github.palexdev.mfxeffects.ripple.CircleRipple;
import javafx.scene.layout.Region;

import java.util.function.Supplier;

/**
 * Abstract class extending {@link Region} and implementing {@link RippleGenerator}.
 * <p></p>
 * This exists just to restrict the visibility of some internal methods, it's also a good base to implement
 * custom generators.
 */
public abstract class RippleGeneratorBase extends Region implements RippleGenerator {

    //================================================================================
    // Constructors
    //================================================================================
    protected RippleGeneratorBase() {
        setRippleSupplier(defaultRippleSupplier());
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This is responsible for building the generator's clip node.
     * <p>
     * By default, this just relies on {@link #getClipSupplier()}
     */
    protected Region buildClip() {
        return getClipSupplier().get();
    }

    /**
     * This is responsible for building the ripple node.
     * <p>
     * By default, this just relies on {@link #getRippleSupplier()}.
     */
    protected Ripple<?> buildRipple() {
        return getRippleSupplier().get();
    }

    /**
     * Since this extends {@link Region}, and the children list is unmodifiable (we want it to be so), this allows internal
     * classes to add the ripple node on the generator.
     */
    protected void setRipple(Ripple<?> ripple) {
        getChildren().setAll(ripple.toNode());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * @return a {@link Supplier} building ripples of type {@link CircleRipple}
     */
    @Override
    public Supplier<Ripple<?>> defaultRippleSupplier() {
        return () -> new CircleRipple(this);
    }
}
