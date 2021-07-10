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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.NumberRange;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtils {

    private NumberUtils() {
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double mapOneRangeToAnother(double value, NumberRange<Double> fromRange, NumberRange<Double> toRange, int decimalPrecision) {
        double deltaA = fromRange.getMax() - fromRange.getMin();
        double deltaB = toRange.getMax() - toRange.getMin();
        double scale = deltaB / deltaA;
        double negA = -1 * fromRange.getMin();
        double offset = (negA * scale) + toRange.getMin();
        double finalNumber = (value * scale) + offset;
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (double) Math.round(finalNumber * calcScale) / calcScale;
    }

    public static double closestValueTo(double val, List<Double> list) {
        if (list.isEmpty()) {
            return 0.0;
        }

        double res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(val - res) >
                    Math.abs(val - list.get(i))) {
                res = list.get(i);
            }
        }

        return res;
    }

    public static double formatTo(double value, int decimalPrecision) {
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (double) Math.round(value * calcScale) / calcScale;
    }

    public static String formatToString(double value, int decimalPrecision) {
        return String.format("%." + decimalPrecision + "f", value);
    }

    public static double getRandomDoubleBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean isEven(int number) {
        return (number % 2 == 0);
    }
}
