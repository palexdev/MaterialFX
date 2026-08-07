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

import io.github.palexdev.mfxeffects.ripple.CircleRipple;
import javafx.scene.layout.Region;

/// Abstract class extending [Region] and implementing [RippleGenerator].
///
/// This exists just to restrict the visibility of some internal methods, but it's also a good base to implement
/// custom generators.
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

    /// This is responsible for building the generator's clip node.
    ///
    /// By default, this just relies on [#getClipSupplier()]
    protected Region buildClip() {
        return getClipSupplier().get();
    }

    /// This is responsible for building the ripple node.
    ///
    /// By default, this just relies on [#getRippleSupplier()].
    protected Ripple<?> buildRipple() {
        return getRippleSupplier().get();
    }

    /// Since this extends [Region], and the children list is unmodifiable (we want it to be so), this allows internal
    /// classes to add the ripple node on the generator.
    protected void setRipple(Ripple<?> ripple) {
        getChildren().setAll(ripple.toNode());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// @return a [Supplier] building ripples of type [CircleRipple]
    @Override
    public Supplier<Ripple<?>> defaultRippleSupplier() {
        return () -> new CircleRipple(this);
    }
}
