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

import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class FilterPaneBuilder<T> extends ControlBuilder<MFXFilterPane<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public FilterPaneBuilder() {
		this(new MFXFilterPane<>());
	}

	public FilterPaneBuilder(MFXFilterPane<T> filterPane) {
		super(filterPane);
	}

	public static <T> FilterPaneBuilder<T> filterPane() {
		return new FilterPaneBuilder<>();
	}

	public static <T> FilterPaneBuilder<T> filterPane(MFXFilterPane<T> filterPane) {
		return new FilterPaneBuilder<>(filterPane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public FilterPaneBuilder<T> setHeaderText(String headerText) {
		node.setHeaderText(headerText);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public FilterPaneBuilder<T> addFilters(AbstractFilter<T, ?>... filters) {
		node.getFilters().addAll(filters);
		return this;
	}

	@SuppressWarnings("unchecked")
	public FilterPaneBuilder<T> setFilters(AbstractFilter<T, ?>... filters) {
		node.getFilters().setAll(filters);
		return this;
	}

	@SuppressWarnings("unchecked")
	public FilterPaneBuilder<T> addActiveFilters(FilterBean<T, ?>... activeFilters) {
		node.getActiveFilters().addAll(activeFilters);
		return this;
	}

	@SuppressWarnings("unchecked")
	public FilterPaneBuilder<T> setActiveFilters(FilterBean<T, ?>... activeFilters) {
		node.getActiveFilters().setAll(activeFilters);
		return this;
	}

	public FilterPaneBuilder<T> setOnFilter(EventHandler<MouseEvent> onFilter) {
		node.setOnFilter(onFilter);
		return this;
	}

	public FilterPaneBuilder<T> setOnReset(EventHandler<MouseEvent> onReset) {
		node.setOnReset(onReset);
		return this;
	}
}
