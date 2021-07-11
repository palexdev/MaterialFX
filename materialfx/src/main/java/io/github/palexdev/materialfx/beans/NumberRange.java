/*
 * Copyright (C) 2021 Parisi Alessandro
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

/**
 * Simple bean to represent a range of values from min to max.
 *
 * @param <T> The type of Number to represent
 */
public class NumberRange<T extends Number> {
    //================================================================================
    // Properties
    //================================================================================
    private final T min;
    private final T max;

    //================================================================================
    // Constructors
    //================================================================================
    public NumberRange(T min, T max) {
        this.min = min;
        this.max = max;
    }

    //================================================================================
    // Methods
    //================================================================================
    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static <T extends Number> NumberRange<T> of(T min, T max) {
        return new NumberRange<>(min, max);
    }
}
