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

import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.RippleClipType;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.scene.control.skin.DateCellSkin;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXDateCell}.
 * <p>
 * This is necessary to make the {@link RippleGenerator work properly}.
 */
public class MFXDateCellSkin extends DateCellSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final RippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDateCellSkin(MFXDateCell dateCell) {
        super(dateCell);

        rippleGenerator = new RippleGenerator(dateCell, new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(15, 15));
        rippleGenerator.setOutDuration(Duration.millis(500));
        dateCell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });

        updateChildren();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippleGenerator != null) {
            getChildren().add(0, rippleGenerator);
        }
    }
}
