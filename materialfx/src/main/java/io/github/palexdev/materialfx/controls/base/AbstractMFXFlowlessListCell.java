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
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Base class for all cells used in list views based on Flowless,
 * defines common properties and behavior.
 * <p>
 * Extends {@link HBox} and implements {@link Cell}.
 *
 * @param <T> the type of data within the ListView
 */
public abstract class AbstractMFXFlowlessListCell<T> extends HBox implements Cell<T, HBox> {
    //================================================================================
    // Properties
    //================================================================================
    protected final AbstractFlowlessListView<T, ?, ?> listView;
    private final ReadOnlyObjectWrapper<T> data;
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(-1);
    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    //================================================================================
    // Constructors
    //================================================================================
    public AbstractMFXFlowlessListCell(AbstractFlowlessListView<T, ?, ?> listView, T data) {
        this(listView, data, 32);
    }

    public AbstractMFXFlowlessListCell(AbstractFlowlessListView<T, ?, ?> listView, T data, double fixedHeight) {
        this.listView = listView;
        this.data = new ReadOnlyObjectWrapper<>(data);
        this.fixedCellSize.set(fixedHeight);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(fixedCellSize);

        initialize();
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Abstract method which defines how the cell should process and show the given data.
     */
    protected abstract void render(T data);

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        addListeners();
    }

    /**
     * Adds listeners to:
     * <p>
     *  - selected property to update the pseudo class state.<p>
     *  - scene property to call the {@link #render(T)} method the first time.<p>
     *  - selection model's selected items property to properly update the state of the selected property.<p>
     *  <p>
     * Adds a filter for MOUSE_PRESSED which calls {@link #updateModel(MouseEvent)}.
     */
    private void addListeners() {
        selected.addListener(invalidate -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));

        sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null) {
                    render(getData());
                    sceneProperty().removeListener(this);
                }
            }
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateModel);
        listView.getSelectionModel().selectedItemsProperty().addListener((InvalidationListener) invalidated -> {
            if (!containsEqualsBoth() && isSelected()) {
                setSelected(false);
            }
        });
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

    /**
     * Specifies the fixed height of the cell.
     */
    public DoubleProperty fixedCellSizeProperty() {
        return fixedCellSize;
    }

    public void setFixedCellSize(double fixedCellSize) {
        this.fixedCellSize.set(fixedCellSize);
    }

    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Selection state of the cell.
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * Updates the selection state of the cell and the state
     * of the selection model as well.
     */
    public void updateModel(MouseEvent event) {
        setSelected(!isSelected());
        if (isSelected()) {
            listView.getSelectionModel().select(getIndex(), getData(), event);
        } else {
            listView.getSelectionModel().clearSelectedItem(getIndex());
        }
    }

    /**
     * Checks if the selection model contains the index and the data of this cell.
     */
    protected boolean containsEqualsBoth() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsKey(getIndex()) &&
                selectionModel.selectedItemsProperty().get(getIndex()).equals(getData());
    }

    /**
     * Checks if the selection model contains the data of the cell but the index is not the same.
     */
    protected boolean containsNotEqualsIndex() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsValue(getData()) &&
                selectionModel.selectedItemsProperty().entrySet()
                        .stream()
                        .anyMatch(entry -> entry.getKey() != getIndex() && entry.getValue().equals(getData()));
    }

    /**
     * Checks if the selection model contains the index of the cell but the data is not the same.
     */
    protected boolean containsNotEqualsData() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsKey(getIndex()) &&
                !selectionModel.selectedItemsProperty().get(getIndex()).equals(getData());
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public HBox getNode() {
        return this;
    }

    /**
     * Inherited doc:
     * <p>
     * {@inheritDoc}
     *
     * <p></p>
     * Updates the index property of the cell with the given index parameter
     * (which is provided by Flowless) then checks and fixes inconsistencies in the
     * selection model.
     *
     *{@link #containsEqualsBoth()},
     *{@link #containsNotEqualsIndex()},
     *{@link #containsNotEqualsData()},
     */
    @Override
    public void updateIndex(int index) {
        setIndex(index);
        if (containsEqualsBoth() && !isSelected()) {
            setSelected(true);
            return;
        }
        if (containsNotEqualsIndex()) {
            listView.getSelectionModel().updateIndex(getData(), index);
            setSelected(true);
            return;
        }
        if (containsNotEqualsData()) {
            listView.getSelectionModel().clearSelectedItem(index);
        }
    }
}
