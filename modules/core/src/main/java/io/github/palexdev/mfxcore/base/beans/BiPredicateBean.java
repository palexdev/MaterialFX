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

package io.github.palexdev.mfxcore.base.beans;

import java.util.function.BiPredicate;

/**
 * A simple immutable bean that wraps a {@link BiPredicate} and a String that represents
 * the name for the predicate.
 * <p></p>
 * Note that the {@link #toString()} method has been overridden to return the given name.
 *
 * @param <T> the type of the first predicate's argument
 * @param <U> the type of the second predicate's argument
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
	public String getName() {
		return name;
	}

	public BiPredicate<T, U> getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return name;
	}
}
