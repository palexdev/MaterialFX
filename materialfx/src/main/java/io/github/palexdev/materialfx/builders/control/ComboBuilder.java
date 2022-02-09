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

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.animation.Animation;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.StringConverter;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComboBuilder<T, C extends MFXComboBox<T>> extends TextFieldBuilder<C> {

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("unchecked")
	public ComboBuilder() {
		this((C) new MFXComboBox<T>());
	}

	public ComboBuilder(C comboBox) {
		super(comboBox);
	}

	public static <T> ComboBuilder<T, MFXComboBox<T>> combo() {
		return new ComboBuilder<>();
	}

	public static <T> ComboBuilder<T, MFXComboBox<T>> combo(MFXComboBox<T> comboBox) {
		return new ComboBuilder<>(comboBox);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ComboBuilder<T, C> selectFirst() {
		node.selectFirst();
		return this;
	}

	public ComboBuilder<T, C> selectNext() {
		node.selectNext();
		return this;
	}

	public ComboBuilder<T, C> selectPrevious() {
		node.selectPrevious();
		return this;
	}

	public ComboBuilder<T, C> selectLast() {
		node.selectLast();
		return this;
	}

	public ComboBuilder<T, C> clearSelection() {
		node.clearSelection();
		return this;
	}

	public ComboBuilder<T, C> selectIndex(int index) {
		node.selectIndex(index);
		return this;
	}

	public ComboBuilder<T, C> selectItem(T item) {
		node.selectItem(item);
		return this;
	}

	public ComboBuilder<T, C> setScrollOnOpen(boolean scrollOnOpen) {
		node.setScrollOnOpen(scrollOnOpen);
		return this;
	}

	public ComboBuilder<T, C> setPopupAlignment(Alignment popupAlignment) {
		node.setPopupAlignment(popupAlignment);
		return this;
	}

	public ComboBuilder<T, C> setPopupOffsetX(double popupOffsetX) {
		node.setPopupOffsetX(popupOffsetX);
		return this;
	}

	public ComboBuilder<T, C> setPopupOffsetY(double popupOffsetY) {
		node.setPopupOffsetY(popupOffsetY);
		return this;
	}

	public ComboBuilder<T, C> setAnimationProvider(BiFunction<Node, Boolean, Animation> animationProvider) {
		node.setAnimationProvider(animationProvider);
		return this;
	}

	public ComboBuilder<T, C> setValue(T value) {
		node.setValue(value);
		return this;
	}

	public ComboBuilder<T, C> setConverter(StringConverter<T> converter) {
		node.setConverter(converter);
		return this;
	}

	public ComboBuilder<T, C> setOnCommit(Consumer<String> onCommit) {
		node.setOnCommit(onCommit);
		return this;
	}

	public ComboBuilder<T, C> setOnCancel(Consumer<String> onCancel) {
		node.setOnCancel(onCancel);
		return this;
	}

	public ComboBuilder<T, C> setItems(ObservableList<T> items) {
		node.setItems(items);
		return this;
	}

	public ComboBuilder<T, C> setCellFactory(Function<T, Cell<T>> cellFactory) {
		node.setCellFactory(cellFactory);
		return this;
	}

	public ComboBuilder<T, C> setOnShowing(EventHandler<Event> onShowing) {
		node.setOnShowing(onShowing);
		return this;
	}

	public ComboBuilder<T, C> setOnShown(EventHandler<Event> onShown) {
		node.setOnShown(onShown);
		return this;
	}

	public ComboBuilder<T, C> setOnHiding(EventHandler<Event> onHiding) {
		node.setOnHiding(onHiding);
		return this;
	}

	public ComboBuilder<T, C> setOnHidden(EventHandler<Event> onHidden) {
		node.setOnHidden(onHidden);
		return this;
	}
}
