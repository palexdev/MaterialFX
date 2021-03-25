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

public abstract class AbstractMFXFlowlessListCell<T> extends HBox implements Cell<T, HBox> {
    protected final AbstractFlowlessListView<T, ?, ?> listView;
    private final ReadOnlyObjectWrapper<T> data;
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(-1);
    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

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

    private void initialize() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        addListeners();
    }

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

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateModel);
        listView.getSelectionModel().selectedItemsProperty().addListener((InvalidationListener) invalidated -> {
            if (!containsEqualsBoth() && isSelected()) {
                setSelected(false);
            }
        });
    }

    public T getData() {
        return data.get();
    }

    public ReadOnlyObjectProperty<T> dataProperty() {
        return data.getReadOnlyProperty();
    }

    protected void setData(T data) {
        this.data.set(data);
    }

    public int getIndex() {
        return index.get();
    }

    public ReadOnlyIntegerProperty indexProperty() {
        return index.getReadOnlyProperty();
    }

    protected void setIndex(int index) {
        this.index.set(index);
    }

    public DoubleProperty fixedCellSizeProperty() {
        return fixedCellSize;
    }

    public void setFixedCellSize(double fixedCellSize) {
        this.fixedCellSize.set(fixedCellSize);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    protected abstract void render(T data);

    @Override
    public HBox getNode() {
        return this;
    }

    public void updateModel(MouseEvent event) {
        setSelected(!isSelected());
        if (isSelected()) {
            listView.getSelectionModel().select(getIndex(), getData(), event);
        } else {
            listView.getSelectionModel().clearSelectedItem(getIndex());
        }
    }

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

    protected boolean containsEqualsBoth() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsKey(getIndex()) &&
                selectionModel.selectedItemsProperty().get(getIndex()).equals(getData());
    }

    protected boolean containsNotEqualsIndex() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsValue(getData()) &&
                selectionModel.selectedItemsProperty().entrySet()
                        .stream()
                        .anyMatch(entry -> entry.getKey() != getIndex() && entry.getValue().equals(getData()));
    }

    protected boolean containsNotEqualsData() {
        IListSelectionModel<T> selectionModel = listView.getSelectionModel();
        return selectionModel.selectedItemsProperty().containsKey(getIndex()) &&
                !selectionModel.selectedItemsProperty().get(getIndex()).equals(getData());
    }
}
