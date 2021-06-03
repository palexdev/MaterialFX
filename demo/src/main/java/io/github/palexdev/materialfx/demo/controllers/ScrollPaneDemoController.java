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


import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ScrollPaneDemoController implements Initializable {

    @FXML
    private MFXScrollPane scrollPaneV;

    @FXML
    private MFXScrollPane scrollPaneVH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXScrollPane.smoothVScrolling(scrollPaneV);
        MFXScrollPane.smoothVScrolling(scrollPaneVH);
    }

    @FXML
    void setRandomTrackColor() {
        scrollPaneV.setTrackColor(ColorUtils.getRandomColor());
        scrollPaneVH.setTrackColor(ColorUtils.getRandomColor());
    }

    @FXML
    void setRandomThumbColor() {
        scrollPaneV.setThumbColor(ColorUtils.getRandomColor());
        scrollPaneVH.setThumbColor(ColorUtils.getRandomColor());
    }

    @FXML
    void setRandomThumbHoverColor() {
        scrollPaneV.setThumbHoverColor(ColorUtils.getRandomColor());
        scrollPaneVH.setThumbHoverColor(ColorUtils.getRandomColor());
    }
}
