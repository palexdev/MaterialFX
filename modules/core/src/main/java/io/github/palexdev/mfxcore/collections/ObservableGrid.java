package io.github.palexdev.mfxcore.collections;

import io.github.palexdev.mfxcore.enums.GridChangeType;
import io.github.palexdev.mfxcore.utils.GridUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Extension of {@link Grid} to provide observables capabilities.
 * Note though that since JavaFX's implementation of listeners/observables and such is utter garbage because of private
 * APIs, complex class structures and others unhappy design decisions... The observability of this data structure is
 * implemented through an {@link ObjectProperty} that always contains the last {@link Change} occurred.
 * <p></p>
 * Instead of implementing {@link Observable}, this implements {@link ObservableValue} to also get {@link ChangeListener}
 * capabilities. Note though that the relevant value carried by the listener is the {@code newValue} as once a {@link Change}
 * has been processed it should be disposed with {@link Change#endChange()} (this will reset the property to a useless state)
 */
public class ObservableGrid<T> extends Grid<T> implements ObservableValue<ObservableGrid.Change<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<Change<T>> change = new SimpleObjectProperty<>() {
		@Override
		protected void fireValueChangedEvent() {
			if (get() == Change.EMPTY) return;
			super.fireValueChangedEvent();
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public ObservableGrid() {
	}

	public ObservableGrid(int nRows, int nColumns) {
		super(nRows, nColumns);
	}

	public ObservableGrid(List<T> data, int nRows, int nColumns) {
		super(data, nRows, nColumns);
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void registerChange(Change<T> change) {
		this.change.set(change);
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
	public static <T> ObservableGrid<T> fromList(List<T> list, int columnsNum) {
		if (list.isEmpty()) {
			if (columnsNum == 0) return new ObservableGrid<>();
			throw new IllegalArgumentException("List is empty, but cols is " + columnsNum);
		}

		if (list.size() % columnsNum != 0)
			throw new IllegalArgumentException("List size must be a multiple of " + columnsNum);

		return new ObservableGrid<>(list, list.size() / columnsNum, columnsNum);
	}

	/**
	 * Given an array of items and the number of columns the grid will have,
	 * generates a new {@code Grid}.
	 * Every {@code columnsNum} the array is "sliced" in rows.
	 *
	 * @param arr        the data to generate the grid
	 * @param columnsNum the number of columns for the grid, also used to "slice" the given array in rows
	 */
	public static <T> ObservableGrid<T> fromArray(T[] arr, int columnsNum) {
		return fromList(List.of(arr), columnsNum);
	}

	/**
	 * Given a 2D array of items generates a new {@code Grid}.
	 *
	 * @param matrix the data to generate the grid
	 */
	public static <T> ObservableGrid<T> fromMatrix(T[][] matrix) {
		int rowsNum = matrix.length;
		int columnsNum = matrix[0].length;
		List<T> tmp = new ArrayList<>();
		for (T[] row : matrix) {
			tmp.addAll(Arrays.asList(row));
		}
		return new ObservableGrid<>(tmp, rowsNum, columnsNum);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public ObservableGrid<T> init() {
		List<T> tmp = new ArrayList<>(getData());
		super.init();
		registerChange(
				Change.of(this, GridChangeType.INIT)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
						.removed(tmp)
		);
		return this;
	}

	@Override
	public ObservableGrid<T> init(int rows, int columns) {
		List<T> tmp = new ArrayList<>(getData());
		super.init(rows, columns);
		registerChange(
				Change.of(this, GridChangeType.INIT)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
						.removed(tmp)
		);
		return this;
	}

	@Override
	public ObservableGrid<T> init(int rows, int columns, T val) {
		List<T> tmp = new ArrayList<>(getData());
		super.init(rows, columns, val);
		registerChange(
				Change.of(this, GridChangeType.INIT)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
						.removed(tmp)
						.added(val)
		);
		return this;
	}

	@Override
	public ObservableGrid<T> init(int rows, int columns, BiFunction<Integer, Integer, T> valFunction) {
		if (rows == 0 || columns == 0) {
			throw new IllegalStateException("Both rows num and columns num must be greater than 0 but they are "
					+ rows + ", " + columns);
		}

		List<T> tmp = new ArrayList<>(getData());
		clear();
		this.rowsNum = rows;
		this.columnsNum = columns;
		List<T> added = new ArrayList<>();
		for (int i = 0; i < totalSize(); i++) {
			Coordinates rc = GridUtils.indToSub(columnsNum, i);
			T val = valFunction.apply(rc.getRow(), rc.getColumn());
			tmp.add(val);
			data.add(val);
		}

		registerChange(
				Change.of(this, GridChangeType.INIT)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
						.removed(tmp)
						.added(added)
		);
		return this;
	}

	@Override
	public void setElement(int index, T val) {
		T rem = getElement(index);
		super.setElement(index, val);
		registerChange(
				Change.of(this, GridChangeType.REPLACE_ELEMENT)
						.setCoordinates(GridUtils.indToSub(getColumnsNum(), index))
						.setStart(index)
						.setEnd(index)
						.setStep(1)
						.removed(rem)
						.added(val)
		);
	}

	@Override
	public void setDiagonal(List<T> diag) {
		List<T> rem = getDiagonal();
		super.setDiagonal(diag);
		registerChange(
				Change.of(this, GridChangeType.REPLACE_DIAGONAL)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(columnsNum + 1)
						.removed(rem)
						.added(diag)
		);
	}

	@Override
	public void addRow(int index, List<T> row) {
		super.addRow(index, row);
		registerChange(
				Change.of(this, GridChangeType.ADD_ROW)
						.setCoordinates(index, -1)
						.setStart(index * columnsNum)
						.setEnd(index * columnsNum + row.size())
						.setStep(1)
						.added(row)
		);
	}

	@Override
	public void addColumn(int index, List<T> column) {
		super.addColumn(index, column);
		registerChange(
				Change.of(this, GridChangeType.ADD_COLUMN)
						.setCoordinates(-1, index)
						.setStart(index)
						.setEnd(((rowsNum - 1) * columnsNum + index) + 1)
						.setStep(columnsNum)
						.added(column)
		);
	}

	@Override
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

		List<T> tmp = getRow(index);
		int start = index * columnsNum;
		int j = 0;
		for (int i = start; i < start + columnsNum; i++) {
			data.set(i, row.get(j));
			j++;
		}
		registerChange(
				Change.of(this, GridChangeType.REPLACE_ROW)
						.setCoordinates(index, -1)
						.setStart(start)
						.setEnd(start + columnsNum)
						.setStep(1)
						.removed(tmp)
						.added(row)
		);
	}

	@Override
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

		List<T> tmp = getColumn(index);
		int end = (rowsNum - 1) * columnsNum + index;
		int j = 0;
		for (int i = index; i <= end; i += columnsNum) {
			data.set(i, column.get(j));
			j++;
		}
		registerChange(
				Change.of(this, GridChangeType.REPLACE_COLUMN)
						.setCoordinates(-1, index)
						.setStart(index)
						.setEnd(end + 1)
						.setStep(columnsNum)
						.removed(tmp)
						.added(column)
		);
	}

	@Override
	public List<T> removeRow(int index) {
		List<T> rem = super.removeRow(index);
		registerChange(
				Change.of(this, GridChangeType.REMOVE_ROW)
						.setCoordinates(index, -1)
						.setStart(index * columnsNum)
						.setEnd(index * columnsNum + columnsNum)
						.setStep(1)
						.removed(rem)
		);
		return rem;
	}

	@Override
	public List<T> removeColumn(int index) {
		List<T> rem = super.removeColumn(index);
		int columnsNum = this.columnsNum + 1;
		registerChange(
				Change.of(this, GridChangeType.REMOVE_COLUMN)
						.setCoordinates(-1, index)
						.setStart(index)
						.setEnd(((rowsNum - 1) * columnsNum + index) + 1)
						.setStep(columnsNum)
						.removed(rem)
		);
		return rem;
	}

	@Override
	public Grid<T> transpose() {
		Grid<T> grid = super.transpose();
		registerChange(
				Change.of(this, GridChangeType.TRANSPOSE)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
		);
		return grid;
	}

	@Override
	public void clear() {
		List<T> tmp = new ArrayList<>(getData());
		super.clear();
		registerChange(
				Change.of(this, GridChangeType.CLEAR)
						.setStart(0)
						.setEnd(totalSize())
						.setStep(1)
						.removed(tmp)
		);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		change.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		change.removeListener(listener);
	}

	@Override
	public void addListener(ChangeListener<? super Change<T>> listener) {
		change.addListener(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super Change<T>> listener) {
		change.removeListener(listener);
	}

	/**
	 * @return the last {@link Change} occurred or {@link Change#EMPTY} if no change occurred
	 * or the last change was disposed with {@link Change#endChange()}
	 */
	@Override
	public Change<T> getValue() {
		return change.get();
	}

	/**
	 * Delegate for {@link #getValue()}
	 */
	public Change<T> getChange() {
		return getValue();
	}

	//================================================================================
	// Internal Classes
	//================================================================================

	/**
	 * Bean used to represent any type of change occurring in a {@link ObservableGrid} data structure.
	 * <p></p>
	 * The {@code Change} brings a series of useful information like:
	 * <p> - The grid's data after the change
	 * <p> - The change's type, see {@link GridChangeType}
	 * <p> - A list containing the added items
	 * <p> - A list containing the removed items
	 * <p> - The coordinates at which the change occurred. Note though that this information is not always available,
	 * see {@link #getCoordinates()}
	 * <p> - The linear index at which the change started (inclusive)
	 * <p> - The linear index at which the change ended (exclusive)
	 * <p> - The "step" which is an integer useful to iterate over the change's [start, end] range.
	 * When a change occurs on a row typically is 1, when it occurs on a column typically it is the number of
	 * columns of the grid before the change.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static class Change<T> {
		public static final Change EMPTY = new Change();

		private ObservableGrid<T> grid;
		private List<T> data;
		private final Pair<Integer, Integer> size;
		private final GridChangeType type;
		private final List<T> added = new ArrayList<>();
		private final List<T> removed = new ArrayList<>();
		private Coordinates coordinates;
		private int start;
		private int end;
		private int step;

		private Change() {
			this.data = List.of();
			this.size = null;
			this.type = null;
		}

		public Change(ObservableGrid<T> grid, GridChangeType type) {
			this.grid = grid;
			this.data = Collections.unmodifiableList(grid.getData());
			this.size = grid.size();
			this.type = type;
		}

		public static <T> Change<T> of(ObservableGrid<T> grid, GridChangeType type) {
			return new ObservableGrid.Change<>(grid, type);
		}

		/**
		 * @return the {@link ObservableGrid} instance this change refers to
		 */
		protected ObservableGrid<T> getGrid() {
			return grid;
		}

		/**
		 * @return the grid's data as an unmodifiable {@link List}
		 */
		public List<T> getData() {
			return data;
		}

		/**
		 * @return the size of the grid as a {@link Pair} object. The key is the number of rows and
		 * the value is the number of columns
		 */
		public Pair<Integer, Integer> getSize() {
			return size;
		}

		/**
		 * @return the type of operation that lead to this change, see {@link GridChangeType}
		 */
		public GridChangeType getType() {
			return type;
		}

		/**
		 * @return a {@link List} containing all the added elements. For "replacement" operations
		 * this will contain all the new items
		 */
		public List<T> getAdded() {
			return added;
		}

		/**
		 * @return a {@link List} containing all the removed elements. For "replacement" operations
		 * this will contain all the elements that have been replaced
		 */
		public List<T> getRemoved() {
			return removed;
		}

		@SafeVarargs
		private Change<T> added(T... data) {
			Collections.addAll(added, data);
			return this;
		}

		private Change<T> added(List<T> data) {
			added.addAll(data);
			return this;
		}

		@SafeVarargs
		private Change<T> removed(T... data) {
			Collections.addAll(removed, data);
			return this;
		}

		private Change<T> removed(List<T> data) {
			removed.addAll(data);
			return this;
		}

		/**
		 * @return a {@link Coordinates} object that specifies the row and column at which the change occurred.
		 * This information is available only for the following changes:
		 * <p> - Set element
		 * <p> - Add row (column will be -1)
		 * <p> - Add column (row will be -1)
		 * <p> - Set row (column will be -1)
		 * <p> - Set column (row will be -1)
		 * <p> - Remove row (column will be -1)
		 * <p> - Remove column (row will be -1)
		 */
		public Coordinates getCoordinates() {
			return coordinates;
		}

		private Change<T> setCoordinates(Coordinates coordinates) {
			this.coordinates = coordinates;
			return this;
		}

		private Change<T> setCoordinates(int row, int column) {
			this.coordinates = Coordinates.of(row, column);
			return this;
		}

		/**
		 * @return the linear index at which the change started
		 */
		public int getStart() {
			return start;
		}

		private Change<T> setStart(int start) {
			this.start = start;
			return this;
		}

		/**
		 * @return the linear index at which the change ended (exclusive)
		 */
		public int getEnd() {
			return end;
		}

		private Change<T> setEnd(int end) {
			this.end = end;
			return this;
		}

		/**
		 * @return an integer useful to iterate over the change's [start, end] range.
		 * When a change occurs on a row typically is 1, when it occurs on a column typically it is the number of
		 * columns of the grid before the change
		 */
		public int step() {
			return step;
		}

		private Change<T> setStep(int step) {
			this.step = step;
			return this;
		}

		/**
		 * Disposes this change and resets the grid's property responsible for holding new changes.
		 */
		public void endChange() {
			grid.registerChange(EMPTY);
			grid = null;
			data = null;
			added.clear();
			removed.clear();
		}
	}
}
