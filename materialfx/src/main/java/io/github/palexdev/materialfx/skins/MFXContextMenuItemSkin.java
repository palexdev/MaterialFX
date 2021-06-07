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
import io.github.palexdev.materialfx.controls.MFXContextMenuItem;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a basic implementation of a {@code Skin} used by every {@link MFXContextMenuItem}.
 * <p>
 * It is basically an HBox which contains two labels to show the text and the accelerator.
 */
public class MFXContextMenuItemSkin extends SkinBase<MFXContextMenuItem> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXContextMenuItemSkin(MFXContextMenuItem item) {
        super(item);

        MFXIconWrapper iconWrapper = new MFXIconWrapper(null, 24);
        iconWrapper.iconProperty().bind(item.iconProperty());

        Label text = new Label();
        text.textProperty().bind(item.textProperty());
        text.minWidthProperty().bind(item.textWidthProperty());
        text.alignmentProperty().bind(item.textAlignmentProperty());
        text.paddingProperty().bind(item.textInsetsProperty());

        Label accelerator = new Label();
        accelerator.getStyleClass().add("accelerator");
        accelerator.textProperty().bind(item.acceleratorProperty());
        accelerator.minWidthProperty().bind(item.acceleratorWidthProperty());
        accelerator.alignmentProperty().bind(item.acceleratorAlignmentProperty());
        accelerator.paddingProperty().bind(item.acceleratorInsetsProperty());

        GridPane gridPane = new GridPane();
        gridPane.vgapProperty().bind(item.spacingProperty());
        gridPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        List<ColumnConstraints> columnConstraints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(Region.USE_COMPUTED_SIZE);

            columnConstraints.add(cc);
        }
        gridPane.getColumnConstraints().setAll(columnConstraints);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(27);
        rowConstraints.setPrefHeight(Region.USE_COMPUTED_SIZE);
        rowConstraints.setValignment(VPos.CENTER);
        gridPane.getRowConstraints().setAll(rowConstraints);

        gridPane.add(iconWrapper, 0, 0);
        gridPane.add(text, 1, 0);
        gridPane.add(accelerator, 2, 0);

        getChildren().setAll(gridPane);
        setListeners();

        item.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> text.requestFocus());
    }

    private void setListeners() {
        MFXContextMenuItem item = getSkinnable();

    }
}
