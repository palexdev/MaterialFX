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
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class MFXTableView<T> extends Control {
    private final String STYLE_CLASS = "mfx-table-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-tableview.css").toString();

    private final ObservableList<T> items = FXCollections.observableArrayList();
    // SELECTION MODEL

    private final ObservableList<MFXTableColumn<T>> columns = FXCollections.observableArrayList();
    private final IntegerProperty maxRows = new SimpleIntegerProperty(10);

    public MFXTableView() {
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public ObservableList<T> getItems() {
        return items;
    }

    public ObservableList<MFXTableColumn<T>> getColumns() {
        return columns;
    }

    public int getMaxRows() {
        return maxRows.get();
    }

    public IntegerProperty maxRowsProperty() {
        return maxRows;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
