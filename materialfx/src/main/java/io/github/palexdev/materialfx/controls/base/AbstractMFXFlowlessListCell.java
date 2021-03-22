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
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public abstract class AbstractMFXFlowlessListCell<T> extends HBox implements Cell<T, HBox> {
    private final ReadOnlyObjectProperty<T> data;
    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public AbstractMFXFlowlessListCell(T data) {
        this(data, 32);
    }

    public AbstractMFXFlowlessListCell(T data, double fixedHeight) {
        this.data = new ReadOnlyObjectWrapper<>(data);
        this.fixedCellSize.set(fixedHeight);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(fixedCellSize);

        initialize();
        render(data);
    }

    private void initialize() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        addListeners();
    }

    private void addListeners() {
        selected.addListener(invalidate -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
    }

    public T getData() {
        return data.get();
    }

    public ReadOnlyObjectProperty<T> dataProperty() {
        return data;
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

    @Override
    public boolean isReusable() {
        return true;
    }

    @Override
    public void reset() {
        getChildren().clear();
    }

    @Override
    public void updateItem(T data) {
        render(data);
    }
}
