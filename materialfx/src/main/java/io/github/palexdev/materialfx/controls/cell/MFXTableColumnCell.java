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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.enums.SortState;
import io.github.palexdev.materialfx.skins.MFXTableColumnCellSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.Comparator;

public class MFXTableColumnCell<T> extends Label {
    private final String STYLE_CLASS = "mfx-table-column-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-table-column-cell.css").toString();

    private final ObjectProperty<Callback<T, ? extends MFXTableRowCell>> rowCellFactory = new SimpleObjectProperty<>();
    private final StringProperty columnName = new SimpleStringProperty("");

    private SortState sortState = SortState.UNSORTED;
    private Comparator<T> comparator;

    public MFXTableColumnCell(String columnName) {
        textProperty().bind(columnNameProperty());
        setColumnName(columnName);
        initialize();
    }

    public MFXTableColumnCell(String columnName, Comparator<T> comparator) {
        textProperty().bind(columnNameProperty());
        setColumnName(columnName);
        this.comparator = comparator;
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public Callback<T, ? extends MFXTableRowCell> getRowCellFactory() {
        return rowCellFactory.get();
    }

    public ObjectProperty<Callback<T, ? extends MFXTableRowCell>> rowCellFactoryProperty() {
        return rowCellFactory;
    }

    public void setRowCellFactory(Callback<T, ? extends MFXTableRowCell> rowCellFactory) {
        this.rowCellFactory.set(rowCellFactory);
    }

    public String getColumnName() {
        return columnName.get();
    }

    public StringProperty columnNameProperty() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public SortState getSortState() {
        return sortState;
    }

    public void setSortState(SortState sortState) {
        this.sortState = sortState;
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableColumnCellSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
