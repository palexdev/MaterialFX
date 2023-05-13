package io.github.palexdev.mfxeffects.ripple.base;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.beans.Size;
import io.github.palexdev.mfxeffects.beans.properties.styleable.StyleableSizeProperty;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.function.Supplier;

/**
 * Public API that all ripple generators should expose.
 */
public interface RippleGenerator {

    /**
     * By default, does nothing.
     * <p>
     * Optionally generators can implement this convenience method to enable the generation of effects
     * on the target region.
     */
    default void enable() {
    }

    /**
     * By default, does nothing.
     * <p>
     * Optionally generators can implement this convenience method to disable the generation of effects
     * on the target region.
     */
    default void disable() {
    }

    /**
     * This is the core method responsible for generating ripple effects.
     * <p>
     * Should define the first phase of the effect, the generation.
     *
     * @see #release()
     */
    void generate(double x, double y);

    /**
     * Opposite of {@link #generate(double, double)}.
     * <p>
     * Should define the second phase of the effect, the fading out
     * <p></p>
     * This is optional, implementations can also rely on a single phase.
     */
    void release();

    /**
     * Shortcut for {@code generate(pos.getX(), pos.getY())}.
     */
    default void generate(Position pos) {
        generate(pos.getX(), pos.getY());
    }

    /**
     * Implementation can specify the actions needed for the generator's disposal.
     * By default, does nothing.
     */
    default void dispose() {
    }

    /**
     * @return the target region which defines some of the core generator's properties, like its geometry, and
     * it's also needed to add the handlers on it
     */
    Region getOwner();

    /**
     * @return the {@link Supplier} used by the generator to clip itself, thus avoiding ripples from
     * overflowing
     */
    Supplier<Region> getClipSupplier();

    /**
     * Sets the {@link Supplier} used by the generator to clip itself, thus avoiding ripples from
     * overflowing.
     */
    void setClipSupplier(Supplier<Region> clipSupplier);

    /**
     * @return the {@link Supplier} used by the generator to create ripples
     */
    Supplier<Ripple<?>> getRippleSupplier();

    /**
     * Sets the {@link Supplier} used by the generator to create ripples.
     */
    void setRippleSupplier(Supplier<Ripple<?>> rippleSupplier);

    /**
     * @return the preferred, default type of ripple the generator uses
     */
    Supplier<Ripple<?>> defaultRippleSupplier();

    boolean doAnimateBackground();

    /**
     * Specifies whether the generator should also animate its background color.
     */
    StyleableBooleanProperty animateBackgroundProperty();

    void setAnimateBackground(boolean animateBackground);

    Color getBackgroundColor();

    /**
     * Specifies the background color to use when animating it, see {@link #animateBackgroundProperty()}.
     */
    StyleableObjectProperty<Color> backgroundColorProperty();

    void setBackgroundColor(Color backgroundColor);

    Color getRippleColor();

    /**
     * Specifies ripple node color.
     */
    StyleableObjectProperty<Color> rippleColorProperty();

    void setRippleColor(Color rippleColor);

    Size getRipplePrefSize();

    /**
     * Specifies the preferred size of the ripple node.
     */
    StyleableSizeProperty ripplePrefSizeProperty();

    void setRipplePrefSize(Size ripplePrefSize);
}
