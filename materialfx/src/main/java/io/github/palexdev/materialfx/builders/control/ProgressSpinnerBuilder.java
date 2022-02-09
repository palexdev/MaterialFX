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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.builders.base.BaseProgressBuilder;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.scene.paint.Color;

public class ProgressSpinnerBuilder extends BaseProgressBuilder<MFXProgressSpinner> {

	//================================================================================
	// Constructors
	//================================================================================
	public ProgressSpinnerBuilder() {
		this(new MFXProgressSpinner());
	}

	public ProgressSpinnerBuilder(MFXProgressSpinner progressSpinner) {
		super(progressSpinner);
	}

	public static ProgressSpinnerBuilder progressSpinner() {
		return new ProgressSpinnerBuilder();
	}

	public static ProgressSpinnerBuilder progressSpinner(MFXProgressSpinner progressSpinner) {
		return new ProgressSpinnerBuilder(progressSpinner);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ProgressSpinnerBuilder setColor1(Color color1) {
		node.setColor1(color1);
		return this;
	}

	public ProgressSpinnerBuilder setColor2(Color color2) {
		node.setColor2(color2);
		return this;
	}

	public ProgressSpinnerBuilder setColor3(Color color3) {
		node.setColor3(color3);
		return this;
	}

	public ProgressSpinnerBuilder setColor4(Color color4) {
		node.setColor4(color4);
		return this;
	}

	public ProgressSpinnerBuilder setRadius(double radius) {
		node.setRadius(radius);
		return this;
	}

	public ProgressSpinnerBuilder setStartingAngle(double startingAngle) {
		node.setStartingAngle(startingAngle);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ProgressSpinnerBuilder setRanges1(NumberRange<Double>... ranges) {
		node.getRanges1().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ProgressSpinnerBuilder setRanges2(NumberRange<Double>... ranges) {
		node.getRanges2().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ProgressSpinnerBuilder setRanges3(NumberRange<Double>... ranges) {
		node.getRanges3().setAll(ranges);
		return this;
	}
}
