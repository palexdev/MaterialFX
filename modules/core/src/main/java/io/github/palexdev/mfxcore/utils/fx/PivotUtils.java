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

package io.github.palexdev.mfxcore.utils.fx;

import io.github.palexdev.mfxcore.base.beans.Position;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;

import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.x;
import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.y;
import static io.github.palexdev.mfxcore.base.beans.Position.position;

public class PivotUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private PivotUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /**
     * Calculates the pivot position based on the given reference bounds and positional alignment.
     */
    public static Position pivotPosition(Bounds referenceBounds, Pos pos) {
        return switch (pos) {
            case TOP_CENTER -> x(referenceBounds.getCenterX());
            case TOP_RIGHT -> x(referenceBounds.getWidth());
            case CENTER -> position(referenceBounds.getCenterX(), referenceBounds.getCenterY());
            case BOTTOM_LEFT -> y(referenceBounds.getHeight());
            case BOTTOM_CENTER -> position(referenceBounds.getCenterX(), referenceBounds.getHeight());
            case BOTTOM_RIGHT -> position(referenceBounds.getWidth(), referenceBounds.getHeight());
            default -> Position.origin();
        };
    }
}
