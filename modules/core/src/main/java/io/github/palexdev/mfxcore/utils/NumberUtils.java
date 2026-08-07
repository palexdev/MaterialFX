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

package io.github.palexdev.mfxcore.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.github.palexdev.mfxcore.base.beans.range.DoubleRange;
import io.github.palexdev.mfxcore.base.beans.range.FloatRange;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.base.beans.range.LongRange;

/// Utils class for working with numbers.
public class NumberUtils {

    private NumberUtils() {}

    /// Limits the given value to the given min-max range by returning the nearest bound if it exceeds or val if it's in range.
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /// Limits the given value to the given min-max range by returning the nearest bound if it exceeds or val if it's in range.
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /// Limits the given value to the given min-max range by returning the nearest bound if it exceeds or val if it's in range.
    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    /// Limits the given value to the given min-max range by returning the nearest bound if it exceeds or val if it's in range.
    public static long clamp(long val, long min, long max) {
        return Math.max(min, Math.min(max, val));
    }

    private static double mapImpl(double value,
                                  double fromMin, double fromMax,
                                  double toMin, double toMax,
                                  int decimalPrecision) {
        double deltaA = fromMax - fromMin;
        double deltaB = toMax - toMin;

        if (deltaA == 0.0 || !Double.isFinite(deltaA) || !Double.isFinite(deltaB)) {
            return toMin; // degenerate range
        }

        double scale = deltaB / deltaA;
        double offset = (-fromMin * scale) + toMin;
        double result = (value * scale) + offset;

        if (!Double.isFinite(result)) {
            return toMin;
        }

        if (decimalPrecision >= 0) {
            double factor = Math.pow(10, decimalPrecision);
            result = Math.round(result * factor) / factor;
        }

        return result;
    }

    /// Maps a value from one range into another, with optional rounding.
    ///
    /// Example:
    /// ```
    /// mapOneRangeToAnother(0, new DoubleRange(-100, 100), new DoubleRange(0, 100), 2)
    /// // -> 50.0
    /// ```
    ///
    /// If the source range has zero width, or a non-finite result occurs, returns the target minimum.
    public static double mapOneRangeToAnother(double value, DoubleRange fromRange, DoubleRange toRange, int decimalPrecision) {
        return mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), decimalPrecision);
    }

    /// Same as [#mapOneRangeToAnother(double, DoubleRange, DoubleRange, int)] but for floats.
    public static float mapOneRangeToAnother(float value, FloatRange fromRange, FloatRange toRange, int decimalPrecision) {
        return (float) mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), decimalPrecision);
    }

    /// Same as [#mapOneRangeToAnother(double, DoubleRange, DoubleRange, int)] but for ints.
    public static int mapOneRangeToAnother(int value, IntegerRange fromRange, IntegerRange toRange, int decimalPrecision) {
        return (int) mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), decimalPrecision);
    }

    /// Same as [#mapOneRangeToAnother(double, DoubleRange, DoubleRange, int)] but for longs.
    public static long mapOneRangeToAnother(long value, LongRange fromRange, LongRange toRange, int decimalPrecision) {
        return Math.round(mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), decimalPrecision));
    }

    /// Maps a value from one range into another (double precision).
    public static double mapOneRangeToAnother(double value, DoubleRange fromRange, DoubleRange toRange) {
        return mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), -1);
    }

    /// Maps a value from one range into another (float precision).
    public static float mapOneRangeToAnother(float value, FloatRange fromRange, FloatRange toRange) {
        return (float) mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), -1);
    }

    /// Maps a value from one range into another (int precision).
    public static int mapOneRangeToAnother(int value, IntegerRange fromRange, IntegerRange toRange) {
        return (int) mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), -1);
    }

    /// Maps a value from one range into another (long precision).
    public static long mapOneRangeToAnother(long value, LongRange fromRange, LongRange toRange) {
        return Math.round(mapImpl(value, fromRange.getMin(), fromRange.getMax(),
            toRange.getMin(), toRange.getMax(), -1));
    }

    /// Given a certain value, finds the closest value in the given numbers list.
    public static double closestValueTo(double val, List<Double> list) {
        if (list.isEmpty()) {
            return 0.0;
        }

        double res = list.getFirst();
        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(val - res) >
                Math.abs(val - list.get(i))) {
                res = list.get(i);
            }
        }

        return res;
    }

    /// Given a certain value, finds the closest value in the given numbers list.
    public static float closestValueTo(float val, List<Float> list) {
        if (list.isEmpty()) {
            return 0;
        }

        float res = list.getFirst();
        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(val - res) >
                Math.abs(val - list.get(i))) {
                res = list.get(i);
            }
        }

        return res;
    }

    /// Given a certain value, finds the closest value in the given numbers list.
    public static int closestValueTo(int val, List<Integer> list) {
        if (list.isEmpty()) {
            return 0;
        }

        int res = list.getFirst();
        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(val - res) >
                Math.abs(val - list.get(i))) {
                res = list.get(i);
            }
        }

        return res;
    }

    /// Given a certain value, finds the closest value in the given numbers list.
    public static long closestValueTo(long val, List<Long> list) {
        if (list.isEmpty()) {
            return 0;
        }

        long res = list.getFirst();
        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(val - res) >
                Math.abs(val - list.get(i))) {
                res = list.get(i);
            }
        }

        return res;
    }

    /// Formats the given double value to have the given number of decimal places.
    public static double formatTo(double value, int decimalPrecision) {
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (double) Math.round(value * calcScale) / calcScale;
    }

    /// Returns the given value as a string the specified number of decimal places.
    @SuppressWarnings("MalformedFormatString")
    public static String formatToString(double value, int decimalPrecision) {
        return String.format("%." + decimalPrecision + "f", value);
    }

    /// Returns a random double between the specified min-max range.
    ///
    /// Uses [ThreadLocalRandom#nextDouble(double,double)].
    public static double getRandomDoubleBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /// Returns a random float value between 0 and 1.
    ///
    /// Uses [ThreadLocalRandom#nextFloat()]
    public static float getRandomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    /// Returns a random int value between the specified min-max range.
    ///
    /// Uses [ThreadLocalRandom#nextInt(int,int)].
    public static int getRandomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /// Returns a random long value between the specified min-max range.
    ///
    /// Uses [ThreadLocalRandom#nextLong(long,long)].
    public static long getRandomLongBetween(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    /// Checks if the given number is even or odd, just a convenience method for aesthetic.
    public static boolean isEven(int number) {
        return (number % 2 == 0);
    }
}
