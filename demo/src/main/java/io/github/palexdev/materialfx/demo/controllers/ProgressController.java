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

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressController implements Initializable {

	@FXML
	private MFXProgressBar determinateBar;

	@FXML
	private MFXProgressSpinner determinateSpinner;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		determinateBar.getRanges1().add(NumberRange.of(0.0, 0.30));
		determinateBar.getRanges2().add(NumberRange.of(0.31, 0.60));
		determinateBar.getRanges3().add(NumberRange.of(0.61, 1.0));

		determinateSpinner.getRanges1().add(NumberRange.of(0.0, 0.30));
		determinateSpinner.getRanges2().add(NumberRange.of(0.31, 0.60));
		determinateSpinner.getRanges3().add(NumberRange.of(0.61, 1.0));

		createAndPlayAnimation(determinateBar);
		createAndPlayAnimation(determinateSpinner);
	}

	private void createAndPlayAnimation(ProgressIndicator indicator) {
		Animation a1 = TimelineBuilder.build()
				.add(
						KeyFrames.of(2000, indicator.progressProperty(), 0.3, Interpolators.INTERPOLATOR_V1),
						KeyFrames.of(4000, indicator.progressProperty(), 0.6, Interpolators.INTERPOLATOR_V1),
						KeyFrames.of(6000, indicator.progressProperty(), 1.0, Interpolators.INTERPOLATOR_V1)
				)
				.getAnimation();

		Animation a2 = TimelineBuilder.build()
				.add(
						KeyFrames.of(1000, indicator.progressProperty(), 0, Interpolators.INTERPOLATOR_V2)
				)
				.getAnimation();

		a1.setOnFinished(end -> PauseBuilder.build()
				.setDuration(Duration.seconds(1))
				.setOnFinished(event -> a2.playFromStart())
				.getAnimation()
				.play()
		);
		a2.setOnFinished(end -> PauseBuilder.build()
				.setDuration(Duration.seconds(1))
				.setOnFinished(event -> a1.playFromStart())
				.getAnimation()
				.play()
		);

		a1.play();
	}
}
