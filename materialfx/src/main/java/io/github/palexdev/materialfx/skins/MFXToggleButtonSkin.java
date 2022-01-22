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
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.skins.base.MFXLabeledSkinBase;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXToggleButton}.
 */
public class MFXToggleButtonSkin extends MFXLabeledSkinBase<MFXToggleButton> {
	//================================================================================
	// Properties
	//================================================================================
	private final StackPane toggleContainer;
	private final Circle circle;
	private final Line line;

	private final MFXCircleRippleGenerator rippleGenerator;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXToggleButtonSkin(MFXToggleButton toggleButton) {
		super(toggleButton);

		// Line
		line = new Line();
		line.getStyleClass().add("line");
		line.endXProperty().bind(toggleButton.lengthProperty().subtract(line.strokeWidthProperty()));
		line.strokeWidthProperty().bind(toggleButton.radiusProperty().multiply(1.5));
		line.setSmooth(true);

		// Circle
		circle = new Circle();
		circle.getStyleClass().add("circle");
		circle.radiusProperty().bind(toggleButton.radiusProperty());
		circle.setSmooth(true);
		circle.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL1));

		// Ripple Generator, Line, Circle container
		toggleContainer = new StackPane(line, circle);
		toggleContainer.setAlignment(Pos.CENTER_LEFT);
		toggleContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		toggleContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		toggleContainer.setPickOnBounds(false);

		rippleGenerator = new MFXCircleRippleGenerator(toggleContainer);
		rippleGenerator.setAnimateBackground(false);
		rippleGenerator.setClipSupplier(() -> null);
		rippleGenerator.setRipplePositionFunction(event -> {
			PositionBean position = new PositionBean();
			position.xProperty().bind(Bindings.createDoubleBinding(
					() -> circle.localToParent(circle.getLayoutBounds()).getCenterX(),
					circle.translateXProperty()
			));
			position.yProperty().bind(Bindings.createDoubleBinding(
					() -> circle.localToParent(circle.getLayoutBounds()).getCenterY(),
					circle.layoutBoundsProperty()
			));
			return position;
		});
		toggleContainer.getChildren().add(0, rippleGenerator);

		// Control's top container
		updateAlignment();
		initContainer();

		getChildren().setAll(topContainer);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void buildAndPlayAnimation(boolean selection) {
		double endX = selection ? line.getBoundsInParent().getMaxX() - circle.getRadius() * 2 : 0;
		TimelineBuilder.build()
				.add(
						KeyFrames.of(0, event -> rippleGenerator.generateRipple(null)),
						KeyFrames.of(150, circle.translateXProperty(), endX, Interpolators.INTERPOLATOR_V1)
				)
				.getAnimation().play();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void addListeners() {
		super.addListeners();
		MFXToggleButton toggleButton = getSkinnable();

		toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> buildAndPlayAnimation(newValue));
		NodeUtils.waitForSkin(
				toggleButton,
				() -> {
					double endX = toggleButton.isSelected() ? line.getLayoutBounds().getWidth() - circle.getRadius() * 2 : 0;
					TimelineBuilder.build().add(KeyFrames.of(150, circle.translateXProperty(), endX, Interpolators.INTERPOLATOR_V1)).getAnimation().play();
				},
				false,
				true
		);
	}

	@Override
	protected Pane getControlContainer() {
		return toggleContainer;
	}
}
