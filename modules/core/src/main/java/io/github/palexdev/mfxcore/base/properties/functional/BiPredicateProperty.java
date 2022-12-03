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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.function.BiPredicate;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link BiPredicate}.
 *
 * @param <T> the predicate's first argument
 * @param <U> the predicate's second argument
 */
public class BiPredicateProperty<T, U> extends ReadOnlyObjectWrapper<BiPredicate<T, U>> {

	//================================================================================
	// Constructors
	//================================================================================
	public BiPredicateProperty() {
	}

	public BiPredicateProperty(BiPredicate<T, U> initialValue) {
		super(initialValue);
	}

	public BiPredicateProperty(Object bean, String name) {
		super(bean, name);
	}

	public BiPredicateProperty(Object bean, String name, BiPredicate<T, U> initialValue) {
		super(bean, name, initialValue);
	}
}
