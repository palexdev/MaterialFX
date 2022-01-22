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

package io.github.palexdev.materialfx.filter.base;

import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Extension of {@link AbstractFilter}, still abstract, limits the U parameter to {@link Number}s.
 */
public abstract class NumberFilter<T, U extends Number> extends AbstractFilter<T, U> {

	//================================================================================
	// Constructors
	//================================================================================
	public NumberFilter(String name, Function<T, U> extractor, StringConverter<U> converter) {
		super(name, extractor, converter);
	}
}
