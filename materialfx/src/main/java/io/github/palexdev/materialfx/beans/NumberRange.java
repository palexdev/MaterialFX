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

import java.util.List;

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

    /**
     * Returns a new instance of NumberRange with the given min and max bounds.
     */
    public static <T extends Number> NumberRange<T> of(T min, T max) {
        return new NumberRange<>(min, max);
    }

    /**
     * Returns a new instance of NumberRange with the given val as both min and max bounds.
     */
    public static <T extends Number> NumberRange<T> of(T val) {
        return new NumberRange<>(val, val);
    }

    /**
     * Checks if the given value is contained in the given range (bounds are included).
     */
    public static boolean inRangeOf(double val, NumberRange<Double> range) {
        return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
    }

    /**
     * Checks if the given value is contained in the given range (bounds are included).
     */
    public static boolean inRangeOf(float val, NumberRange<Float> range) {
        return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
    }

    /**
     * Checks if the given value is contained in the given range (bounds are included).
     */
    public static boolean inRangeOf(int val, NumberRange<Integer> range) {
        return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
    }

    /**
     * Checks if the given value is contained in the given range (bounds are included).
     */
    public static boolean inRangeOf(long val, NumberRange<Long> range) {
        return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
    }

    /**
     * Checks if the given value is contained in any of the given ranges (bounds are included).
     */
    public static boolean inRangeOf(double val, List<NumberRange<Double>> ranges) {
        return ranges.stream().anyMatch(range -> inRangeOf(val, range));
    }

    /**
     * Checks if the given value is contained in any of the given ranges (bounds are included).
     */
    public static boolean inRangeOf(float val, List<NumberRange<Float>> ranges) {
        return ranges.stream().anyMatch(range -> inRangeOf(val, range));
    }

    /**
     * Checks if the given value is contained in any of the given ranges (bounds are included).
     */
    public static boolean inRangeOf(int val, List<NumberRange<Integer>> ranges) {
        return ranges.stream().anyMatch(range -> inRangeOf(val, range));
    }

    /**
     * Checks if the given value is contained in any of the given ranges (bounds are included).
     */
    public static boolean inRangeOf(long val, List<NumberRange<Long>> ranges) {
        return ranges.stream().anyMatch(range -> inRangeOf(val, range));
    }
}
