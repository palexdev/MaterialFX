/*
 * Copyright (C) 2021 Parisi Alessandro
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
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.selection.TableSelectionModel;
import io.github.palexdev.materialfx.selection.base.ITableSelectionModel;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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

    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<ITableSelectionModel<T>> selectionModel = new SimpleObjectProperty<>();
    private final ObservableList<MFXTableColumn<T>> tableColumns = FXCollections.observableArrayList();
    private final MFXTableSortModel<T> sortModel;

    private final SupplierProperty<Region> headerSupplier = new SupplierProperty<>();
    private final DoubleProperty headerHeight = new SimpleDoubleProperty(48);
    private final StringProperty headerText = new SimpleStringProperty("");
    private final ObjectProperty<Node> headerIcon = new SimpleObjectProperty<>();

    private final DoubleProperty fixedRowsHeight = new SimpleDoubleProperty(30);
    private final IntegerProperty maxRowsPerPage = new SimpleIntegerProperty(20);

    private final ListChangeListener<? super T> changeListener;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableView() {
        this(FXCollections.observableArrayList());
    }

    public MFXTableView(ObservableList<T> items) {
        setItems(items);

        sortModel = new MFXTableSortModel<>(tableColumns);

        changeListener = change -> {
            if (getSelectionModel().getSelectedItems().isEmpty()) {
                return;
            }
            if (change.getList().isEmpty()) {
                getSelectionModel().clearSelection();
                return;
            }

            getSelectionModel().setUpdating(true);
            Map<Integer, Integer> addedOffsets = new HashMap<>();
            Map<Integer, Integer> removedOffsets = new HashMap<>();

            while (change.next()) {
                if (change.wasAdded()) {
                    int from = change.getFrom();
                    int to = change.getTo();
                    int offset = to - from;
                    addedOffsets.put(from, offset);
                }
                if (change.wasRemoved()) {
                    int from = change.getFrom();
                    int offset = change.getRemovedSize();
                    IntStream.range(from, from + offset)
                            .filter(getSelectionModel()::containSelected)
                            .forEach(getSelectionModel()::clearSelectedItem);
                    removedOffsets.put(from, offset);
                }
            }
            updateSelection(addedOffsets, removedOffsets);
        };

        getItems().addListener(changeListener);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(STYLE_CLASS);
        defaultHeaderSupplier();
        defaultSelectionModel();

        items.addListener((observable, oldValue, newValue) -> {
            getSelectionModel().clearSelection();
            if (oldValue != null) {
                oldValue.removeListener(changeListener);
            }
            if (newValue != null) {
                newValue.addListener(changeListener);
            }
        });
    }

    /**
     * Installs the default selection model in this table view.
     *
     * @see #selectionModelProperty()
     */
    protected void defaultSelectionModel() {
        TableSelectionModel<T> selectionModel = new TableSelectionModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    /**
     * Installs the default header supplier in this table view.
     *
     * @see #headerSupplierProperty()
     */
    protected void defaultHeaderSupplier() {
        setHeaderSupplier(() -> {
            Label header = new Label();
            header.getStyleClass().add("header");
            header.setMinHeight(Region.USE_PREF_SIZE);
            header.prefHeightProperty().bind(Bindings.createDoubleBinding(
                    () -> getHeaderText().isEmpty() ? 0 : getHeaderHeight(),
                    headerText, headerHeight
            ));
            header.setMaxWidth(Double.MAX_VALUE);
            header.textProperty().bind(headerText);
            header.graphicProperty().bind(headerIcon);
            VBox.setMargin(header, new Insets(5, 10, 0, 10));

            return header;
        });
    }

    protected void updateSelection(Map<Integer, Integer> addedOffsets, Map<Integer, Integer> removedOffsets) {
        MapProperty<Integer, T> selectedItems = getSelectionModel().selectedItemsProperty();
        ObservableMap<Integer, T> updatedMap = FXCollections.observableHashMap();
        selectedItems.forEach((key, value) -> {
            int sum = addedOffsets.entrySet().stream()
                    .filter(entry -> entry.getKey() <= key)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            int diff = removedOffsets.entrySet().stream()
                    .filter(entry -> entry.getKey() < key)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            int shift = sum - diff;
            updatedMap.put(key + shift, value);
        });
        if (!selectedItems.equals(updatedMap)) {
            selectedItems.set(updatedMap);
        }
        getSelectionModel().setUpdating(false);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    /**
     * Specifies the items observable list for the table.
     */
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public ITableSelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    /**
     * Specifies the selection model to be used.
     */
    public ObjectProperty<ITableSelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public void setSelectionModel(ITableSelectionModel<T> selectionModel) {
        this.selectionModel.set(selectionModel);
    }

    /**
     * @return the table columns observable list
     */
    public ObservableList<MFXTableColumn<T>> getTableColumns() {
        return tableColumns;
    }

    /**
     * Replaces the table columns with the given list.
     */
    public void setTableColumns(List<MFXTableColumn<T>> columns) {
        tableColumns.setAll(columns);
    }

    /**
     * @return this table sort model instance.
     */
    public MFXTableSortModel<T> getSortModel() {
        return sortModel;
    }

    public Supplier<Region> getHeaderSupplier() {
        return headerSupplier.get();
    }

    /**
     * Specifies the supplier used in the table skin to build the column header region.
     * <p>
     * The default supplier makes use of the following properties as well:
     * <p> - {@link #headerHeightProperty()}
     * <p> - {@link #headerTextProperty()}
     * <p> - {@link #headerIconProperty()}
     */
    public SupplierProperty<Region> headerSupplierProperty() {
        return headerSupplier;
    }

    public void setHeaderSupplier(Supplier<Region> headerSupplier) {
        this.headerSupplier.set(headerSupplier);
    }

    public double getHeaderHeight() {
        return headerHeight.get();
    }

    /**
     * Specifies the header height.
     */
    public DoubleProperty headerHeightProperty() {
        return headerHeight;
    }

    public void setHeaderHeight(double headerHeight) {
        this.headerHeight.set(headerHeight);
    }

    public String getHeaderText() {
        return headerText.get();
    }

    /**
     * Specifies the header text.
     */
    public StringProperty headerTextProperty() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText.set(headerText);
    }

    public Node getHeaderIcon() {
        return headerIcon.get();
    }

    /**
     * Specifies the header icon.
     */
    public ObjectProperty<Node> headerIconProperty() {
        return headerIcon;
    }

    public void setHeaderIcon(Node headerIcon) {
        this.headerIcon.set(headerIcon);
    }

    public double getFixedRowsHeight() {
        return fixedRowsHeight.get();
    }

    /**
     * Specifies the height of the rows.
     */
    public DoubleProperty fixedRowsHeightProperty() {
        return fixedRowsHeight;
    }

    public void setFixedRowsHeight(double fixedRowsHeight) {
        this.fixedRowsHeight.set(fixedRowsHeight);
    }

    public int getMaxRowsPerPage() {
        return maxRowsPerPage.get();
    }

    /**
     * Specifies the max number of rows that can be shown in a single page.
     * This value is used by the table combo box.
     * <p></p>
     * <b>
     * N.B: Values must be multiples of 5/10 otherwise the navigation system will break.
     * </b>
     */
    public IntegerProperty maxRowsPerPageProperty() {
        return maxRowsPerPage;
    }

    public void setMaxRowsPerPage(int maxRowsPerPage) {
        this.maxRowsPerPage.set(maxRowsPerPage);
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
    // Events
    //================================================================================

    /**
     * Events class for the table view.
     * <p>
     * Defines a new EventTypes:
     * <p>
     * - FORCE_UPDATE_EVENT: used to manually update the table <p></p>
     */
    public static class MFXTableViewEvent extends Event {

        public static final EventType<MFXTableViewEvent> FORCE_UPDATE_EVENT = new EventType<>(ANY, "FORCE_UPDATE_EVENT");

        public MFXTableViewEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }
    }

    /**
     * Forces the table to update
     * <p>
     * This is especially useful when the model is not built with
     * JavaFX properties so when it changes the table must be updated manually
     */
    public void updateTable() {
        Event.fireEvent(this, new MFXTableViewEvent(MFXTableViewEvent.FORCE_UPDATE_EVENT));
    }
}
