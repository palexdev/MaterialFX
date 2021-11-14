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

import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TogglesController implements Initializable {

    @FXML
    private MFXToggleButton toggleButton;

    @FXML
    private MFXRectangleToggleNode rec1;

    @FXML
    private MFXRectangleToggleNode rec2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rec1.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec1.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec2.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec2.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
    }

    @FXML
    private void handleButtonClick() {
        // TODO check, maybe make specific method
        // TODO needs to be better in CSS, maybe not use derive()
        toggleButton.setStyle("-mfx-main: " + ColorUtils.toCss(ColorUtils.getRandomColor()));
        //toggleButton.setToggleColor(ColorUtils.getRandomColor()); // TODO change, or reimplement
        toggleButton.setSelected(false);
    }
}
