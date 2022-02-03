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
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class TableBuilder<T, V extends MFXTableView<T>> extends ControlBuilder<V> {

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("unchecked")
	public TableBuilder() {
		this((V) new MFXTableView<T>());
	}

	public TableBuilder(V tableView) {
		super(tableView);
	}

	public static <T> TableBuilder<T, MFXTableView<T>> table() {
		return new TableBuilder<>();
	}

	public static <T> TableBuilder<T, MFXTableView<T>> table(MFXTableView<T> tableView) {
		return new TableBuilder<>(tableView);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public TableBuilder<T, V> autosizeColumnsOnInitialization() {
		node.autosizeColumnsOnInitialization();
		return this;
	}

	public TableBuilder<T, V> scrollBy(double pixels) {
		node.scrollBy(pixels);
		return this;
	}

	public TableBuilder<T, V> scrollTo(int index) {
		node.scrollTo(index);
		return this;
	}

	public TableBuilder<T, V> scrollToFirst() {
		node.scrollToFirst();
		return this;
	}

	public TableBuilder<T, V> scrollToLast() {
		node.scrollToLast();
		return this;
	}

	public TableBuilder<T, V> scrollToPixel(double pixel) {
		node.scrollToPixel(pixel);
		return this;
	}

	public TableBuilder<T, V> setHSpeed(double unit, double block) {
		node.setHSpeed(unit, block);
		return this;
	}

	public TableBuilder<T, V> setVSpeed(double unit, double block) {
		node.setVSpeed(unit, block);
		return this;
	}

	public TableBuilder<T, V> enableSmoothScrolling(double speed) {
		node.features().enableSmoothScrolling(speed);
		return this;
	}

	public TableBuilder<T, V> enableSmoothScrolling(double speed, double trackPadAdjustment) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment);
		return this;
	}

	public TableBuilder<T, V> enableSmoothScrolling(double speed, double trackPadAdjustment, double scrollThreshold) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment, scrollThreshold);
		return this;
	}

	public TableBuilder<T, V> enableBounceEffect() {
		node.features().enableBounceEffect();
		return this;
	}

	public TableBuilder<T, V> enableBounceEffect(double strength, double maxOverscroll) {
		node.features().enableBounceEffect(strength, maxOverscroll);
		return this;
	}

	public TableBuilder<T, V> setItems(ObservableList<T> items) {
		node.setItems(items);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TableBuilder<T, V> addColumns(MFXTableColumn<T>... columns) {
		node.getTableColumns().addAll(columns);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TableBuilder<T, V> setColumns(MFXTableColumn<T>... columns) {
		node.getTableColumns().setAll(columns);
		return this;
	}

	public TableBuilder<T, V> setTableRowFactory(Function<T, MFXTableRow<T>> tableRowFactory) {
		node.setTableRowFactory(tableRowFactory);
		return this;
	}

	public TableBuilder<T, V> setFilter(Predicate<T> filter) {
		node.getTransformableList().setPredicate(filter);
		return this;
	}

	public TableBuilder<T, V> setComparator(Comparator<T> comparator) {
		node.getTransformableList().setComparator(comparator);
		return this;
	}

	public TableBuilder<T, V> setComparator(Comparator<T> comparator, boolean isReverse) {
		node.getTransformableList().setComparator(comparator, isReverse);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TableBuilder<T, V> addFilters(AbstractFilter<T, ?>... filters) {
		node.getFilters().addAll(filters);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TableBuilder<T, V> setFilters(AbstractFilter<T, ?>... filters) {
		node.getFilters().setAll(filters);
		return this;
	}

	public TableBuilder<T, V> setFooterVisible(boolean footerVisible) {
		node.setFooterVisible(footerVisible);
		return this;
	}
}
