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

import java.util.Objects;
import java.util.WeakHashMap;

/**
 * This singleton class manages bidirectional bindings.
 * <p></p>
 * All bindings are stored in a map which associates the target property to a {@link BiBindingHelper}.
 * For bidirectional bindings a given target property will always have one and only helper at any time.
 * The sources list is managed by that manager, when a new bind is made on the same target, a new {@link BiBindingHelper}
 * is created, which is the combination of the old one and the new one, {@link BiBindingHelper#newFor(BiBindingHelper, BiBindingHelper, boolean)} is used.
 * <p></p>
 * When a binding is not necessary anymore it's opportune to dispose it either with {@link #dispose()} or
 * {@link #unbind(ObservableValue, ObservableValue)} or {@link #clear(ObservableValue)}.
 * <p></p>
 * This mechanism is so flexible that it also allows to bind read-only properties on the sole condition that
 * you must have a way to set that property and it must be specified with {@link BiBindingBuilder}.
 * <p>
 * For these properties be careful during the unbind as in JavaFX the getReadOnlyProperty() method returns
 * a completely different instance. For this reason the manager also offers unbind methods to unbind read-only wrappers.
 * <p></p>
 * To make the bindings building more readable it uses {@link BiBindingBuilder}.
 */
@SuppressWarnings("unchecked, rawtypes")
public class BiBindingManager {
	//================================================================================
	// Singleton
	//================================================================================
	private static final BiBindingManager instance = new BiBindingManager();

	/**
	 * Retrieves the instance of the BiBindingManager
	 */
	public static BiBindingManager instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final WeakHashMap<ObservableValue, BiBindingHelper> bindings = new WeakHashMap<>();

	//================================================================================
	// Constructors
	//================================================================================
	private BiBindingManager() {}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Creates and returns a new {@link BiBindingBuilder} with the given target.
	 */
	public <T> BiBindingBuilder<T> bindBidirectional(ObservableValue<? extends T> target) {
		return new BiBindingBuilder<>(target);
	}

	/**
	 * Creates and returns a new {@link BiBindingBuilder} with the given target. Since it is a {@link Property}
	 * a default targetUpdater is also set by using {@link Property#setValue(Object)}.
	 */
	public <T> BiBindingBuilder<T> bindBidirectional(Property<T> target) {
		return new BiBindingBuilder<>(target).with((oldValue, newValue) -> target.setValue(newValue));
	}

	/**
	 * Creates a bidirectional binding with the given arguments.
	 * <p></p>
	 * The {@link BindingBuilder} specifies the target, the targetUpdater and the source.
	 * <p>
	 * Calls {@link #bindingCheck(ObservableValue)} on the target before proceeding.
	 * <p></p>
	 * The {@link BiBindingHelper} to use must be computed properly as there are several cases:
	 * <p> - The target is already bound to something: in this case a new helper is created by combining the
	 * existing one and the given one using {@link BiBindingHelper#newFor(BiBindingHelper, BiBindingHelper, boolean)}
	 * <p> - The given helper is null or equal to the existing one: in this case the existing one is used
	 * and all the sources of the given one are added to the existing one
	 * <p> The target is not already bound: if the given helper is null a new one is created
	 * otherwise the given one is used.
	 * <p></p>
	 * If the given {@link BiBindingBuilder}'s eager evaluation flag is true (by default it is)
	 * {@link BiBindingHelper#invalidate()} is called on the chosen helper.
	 * <p></p>
	 * At the end the entry, target-helper, in put into the map.
	 * <p></p>
	 * If using {@link BiBindingBuilder#create()} it's not necessary to call this method too!!
	 */
	public <T> BiBindingManager apply(BiBindingBuilder<T> bindingBuilder, BiBindingHelper<T> bindingHelper, boolean override) {
		ObservableValue<? extends T> target = bindingBuilder.target();
		bindingCheck(target);

		BiBindingHelper<T> helper;
		if (bindings.containsKey(target)) {
			BiBindingHelper<T> existing = bindings.get(target);
			if (bindingHelper != null && existing != bindingHelper) {
				helper = BiBindingHelper.newFor(existing, bindingHelper, override);
				helper.bind(bindingBuilder.target());
			} else {
				helper = existing;
				helper.addSources(bindingBuilder.getSources());
			}
		} else {
			helper = Objects.requireNonNullElseGet(bindingHelper, BiBindingHelper::new);
			helper.bind(target).with(bindingBuilder.targetUpdater()).addSources(bindingBuilder.getSources());
		}

		if (bindingBuilder.isEagerEvaluation()) {
			helper.invalidate();
		}
		bindings.put(bindingBuilder.target(), helper);

		return this;
	}

	/**
	 * Removes the given source from the given target's binding.
	 * <p></p>
	 * If by removing the source, the helper becomes empty, the target is removed
	 * from the bindings map.
	 */
	public <T> BiBindingManager unbind(ObservableValue<? extends T> target, ObservableValue<? extends T> source) {
		BiBindingHelper<T> bindingHelper = bindings.get(target);
		if (bindingHelper != null) {
			bindingHelper.unbind(source);
			if (bindingHelper.size() == 0) {
				bindings.remove(target);
			}
		}
		return this;
	}

	/**
	 * Calls {@link BiBindingHelper#clear()} for the given target (if existing in tha map).
	 */
	public <T> BiBindingManager clear(ObservableValue<? extends T> target) {
		BiBindingHelper<T> bindingHelper = bindings.get(target);
		if (bindingHelper != null) {
			bindingHelper.clear();
			bindings.remove(target);
		}
		return this;
	}

	/**
	 * Calls {@link BiBindingHelper#dispose()} for the given target (if existing in tha map).
	 */
	public <T> BiBindingManager disposeFor(ObservableValue<? extends T> target) {
		BiBindingHelper<T> bindingHelper = bindings.get(target);
		if (bindingHelper != null) {
			bindingHelper.dispose();
			bindings.remove(target);
		}
		return this;
	}

	/**
	 * Disposes all the bindings and clears the map.
	 */
	public BiBindingManager dispose() {
		bindings.forEach((observable, helper) -> helper.dispose());
		bindings.clear();
		return this;
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyBooleanWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyBooleanWrapper target, ObservableValue<? extends Boolean> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyStringWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyStringWrapper target, ObservableValue<? extends String> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyIntegerWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyIntegerWrapper target, ObservableValue<? extends Integer> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyLongWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyLongWrapper target, ObservableValue<? extends Long> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyFloatWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyFloatWrapper target, ObservableValue<? extends Float> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyDoubleWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public BiBindingManager unbindReadOnly(ReadOnlyDoubleWrapper target, ObservableValue<? extends Double> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyObjectWrapper#getReadOnlyProperty()} and
	 * calls {@link #unbind(ObservableValue, ObservableValue)} on it.
	 */
	public <T> BiBindingManager unbindReadOnly(ReadOnlyObjectWrapper<T> target, ObservableValue<? extends T> source) {
		return unbind(target.getReadOnlyProperty(), source);
	}

	/**
	 * Checks if the bindings map contains the given target.
	 */
	public <T> boolean isBound(ObservableValue<? extends T> target) {
		return bindings.containsKey(target);
	}

	/**
	 * @return the number of bindings actually present in the map
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
	 * Checks if the given target is bound unidirectionally and unbinds it!
	 */
	private <T> void bindingCheck(ObservableValue<? extends T> target) {
		if (BindingManager.instance().isBound(target)) {
			BindingManager.instance().unbind(target);
		}
	}
}
