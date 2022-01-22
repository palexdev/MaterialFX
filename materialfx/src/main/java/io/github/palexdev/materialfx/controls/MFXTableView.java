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
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.collections.TransformableListWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.others.observables.When;
import io.github.palexdev.virtualizedfx.beans.NumberRange;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import io.github.palexdev.virtualizedfx.utils.ListChangeHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.function.Function;

/**
 * This is the implementation of a table view following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Control} and provides a new skin since it is built from scratch.
 *
 * @param <T> The type of the data within the table.
 * @see MFXTableViewSkin
 */
public class MFXTableView<T> extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-table-view";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTableView.css");
	protected final SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow;
	protected final ReadOnlyBooleanWrapper virtualFlowInitialized = new ReadOnlyBooleanWrapper();

	private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>();
	private final ListChangeListener<? super T> itemsChanged = this::itemsChanged;

	private final IMultipleSelectionModel<T> selectionModel = new MultipleSelectionModel<>(items);
	private final ObservableList<MFXTableColumn<T>> tableColumns = FXCollections.observableArrayList();
	private final FunctionProperty<T, MFXTableRow<T>> tableRowFactory = new FunctionProperty<>(item -> new MFXTableRow<>(this, item));

	private final TransformableListWrapper<T> transformableList = new TransformableListWrapper<>(FXCollections.observableArrayList());
	private final ObservableList<AbstractFilter<T, ?>> filters = FXCollections.observableArrayList();
	private final InvalidationListener itemsInvalid = invalidated -> transformableList.setAll(getItems());
	private final BooleanProperty footerVisible = new SimpleBooleanProperty(true);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableView() {
		this(FXCollections.observableArrayList());
	}

	public MFXTableView(ObservableList<T> items) {
		setItems(items);
		rowsFlow = new SimpleVirtualFlow<>(
				transformableList,
				getTableRowFactory(),
				Orientation.VERTICAL
		) {
			@Override
			public String getUserAgentStylesheet() {
				return MFXTableView.this.getUserAgentStylesheet();
			}
		};
		rowsFlow.cellFactoryProperty().bind(tableRowFactoryProperty());
		VBox.setVgrow(rowsFlow, Priority.ALWAYS);

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);

		transformableList.setAll(getItems());
		itemsProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.removeListener(itemsChanged);
				oldValue.removeListener(itemsInvalid);
			}
			if (newValue != null) {
				newValue.addListener(itemsChanged);
				newValue.addListener(itemsInvalid);
				transformableList.setAll(newValue);
			}
		});

		getItems().addListener(itemsChanged);
		getItems().addListener(itemsInvalid);
	}

	/**
	 * Responsible for updating the selection when the items list changes.
	 */
	protected void itemsChanged(ListChangeListener.Change<? extends T> change) {
		IMultipleSelectionModel<T> selectionModel = getSelectionModel();
		if (selectionModel.getSelection().isEmpty()) return;

		if (change.getList().isEmpty()) {
			selectionModel.clearSelection();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.processChange(change, NumberRange.of(0, Integer.MAX_VALUE));
		ListChangeProcessor updater = new ListChangeProcessor(new HashSet<>(selectionModel.getSelection().keySet()));
		c.processReplacement((changed, removed) -> selectionModel.replaceSelection(changed.toArray(Integer[]::new)));
		c.processAddition((from, to, added) -> {
			updater.computeAddition(added.size(), from);
			selectionModel.replaceSelection(updater.getIndexes().toArray(Integer[]::new));
		});
		c.processRemoval((from, to, removed) -> {
			updater.computeRemoval(removed, from);
			getSelectionModel().replaceSelection(updater.getIndexes().toArray(Integer[]::new));
		});
	}

	/**
	 * Allows to programmatically update the table.
	 * <p>
	 * Uses {@link MFXTableRow#updateRow()} on the currently built rows, {@link SimpleVirtualFlow#getCells()}.
	 */
	public void update() {
		rowsFlow.getCells().values().forEach(MFXTableRow::updateRow);
	}

	/**
	 * Autosize all the table columns.
	 */
	public void autosizeColumns() {
		tableColumns.forEach(this::autosizeColumn);
	}

	/**
	 * Autosizes the column at the given index.
	 * <p>
	 * This method fails silently if it can not get the column at index.
	 */
	public void autosizeColumn(int index) {
		try {
			MFXTableColumn<T> column = tableColumns.get(index);
			autosizeColumn(column);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Autosizes the given column.
	 */
	public void autosizeColumn(MFXTableColumn<T> column) {
		int index = tableColumns.indexOf(column);
		if (index == -1) return;

		Collection<MFXTableRow<T>> rows = rowsFlow.getCells().values();
		List<Double> minSizes = new ArrayList<>();
		minSizes.add(column.getWidth());
		rows.forEach(row -> {
			ObservableList<MFXTableRowCell<T, ?>> rowCells = row.getCells();
			if (rowCells.isEmpty()) return;
			MFXTableRowCell<T, ?> rowCell = rowCells.get(index);
			rowCell.requestLayout();
			minSizes.add(rowCell.computePrefWidth(-1));
		});
		double max = minSizes.stream().max(Double::compareTo).orElse(-1.0);
		if (max != -1.0) {
			column.setMinWidth(max);
		}
	}

	/**
	 * This should be called only if you need to autosize the columns
	 * before the table is laid out/initialized.
	 * <p>
	 * Calling this afterwards won't have any effect.
	 */
	public void autosizeColumnsOnInitialization() {
		if (isVirtualFlowInitialized()) return;
		When.onChanged(virtualFlowInitialized)
				.then((oldValue, newValue) -> autosizeColumns())
				.oneShot()
				.listen();
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate for {@link SimpleVirtualFlow#getCell(int)}.
	 */
	public MFXTableRow<T> getCell(int index) {
		return rowsFlow.getCell(index);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#getCells()}.
	 */
	public Map<Integer, MFXTableRow<T>> getCells() {
		return rowsFlow.getCells();
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#scrollBy(double)}.
	 */
	public void scrollBy(double pixels) {
		rowsFlow.scrollBy(pixels);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#scrollTo(int)}.
	 */
	public void scrollTo(int index) {
		rowsFlow.scrollTo(index);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#scrollToFirst()}.
	 */
	public void scrollToFirst() {
		rowsFlow.scrollToFirst();
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#scrollToLast()}.
	 */
	public void scrollToLast() {
		rowsFlow.scrollToLast();
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#scrollToPixel(double)}.
	 */
	public void scrollToPixel(double pixel) {
		rowsFlow.scrollToPixel(pixel);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#setHSpeed(double, double)}.
	 */
	public void setHSpeed(double unit, double block) {
		rowsFlow.setHSpeed(unit, block);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#setVSpeed(double, double)}.
	 */
	public void setVSpeed(double unit, double block) {
		rowsFlow.setVSpeed(unit, block);
	}

	/**
	 * Delegate for {@link SimpleVirtualFlow#features()}.
	 */
	public SimpleVirtualFlow<T, MFXTableRow<T>>.Features features() {
		return rowsFlow.features();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTableViewSkin<>(this, rowsFlow);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		if (!isVirtualFlowInitialized() && rowsFlow.getCellHeight() > 0) virtualFlowInitialized.set(true);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public ObservableList<T> getItems() {
		return items.get();
	}

	/**
	 * Specifies the table's {@link ObservableList} containing the items.
	 */
	public ObjectProperty<ObservableList<T>> itemsProperty() {
		return items;
	}

	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}

	/**
	 * @return the selection model used by the table to handle row selection
	 */
	public IMultipleSelectionModel<T> getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return the list containing the table's columns
	 */
	public ObservableList<MFXTableColumn<T>> getTableColumns() {
		return tableColumns;
	}

	public Function<T, MFXTableRow<T>> getTableRowFactory() {
		return tableRowFactory.get();
	}

	/**
	 * Specifies the {@link Function} used to generate the table rows.
	 */
	public FunctionProperty<T, MFXTableRow<T>> tableRowFactoryProperty() {
		return tableRowFactory;
	}

	public void setTableRowFactory(Function<T, MFXTableRow<T>> tableRowFactory) {
		this.tableRowFactory.set(tableRowFactory);
	}

	/**
	 * @return the list that is effectively used by the {@link SimpleVirtualFlow} (which contains the table rows).
	 * This list is capable of filtering and sorting.
	 * @see TransformableListWrapper
	 * @see TransformableList
	 */
	public TransformableListWrapper<T> getTransformableList() {
		return transformableList;
	}

	/**
	 * @return the list containing the filters' information used by the
	 * {@link  MFXFilterPane} to filter the table
	 */
	public ObservableList<AbstractFilter<T, ?>> getFilters() {
		return filters;
	}

	public boolean isFooterVisible() {
		return footerVisible.get();
	}

	/**
	 * Specifies whether the table's footer is visible
	 */
	public BooleanProperty footerVisibleProperty() {
		return footerVisible;
	}

	public void setFooterVisible(boolean footerVisible) {
		this.footerVisible.set(footerVisible);
	}

	public boolean isVirtualFlowInitialized() {
		return virtualFlowInitialized.get();
	}

	/**
	 * Useful property to inform that the table layout
	 * has been initialized/is ready.
	 * <p>
	 * For example it is used by {@link #autosizeColumnsOnInitialization()}
	 * to autosize the columns before the table is even laid out by using a
	 * listener.
	 * <p>
	 * It is considered initialized as soon as the {@link SimpleVirtualFlow}
	 * retrieves the cells' height.
	 */
	public ReadOnlyBooleanProperty virtualFlowInitializedProperty() {
		return virtualFlowInitialized.getReadOnlyProperty();
	}
}
