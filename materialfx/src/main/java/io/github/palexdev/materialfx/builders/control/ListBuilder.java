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

import io.github.palexdev.materialfx.builders.base.BaseListViewBuilder;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;

import java.util.function.Function;

public class ListBuilder<T> extends BaseListViewBuilder<T, MFXListCell<T>, MFXListView<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public ListBuilder() {
		this(new MFXListView<>());
	}

	public ListBuilder(MFXListView<T> listView) {
		super(listView);
	}

	public static <T> ListBuilder<T> list() {
		return new ListBuilder<>();
	}

	public static <T> ListBuilder<T> list(MFXListView<T> listView) {
		return new ListBuilder<>(listView);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	
	public ListBuilder<T> scrollBy(double pixels) {
		node.scrollBy(pixels);
		return this;
	}

	public ListBuilder<T> scrollTo(int index) {
		node.scrollTo(index);
		return this;
	}

	public ListBuilder<T> scrollToFirst() {
		node.scrollToFirst();
		return this;
	}

	public ListBuilder<T> scrollToLast() {
		node.scrollToLast();
		return this;
	}

	public ListBuilder<T> scrollToPixel(double pixel) {
		node.scrollToPixel(pixel);
		return this;
	}

	public ListBuilder<T> setHSpeed(double unit, double block) {
		node.setHSpeed(unit, block);
		return this;
	}

	public ListBuilder<T> setVSpeed(double unit, double block) {
		node.setVSpeed(unit, block);
		return this;
	}

	public ListBuilder<T> setCellFactory(Function<T, MFXListCell<T>> cellFactory) {
		node.setCellFactory(cellFactory);
		return this;
	}

	public ListBuilder<T> enableSmoothScrolling(double speed) {
		node.features().enableSmoothScrolling(speed);
		return this;
	}

	public ListBuilder<T> enableSmoothScrolling(double speed, double trackPadAdjustment) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment);
		return this;
	}

	public ListBuilder<T> enableSmoothScrolling(double speed, double trackPadAdjustment, double scrollThreshold) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment, scrollThreshold);
		return this;
	}

	public ListBuilder<T> enableBounceEffect() {
		node.features().enableBounceEffect();
		return this;
	}

	public ListBuilder<T> enableBounceEffect(double strength, double maxOverscroll) {
		node.features().enableBounceEffect(strength, maxOverscroll);
		return this;
	}
}
