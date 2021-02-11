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

package io.github.palexdev.materialfx.controls.tableview;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.cell.tableview.MFXTableRow;
import io.github.palexdev.materialfx.skins.tableview.MFXTableViewSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class MFXTableView<S> extends TableView<S> {
    private final String STYLE_CLASS = "mfx-table-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/tableview/mfx-tableview.css").toString();

    public MFXTableView() {
        initialize();
    }

    public MFXTableView(ObservableList<S> items) {
        super(items);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setRowFactory(row -> new MFXTableRow<>());
        setFixedCellSize(27);
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
