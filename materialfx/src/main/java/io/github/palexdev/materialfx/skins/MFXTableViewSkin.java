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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumnCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MFXTableViewSkin<T> extends SkinBase<MFXTableView<T>> {
    private final VBox container;
    private final HBox columnsContainer;
    private final VBox rowsContainer;

    public MFXTableViewSkin(MFXTableView<T> tableView) {
        super(tableView);

        container = new VBox();
        container.getStyleClass().setAll("container");

        columnsContainer = new HBox(10);
        columnsContainer.getStyleClass().setAll("columns-container");
        columnsContainer.prefWidthProperty().bind(columnsContainer.widthProperty());
        columnsContainer.setPrefHeight(30);
        columnsContainer.setAlignment(Pos.CENTER_LEFT);
        columnsContainer.setPadding(new Insets(0, 10, 0, 10));

        rowsContainer = new VBox();
        //rowsContainer.setPrefHeight(tableView.getMaxRows() * tableView.getFixedRowsHeight());
        container.getChildren().addAll(columnsContainer, rowsContainer);
        getChildren().add(container);

        buildColumns();
        buildRows();
    }

    protected void buildColumns() {
        MFXTableView<T> tableView = getSkinnable();

        for (MFXTableColumn<T> column : tableView.getColumns()) {
            MFXTableColumnCell columnCell = column.getColumnCellFactory().call(column);
            columnCell.setStyle("-fx-border-color: red");
            columnCell.setMaxHeight(Double.MAX_VALUE);
            columnCell.setPadding(new Insets(0, 5, 0, 5));
            columnsContainer.getChildren().add(columnCell);
        }
    }

    protected void buildRows() {
        MFXTableView<T> tableView = getSkinnable();

        for (T item : tableView.getItems()) {
            HBox box = new HBox(10);
            box.setPadding(new Insets(0, 10, 0, 10));
            for (MFXTableColumn<T> column : tableView.getColumns()) {
                MFXTableRowCell rowCell = column.getRowCellFactory().call(item);
                box.getChildren().add(rowCell);
            }
            rowsContainer.getChildren().add(box);
        }
    }
}
