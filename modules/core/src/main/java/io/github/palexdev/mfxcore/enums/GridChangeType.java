/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.enums;

import io.github.palexdev.mfxcore.collections.ObservableGrid;

/**
 * Enumeration used by {@link ObservableGrid.Change} to specify the type of change.
 */
public enum GridChangeType {
	/**
	 * Specifies that the {@code Change} occurred because of a grid initialization
	 */
	INIT,

	/**
	 * Specifies that the {@code Change} occurred because of a clear of the grid's data
	 */
	CLEAR,

	/**
	 * Specifies that the {@code Change} occurred because of a replacement of a single element.
	 * <p>
	 * The "removed" list carries the replaced element, the "added" list carries the new element.
	 */
	REPLACE_ELEMENT,

	/**
	 * Specifies that the {@code Change} occurred because of a replacement of a row.
	 * <p>
	 * The "removed" list carries the replaced row, thr "added" list carries the new row.
	 */
	REPLACE_ROW,

	/**
	 * Specifies that the {@code Change} occurred because of a replacement of a column.
	 * <p>
	 * The "removed" list carries the replaced column, thr "added" list carries the new column.
	 */
	REPLACE_COLUMN,

	/**
	 * Specifies that the {@code Change} occurred because of a replacement of the diagonal.
	 * <p>
	 * The "removed" list carries the replaced diagonal, thr "added" list carries the new diagonal.
	 */
	REPLACE_DIAGONAL,

	/**
	 * Specifies that the {@code Change} occurred because of the addition of a row.
	 */
	ADD_ROW,

	/**
	 * Specifies that the {@code Change} occurred because of the addition of a column.
	 */
	ADD_COLUMN,

	/**
	 * Specifies that the {@code Change} occurred because of the removal of a row.
	 */
	REMOVE_ROW,

	/**
	 * Specifies that the {@code Change} occurred because of the removal of a column.
	 */
	REMOVE_COLUMN,

	/**
	 * Specifies that the {@code Change} occurred because of a transposition.
	 */
	TRANSPOSE
}
