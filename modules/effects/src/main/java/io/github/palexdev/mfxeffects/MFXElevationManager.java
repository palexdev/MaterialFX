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

package io.github.palexdev.mfxeffects;

import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/// Utility class which manages a preset number of `DropShadow` effects ordered by `DepthLevel`, but
/// it also allows to create custom `DropShadow` effects with [#shadowOf(Color, double, double, double, double)].
///
/// [ElevationLevel]
public class MFXElevationManager {

    /// Returns a new instance of `DropShadow` with the specified characteristics.
    ///
    /// @return The desired custom `DropShadow` effect
    /// @see DropShadow
    public static DropShadow shadowOf(Color color, double radius, double spread, double offsetX, double offsetY) {
        return new DropShadow(
            BlurType.GAUSSIAN,
            color,
            radius,
            spread,
            offsetX,
            offsetY
        );
    }

    /// Retrieves the `DropShadow` associated with the specified `DepthLevel`.
    ///
    /// @param level The desired `DepthLevel` between 1 and 5
    /// @return The desired `DropShadow` effect
    public static DropShadow shadowOf(ElevationLevel level) {
        return new DropShadow(
            BlurType.GAUSSIAN,
            level.getColor(),
            level.getRadius(),
            level.getSpread(),
            level.getOffsetX(),
            level.getOffsetY()
        );
    }

    /// Retrieves the `DropShadow` associated with the specified `DepthLevel` added to delta.
    ///
    /// Example 1: for a depth level equal to 3 and a delta equal to 2, the returned `DropShadow` effect is
    /// the effected associated with a depth level of 5.
    ///
    /// Example 2: for a depth level equal to 5 and a delta equal to whatever integer, the returned `DropShadow` effect is
    /// the effected associated with a depth level of 5.
    ///
    /// @param level The desired `DepthLevel` between 1 and 5
    /// @param delta The number of levels to shift
    /// @return The final `DropShadow` effect}
    ///
    ///
    /// [#nextLevel(ElevationLevel)]
    public static DropShadow shadowOf(ElevationLevel level, int delta) {
        ElevationLevel endLevel = level;
        for (int i = 0; i < delta; i++) {
            endLevel = nextLevel(endLevel);
        }
        return shadowOf(endLevel);
    }

    /// From a starting `DepthLevel` retrieves the `DropShadow` effect associated with the next `DepthLevel`.
    ///
    /// @param startLevel The starting `DepthLevel`
    /// @return The `DropShadow` effect associated with the next `DepthLevel`
    /// @see ElevationLevel
    private static ElevationLevel nextLevel(ElevationLevel startLevel) {
        return !(startLevel.equals(ElevationLevel.LEVEL5)) ? startLevel.next() : ElevationLevel.LEVEL5;
    }
}
