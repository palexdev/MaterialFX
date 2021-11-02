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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.beans.PositionBean;
import javafx.scene.control.skin.DateCellSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXDateCell}.
 * <p>
 * This is necessary to make the {@link MFXCircleRippleGenerator work properly}.
 */
public class MFXDateCellSkin extends DateCellSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXCircleRippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDateCellSkin(MFXDateCell dateCell) {
        super(dateCell);

        rippleGenerator = new MFXCircleRippleGenerator(dateCell);
        rippleGenerator.setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(15).build(dateCell));
        rippleGenerator.setRippleColor(Color.rgb(220, 220, 220, 0.6));
        rippleGenerator.setRipplePositionFunction(event -> new PositionBean(event.getX(), event.getY()));
        dateCell.addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);

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
