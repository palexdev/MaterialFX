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

package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;
import io.github.palexdev.mfxcore.enums.BindingType;
import javafx.beans.value.ObservableValue;

/**
 * A simple bean to represent the target for any type of {@link IBinding}.
 * <p>
 * This contains the {@link ObservableValue} which will be the target of the binding (the target is the one that
 * will be updated by the binding' sources) and other useful information that are shared across the binding instance and
 * the sources if needed.
 *
 * @param <T> the observable's type
 */
public class Target<T> {
	//================================================================================
	// Properties
	//================================================================================
	private ObservableValue<? extends T> observable;
	protected boolean fromSource;
	protected boolean ignoreBinding;
	protected BindingType bindingType;

	//================================================================================
	// Constructors
	//================================================================================
	public Target(ObservableValue<? extends T> observable) {
		assert observable != null;
		this.observable = observable;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Disposes this target by setting tha observable to null.
	 */
	protected void dispose() {
		observable = null;
		fromSource = false;
	}

	/**
	 * @return whether the update comes from a change of the target that is propagating to the sources
	 */
	public boolean isFromSource() {
		return fromSource;
	}

	/**
	 * @return whether the update should be done anyway, regardless the binding status
	 */
	public boolean isIgnoreBinding() {
		return ignoreBinding;
	}

	/**
	 * @return the type of binding shared between the {@link IBinding} and its sources
	 */
	public BindingType bindingType() {
		return bindingType;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Shortcut for {@code getObservable().getValue}.
	 */
	public T getValue() {
		return observable.getValue();
	}

	/**
	 * @return the target's observable
	 */
	public ObservableValue<? extends T> getObservable() {
		return observable;
	}
}
