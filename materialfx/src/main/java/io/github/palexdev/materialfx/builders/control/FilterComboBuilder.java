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

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class FilterComboBuilder<T> extends ComboBuilder<T, MFXFilterComboBox<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public FilterComboBuilder() {
		this(new MFXFilterComboBox<>());
	}

	public FilterComboBuilder(MFXFilterComboBox<T> comboBox) {
		super(comboBox);
	}

	public static <T> FilterComboBuilder<T> filterCombo() {
		return new FilterComboBuilder<>();
	}

	public static <T> FilterComboBuilder<T> filterCombo(MFXFilterComboBox<T> comboBox) {
		return new FilterComboBuilder<>(comboBox);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public FilterComboBuilder<T> setSearchText(String searchText) {
		node.setSearchText(searchText);
		return this;
	}

	public FilterComboBuilder<T> setFilterFunction(Function<String, Predicate<T>> filterFunction) {
		node.setFilterFunction(filterFunction);
		return this;
	}

	public FilterComboBuilder<T> setResetOnPopupHidden(boolean resetOnPopupHidden) {
		node.setResetOnPopupHidden(resetOnPopupHidden);
		return this;
	}
	
	public FilterComboBuilder<T> setFilter(Predicate<T> filter) {
		node.getFilterList().setPredicate(filter);
		return this;
	}
	
	public FilterComboBuilder<T> setComparator(Comparator<T> comparator) {
		node.getFilterList().setComparator(comparator);
		return this;
	}

	public FilterComboBuilder<T> setComparator(Comparator<T> comparator, boolean isReverse) {
		node.getFilterList().setComparator(comparator, isReverse);
		return this;
	}
}
