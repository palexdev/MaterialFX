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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.flowless.Cell;
import io.github.palexdev.materialfx.selection.ListSelectionModel;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Base class for all cells used in list views based on Flowless,
 * defines common properties and behavior (e.g selection), has the selected property
 * and PseudoClass ":selected" for usage in CSS.
 * <p>
 * Extends {@link HBox} and implements {@link Cell}.
 *
 * @param <T> the type of data within the ListView
 */
public abstract class AbstractMFXFlowlessListCell<T> extends HBox implements Cell<T, HBox> {
    //================================================================================
    // Properties
    //================================================================================
    protected final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(-1);
    protected final ReadOnlyObjectWrapper<T> data = new ReadOnlyObjectWrapper<>();
    protected final DoubleProperty fixedCellHeight = new SimpleDoubleProperty();

    private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper empty = new ReadOnlyBooleanWrapper();
    protected final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    protected final PseudoClass EMPTY_PSEUDO_CLASS = PseudoClass.getPseudoClass("empty");

    protected final BooleanProperty showEmpty = new SimpleBooleanProperty(false);

    //================================================================================
    // Constructors
    //================================================================================
    public AbstractMFXFlowlessListCell(T data) {
        this(data, 32);
    }

    public AbstractMFXFlowlessListCell(T data, double fixedHeight) {
        setData(data);
        setFixedCellHeight(fixedHeight);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(fixedCellHeight);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Abstract method which defines how the cell should process and show the given data.
     */
    protected abstract void render(T data);

    protected abstract IListSelectionModel<T> getSelectionModel();

    //================================================================================
    // Methods
    //================================================================================
    protected void initialize() {
        setBehavior();
    }

    /**
     * Sets the following behaviors:
     * <p>
     * - Calls {@link #updateSelection(MouseEvent)} on mouse pressed.<p>
     * - Updates the selected PseudoClass state when selected property changes.<p>
     * - Calls {@link #afterUpdateIndex()} when the index property changes.<p>
     * - Updates the selected property according to the list view' selection model changes.
     */
    protected void setBehavior() {
        selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
        empty.addListener(invalidated -> {
            pseudoClassStateChanged(EMPTY_PSEUDO_CLASS, empty.get());
            if (isEmpty()) {
                if (!isShowEmpty()) {
                    prefHeightProperty().unbind();
                    setPrefHeight(0);
                } else {
                    prefHeightProperty().bind(fixedCellHeight);
                }
            }
        });
        addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateSelection);
        index.addListener(invalidated -> afterUpdateIndex());
        getSelectionModel().selectedItemsProperty().addListener((InvalidationListener) invalidated -> setSelected(getSelectionModel().containSelected(getIndex())));
    }

    /**
     * Inverts the selected property state (from true to false and viceversa),
     * then according to the new state updates the selection model.
     * <p></p>
     * If true and the selection model doesn't already contain the cell index then calls
     * {@link ListSelectionModel#select(int, Object, MouseEvent)} with the cell's index and data.
     * <p></p>
     * If false calls {@link ListSelectionModel#clearSelectedItem(int)} with the cell's index.
     */
    protected void updateSelection(MouseEvent mouseEvent) {
        IListSelectionModel<T> selectionModel = getSelectionModel();
        setSelected(!isSelected());

        boolean selected = isSelected();
        int index = getIndex();
        if (!selected && selectionModel.containSelected(index)) {
            selectionModel.clearSelectedItem(index);
        } else if (selected && !selectionModel.containSelected(index)) {
            selectionModel.select(index, getData(), mouseEvent);
        }
    }

    /**
     * After the index property is updated by {@link #updateIndex(int)} this method
     * is called to set the selected property state accordingly to the selection model state.
     * <p></p>
     * If the cell is not selected but the selection model contains the cell's index then
     * sets the selected property to true.
     * <p></p>
     * If the cell is selected but the selection model doesn't contain the cell's index then
     * sets the selected property to false.
     */
    protected void afterUpdateIndex() {
        IListSelectionModel<T> selectionModel = getSelectionModel();

        boolean selected = isSelected();
        int index = getIndex();
        if (!selected && selectionModel.containSelected(index)) {
            setSelected(true);
        } else if (selected && !selectionModel.containSelected(index)) {
            setSelected(false);
        }
    }

    public int getIndex() {
        return index.get();
    }

    /**
     * Index property of the cell.
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
     * Data property of the cell.
     */
    public ReadOnlyObjectProperty<T> dataProperty() {
        return data.getReadOnlyProperty();
    }

    protected void setData(T data) {
        this.data.set(data);
    }

    public double getFixedCellHeight() {
        return fixedCellHeight.get();
    }

    /**
     * Specifies the fixed height of the cell.
     */
    public DoubleProperty fixedCellHeightProperty() {
        return fixedCellHeight;
    }

    public void setFixedCellHeight(double fixedCellHeight) {
        this.fixedCellHeight.set(fixedCellHeight);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selected.getReadOnlyProperty();
    }

    protected void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isEmpty() {
        return empty.get();
    }

    /**
     * Specifies if the cell is empty.
     */
    public ReadOnlyBooleanProperty emptyProperty() {
        return empty.getReadOnlyProperty();
    }

    protected void setEmpty(boolean empty) {
        this.empty.set(empty);
    }

    public boolean isShowEmpty() {
        return showEmpty.get();
    }

    /**
     * Specifies if empty cell should be visible anyway.
     * <p></p>
     * False by default, to change this behavior you must change the listview's
     * cell factory {@link AbstractMFXFlowlessListView#cellFactoryProperty()}
     */
    public BooleanProperty showEmptyProperty() {
        return showEmpty;
    }

    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty.set(showEmpty);
    }

    /**
     * Updates the index property of the cell with the given index (provided automatically by Flowless).
     */
    @Override
    public void updateIndex(int index) {
        this.index.set(index);
    }
}
