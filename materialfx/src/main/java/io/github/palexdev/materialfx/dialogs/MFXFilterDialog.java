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

package io.github.palexdev.materialfx.dialogs;

import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.function.Predicate;

/**
 * Dialog that shows a {@link MFXFilterPane} to produce a filter, {@link Predicate}.
 */
public class MFXFilterDialog<T> extends MFXGenericDialog {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-filter-dialog";
	private final MFXFilterPane<T> filterPane;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterDialog(MFXFilterPane<T> filterPane) {
		this.filterPane = filterPane;
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setHeaderText("");
		setHeaderIcon(null);
		buildContent();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void buildContent() {
		setContent(filterPane);
	}

	@Override
	protected void buildScrollableContent(boolean smoothScrolling) {
		buildContent();
	}

	@Override
	protected double computeMinWidth(double height) {
		return snappedLeftInset() + filterPane.prefWidth(-1) + snappedRightInset();
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public Predicate<T> filter() {
		return filterPane.filter();
	}

	public ObservableList<AbstractFilter<T, ?>> getFilters() {
		return filterPane.getFilters();
	}

	public ObservableList<FilterBean<T, ?>> getActiveFilters() {
		return filterPane.getActiveFilters();
	}

	public void setOnFilter(EventHandler<MouseEvent> onFilter) {
		filterPane.setOnFilter(onFilter);
	}

	public void setOnReset(EventHandler<MouseEvent> onReset) {
		filterPane.setOnReset(onReset);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXFilterPane<T> getFilterPane() {
		return filterPane;
	}
}
