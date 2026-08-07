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

package io.github.palexdev.mfxcore.base.beans;

import javafx.animation.Transition;

//@formatter:off fuck IntelliJ I guess
/// This is an extension of [Position] to be used with [Transitions][Transition] that start from a point `P(x, y)` and end at a point `P1(endX, endY)`.
///
/// A very basic example:
///
/// Let's say I want to move a point `P` from `(x, y)` to the left with an animation so `(x1, y)`.
/// The transition would probably look like this:
/// ```
/// double startX = ...;
/// double startY = ...;
/// double endX = ...;
/// double endY = startY;
/// // The y coordinate doesn't change, so it is equal to the start one
/// PositionTransition position = PositionTransition.of(startX, startY, endX, endY);
/// Transition move = new Transition() {
///   @Override
///   void interpolate(double frac){
///     p.setX(x - position.deltaX() * frac);
///   }
/// };
///```
//@formatter:on
public record PositionTransition(
    double startX, double startY,
    double endX, double endY
) {

    //================================================================================
    // Static Methods
    //================================================================================
    public static PositionTransition of(double x, double y, double endX, double endY) {
        return new PositionTransition(x, y, endX, endY);
    }

    //================================================================================
    // Methods
    //================================================================================
    public PositionTransition withStartX(double startX) {
        return new PositionTransition(startX, startY, endX, endY);
    }

    public PositionTransition withStartY(double startY) {
        return new PositionTransition(startX, startY, endX, endY);
    }

    public PositionTransition withEndX(double endX) {
        return new PositionTransition(startX, startY, endX, endY);
    }

    public PositionTransition withEndY(double endY) {
        return new PositionTransition(startX, startY, endX, endY);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the difference between the star x and end x coordinates
    public double deltaX() {
        return startX() - endX();
    }

    /// @return the difference between the start y and end y coordinates
    public double deltaY() {
        return startY() - endY();
    }
}
