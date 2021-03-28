/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumnCell;
import io.github.palexdev.materialfx.selection.TableSelectionModel;
import io.github.palexdev.materialfx.selection.base.ITableSelectionModel;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * This is the implementation of a table view following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Control} and provides a new skin since it is built from scratch.
 *
 * @param <T> The type of the data within the table.
 */
public class MFXTableView<T> extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-table-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-tableview.css");

    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final ObjectProperty<ITableSelectionModel<T>> selectionModel = new SimpleObjectProperty<>(null);

    private final ObservableList<MFXTableColumnCell<T>> columns = FXCollections.observableArrayList();
    private final IntegerProperty maxRows = new SimpleIntegerProperty(10);
    private final IntegerProperty maxRowsCombo = new SimpleIntegerProperty(20);
    private final DoubleProperty fixedRowsHeight = new SimpleDoubleProperty(27);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableView() {
        installSelectionModel();
        initialize();
    }

    public MFXTableView(double fixedRowsHeight) {
        installSelectionModel();

        setFixedRowsHeight(fixedRowsHeight);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    /**
     * Installs the default selection model in this table view.
     */
    protected void installSelectionModel() {
        ITableSelectionModel<T> selectionModel = new TableSelectionModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    public ObservableList<T> getItems() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.setAll(items);
    }

    public ITableSelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    /**
     * Specifies the selection model used by the control.
     */
    public ObjectProperty<ITableSelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public void setSelectionModel(ITableSelectionModel<T> selectionModel) {
        this.selectionModel.set(selectionModel);
    }

    public ObservableList<MFXTableColumnCell<T>> getColumns() {
        return columns;
    }

    public int getMaxRows() {
        return maxRows.get();
    }

    /**
     * Specifies the max rows per page.
     */
    public IntegerProperty maxRowsProperty() {
        return maxRows;
    }

    public int getMaxRowsCombo() {
        return maxRowsCombo.get();
    }

    /**
     * Specifies the max value in the combo box.
     */
    public IntegerProperty maxRowsComboProperty() {
        return maxRowsCombo;
    }

    public void setMaxRowsCombo(int maxRowsCombo) {
        this.maxRowsCombo.set(maxRowsCombo);
    }

    public double getFixedRowsHeight() {
        return fixedRowsHeight.get();
    }

    /**
     * Specifies the max height of all rows in the table.
     */
    public DoubleProperty fixedRowsHeightProperty() {
        return fixedRowsHeight;
    }

    public void setFixedRowsHeight(double fixedRowsHeight) {
        this.fixedRowsHeight.set(fixedRowsHeight);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    //================================================================================
    // Events Class
    //================================================================================

    /**
     * Events class for the table view.
     * <p>
     * Defines a new EventType:
     * <p>
     * - FORCE_UPDATE_EVENT: this event is captures by the table view's skin to force an update
     * of the rows. This is useful when the model is not based on JavaFX's properties because when
     * some item changes the data is not updated automatically, so it must be done manually.
     * <p>
     */
    public static class TableViewEvent extends Event {
        public static final EventType<TableViewEvent> FORCE_UPDATE_EVENT = new EventType<>(ANY, "FORCE_UPDATE_EVENT");

        public TableViewEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }
    }
}
