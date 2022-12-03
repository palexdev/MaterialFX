/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.properties.functional;

import io.github.palexdev.mfxcore.base.TriFunction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link TriFunction}.
 *
 * @param <T> the function's first argument
 * @param <U> the function's second argument
 * @param <V> the function's third argument
 * @param <R> the function's return type
 */
public class TriFunctionProperty<T, U, V, R> extends ReadOnlyObjectWrapper<TriFunction<T, U, V, R>> {

	//================================================================================
	// Constructors
	//================================================================================
	public TriFunctionProperty() {
	}

	public TriFunctionProperty(TriFunction<T, U, V, R> initialValue) {
		super(initialValue);
	}

	public TriFunctionProperty(Object bean, String name) {
		super(bean, name);
	}

	public TriFunctionProperty(Object bean, String name, TriFunction<T, U, V, R> initialValue) {
		super(bean, name, initialValue);
	}
}
