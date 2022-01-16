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

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.skins.base.MFXLabeledSkinBase;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXRadioButton}.
 */
public class MFXRadioButtonSkin extends MFXLabeledSkinBase<MFXRadioButton> {
	//================================================================================
	// Properties
	//================================================================================
	private final StackPane radioContainer;
	private final Circle radio;
	private final Circle dot;

	private final MFXCircleRippleGenerator rippleGenerator;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXRadioButtonSkin(MFXRadioButton radioButton) {
		super(radioButton);

		radio = new Circle();
		radio.getStyleClass().add("radio");
		radio.radiusProperty().bind(radioButton.radiusProperty());
		radio.setSmooth(true);

		dot = new Circle();
		dot.getStyleClass().add("dot");
		dot.radiusProperty().bind(radioButton.radiusProperty());
		dot.setScaleX(0);
		dot.setScaleY(0);
		dot.setSmooth(true);

		radioContainer = new StackPane(radio, dot);

		rippleGenerator = new MFXCircleRippleGenerator(radioContainer);
		rippleGenerator.setAnimateBackground(false);
		rippleGenerator.setClipSupplier(() -> null);
		rippleGenerator.setRipplePositionFunction(event -> {
			PositionBean position = new PositionBean();
			position.setX(radio.getBoundsInParent().getCenterX());
			position.setY(radio.getBoundsInParent().getCenterY());
			return position;
		});
		radioContainer.getChildren().add(0, rippleGenerator);
		rippleGenerator.setManaged(false);

		updateAlignment();
		initContainer();

		getChildren().setAll(topContainer);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void animate(boolean selected) {
		MFXRadioButton radioButton = getSkinnable();
		double radius = radioButton.getRadius();
		double scale = (radius - radioButton.getRadioGap()) / radius;
		TimelineBuilder.build()
				.add(KeyFrames.of(
						100,
						new KeyValue(dot.scaleXProperty(), selected ? scale : 0, Interpolators.EASE_OUT.toInterpolator()),
						new KeyValue(dot.scaleYProperty(), selected ? scale : 0, Interpolators.EASE_OUT.toInterpolator())
				))
				.getAnimation()
				.play();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void addListeners() {
		super.addListeners();
		MFXRadioButton radioButton = getSkinnable();

		radioButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> radioButton.fire());
		radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
			animate(newValue);
			rippleGenerator.generateRipple(null);
		});

		radioButton.radioGapProperty().addListener(invalidated -> {
			if (radioButton.isSelected()) {
				double radius = radioButton.getRadius();
				double scale = (radius - radioButton.getRadioGap()) / radius;
				dot.setScaleX(scale);
				dot.setScaleY(scale);
			}
		});

		NodeUtils.waitForSkin(radioButton, () -> {
			if (radioButton.isSelected()) animate(true);
		}, false, false);
	}

	@Override
	protected Pane getControlContainer() {
		return radioContainer;
	}
}
