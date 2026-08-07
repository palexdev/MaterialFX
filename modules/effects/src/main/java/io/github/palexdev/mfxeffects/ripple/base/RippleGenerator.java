/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxeffects.ripple.base;

import java.util.function.Supplier;

import io.github.palexdev.mfxeffects.beans.Position;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/// Public API that all ripple generators should expose.
public interface RippleGenerator {

    /// By default, does nothing.
    ///
    /// Optionally, generators can implement this convenience method to enable the generation of effects
    /// on the target region.
    default void enable() {
    }

    /// By default, does nothing.
    ///
    /// Optionally, generators can implement this convenience method to disable the generation of effects
    /// on the target region.
    default void disable() {
    }

    /// This is the core method responsible for generating ripple effects.
    ///
    /// Should define the first phase of the effect, the generation.
    ///
    /// @see #release()
    void generate(double x, double y);

    /// Opposite of [#generate(double, double)].
    ///
    /// Should define the second phase of the effect, the fading out
    ///
    /// This is optional, implementations can also rely on a single phase.
    void release();

    /// Shortcut for `generate(pos.x(), pos.y())`, although if the given position is `null` does not generate.
    default void generate(Position pos) {
        if (pos != null)
            generate(pos.x(), pos.y());
    }

    /// Implementation can specify the actions needed for the generator's disposal.
    /// By default, does nothing.
    default void dispose() {
    }

    /// @return the target region which defines some of the core generator's properties, like its geometry, and
    /// it's also necessary to add the handlers on it
    Region getOwner();

    /// @return the [Supplier] used by the generator to clip itself, thus avoiding ripples from
    /// overflowing
    Supplier<Region> getClipSupplier();

    /// Sets the [Supplier] used by the generator to clip itself, thus avoiding ripples from
    /// overflowing.
    void setClipSupplier(Supplier<Region> clipSupplier);

    /// @return the [Supplier] used by the generator to create ripples
    Supplier<Ripple<?>> getRippleSupplier();

    /// Sets the [Supplier] used by the generator to create ripples.
    void setRippleSupplier(Supplier<Ripple<?>> rippleSupplier);

    /// @return the preferred, default type of ripple the generator uses
    Supplier<Ripple<?>> defaultRippleSupplier();

    default boolean doAnimateBackground() {
        return animateBackgroundProperty().get();
    }

    /// Specifies whether the generator should also animate its background color.
    StyleableBooleanProperty animateBackgroundProperty();

    default void setAnimateBackground(boolean animateBackground) {
        animateBackgroundProperty().set(animateBackground);
    }

    default Color getBackgroundColor() {
        return backgroundColorProperty().get();
    }

    /// Specifies the background color to use when animating it, see [#animateBackgroundProperty()].
    StyleableObjectProperty<Color> backgroundColorProperty();

    default void setBackgroundColor(Color backgroundColor) {
        backgroundColorProperty().set(backgroundColor);
    }

    default Color getRippleColor() {
        return rippleColorProperty().get();
    }

    /// Specifies ripple node color.
    StyleableObjectProperty<Color> rippleColorProperty();

    default void setRippleColor(Color rippleColor) {
        rippleColorProperty().set(rippleColor);
    }

    default boolean isNoClip() {
        return noClipProperty().get();
    }

    /// Instructs the ripple generator to not clip itself, giving the user a finer control over the ripple effect.
    StyleableBooleanProperty noClipProperty();

    default void setNoClip(boolean noClip) {
        noClipProperty().set(noClip);
    }
}
