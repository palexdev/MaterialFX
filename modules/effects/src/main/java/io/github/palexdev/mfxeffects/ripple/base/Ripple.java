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

import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.scene.shape.Shape;

/// Public API for ripples that should be used by [RippleGenerator].
///
/// In coordination with the new [MFXRippleGenerator], the API has also been expanded to support
/// the 'dual-phase' ripple effect. Also, there's been a shift in responsibilities.
/// The ripple can build any number of animations it needs, as long as it specifies the behavior for the two phases: 'press' and 'release'.
/// It's also worth mentioning that since the background animation for the generator may need to be 'interconnected' with
/// the other animations, it's the ripple's responsibility to build an animation for it and play/stop it.
public interface Ripple<S extends Shape> {

    /// @return the ripple's node
    S toNode();

    /// Should initialize the ripple if needed.
    void init();

    /// This is typically given by a [RippleGenerator] during the generation of a ripple.
    /// Should be used to set the position of the ripple.
    void position(double x, double y);

    /// Should implement the logic to build/play the animations responsible for showing the ripple.
    void playIn();

    /// Should implement the logic to build/play the animations responsible for make the ripple disappear.
    void playOut();
}
