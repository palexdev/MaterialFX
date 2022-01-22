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

import io.github.palexdev.materialfx.bindings.base.AbstractBindingHelper;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

/**
 * Binding helper for unidirectional bindings.
 * <p></p>
 * Bindings are syntactical sugar, because basically it's a listener attached to an observable value
 * which acts as a 'source', when it changes the target is updated.
 * <p></p>
 * The issue though is that this 'syntactical sugar' mechanism is managed by JavaFX, some functionalities are
 * private, unchangeable. For example, JavaFX properties are not settable when they are bound because an internal
 * flag doesn't allow it. To replicate this behavior the only way is to override the property's 'isBound' method
 * to use the {@link BindingManager}, an example:
 * <p></p>
 * <pre>
 * {@code
 *         BindingManager bindingManager = BindingManager.instance();
 *         IntegerProperty property = new SimpleIntegerProperty() {
 *             @Override
 *             public boolean isBound() {
 *                 return bindingManager.isBound(this) && !bindingManager.isIgnoreBinding(this);
 *             }
 *         };
 *         IntegerProperty source = new SimpleIntegerProperty();
 *
 *         bindingManager.bind(property).to(source).create();
 *         source.set(8);
 * }
 * </pre>
 * <p></p>
 * The 'ignoreBinding' flag is necessary because when the updateTarget is triggered we must tell
 * the property to allow the modification because it is caused by the binding helper.
 *
 * @param <T> the properties' value type
 */
public class BindingHelper<T> extends AbstractBindingHelper<T> {
	//================================================================================
	// Properties
	//================================================================================
	private ObservableValue<? extends T> source;
	protected boolean ignoreBinding;

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the target to the specified one.
	 */
	public BindingHelper<T> bind(ObservableValue<? extends T> target) {
		this.target = target;
		return this;
	}

	/**
	 * Sets the targetUpdater {@link BiConsumer}.
	 */
	@Override
	public BindingHelper<T> with(BiConsumer<T, T> targetUpdater) {
		this.targetUpdater = targetUpdater;
		return this;
	}

	/**
	 * Sets the binding source to the given one.
	 * <p>
	 * Also calls {@link #beforeBind()} and {@link #afterBind()}.
	 */
	public BindingHelper<T> to(ObservableValue<? extends T> source) {
		this.source = source;
		beforeBind();
		source.addListener(sourceListener);
		afterBind();
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Sets the 'ignoreBinding' flag to true then calls the super method.
	 * <p>
	 * The whole process is wrapped in a try-finally block since it's as important that the flag
	 * is reset at the end.
	 */
	@Override
	protected void updateTarget(ObservableValue<? extends T> source, T oldValue, T newValue) {
		try {
			ignoreBinding = true;
			super.updateTarget(source, oldValue, newValue);
		} finally {
			ignoreBinding = false;
		}
	}

	/**
	 * Causes the target to update with the current source's value.
	 * <p></p>
	 * This is necessary to 'simulate' the JavaFX's eager evaluation of bindings.
	 */
	public void invalidate() {
		T value = source.getValue();
		updateTarget(source, value, value);
	}

	/**
	 * Removes the sourceListener from the source, then
	 * sets the source to null.
	 * <p>
	 * This means that the helper won't be usable anymore until {@link #to(ObservableValue)} is called again.
	 * <p></p>
	 * Also calls {@link #beforeUnbind()}, {@link #afterUnbind()}.
	 */
	public void unbind() {
		beforeUnbind();
		source.removeListener(sourceListener);
		source = null;
		afterUnbind();
	}

	/**
	 * Calls {@link #unbind()} and in addition to that
	 * also the target is set to null.
	 * <p>
	 * This means that the helper won't be usable anymore until {@link #bind(ObservableValue)} and
	 * {@link #to(ObservableValue)} are called again.
	 */
	public void dispose() {
		unbind();
		target = null;
	}

	/**
	 * Asks the {@link BindingManager} to check if this helper's target
	 * is bound.
	 */
	public boolean isBound() {
		return BindingManager.instance().isBound(target);
	}

	/**
	 * Checks if the binding should be ignored.
	 */
	public boolean isIgnoreBinding() {
		return ignoreBinding;
	}

	/**
	 * Checks if the helper has been disposed before.
	 */
	@Override
	public boolean isDispose() {
		return target == null;
	}
}
