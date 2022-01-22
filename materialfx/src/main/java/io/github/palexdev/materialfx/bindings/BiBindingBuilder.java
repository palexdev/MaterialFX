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

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Helper class for {@link BiBindingManager}.
 * <p>
 * Makes the creation of bidirectional bindings easier with fluent methods.
 */
public class BiBindingBuilder<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObservableValue<? extends T> target;
	private BiConsumer<T, T> targetUpdater;
	private final BindingsMap<ObservableValue<? extends T>, BiConsumer<T, T>> sources = new BindingsMap<>();
	private BiBindingHelper<T> bindingHelper;
	private boolean eagerEvaluation = true;
	private boolean override = false;

	//================================================================================
	// Constructors
	//================================================================================
	public BiBindingBuilder(ObservableValue<? extends T> target) {
		this.target = target;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the {@link BiConsumer} function responsible for updating the target
	 * when the source changes.
	 */
	public BiBindingBuilder<T> with(BiConsumer<T, T> targetUpdater) {
		this.targetUpdater = targetUpdater;
		return this;
	}

	/**
	 * Specifies the {@link BiBindingHelper} that will be used for the binding.
	 * <p>
	 * This is optional as a default helper will be created when needed.
	 */
	public BiBindingBuilder<T> withHelper(BiBindingHelper<T> bindingHelper) {
		this.bindingHelper = bindingHelper;
		return this;
	}

	/**
	 * Calls {@link #to(ObservableValue, BiConsumer)} with the given property.
	 * Since it is a {@link Property} the {@link BiConsumer} will make use of {@link Property#setValue(Object)}.
	 */
	public BiBindingBuilder<T> to(Property<T> source) {
		return to(source, (oldValue, newValue) -> source.setValue(newValue));
	}

	/**
	 * Adds a new entry in the map which associates the given source to the given
	 * {@link BiConsumer} that is responsible for updating the source when needed.
	 */
	public BiBindingBuilder<T> to(ObservableValue<T> source, BiConsumer<T, T> sourceUpdater) {
		this.sources.put(source, sourceUpdater);
		return this;
	}

	/**
	 * Allows adding multiple entries to the map at once.
	 * Basically the same as {@link #to(ObservableValue, BiConsumer)}.
	 */
	@SafeVarargs
	public final BiBindingBuilder<T> to(Map.Entry<ObservableValue<? extends T>, BiConsumer<T, T>>... sources) {
		this.sources.putAll(sources);
		return this;
	}

	/**
	 * Disables the binding eager evaluation mechanism.
	 * At the time of the binding the values won't be updated automatically.
	 */
	public BiBindingBuilder<T> lazy() {
		this.eagerEvaluation = false;
		return this;
	}

	/**
	 * If a binding for the specified target is already present specifies
	 * if this builder's targetUpdater must replace the existing one.
	 */
	public BiBindingBuilder<T> override(boolean override) {
		this.override = override;
		return this;
	}

	/**
	 * @return the target observable
	 */
	public ObservableValue<? extends T> target() {
		return target;
	}

	/**
	 * @return the target updater
	 */
	public BiConsumer<T, T> targetUpdater() {
		return targetUpdater;
	}

	/**
	 * @return the map containing the sources and their updater
	 */
	public BindingsMap<ObservableValue<? extends T>, BiConsumer<T, T>> getSources() {
		return sources;
	}

	/**
	 * @return if the bindings uses eager evaluation or it's lazy
	 */
	public boolean isEagerEvaluation() {
		return eagerEvaluation;
	}

	/**
	 * Confirms the creation of the binding by calling {@link BiBindingManager#apply(BiBindingBuilder, BiBindingHelper, boolean)}.
	 */
	public BiBindingManager create() {
		return BiBindingManager.instance().apply(this, bindingHelper, override);
	}
}
