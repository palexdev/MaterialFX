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

package io.github.palexdev.materialfx.bindings;

import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

/**
 * Helper class for the {@link BindingManager}.
 * <p>
 * Makes the creation of unidirectional bindings easier with fluent methods.
 */
public class BindingBuilder<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObservableValue<? extends T> target;
	private ObservableValue<? extends T> source;
	private BiConsumer<T, T> updater;

	//================================================================================
	// Constructors
	//================================================================================
	public BindingBuilder(ObservableValue<? extends T> target) {
		this.target = target;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the binding's source.
	 */
	public BindingBuilder<T> to(ObservableValue<? extends T> source) {
		this.source = source;
		return this;
	}

	/**
	 * Sets the {@link BiConsumer} function responsible for updating the target
	 * when the source changes.
	 */
	public BindingBuilder<T> with(BiConsumer<T, T> updater) {
		this.updater = updater;
		return this;
	}

	/**
	 * @return the target observable
	 */
	public ObservableValue<? extends T> target() {
		return target;
	}

	/**
	 * @return the source observable
	 */
	public ObservableValue<? extends T> source() {
		return source;
	}

	/**
	 * @return the target updater
	 */
	public BiConsumer<T, T> targetUpdater() {
		return updater;
	}

	/**
	 * Confirms the creation of the binding by calling {@link BindingManager#apply(BindingBuilder, BindingHelper)}.
	 */
	public BindingManager create() {
		BindingHelper<T> bindingHelper = new BindingHelper<>();
		bindingHelper.with(updater);
		return BindingManager.instance().apply(this, bindingHelper);
	}
}
