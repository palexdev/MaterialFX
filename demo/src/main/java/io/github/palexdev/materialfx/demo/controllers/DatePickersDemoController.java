/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.demo.MFXDemoResourcesLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DatePickersDemoController implements Initializable {

    @FXML
    private MFXDatePicker customPicker;

    @FXML
    private StackPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = MFXDemoResourcesLoader.load("css/CustomDatePicker.css");
        customPicker.getContent().getStylesheets().add(css);

        MFXDatePicker initialized = new MFXDatePicker(LocalDate.now());
        initialized.setColorText(true);
        pane.getChildren().add(initialized);
        StackPane.setMargin(initialized, new Insets(10, 0, 0, 0));
    }
}
