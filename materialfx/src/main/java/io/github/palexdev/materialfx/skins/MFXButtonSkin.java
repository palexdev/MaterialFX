/*
 * Copyright (C) 2022 Parisi Alessandro
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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.input.MouseEvent;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXButton}.
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
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds listeners to: depthLevel and buttonType properties.
	 */
	private void setListeners() {
		MFXButton button = (MFXButton) getSkinnable();
		MFXCircleRippleGenerator rippleGenerator = button.getRippleGenerator();

		button.depthLevelProperty().addListener((observable, oldValue, newValue) -> updateButtonType());
		button.buttonTypeProperty().addListener((observable, oldValue, newValue) -> updateButtonType());

		button.addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
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
				button.setPickOnBounds(true);
				break;
			}
		}
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected void updateChildren() {
		super.updateChildren();

		MFXButton button = (MFXButton) getSkinnable();
		if (!getChildren().contains(button.getRippleGenerator())) {
			getChildren().add(0, button.getRippleGenerator());
		}
	}
}
