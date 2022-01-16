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
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the HBox that contains the table row cells built by each column.
 * <p></p>
 * The new implementation of the table view makes use of {@link SimpleVirtualFlow} to contain
 * the rows, this makes the table view super efficient, that's also why the new {@code MFXTableRow}
 * implements {@link Cell}.
 * <p></p>
 * {@link MFXTableRowCell} though, are not reusable {@link Cell}s. So, to keep things efficient table rows
 * now build the cells only once (or when needed by the table view) and simply updates them when the {@link #dataProperty()} changes,
 * by using {@link #updateCells(Object)}. This mechanism should also simplify working with non JavaFX models (which do not use observables).
 */
public class MFXTableRow<T> extends HBox implements Cell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-table-row";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTableView.css");

	private final MFXTableView<T> tableView;
	private final ObservableList<MFXTableRowCell<T, ?>> cells = FXCollections.observableArrayList();
	private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();
	private final ReadOnlyObjectWrapper<T> data = new ReadOnlyObjectWrapper<>();

	protected final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	protected static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableRow(MFXTableView<T> tableView, T data) {
		this.tableView = tableView;
		setData(data);
		setMinHeight(USE_PREF_SIZE);
		setPrefHeight(32);
		setMaxHeight(USE_PREF_SIZE);
		initialize();
		buildCells();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);

		setBehavior();
		setupRippleGenerator();
	}

	/**
	 * Adds the needed listeners/handlers to manage the selection state.
	 *
	 * @see #updateSelection(MouseEvent).
	 */
	private void setBehavior() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(
				() -> tableView.getSelectionModel().getSelection().containsKey(getIndex()),
				tableView.getSelectionModel().selectionProperty(), index
		));

		addEventFilter(MouseEvent.MOUSE_CLICKED, this::updateSelection);
	}

	/**
	 * Initializes the ripple generator.
	 */
	protected void setupRippleGenerator() {
		rippleGenerator.setManaged(false);
		rippleGenerator.setRipplePositionFunction(event -> PositionBean.of(event.getX(), event.getY()));
		rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				rippleGenerator.generateRipple(event);
			}
		});
	}

	/**
	 * Public API to update the row's cells.
	 * <p>
	 * Simply calls {@link #updateRow(Object)} given that
	 * the current {@link #dataProperty()} is not null.
	 */
	public void updateRow() {
		T data = getData();
		if (data == null) return;
		updateRow(data);
	}

	/**
	 * Called by the {@link #updateItem(Object)} method.
	 * Responsible for updating the cells, {@link #updateCells(Object)}, or building them
	 * if not yet done, {@link #buildCells()}.
	 */
	protected void updateRow(T data) {
		if (cells.isEmpty()) {
			buildCells();
		} else {
			updateCells(data);
		}
	}

	/**
	 * Responsible for updating the row cells by calling {@link MFXTableRowCell#update(Object)}.
	 */
	protected void updateCells(T data) {
		cells.forEach(cell -> cell.update(data));
	}

	/**
	 * Responsible for building the row's cells when needed.
	 * <p>
	 * For each column specified by the table view, {@link MFXTableView#getTableColumns()}, retrieves the
	 * {@link MFXTableColumn#rowCellFactoryProperty()}, build the cell with the row's data, {@link #dataProperty()},
	 * updates the cell, {@link MFXTableRowCell#update(Object)}, then adds to the list.
	 * At the end calls {@link #updateChildren(List)} with the built cells list.
	 * <p></p>
	 * If the row's data is null, exits immediately.
	 */
	public void buildCells() {
		T data = getData();
		if (data == null) return;

		if (!cells.isEmpty()) cells.clear();
		ObservableList<MFXTableColumn<T>> columns = tableView.getTableColumns();
		for (MFXTableColumn<T> column : columns) {
			MFXTableRowCell<T, ?> cell = column.getRowCellFactory().apply(data);
			cell.update(data);
			cells.add(cell);
		}
		updateChildren(cells);
	}

	/**
	 * Responsible for populating the row with the given children list.
	 * Since the row also has a ripple generator, this is added at the start of the given list.
	 */
	private void updateChildren(List<MFXTableRowCell<T, ?>> children) {
		List<Node> finalList = new LinkedList<>(children);
		finalList.add(0, rippleGenerator);
		getChildren().setAll(finalList);
	}

	/**
	 * Responsible for handling the selection triggered by a {@link MouseEvent}.
	 * <p>
	 * According to the index and the selection state this can: deselect the row,
	 * add the row to the selection model, replace the selection with only this row.
	 */
	private void updateSelection(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) return;

		int index = getIndex();
		if (event.isControlDown()) {
			if (isSelected()) {
				tableView.getSelectionModel().deselectIndex(index);
			} else {
				tableView.getSelectionModel().selectIndex(index);
			}
			return;
		}

		if (event.isShiftDown()) {
			tableView.getSelectionModel().expandSelection(index);
			return;
		}

		tableView.getSelectionModel().replaceSelection(index);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void updateItem(T data) {
		setData(data);
		updateRow(data);
	}

	@Override
	public void updateIndex(int index) {
		setIndex(tableView.getTransformableList().viewToSource(index));
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the row's cells as an unmodifiable observable list
	 */
	public ObservableList<MFXTableRowCell<T, ?>> getCells() {
		return FXCollections.unmodifiableObservableList(cells);
	}

	public int getIndex() {
		return index.get();
	}

	/**
	 * Specifies the row's index.
	 */
	public ReadOnlyIntegerProperty indexProperty() {
		return index.getReadOnlyProperty();
	}

	protected void setIndex(int index) {
		this.index.set(index);
	}

	public T getData() {
		return data.get();
	}

	/**
	 * Specifies the item represented by the row.
	 */
	public ReadOnlyObjectProperty<T> dataProperty() {
		return data.getReadOnlyProperty();
	}

	protected void setData(T data) {
		this.data.set(data);
	}

	public boolean isSelected() {
		return selected.get();
	}

	/**
	 * Specifies the selection state of the row.
	 */
	public ReadOnlyBooleanProperty selectedProperty() {
		return selected.getReadOnlyProperty();
	}

	protected void setSelected(boolean selected) {
		this.selected.set(selected);
	}
}
