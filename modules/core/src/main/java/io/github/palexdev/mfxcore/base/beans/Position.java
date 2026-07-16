/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.base.properties.PositionProperty;

/// Simple record to represent a position as <x, y>.
///
/// For usage in dynamic scenarios use the related [PositionProperty].
public record Position(double x, double y) {

    //================================================================================
    // Static Methods
    //================================================================================

    // TODO we should replace `of` everywhere with more significant names for static imports
    @Deprecated(forRemoval = true)
    public static Position of(double x, double y) {
        return new Position(x, y);
    }

    public static Position position(double x, double y) {
        return new Position(x, y);
    }

    /// @return a new `Position` object with both x and y set to 0
    public static Position origin() {
        return of(0, 0);
    }

    //================================================================================
    // Methods
    //================================================================================

    public Position x(double x) {
        return new Position(x, y);
    }

    public Position y(double y) {
        return new Position(x, y);
    }

    //================================================================================
    // Builder
    //================================================================================

    //@formatter:off
    public static class PositionBuilder {
        public static Position x(double x) {
            return new Position(x, 0);
        }
        public static Position y(double y) {
            return new Position(0, y);
        }
    }
}
