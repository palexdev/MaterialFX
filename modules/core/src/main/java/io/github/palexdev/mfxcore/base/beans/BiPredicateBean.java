/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.base.beans;

import java.util.function.BiPredicate;

/// A simple immutable bean that wraps a [BiPredicate] and a String that represents the name for the predicate.
///
/// Note that the [#toString()] method has been overridden to return the given name.
///
/// @param <T> the type of the first predicate's argument
/// @param <U> the type of the second predicate's argument
public record BiPredicateBean<T, U>(String name, BiPredicate<T, U> predicate) {

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String toString() {
        return name;
    }
}
