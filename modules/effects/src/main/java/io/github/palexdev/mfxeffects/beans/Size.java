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

package io.github.palexdev.mfxeffects.beans;

import io.github.palexdev.mfxeffects.beans.properties.SizeProperty;

/// Simple record to represent the size of something as <w, h>.
///
/// For usage in dynamic scenarios use the related [SizeProperty].
public record Size(double width, double height) {

    //================================================================================
    // Static Methods
    //================================================================================
    public static Size of(double width, double height) {
        return new Size(width, height);
    }

    /// @return a new `Size` object with both width and height set to 0
    public static Size zero() {
        return of(0, 0);
    }

    /// @return a new `Size` object with both width and height set to -1
    public static Size invalid() {
        return of(-1, -1);
    }

    //================================================================================
    // Methods
    //================================================================================
    public Size withWidth(double width) {
        return new Size(width, height);
    }

    public Size withHeight(double height) {
        return new Size(width, height);
    }
}
