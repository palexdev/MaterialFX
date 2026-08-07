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

package io.github.palexdev.mfxcore.utils;

import java.util.function.BiFunction;

import io.github.palexdev.mfxcore.collections.Grid.Coordinates;

/// A set of utilities for grids/matrices
public class GridUtils {

    private GridUtils() {
    }

    /// Converts the given subscripts/coordinate to a linear index: `index = row * nColumns + column`
    ///
    /// @param nColumns the grid's number of columns
    public static int subToInd(int nColumns, int row, int column) {
        return row * nColumns + column;
    }

    /// Given the grid's number of columns, converted the given index to the row index.
    public static int indToRow(int nColumns, int index) {
        return index / nColumns;
    }

    /// Given the grid's number of columns, converted the given index to the column index.
    public static int indToCol(int nColumns, int index) {
        return index % nColumns;
    }

    /// Converts the given linear index to subscripts as a [Coordinates] object:
    /// - `rowIndex = index / columnsNum`
    /// - `columnIndex = index % columnsNum`
    ///
    /// @param nColumns the grid's number of columns
    public static Coordinates indToSub(int nColumns, int index) {
        return Coordinates.of(indToRow(nColumns, index), indToCol(nColumns, index));
    }

    /// Converts the given linear index to subscripts and uses them to return an object built using the given [BiFunction]:
    /// - `rowIndex = index / columnsNum`
    /// - `columnIndex = index % columnsNum`
    ///
    /// @param nColumns the grid's number of columns
    public static <T> T indToSub(int nColumns, int index, BiFunction<Integer, Integer, T> supplier) {
        return supplier.apply(indToRow(nColumns, index), indToCol(nColumns, index));
    }
}
