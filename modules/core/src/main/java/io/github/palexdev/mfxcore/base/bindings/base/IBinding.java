/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.bindings.base;

import io.github.palexdev.mfxcore.base.bindings.MFXBindings;
import io.github.palexdev.mfxcore.base.bindings.Target;
import io.github.palexdev.mfxcore.enums.BindingState;
import javafx.beans.value.ObservableValue;

/**
 * Public APIs for both unidirectional and bidirectional bindings.
 *
 * @param <T> the type of the target's observable
 */
public interface IBinding<T> {

	/**
	 * This is the method responsible for "activating" the binding.
	 */
	IBinding<T> get();

	/**
	 * Invalidates the binding by forcing the source to update the target
	 * with its current value as both old and new values.
	 */
	IBinding<T> invalidate();

	/**
	 * Invalidates the binding by forcing the target to update the sources
	 * with its current value as both the old and new values.
	 * <p></p>
	 * Unidirectional bindings will throw an {@link UnsupportedOperationException}.
	 */
	IBinding<T> invalidateSource();

	/**
	 * This method is responsible for "deactivating" this binding.
	 * <p>
	 * Specific operations may vary according to the binding type.
	 */
	IBinding<T> unbind();

	/**
	 * This method is responsible for "deactivating" this binding, but unlike {@link #unbind()}
	 * that still lets you re-set/re-use the binding, this will stop you from re-activating it.
	 */
	void dispose();

	/**
	 * @return the {@link Target} object responsible for keeping the binding's target
	 * {@link ObservableValue} and other useful info.
	 */
	Target<T> getTarget();

	/**
	 * @return the state of the binding, see {@link BindingState}
	 */
	BindingState state();

	/**
	 * Checks if the state of the binding is {@link BindingState#DISPOSED}.
	 */
	default boolean isDisposed() {
		return state() == BindingState.DISPOSED;
	}

	// TODO note that this checks only the state not MFXBindings

	/**
	 * Checks if the bindings state is {@link BindingState#BOUND}.
	 * <p>
	 * <b>Note</b> that this will only check the binding's state and not the target.
	 * A full check should also consider calling {@link MFXBindings#isBound(Target)}.
	 */
	default boolean mayBeBound() {
		return state() == BindingState.BOUND;
	}
}
