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
import io.github.palexdev.materialfx.controls.base.AbstractFlowlessListView;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessCheckListCell;
import io.github.palexdev.materialfx.selection.ListCheckModel;
import io.github.palexdev.materialfx.selection.base.IListCheckModel;
import io.github.palexdev.materialfx.skins.MFXFlowlessListViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

public class MFXFlowlessCheckListView<T> extends AbstractFlowlessListView<T, MFXFlowlessCheckListCell<T>, IListCheckModel<T>> {
    private final String STYLE_CLASS = "mfx-check-list-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-flowless-check-listview.css");

    public MFXFlowlessCheckListView() {
        this(FXCollections.observableArrayList());
    }

    public MFXFlowlessCheckListView(ObservableList<T> items) {
        super(items);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    @Override
    protected void setDefaultCellFactory() {
        setCellFactory(item -> new MFXFlowlessCheckListCell<>(this, item));
    }

    @Override
    protected void setDefaultSelectionModel() {
        IListCheckModel<T> selectionModel = new ListCheckModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXFlowlessListViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
