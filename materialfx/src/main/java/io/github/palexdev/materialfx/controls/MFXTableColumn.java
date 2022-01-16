/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.functional.ComparatorProperty;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.SortState;
import io.github.palexdev.materialfx.skins.MFXTableColumnSkin;
import io.github.palexdev.materialfx.utils.DragResizer;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;
import java.util.function.Function;

/**
 * This is the implementation of the column cells used in the {@link MFXTableView} control.
 * <p></p>
 * To make it easily customizable, extends {@link Labeled} and provides a new default skin, {@link  MFXTableColumnSkin}.
 * <p>
 * Defines the following new PseudoClasses for usage in CSS:
 * <p> - ":dragged", to customize the column when it is dragged
 * <p> - ":resizable", to customize the column depending on {@link #columnResizableProperty()}
 * <p></p>
 * Each column cell has the following responsibilities:
 * - <p> Has a row cell factory because each column knows how to build the corresponding row cell in each table row
 * - <p> Has a sort state and a comparator because each column knows how to sort the rows based on the given comparator, also
 * retains its sort state thus allowing switching between ASCENDING, DESCENDING, UNSORTED
 * <p></p>
 * Some side notes...
 * <p>
 * Even if not specified by the user, the {@link #rowCellFactoryProperty()} will always bind the new cells' width to the
 * width of the column, this is needed to make the columns autosize and the {@link  DragResizer} work properly.
 * <p>
 * The {@link #columnResizableProperty()} controls the {@link DragResizer}, installing it if true and uninstalling it if false.
 * All columns by default have a minimum width set to 100, can be changed of course as you like.
 * <p>
 * Unlike the old implementation, where all the "system" was managed by the table view skin, the new implementation vastly
 * improves the separation of roles. The cells' width (mentioned before), the sorting and the icon animation are all handled by the
 * columns now. To communicate with the table skin, the table column now uses {@link Event}s, see {@link MFXTableColumnEvent}.
 *
 * @see MFXTableColumnSkin
 */
