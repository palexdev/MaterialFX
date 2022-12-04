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

package io.github.palexdev.mfxcore.collections;

import io.github.palexdev.mfxcore.utils.ColumnIterator;
import io.github.palexdev.mfxcore.utils.GridUtils;
import io.github.palexdev.mfxcore.utils.RowIterator;
import javafx.util.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of a dynamic matrix in Java.
 * <p>
 * This collection is backed by a 1D list and uses a row-major memory layout.
 * <p>
 * See <a href="https://eli.thegreenplace.net/2015/memory-layout-of-multi-dimensional-arrays">Explanation</a>
 * <p></p>
 * Note that because the grid uses a 1D data structure it can operate on both linear indexes or subscripts/coordinates.
 * <p>
 * <pre>
 * {@code
 * // This is how a 5x5 matrix appears with subscripts
 *     0 1 2 3 4
 * 0 [ A B C D E ]
 * 1 [ F G H I J ]
 * 2 [ K L M N O ]
 * 3 [ P Q R S T ]
 * 4 [ U V W X Y ]
 *
 * // This is how linear indexes work
 * [  0  1  2  3  4 ]
 * [  5  6  7  8  9 ]
 * [ 10 11 12 13 14 ]
 * [ 15 16 17 18 19 ]
 * [ 20 21 22 23 24 ]
 * // So...
 * Grid<String> grid = ...;
 * String e1 = grid.getElement(11); // Equals grid.getElement(2, 1); Both are "L"
 * String e2 = grid.getElement(23); // Equals grid.getElement(4, 3); Both are "X"
 * }
 * </pre>
 * <p></p>
 * These are all the possible operations:
 * <p> - Create a grid from a list, an array or a 2D array
 * <p> - Fill the grid with nulls, a given val, or a function to get the desired val
 * <p> - Get an element by linear index or coordinates
 * <p> - Set an element by linear index or coordinates
 * <p> - Get the diagonal
 * <p> - Set the diagonal
 * <p> - Get a row by index
 * <p> - Get a column by index
 * <p> - Add a row at end or at a given valid index
 * <p> - Add a column at end or at a given valid index
 * <p> - Replace a row at index
 * <p> - Replace a column at index
 * <p> - Remove a row at start, end, or at a given valid index
 * <p> - Remove a column at start, end, or at a given valid index
 * <p> - Transpose the grid
 * <p> - Clear the grid
 * <p> - Convert a linear index to coordinates
 * <p> - Convert a coordinate to a linear index
 * <p> - Iterate linearly (from 0 to {@link #totalSize()}), over rows {@link RowIterator},
 * over columns {@link ColumnIterator}
 *
 * @param <T> the type of data contained in the grid
 */
public class Grid<T> implements Iterable<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final List<T> data = new ArrayList<>();
	protected int rowsNum;
	protected int columnsNum;

	//================================================================================
	// Constructors
	//================================================================================
	public Grid() {
	}

	public Grid(int nRows, int nColumns) {
		this.rowsNum = nRows;
		this.columnsNum = nColumns;
	}

	public Grid(List<T> data, int nRows, int nColumns) {
		this.data.addAll(data);
		this.rowsNum = nRows;
		this.columnsNum = nColumns;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Given a list of items and the number of columns the grid will have,
	 * generates a new {@code Grid}.
	 * Every {@code columnsNum} the list is "sliced" in rows.
	 *
	 * @param list       the data to generate the grid
	 * @param columnsNum the number of columns for the grid, also used to "slice" the given list in rows
	 */
	public static <T> Grid<T> fromList(List<T> list, int columnsNum) {
		if (list.isEmpty()) {
			if (columnsNum == 0) return new Grid<>();
			throw new IllegalArgumentException("List is empty, but cols is " + columnsNum);
		}

		if (list.size() % columnsNum != 0)
			throw new IllegalArgumentException("List size must be a multiple of " + columnsNum);

		return new Grid<>(list, list.size() / columnsNum, columnsNum);
	}

	/**
	 * Given an array of items and the number of columns the grid will have,
	 * generates a new {@code Grid}.
	 * Every {@code columnsNum} the array is "sliced" in rows.
	 *
	 * @param arr        the data to generate the grid
	 * @param columnsNum the number of columns for the grid, also used to "slice" the given array in rows
	 */
	public static <T> Grid<T> fromArray(T[] arr, int columnsNum) {
		return fromList(List.of(arr), columnsNum);
	}

	/**
	 * Given a 2D array of items generates a new {@code Grid}.
	 *
	 * @param matrix the data to generate the grid
	 */
	public static <T> Grid<T> fromMatrix(T[][] matrix) {
		int rowsNum = matrix.length;
		int columnsNum = matrix[0].length;
		List<T> tmp = new ArrayList<>();
		for (T[] row : matrix) {
			tmp.addAll(Arrays.asList(row));
		}
		return new Grid<>(tmp, rowsNum, columnsNum);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Clears the grid data and using the already available {@link #getRowsNum()} and {@link #getColumnsNum()},
	 * fills it with {@code null}.
	 *
	 * @throws IllegalStateException if the rows num or columns num are 0
	 */
	public Grid<T> init() {
		if (rowsNum == 0 || columnsNum == 0)
			throw new IllegalStateException("Both rows num and columns num must be greater than 0 but they are "
					+ rowsNum + ", " + columnsNum);

		data.clear();
		for (int i = 0; i < totalSize(); i++) {
			data.add(null);
		}
		return this;
	}

	/**
	 * Clears the grid data and uses the given rows num and columns num to fill it with {@code null}.
	 *
	 * @throws IllegalStateException if the rows num or columns num are 0
	 */
	public Grid<T> init(int rows, int columns) {
		if (rows == 0 || columns == 0)
			throw new IllegalStateException("Both rows num and columns num must be greater than 0 but they are "
					+ rows + ", " + columns);

		clear();
		this.rowsNum = rows;
		this.columnsNum = columns;
		for (int i = 0; i < totalSize(); i++) {
			data.add(null);
		}
		return this;
	}

	/**
	 * Clears the grid and uses the given rows num and columns num to fill it with the given value.
	 *
	 * @throws IllegalStateException if the rows num or columns num are 0
	 */
	public Grid<T> init(int rows, int columns, T val) {
		if (rows == 0 || columns == 0)
			throw new IllegalStateException("Both rows num and columns num must be greater than 0 but they are "
					+ rows + ", " + columns);

		clear();
		this.rowsNum = rows;
		this.columnsNum = columns;
		for (int i = 0; i < totalSize(); i++) {
			data.add(val);
		}
		return this;
	}

	/**
	 * Clears the grid and uses the given rows num and columns num to fill it with values provided by the given
	 * {@link BiFunction}.
	 *
	 * @param valFunction the function accepts the row index and column index of the loop and is expected
	 *                    to return a T value to add into the grid
	 * @throws IllegalStateException if the rows num or columns num are 0
	 */
	public Grid<T> init(int rows, int columns, BiFunction<Integer, Integer, T> valFunction) {
		if (rows == 0 || columns == 0)
			throw new IllegalStateException("Both rows num and columns num must be greater than 0 but they are "
					+ rows + ", " + columns);

		clear();
		this.rowsNum = rows;
		this.columnsNum = columns;
		for (int i = 0; i < totalSize(); i++) {
			Coordinates rc = GridUtils.indToSub(columns, i);
			data.add(valFunction.apply(rc.getRow(), rc.getColumn()));
		}
		return this;
	}

	/**
	 * @return the element at the given linear index
	 */
	public T getElement(int index) {
		return data.get(index);
	}

	/**
	 * @return the element at the given coordinates.
	 * Uses {@link #getElement(int)} by first converting them to a linear index, {@link GridUtils#subToInd(int, int, int)}
	 */
	public T getElement(int row, int column) {
		int index = GridUtils.subToInd(columnsNum, row, column);
		return getElement(index);
	}

	/**
	 * Replaces the element at the given linear index with the given value.
	 */
	public void setElement(int index, T val) {
		data.set(index, val);
	}

	/**
	 * Replaces the element at the given coordinates with the given value.
	 * Uses {@link #setElement(int, Object)} by first converting them to a linear index, {@link GridUtils#subToInd(int, int, int)}
	 */
	public void setElement(int row, int column, T val) {
		setElement(GridUtils.subToInd(columnsNum, row, column), val);
	}

	/**
	 * @return a list containing the elements along the diagonal of the grid. An empty list
	 * if the grid is empty
	 */
	public List<T> getDiagonal() {
		if (isEmpty()) return List.of();
		return IntStream.iterate(0, i -> i < totalSize(), i -> i + (columnsNum + 1))
				.mapToObj(this::getElement)
				.collect(Collectors.toList());
	}

	/**
	 * Calls {@link #setDiagonal(List)} by using {@link Arrays#asList(Object[])}
	 */
	@SafeVarargs
	public final void setDiagonal(T... diag) {
		setDiagonal(Arrays.asList(diag));
	}

	/**
	 * Replaces the diagonal of the grid with the given one.
	 *
	 * @throws IllegalStateException    if this grid is not a square matrix. In other words if the
	 *                                  rows num and the columns num are not the same
	 * @throws IllegalArgumentException if the given diagonal is empty or if its size is not N
	 *                                  (where N is both the number of rows and columns)
	 */
	public void setDiagonal(List<T> diag) {
		if (rowsNum != columnsNum)
			throw new IllegalStateException("Rows num and columns num are not the same, expecting square matrix");

		if (diag.isEmpty())
			throw new IllegalArgumentException("Diagonal cannot be empty");

		if (diag.size() != rowsNum)
			throw new IllegalArgumentException("Diagonal size does not math, expecting " + rowsNum + ", but was " + diag.size());

		for (int i = 0; i < rowsNum; i++) {
			int index = GridUtils.subToInd(columnsNum, i, i);
			data.set(index, diag.get(i));
		}
	}


	/**
	 * @return a list containing the elements of the row at the given index
	 * @throws IndexOutOfBoundsException if the given index is not a valid row index
	 */
	public List<T> getRow(int index) {
		int start = index * columnsNum;
		int end = start + columnsNum;
		return IntStream.range(start, end)
				.mapToObj(data::get)
				.collect(Collectors.toList());
	}

	/**
	 * @return a list containing the elements of the row at the given index, except
	 * for the items that are positioned in the specified "skippingColumns"
	 */
	public List<T> getRow(int index, Integer... skippingColumns) {
		int start = index * columnsNum;
		int end = start + columnsNum;
		Set<Integer> tmpSet = Set.of(skippingColumns);
		return IntStream.range(start, end)
				.filter(i -> !tmpSet.contains(GridUtils.indToCol(columnsNum, i)))
				.mapToObj(data::get)
				.collect(Collectors.toList());
	}

	/**
	 * @return a list containing the elements of the column at the given index
	 * @throws IndexOutOfBoundsException if the given index is not a valid column index
	 */
	public List<T> getColumn(int index) {
		int end = (rowsNum - 1) * columnsNum + index;
		return IntStream.iterate(index, i -> i <= end, i -> i + columnsNum)
				.mapToObj(data::get)
				.collect(Collectors.toList());
	}

	/**
	 * @return a list containing the elements of the column at the given index, except
	 * for the items that are positioned in the specified "skippingRows"
	 */
	public List<T> getColumn(int index, Integer... skippingRows) {
		int end = (rowsNum - 1) * columnsNum + index;
		Set<Integer> tmpSet = Set.of(skippingRows);
		return IntStream.iterate(index, i -> i <= end, i -> i + columnsNum)
				.filter(i -> !tmpSet.contains(GridUtils.indToRow(columnsNum, i)))
				.mapToObj(data::get)
				.collect(Collectors.toList());
	}

	/**
	 * Calls {@link #addRow(int, Object[])} with {@link #getRowsNum()} as index,
	 * results in appending a new row at the end.
	 */
	@SafeVarargs
	public final void addRow(T... row) {
		addRow(rowsNum, row);
	}

	/**
	 * Calls {@link #addRow(int, List)} by using {@link Arrays#asList(Object[])}.
	 */
	@SafeVarargs
	public final void addRow(int index, T... row) {
		addRow(index, Arrays.asList(row));
	}

	/**
	 * Calls {@link #addRow(int, List)} with {@link #getRowsNum()} as index,
	 * results in appending a new row at the end.
	 */
	public void addRow(List<T> row) {
		addRow(rowsNum, row);
	}

	/**
	 * Adds the given row at the given index.
	 *
	 * @throws IllegalArgumentException  if the given row is empty or if its size is not equal to the num of columns
	 * @throws IndexOutOfBoundsException if the given index is not a valid row index
	 */
	public void addRow(int index, List<T> row) {
		if (row.isEmpty())
			throw new IllegalArgumentException("Row to add cannot be empty");

		if (isEmpty()) {
			rowsNum++;
			columnsNum = row.size();
			data.addAll(row);
			return;
		}

		if (index < 0 || index > rowsNum)
			throw new IndexOutOfBoundsException(index);

		if (row.size() != columnsNum)
			throw new IllegalArgumentException("Row to add does not match. Length must be " + columnsNum + ", but was " + row.size());

		int linearIndex = index * columnsNum;
		rowsNum++;
		data.addAll(linearIndex, row);
	}

	/**
	 * Calls {@link #addColumn(int, Object[])} with {@link #getColumnsNum()} as index,
	 * results in appending a new column at the end.
	 */
	@SafeVarargs
	public final void addColumn(T... column) {
		addColumn(columnsNum, column);
	}

	/**
	 * Calls {@link #addColumn(int, List)} by using {@link Arrays#asList(Object[])}.
	 */
	@SafeVarargs
	public final void addColumn(int index, T... column) {
		addColumn(index, Arrays.asList(column));
	}

	/**
	 * Calls {@link #addColumn(int, List)} with {@link #getColumnsNum()} as index,
	 * results in appending a new row at the end.
	 */
	public void addColumn(List<T> column) {
		addColumn(columnsNum, column);
	}

	/**
	 * Adds the given column at the given index.
	 *
	 * @throws IllegalArgumentException  if the given column is empty or if its size is not equal to the num of rows
	 * @throws IndexOutOfBoundsException if the given index is not a valid column index
	 */
	public void addColumn(int index, List<T> column) {
		if (column.isEmpty())
			throw new IllegalArgumentException("Column to add cannot be empty");

		if (isEmpty()) {
			columnsNum++;
			rowsNum = column.size();
			data.addAll(column);
			return;
		}

		if (index < 0 || index > columnsNum)
			throw new IndexOutOfBoundsException(index);

		if (column.size() != rowsNum)
			throw new IllegalArgumentException("Column to add does not match. Length must be " + rowsNum + ", but was " + column.size());

		columnsNum++;
		int end = (rowsNum - 1) * columnsNum + index;
		int j = 0;
		for (int i = index; i <= end; i += columnsNum) {
			data.add(i, column.get(j));
			j++;
		}
	}

	/**
	 * Calls {@link #setRow(int, List)} by using {@link Arrays#asList(Object[])}.
	 */
	@SafeVarargs
	public final void setRow(int index, T... row) {
		setRow(index, Arrays.asList(row));
	}

	/**
	 * Replaces the row at the given index with the given row.
	 *
	 * @throws IllegalArgumentException  if the given row is empty or its size is not equal to the num of columns
	 * @throws IndexOutOfBoundsException if the given index is not a valid row index
	 */
	public void setRow(int index, List<T> row) {
		if (row.isEmpty())
			throw new IllegalArgumentException("Row to set cannot be empty");

		if (row.size() > columnsNum)
			throw new IllegalArgumentException("Row size does not match, expecting " + columnsNum + ", but was " + row.size());

		if (isEmpty() && index == 0) {
			addRow(row);
			return;
		}

		if (index < 0 || index > rowsNum)
			throw new IndexOutOfBoundsException(index);

		int start = index * columnsNum;
		int j = 0;
		for (int i = start; i < start + columnsNum; i++) {
			data.set(i, row.get(j));
			j++;
		}
	}

	/**
	 * Calls {@link #setColumn(int, List)} by using {@link Arrays#asList(Object[])}.
	 */
	@SafeVarargs
	public final void setColumn(int index, T... column) {
		setColumn(index, Arrays.asList(column));
	}

	/**
	 * Replaces the column at the given index with the given column.
	 *
	 * @throws IllegalArgumentException  if the given column is empty or its size is not equal to the num of rows
	 * @throws IndexOutOfBoundsException if the given index is not a valid column index
	 */
	public void setColumn(int index, List<T> column) {
		if (column.isEmpty())
			throw new IllegalArgumentException("Column to set cannot be empty");

		if (column.size() > rowsNum)
			throw new IllegalArgumentException("Column size does not match, expecting " + rowsNum + ", but was " + column.size());

		if (isEmpty() && index == 0) {
			addColumn(column);
			return;
		}

		if (index < 0 || index > columnsNum)
			throw new IndexOutOfBoundsException(index);

		int end = (rowsNum - 1) * columnsNum + index;
		int j = 0;
		for (int i = index; i <= end; i += columnsNum) {
			data.set(i, column.get(j));
			j++;
		}
	}

	/**
	 * Removed the first row.
	 *
	 * @return the removed row
	 */
	public List<T> removeFirstRow() {
		return removeRow(0);
	}

	/**
	 * Removes the last row.
	 *
	 * @return the removed row
	 */
	public List<T> removeLastRow() {
		return removeRow(rowsNum - 1);
	}

	/**
	 * Remove the row at the given index.
	 *
	 * @return the removed row
	 * @throws IndexOutOfBoundsException if the given index is not a valid row index
	 */
	public List<T> removeRow(int index) {
		if (index < 0 || index >= rowsNum)
			throw new IndexOutOfBoundsException(index);

		List<T> tmp = new ArrayList<>();
		int start = index * columnsNum;
		int end = start + columnsNum;
		rowsNum--;
		for (int i = start; i < end; i++) {
			tmp.add(data.remove(start));
		}
		return tmp;
	}

	/**
	 * Removes the first column.
	 *
	 * @return the removed column
	 */
	public List<T> removeFirstColumn() {
		return removeColumn(0);
	}

	/**
	 * Removes the last column.
	 *
	 * @return the removed column
	 */
	public List<T> removeLastColumn() {
		return removeColumn(columnsNum - 1);
	}

	/**
	 * Remove the column at the given index.
	 *
	 * @return the removed column
	 * @throws IndexOutOfBoundsException if the given index is not a valid column index
	 */
	public List<T> removeColumn(int index) {
		if (index < 0 || index >= columnsNum)
			throw new IndexOutOfBoundsException(index);

		List<T> tmp = new ArrayList<>();
		int end = (rowsNum - 1) * columnsNum + index;
		int j = 0;
		for (int i = index; i <= end; i += columnsNum) {
			tmp.add(data.remove(i - j));
			j++;
		}
		columnsNum--;
		return tmp;
	}

	/**
	 * Transposes this grid and returns itself.
	 */
	public Grid<T> transpose() {
		List<T> tmp = new ArrayList<>();
		for (int j = 0; j < columnsNum; j++) {
			for (int i = 0; i < rowsNum; i++) {
				int linear = GridUtils.subToInd(columnsNum, i, j);
				tmp.add(data.get(linear));
			}
		}

		// Swap rows/columns num
		rowsNum = rowsNum + columnsNum;
		columnsNum = rowsNum - columnsNum;
		rowsNum = rowsNum - columnsNum;

		data.clear();
		data.addAll(tmp);
		return this;
	}

	/**
	 * Clears the grid, also sets both rowsNum and columnsNum to 0.
	 */
	public void clear() {
		rowsNum = 0;
		columnsNum = 0;
		data.clear();
	}

	/**
	 * @return the total size of the grid, given by {@code rowsNum * columnsNum}
	 */
	public int totalSize() {
		return rowsNum * columnsNum;
	}

	/**
	 * @return the size of the grid as a {@link Pair}. The key is the number of rows
	 * and the value is the number of columns
	 */
	public Pair<Integer, Integer> size() {
		return new Pair<>(rowsNum, columnsNum);
	}

	/**
	 * Delegate for {@link List#isEmpty()}.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Iterator<T> iterator() {
		return data.iterator();
	}

	/**
	 * Delegate for {@link List#listIterator(int)}.
	 */
	public Iterator<T> iterator(int start) {
		return data.listIterator(start);
	}

	/**
	 * @return an iterator capable of iterating the grid by rows
	 * @see RowIterator
	 */
	public Iterator<List<T>> rowIterator() {
		return new RowIterator<>(this);
	}

	/**
	 * @return an iterator capable of iterating the grid by columns
	 * @see ColumnIterator
	 */
	public Iterator<List<T>> columnIterator() {
		return new ColumnIterator<>(this);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================ù

	/**
	 * @return the underlying data structure
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * @return the grid's number of rows
	 */
	public int getRowsNum() {
		return rowsNum;
	}

	/**
	 * @return the grid's number of columns
	 */
	public int getColumnsNum() {
		return columnsNum;
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	public static class Coordinates {
		private final int row;
		private final int column;

		public Coordinates(int row, int column) {
			this.row = row;
			this.column = column;
		}

		/**
		 * @return a new {@code Coordinate} object given the row and column indexes
		 */
		public static Coordinates of(int row, int column) {
			return new Coordinates(row, column);
		}

		/**
		 * @param index    the linear index of the item in the grid
		 * @param nColumns the grid's number of columns
		 * @return a new {@code Coordinate} object given the linear index of an item and the
		 * grid's number of columns
		 */
		public static Coordinates linear(int index, int nColumns) {
			return new Coordinates(
					GridUtils.indToRow(nColumns, index),
					GridUtils.indToCol(nColumns, index)
			);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Coordinates that = (Coordinates) o;
			return getRow() == that.getRow() && getColumn() == that.getColumn();
		}

		@Override
		public int hashCode() {
			return Objects.hash(getRow(), getColumn());
		}

		@Override
		public String toString() {
			return "Coordinate{" +
					"row=" + row +
					", column=" + column +
					'}';
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}
	}
}