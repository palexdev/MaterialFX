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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.input.MouseEvent;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXButton}.
 */
public class MFXButtonSkin extends ButtonSkin {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonSkin(MFXButton button) {
        super(button);

        setListeners();
        updateButtonType();

        updateChildren();
        setupRippleGenerator();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Binds the button's ripple properties to the ripple generator ones.
     */
    protected void setupRippleGenerator() {
        MFXButton button = (MFXButton) getSkinnable();
        RippleGenerator rippleGenerator = button.getRippleGenerator();

        button.rippleColorProperty().bind(rippleGenerator.rippleColorProperty());
        button.rippleRadiusProperty().bind(rippleGenerator.rippleRadiusProperty());
        button.rippleInDurationProperty().bind(rippleGenerator.inDurationProperty());
        button.rippleOutDurationProperty().bind(rippleGenerator.outDurationProperty());
    }

    /**
     * Adds listeners to: depthLevel and buttonType properties.
     */
    private void setListeners() {
        MFXButton button = (MFXButton) getSkinnable();
        RippleGenerator rippleGenerator = button.getRippleGenerator();

        button.depthLevelProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && button.getButtonType().equals(ButtonType.RAISED)) {
                button.setEffect(MFXDepthManager.shadowOf(newValue));
            }
        });

        button.buttonTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                updateButtonType();
            }
        });

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });

    }

    /**
     * Changes the button type.
     */
    private void updateButtonType() {
        MFXButton button = (MFXButton) getSkinnable();

        switch (button.getButtonType()) {
            case RAISED: {
                button.setEffect(MFXDepthManager.shadowOf(button.getDepthLevel()));
                button.setPickOnBounds(false);
                break;
            }
            case FLAT: {
                button.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL0));
                button.setPickOnBounds(true);
                break;
            }
        }
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        getChildren().add(0, ((MFXButton) getSkinnable()).getRippleGenerator());
    }
}
