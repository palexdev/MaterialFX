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
