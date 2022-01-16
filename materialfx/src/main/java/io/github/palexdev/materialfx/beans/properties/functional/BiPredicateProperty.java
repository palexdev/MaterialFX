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

package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.BiPredicate;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link BiPredicate}.
 *
 * @param <T> the predicate's first argument
 * @param <U> the predicate's second argument
 */
public class BiPredicateProperty<T, U> extends SimpleObjectProperty<BiPredicate<T, U>> {

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
