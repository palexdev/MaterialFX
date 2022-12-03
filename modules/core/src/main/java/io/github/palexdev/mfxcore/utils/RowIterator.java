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