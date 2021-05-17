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

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class LabelsDemoController implements Initializable {

    @FXML
    private MFXLabel custom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXIconWrapper leading = new MFXIconWrapper(new MFXFontIcon("mfx-filter", 15), 22).defaultRippleGeneratorBehavior();
        MFXIconWrapper trailing = new MFXIconWrapper(new MFXFontIcon("mfx-info-circle", 15), 22).defaultRippleGeneratorBehavior();

        NodeUtils.makeRegionCircular(leading);
        NodeUtils.makeRegionCircular(trailing);

        MFXCircleRippleGenerator lrg = leading.getRippleGenerator();
        lrg.setRippleColor(Color.web("#849ED7"));

        MFXCircleRippleGenerator trg = trailing.getRippleGenerator();
        trg.setRippleColor(Color.web("#849ED7"));

        custom.setLeadingIcon(leading);
        custom.setTrailingIcon(trailing);
    }
}
