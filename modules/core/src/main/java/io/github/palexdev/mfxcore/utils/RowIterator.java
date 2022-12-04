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
 * Custom {@link Iterator} capable of iterating over a {@link Grid} structure per row.
 * Internally uses a "cursor" to keep track of the current visiting row.
 */
public class RowIterator<T> implements Iterator<List<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final Grid<T> grid;
	private int cursor = 0;

	//================================================================================
	// Constructors
	//================================================================================
	public RowIterator(Grid<T> grid) {
		this.grid = grid;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 *
	 * @return whether the cursor is still lesser than {@link Grid#getRowsNum()}
	 */
	@Override
	public boolean hasNext() {
		return cursor < grid.getRowsNum();
	}

	/**
	 * @return the row at the current cursor value by using {@link Grid#getRow(int)}
	 */
	@Override
	public List<T> next() {
		List<T> row = grid.getRow(cursor);
		cursor++;
		return row;
	}

	/**
	 * Removes the row at the current cursor value by using {@link Grid#removeRow(int)}.
	 * <p>
	 * The removal is done only if {@link #hasNext()} is true.
	 */
	@Override
	public void remove() {
		if (hasNext()) grid.removeRow(cursor);
	}
}