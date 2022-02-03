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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.paint.Paint;

import java.util.List;

public class StepperBuilder extends ControlBuilder<MFXStepper> {

	//================================================================================
	// Constructors
	//================================================================================
	public StepperBuilder() {
		this(new MFXStepper());
	}

	public StepperBuilder(MFXStepper stepper) {
		super(stepper);
	}

	public static StepperBuilder stepper() {
		return new StepperBuilder();
	}

	public static StepperBuilder stepper(MFXStepper stepper) {
		return new StepperBuilder(stepper);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public StepperBuilder setStepperToggles(List<MFXStepperToggle> stepperToggles) {
		node.setStepperToggles(stepperToggles);
		return this;
	}

	public StepperBuilder setAnimationDuration(double animationDuration) {
		node.setAnimationDuration(animationDuration);
		return this;
	}

	public StepperBuilder setEnableContentValidationOnError(boolean enableContentValidationOnError) {
		node.setEnableContentValidationOnError(enableContentValidationOnError);
		return this;
	}

	public StepperBuilder setSpacing(double spacing) {
		node.setSpacing(spacing);
		return this;
	}

	public StepperBuilder setExtraSpacing(double extraSpacing) {
		node.setExtraSpacing(extraSpacing);
		return this;
	}

	public StepperBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public StepperBuilder setBaseColor(Paint baseColor) {
		node.setBaseColor(baseColor);
		return this;
	}

	public StepperBuilder setAltColor(Paint altColor) {
		node.setAltColor(altColor);
		return this;
	}

	public StepperBuilder setProgressBarBorderRadius(double progressBarBorderRadius) {
		node.setProgressBarBorderRadius(progressBarBorderRadius);
		return this;
	}

	public StepperBuilder setProgressBarBackground(Paint progressBarBackground) {
		node.setProgressBarBackground(progressBarBackground);
		return this;
	}

	public StepperBuilder setProgressColor(Paint progressColor) {
		node.setProgressColor(progressColor);
		return this;
	}

	public StepperBuilder setAnimated(boolean animated) {
		node.setAnimated(animated);
		return this;
	}

	public StepperBuilder setOnBeforeNext(EventHandler<MFXStepper.MFXStepperEvent> onBeforeNext) {
		node.setOnBeforeNext(onBeforeNext);
		return this;
	}

	public StepperBuilder setOnNext(EventHandler<MFXStepper.MFXStepperEvent> onNext) {
		node.setOnNext(onNext);
		return this;
	}

	public StepperBuilder setOnBeforePrevious(EventHandler<MFXStepper.MFXStepperEvent> onBeforePrevious) {
		node.setOnBeforePrevious(onBeforePrevious);
		return this;
	}

	public StepperBuilder setOnPrevious(EventHandler<MFXStepper.MFXStepperEvent> onPrevious) {
		node.setOnPrevious(onPrevious);
		return this;
	}

	public StepperBuilder setOnLastNext(EventHandler<MFXStepper.MFXStepperEvent> onLastNext) {
		node.setOnLastNext(onLastNext);
		return this;
	}

	public StepperBuilder setOnValidationFailed(EventHandler<MFXStepper.MFXStepperEvent> onValidationFailed) {
		node.setOnValidationFailed(onValidationFailed);
		return this;
	}
}
