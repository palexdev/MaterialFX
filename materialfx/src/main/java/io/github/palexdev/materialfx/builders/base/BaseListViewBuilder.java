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

package io.github.palexdev.materialfx.builders.base;

import io.github.palexdev.materialfx.controls.base.AbstractMFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class BaseListViewBuilder<T, C extends Cell<T>, L extends AbstractMFXListView<T, C>> extends ControlBuilder<L> {

	//================================================================================
	// Constructors
	//================================================================================
	public BaseListViewBuilder(L control) {
		super(control);
	}

	public static <T, C extends Cell<T>> BaseListViewBuilder<T, C, AbstractMFXListView<T, C>> baseList(AbstractMFXListView<T, C> list) {
		return new BaseListViewBuilder<>(list);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public BaseListViewBuilder<T, C, L> setTrackColor(Paint trackColor) {
		node.setTrackColor(trackColor);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setThumbColor(Paint thumbColor) {
		node.setThumbColor(thumbColor);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setThumbHoverColor(Paint thumbHoverColor) {
		node.setThumbHoverColor(thumbHoverColor);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setHideAfter(Duration hideAfter) {
		node.setHideAfter(hideAfter);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setItems(ObservableList<T> items) {
		node.setItems(items);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setConverter(StringConverter<T> converter) {
		node.setConverter(converter);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setHideScrollBars(boolean hideScrollBars) {
		node.setHideScrollBars(hideScrollBars);
		return this;
	}

	public BaseListViewBuilder<T, C, L> setDepthLevel(DepthLevel depthLevel) {
		node.setDepthLevel(depthLevel);
		return this;
	}
}
