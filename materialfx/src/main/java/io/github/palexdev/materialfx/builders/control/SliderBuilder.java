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
import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.enums.SliderEnums;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.function.Supplier;

public class SliderBuilder extends ControlBuilder<MFXSlider> {

	//================================================================================
	// Constructors
	//================================================================================
	public SliderBuilder() {
		this(new MFXSlider());
	}

	public SliderBuilder(MFXSlider control) {
		super(control);
	}

	public static SliderBuilder slider() {
		return new SliderBuilder();
	}

	public static SliderBuilder slider(MFXSlider slider) {
		return new SliderBuilder(slider);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public SliderBuilder setMin(double min) {
		node.setMin(min);
		return this;
	}

	public SliderBuilder setMax(double max) {
		node.setMax(max);
		return this;
	}

	public SliderBuilder setValue(double value) {
		node.setValue(value);
		return this;
	}

	public SliderBuilder setThumbSupplier(Supplier<Node> thumbSupplier) {
		node.setThumbSupplier(thumbSupplier);
		return this;
	}

	public SliderBuilder setPopupSupplier(Supplier<Region> popupSupplier) {
		node.setPopupSupplier(popupSupplier);
		return this;
	}

	public SliderBuilder setPopupPadding(double popupPadding) {
		node.setPopupPadding(popupPadding);
		return this;
	}

	public SliderBuilder setDecimalPrecision(int decimalPrecision) {
		node.setDecimalPrecision(decimalPrecision);
		return this;
	}

	public SliderBuilder setEnableKeyboard(boolean enableKeyboard) {
		node.setEnableKeyboard(enableKeyboard);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SliderBuilder setRanges1(NumberRange<Double>... ranges) {
		node.getRanges1().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SliderBuilder setRanges2(NumberRange<Double>... ranges) {
		node.getRanges2().setAll(ranges);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SliderBuilder setRanges3(NumberRange<Double>... ranges) {
		node.getRanges3().setAll(ranges);
		return this;
	}

	public SliderBuilder setSliderMode(SliderEnums.SliderMode sliderMode) {
		node.setSliderMode(sliderMode);
		return this;
	}

	public SliderBuilder setUnitIncrement(double unitIncrement) {
		node.setUnitIncrement(unitIncrement);
		return this;
	}

	public SliderBuilder setAlternativeUnitIncrement(double alternativeUnitIncrement) {
		node.setAlternativeUnitIncrement(alternativeUnitIncrement);
		return this;
	}

	public SliderBuilder setTickUnit(double tickUnit) {
		node.setTickUnit(tickUnit);
		return this;
	}

	public SliderBuilder setShowMajorTicks(boolean showMajorTicks) {
		node.setShowMajorTicks(showMajorTicks);
		return this;
	}

	public SliderBuilder setShowMinorTicks(boolean showMinorTicks) {
		node.setShowMinorTicks(showMinorTicks);
		return this;
	}

	public SliderBuilder setShowTicksAtEdges(boolean showTicksAtEdges) {
		node.setShowTicksAtEdges(showTicksAtEdges);
		return this;
	}

	public SliderBuilder setMinorTicksCount(int minorTicksCount) {
		node.setMinorTicksCount(minorTicksCount);
		return this;
	}

	public SliderBuilder setAnimateOnPress(boolean animateOnPress) {
		node.setAnimateOnPress(animateOnPress);
		return this;
	}

	public SliderBuilder setBidirectional(boolean bidirectional) {
		node.setBidirectional(bidirectional);
		return this;
	}

	public SliderBuilder setOrientation(Orientation orientation) {
		node.setOrientation(orientation);
		return this;
	}

	public SliderBuilder setPopupSide(SliderEnums.SliderPopupSide popupSide) {
		node.setPopupSide(popupSide);
		return this;
	}
}
