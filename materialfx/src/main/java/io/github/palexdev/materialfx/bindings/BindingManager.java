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

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.util.WeakHashMap;
import java.util.function.BiConsumer;

/**
 * This singleton class manages unidirectional bindings.
 * <p></p>
 * All bindings are stored in a map which associates the target property to a {@link BindingHelper}.
 * For unidirectional bindings a given target property will always have one and only helper at any time.
 * <p></p>
 * When a binding is not necessary anymore it's opportune to dispose it either with {@link #dispose()} or
 * {@link #unbind(ObservableValue)}.
 * <p></p>
 * This mechanism is so flexible that it also allows to bind read-only properties on the sole condition that
 * you must have a way to set that property and it must be specified with {@link BindingBuilder#with(BiConsumer)}.
 * <p>
 * For these properties be careful during the unbind as in JavaFX the getReadOnlyProperty() method returns
 * a completely different instance. For this reason the manager also offers unbind methods to unbind read-only wrappers.
 * <p></p>
 * To make the bindings building more readable it uses {@link BindingBuilder}.
 */
@SuppressWarnings("rawtypes")
public class BindingManager {
	//================================================================================
	// Singleton
	//================================================================================
	private static final BindingManager instance = new BindingManager();

	/**
	 * Retrieves the instance of the BindingManager.
	 */
	public static BindingManager instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final WeakHashMap<ObservableValue, BindingHelper> bindings = new WeakHashMap<>();

	//================================================================================
	// Constructors
	//================================================================================
	private BindingManager() {}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Creates and returns a new {@link BindingBuilder} with the given target.
	 */
	public <T> BindingBuilder<T> bind(ObservableValue<? extends T> target) {
		return new BindingBuilder<>(target);
	}

	/**
	 * Creates a new {@link BindingBuilder} with the given target.
	 * <p>
	 * The difference with the above method is that it accepts a {@link Property},
	 * which means that it's not necessary to specify how to update the target,
	 * by default it uses {@link Property#setValue(Object)}.
	 */
	public <T> BindingBuilder<T> bind(Property<T> target) {
		return new BindingBuilder<>(target).with((oldValue, newValue) -> target.setValue(newValue));
	}

	/**
	 * Creates a unidirectional binding with the given arguments.
	 * <p></p>
	 * The {@link BindingBuilder} specifies the target, the targetUpdater and the source.
	 * <p>
	 * Calls {@link #biBindingCheck(ObservableValue)} on the target before proceeding.
	 * <p>
	 * If the target is already bound, {@link #unbind(ObservableValue)} is called on the target before
	 * proceeding.
	 * <p>
	 * At this point puts a new entry in the bindings map with the target and the specified {@link BindingHelper}.
	 * Then the actual bind happens by calling the binding helper methods.
	 * <p></p>
	 * If using {@link BindingBuilder#create()} it's not necessary to call this method too!!
	 */
	public <T> BindingManager apply(BindingBuilder<T> bindingBuilder, BindingHelper<T> bindingHelper) {
		ObservableValue<? extends T> target = bindingBuilder.target();
		biBindingCheck(target);

		if (bindings.containsKey(target)) {
			unbind(target);
		}
		bindings.put(target, bindingHelper);
		bindingHelper.bind(target).to(bindingBuilder.source());
		return this;
	}

	/**
	 * If the given target is in the map, removes it and calls {@link BindingHelper#unbind()}.
	 */
	public <T> BindingManager unbind(ObservableValue<T> target) {
		BindingHelper bindingHelper = bindings.remove(target);
		if (bindingHelper != null) {
			bindingHelper.unbind();
		}
		return this;
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyBooleanWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyBooleanWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyStringWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyStringWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyIntegerWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyIntegerWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyLongWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyLongWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyFloatWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyFloatWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyDoubleWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public BindingManager unbindReadOnly(ReadOnlyDoubleWrapper target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyObjectWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue)} on it.
	 */
	public <T> BindingManager unbindReadOnly(ReadOnlyObjectWrapper<T> target) {
		return unbind(target.getReadOnlyProperty());
	}

	/**
	 * Unbinds every property stored in the manager and then clears the bindings map.
	 */
	public void dispose() {
		bindings.values().forEach(BindingHelper::unbind);
		bindings.clear();
	}

	/**
	 * The number of bindings in the map.
	 */
	public int size() {
		return bindings.size();
	}

	/**
	 * Checks if {@link #size()} is 0;
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Checks if there's an existing binding for the given target and the
	 * associated {@link BindingHelper}'s ignoreBinding state.
	 */
	public boolean isIgnoreBinding(ObservableValue target) {
		BindingHelper<?> bindingHelper = bindings.get(target);
		return bindingHelper != null && bindingHelper.isIgnoreBinding();
	}

	/**
	 * Checks if the bindings map contains the given target.
	 */
	public boolean isBound(ObservableValue target) {
		return bindings.containsKey(target);
	}

	/**
	 * Checks if the given target is bound bidirectionally and disposes it!
	 */
	private <T> void biBindingCheck(ObservableValue<? extends T> target) {
		if (BiBindingManager.instance().isBound(target)) {
			BiBindingManager.instance().disposeFor(target);
		}
	}
}
