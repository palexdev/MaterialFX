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

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXFlowlessListView;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessListCell;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FontResourcesDemoController implements Initializable {

    @FXML
    private MFXFlowlessListView<HBox> list;

    @FXML
    private MFXLabel count;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list.setCellFactory(hBox -> {
            MFXFlowlessListCell<HBox> cell = new MFXFlowlessListCell<>(list, hBox);
            cell.setFixedCellHeight(48);
            return cell;
        });
        ScrollUtils.addSmoothScrolling(list, 5);
        populateList();
        count.setText(list.getItems().size() + " Icons");
    }

    private void populateList() {
        List<FontResources> fontResources = Arrays.asList(FontResources.values());
        fontResources.sort(Comparator.comparing(FontResources::name));


        List<HBox> resBoxes = fontResources.stream().map(this::buildNode).collect(Collectors.toList());
        list.setItems(resBoxes);
    }

    private HBox buildNode(FontResources fontResource) {
        MFXFontIcon icon = new MFXFontIcon(fontResource.getDescription(), 20);
        MFXLabel l1 = new MFXLabel();
        l1.setLineColor(Color.TRANSPARENT);
        l1.setUnfocusedLineColor(Color.TRANSPARENT);
        l1.setStyle("-fx-background-color: transparent");
        l1.setText("Description: " + fontResource.getDescription());
        l1.setMinWidth(300);
        l1.setAlignment(Pos.CENTER_LEFT);

        MFXLabel l2 = new MFXLabel();
        l2.setLineColor(Color.TRANSPARENT);
        l2.setUnfocusedLineColor(Color.TRANSPARENT);
        l2.setStyle("-fx-background-color: transparent");
        l2.setText("Code: " + Integer.toHexString(fontResource.getCode() | 0x10000).substring(1).toUpperCase());
        l2.setMinWidth(300);
        l2.setAlignment(Pos.CENTER_LEFT);

        Separator s1 = new Separator(Orientation.VERTICAL);
        s1.setStyle("-fx-fill: white");
        Separator s2 = new Separator(Orientation.VERTICAL);
        s2.setStyle("-fx-fill: white");


        HBox box = new HBox(10, new MFXIconWrapper(icon, 24), s1, l1, s2, l2);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
