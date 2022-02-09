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
import io.github.palexdev.materialfx.controls.MFXProgressBar;

public class ProgressBarBuilder extends BaseProgressBuilder<MFXProgressBar> {

	//================================================================================
	// Constructors
	//================================================================================
	public ProgressBarBuilder() {
		this(new MFXProgressBar());
	}

	public ProgressBarBuilder(MFXProgressBar progressBar) {
		super(progressBar);
	}

	public static ProgressBarBuilder progressBar() {
		return new ProgressBarBuilder();
	}

	public static ProgressBarBuilder progressBar(MFXProgressBar progressIndicator) {
		return new ProgressBarBuilder(progressIndicator);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	@SuppressWarnings("unchecked")
	public ProgressBarBuilder setRanges1(NumberRange<Double>... ranges) {
		node.getRanges1().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ProgressBarBuilder setRanges2(NumberRange<Double>... ranges) {
		node.getRanges2().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ProgressBarBuilder setRanges3(NumberRange<Double>... ranges) {
		node.getRanges3().setAll(ranges);
		return this;
	}

	public ProgressBarBuilder setAnimationSpeed(double animationSpeed) {
		node.setAnimationSpeed(animationSpeed);
		return this;
	}
}