public class MFXTableColumn<T> extends Labeled {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-table-column";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTableView.css");

	private final FunctionProperty<T, MFXTableRowCell<T, ?>> rowCellFactory = new FunctionProperty<>() {
		@Override
		public void set(Function<T, MFXTableRowCell<T, ?>> newValue) {
			super.set(newValue.andThen(cell -> {
				cell.setMinWidth(USE_PREF_SIZE);
				cell.prefWidthProperty().bind(widthProperty());
				cell.setMaxWidth(USE_PREF_SIZE);
				return cell;
			}));
		}
	};

	private final ObjectProperty<SortState> sortState = new SimpleObjectProperty<>(SortState.UNSORTED) {
		@Override
		public void set(SortState newValue) {
			if (getComparator() == null) {
				super.set(SortState.UNSORTED);
				return;
			}
			super.set(newValue);
		}

		@Override
		protected void invalidated() {
			SortState sortState = getSortState();
			Comparator<T> comparator = (sortState == SortState.DESCENDING) ? getComparator().reversed() : getComparator();
			fireEvent(new MFXTableColumnEvent<>(MFXTableColumnEvent.SORTING_EVENT, MFXTableColumn.this, comparator, sortState));
		}
	};

	private final ComparatorProperty<T> comparator = new ComparatorProperty<>() {
		@Override
		protected void invalidated() {
			SortState sortState = getSortState();
			Comparator<T> comparator = (sortState == SortState.DESCENDING) ? getComparator().reversed() : getComparator();
			fireEvent(new MFXTableColumnEvent<>(MFXTableColumnEvent.SORTING_EVENT, MFXTableColumn.this, comparator, sortState));
		}
	};

	private final ReadOnlyBooleanWrapper dragged = new ReadOnlyBooleanWrapper();
	private final BooleanProperty columnResizable = new SimpleBooleanProperty(false);
	protected static final PseudoClass DRAGGED_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragged");
	protected static final PseudoClass RESIZABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("resizable");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableColumn() {
		this("");
	}

	public MFXTableColumn(String name) {
		this(name, false);
	}

	public MFXTableColumn(String name, boolean resizable) {
		super(name);
		setColumnResizable(resizable);
		initialize();
	}

	public MFXTableColumn(String text, Comparator<T> comparator) {
		super(text);
		setComparator(comparator);
		initialize();
	}

	public MFXTableColumn(String text, boolean resizable, Comparator<T> comparator) {
		super(text);
		setColumnResizable(resizable);
		setComparator(comparator);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setAlignment(Pos.CENTER_LEFT);
		setMinWidth(100);

		columnResizable.addListener(invalidated -> pseudoClassStateChanged(RESIZABLE_PSEUDO_CLASS, columnResizable.get()));
		pseudoClassStateChanged(RESIZABLE_PSEUDO_CLASS, columnResizable.get());

		dragged.addListener(invalidated -> pseudoClassStateChanged(DRAGGED_PSEUDO_CLASS, dragged.get()));
		addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> dragged.set(true));
		addEventFilter(MouseEvent.MOUSE_RELEASED, event -> dragged.set(false));

		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (getComparator() == null || event.getButton() != MouseButton.PRIMARY) return;
			setSortState(getSortState().next());
		});
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTableColumnSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Function<T, MFXTableRowCell<T, ?>> getRowCellFactory() {
		return rowCellFactory.get();
	}

	/**
	 * Specifies the {@link Function} used to build the row's cells.
	 */
	public FunctionProperty<T, MFXTableRowCell<T, ?>> rowCellFactoryProperty() {
		return rowCellFactory;
	}

	public void setRowCellFactory(Function<T, MFXTableRowCell<T, ?>> rowCellFactory) {
		this.rowCellFactory.set(rowCellFactory);
	}

	public SortState getSortState() {
		return sortState.get();
	}

	/**
	 * Specifies the sort state of the column.
	 */
	public ObjectProperty<SortState> sortStateProperty() {
		return sortState;
	}

	public void setSortState(SortState sortState) {
		this.sortState.set(sortState);
	}

	public Comparator<T> getComparator() {
		return comparator.get();
	}

	/**
	 * Specifies the {@link Comparator} used to sort the column.
	 */
	public ComparatorProperty<T> comparatorProperty() {
		return comparator;
	}

	public void setComparator(Comparator<T> comparator) {
		this.comparator.set(comparator);
	}

	public boolean isDragged() {
		return dragged.get();
	}

	/**
	 * Specifies whether the column is being dragged.
	 */
	public ReadOnlyBooleanProperty draggedProperty() {
		return dragged.getReadOnlyProperty();
	}

	protected void setDragged(boolean dragged) {
		this.dragged.set(dragged);
	}

	public boolean isColumnResizable() {
		return columnResizable.get();
	}

	/**
	 * Specifies whether the column can be resized.
	 */
	public BooleanProperty columnResizableProperty() {
		return columnResizable;
	}

	public void setColumnResizable(boolean columnResizable) {
		this.columnResizable.set(columnResizable);
	}

	//================================================================================
	// Events
	//================================================================================

	/**
	 * This class introduces new {@link Event}s for {@link  MFXTableColumn}s, such as:
	 * <p> - SORTING_EVENT: this event is used to tell the table view skin that the column is being sorted
	 */
	@SuppressWarnings("rawtypes")
	public static class MFXTableColumnEvent<T> extends Event {
		private final MFXTableColumn<T> column;
		private final Comparator<T> comparator;
		private final SortState sortState;

		public static final EventType<? extends MFXTableColumnEvent> SORTING_EVENT = new EventType<>(ANY, "SORTING_EVENT");

		public MFXTableColumnEvent(EventType<? extends Event> eventType, MFXTableColumn<T> column, Comparator<T> comparator, SortState sortState) {
			super(eventType);
			this.column = column;
			this.comparator = comparator;
			this.sortState = sortState;
		}

		public MFXTableColumn<T> getColumn() {
			return column;
		}

		public Comparator<T> getComparator() {
			return comparator;
		}

		public SortState getSortState() {
			return sortState;
		}
	}
}
