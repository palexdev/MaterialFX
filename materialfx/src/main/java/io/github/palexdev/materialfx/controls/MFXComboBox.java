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
import io.github.palexdev.materialfx.controls.enums.ComboBoxStyles;
import io.github.palexdev.materialfx.skins.MFXComboBoxSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class MFXComboBox<T> extends Control {
    private final String STYLE_CLASS = "mfx-combo-box";
    private final String STYLESHEET;

    private final ObjectProperty<T> selectedValue = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    private final DoubleProperty maxPopupHeight = new SimpleDoubleProperty(200);

    public MFXComboBox() {
        this(FXCollections.observableArrayList());
    }

    public MFXComboBox(ComboBoxStyles style) {
        this(FXCollections.observableArrayList(), style);
    }

    public MFXComboBox(ObservableList<T> items) {
        this(items, ComboBoxStyles.STYLE1);
    }

    public MFXComboBox(ObservableList<T> items, ComboBoxStyles style) {
        this.STYLESHEET = MFXResourcesLoader.load(style.getStyleSheetPath()).toString();
        this.items.set(items);

        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public T getSelectedValue() {
        return selectedValue.get();
    }

    public ObjectProperty<T> selectedValueProperty() {
        return selectedValue;
    }

    public void setSelectedValue(T selectedValue) {
        this.selectedValue.set(selectedValue);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public double getMaxPopupHeight() {
        return maxPopupHeight.get();
    }

    public DoubleProperty maxPopupHeightProperty() {
        return maxPopupHeight;
    }

    public void setMaxPopupHeight(double maxPopupHeight) {
        this.maxPopupHeight.set(maxPopupHeight);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXComboBoxSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
