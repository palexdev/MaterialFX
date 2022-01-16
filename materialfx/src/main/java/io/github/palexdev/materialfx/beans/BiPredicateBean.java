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

package io.github.palexdev.materialfx.beans;

import java.util.function.BiPredicate;

/**
 * A simple bean that wraps a {@link BiPredicate} and s String that represents
 * the name for the predicate.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument the predicate
 */
public class BiPredicateBean<T, U> {
	//================================================================================
	// Properties
	//================================================================================
	private final String name;
	private final BiPredicate<T, U> predicate;

	//================================================================================
	// Constructors
	//================================================================================
	public BiPredicateBean(String name, BiPredicate<T, U> predicate) {
		this.name = name;
		this.predicate = predicate;
	}

	//================================================================================
	// Getters
	//================================================================================
	public String name() {
		return name;
	}

	public BiPredicate<T, U> predicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return name;
	}
}
