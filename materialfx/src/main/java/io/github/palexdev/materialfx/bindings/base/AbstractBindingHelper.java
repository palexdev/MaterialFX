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

package io.github.palexdev.materialfx.bindings.base;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

/**
 * Base class for binding helpers.
 * <p></p>
 * Specifies common properties such as:
 * <p> - The 'target', which is the property that will be updated when the source changes
 * <p> - The action to perform to update the target (also called targetUpdater), it's a {@link BiConsumer} that offers both the oldValue and the newValue
 * <p> - A listener called 'sourceListener', which is added to the source property and triggers the {@link #updateTarget(ObservableValue, Object, Object)}
 * <p></p>
 * Specifies common behaviors such as:
 * <p> - Abstract base methods to: 'bind' the target to the source, specify the targetUpdater, allow to 'invalidate' the
 * binding and cause computations specified by the helper subclass, specify how to 'dispose' the helper and check if it's been
 * disposed before
 * <p> - The method responsible for updating the target (triggered by the sourceListener)
 * <p> - The actions to perform: before/after the target update, before/after the binding, before/after the unbinding
 *
 * @param <T>
 */
public abstract class AbstractBindingHelper<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected ObservableValue<? extends T> target;
	protected BiConsumer<T, T> targetUpdater;
	protected final ChangeListener<? super T> sourceListener = this::updateTarget;

	//================================================================================
	// Abstract Properties
	//================================================================================
	public abstract AbstractBindingHelper<T> bind(ObservableValue<? extends T> target);

	public abstract AbstractBindingHelper<T> with(BiConsumer<T, T> targetUpdater);

	public abstract void invalidate();

	public abstract void dispose();

	public abstract boolean isDispose();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Invoked by the sourceListener, it's responsible for updating the specified target
	 * using the specified targetUpdater {@link BiConsumer}.
	 * <p>
	 * Also calls {@link #beforeUpdateTarget()} and {@link #afterUpdateTarget()}.
	 *
	 * @param source   the source property
	 * @param oldValue the source's oldValue
	 * @param newValue the source's newValue
	 */
	protected void updateTarget(ObservableValue<? extends T> source, T oldValue, T newValue) {
		beforeUpdateTarget();
		targetUpdater.accept(oldValue, newValue);
		afterUpdateTarget();
	}

	/**
	 * Empty by default.
	 */
	protected void beforeUpdateTarget() {}

	/**
	 * Empty by default.
	 */
	protected void afterUpdateTarget() {}

	/**
	 * Empty by default.
	 */
	protected void beforeBind() {}

	/**
	 * Empty by default.
	 */
	protected void afterBind() {}

	/**
	 * Empty by default.
	 */
	protected void beforeUnbind() {}

	/**
	 * Empty by default.
	 */
	protected void afterUnbind() {}
}
