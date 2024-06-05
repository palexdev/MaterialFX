/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.collections.Grid;

import java.util.Iterator;
import java.util.List;

/**
 * Custom {@link Iterator} capable of iterating over a {@link Grid} structure per column.
 * Internally uses a "cursor" to keep track of the current visiting column.
 */
public class ColumnIterator<T> implements Iterator<List<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final Grid<T> grid;
	private int cursor = 0;

	//================================================================================
	// Constructors
	//================================================================================
	public ColumnIterator(Grid<T> grid) {
		this.grid = grid;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 *
	 * @return whether the cursor is still lesser than {@link Grid#getColumnsNum()}
	 */
	@Override
	public boolean hasNext() {
		return cursor < grid.getColumnsNum();
	}

	/**
	 * @return the column at the current cursor value by using {@link Grid#getColumn(int)}
	 */
	@Override
	public List<T> next() {
		List<T> column = grid.getColumn(cursor);
		cursor++;
		return column;
	}

	/**
	 * Removes the column at the current cursor value by using {@link Grid#removeColumn(int)}.
	 * <p>
	 * The removal is done only if {@link #hasNext()} is true.
	 */
	@Override
	public void remove() {
		if (hasNext()) grid.removeColumn(cursor);
	}
}