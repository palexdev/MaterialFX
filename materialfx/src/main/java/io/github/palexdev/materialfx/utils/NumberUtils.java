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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.NumberRange;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utils class for working with numbers.
 */
public class NumberUtils {

	private NumberUtils() {}

	/**
	 * Limits the given value to the given min-max range by returning the nearest bound
	 * if it exceeds or val if it's in range.
	 */
	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	/**
	 * Limits the given value to the given min-max range by returning the nearest bound
	 * if it exceeds or val if it's in range.
	 */
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	/**
	 * Limits the given value to the given min-max range by returning the nearest bound
	 * if it exceeds or val if it's in range.
	 */
	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	/**
	 * Limits the given value to the given min-max range by returning the nearest bound
	 * if it exceeds or val if it's in range.
	 */
	public static long clamp(long val, long min, long max) {
		return Math.max(min, Math.min(max, val));
	}

	/**
	 * Given a certain value, the range of possible values, and a different range, converts the given value
	 * from its range to the given second range.
	 * <p></p>
	 * For example let's say I have a value of 0 that can go from -100 to 100 and I want to convert the
	 * value to a range of 0 to 100, the converted value will be 50 (0 is at the middle in the -100-100 range, and
	 * 50 is at the middle in the 0-100 range).
	 */
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

	/**
	 * Given a certain value, the range of possible values, and a different range, converts the given value
	 * from its range to the given second range.
	 * <p></p>
	 * For example let's say I have a value of 0 that can go from -100 to 100 and I want to convert the
	 * value to a range of 0 to 100, the converted value will be 50 (0 is at the middle in the -100-100 range, and
	 * 50 is at the middle in the 0-100 range).
	 */
	public static float mapOneRangeToAnother(float value, NumberRange<Float> fromRange, NumberRange<Float> toRange, int decimalPrecision) {
		double deltaA = fromRange.getMax() - fromRange.getMin();
		double deltaB = toRange.getMax() - toRange.getMin();
		double scale = deltaB / deltaA;
		double negA = -1 * fromRange.getMin();
		double offset = (negA * scale) + toRange.getMin();
		double finalNumber = (value * scale) + offset;
		int calcScale = (int) Math.pow(10, decimalPrecision);
		return (float) Math.round(finalNumber * calcScale) / calcScale;
	}

	/**
	 * Given a certain value, the range of possible values, and a different range, converts the given value
	 * from its range to the given second range.
	 * <p></p>
	 * For example let's say I have a value of 0 that can go from -100 to 100 and I want to convert the
	 * value to a range of 0 to 100, the converted value will be 50 (0 is at the middle in the -100-100 range, and
	 * 50 is at the middle in the 0-100 range).
	 */
	public static int mapOneRangeToAnother(int value, NumberRange<Integer> fromRange, NumberRange<Integer> toRange, int decimalPrecision) {
		double deltaA = fromRange.getMax() - fromRange.getMin();
		double deltaB = toRange.getMax() - toRange.getMin();
		double scale = deltaB / deltaA;
		double negA = -1 * fromRange.getMin();
		double offset = (negA * scale) + toRange.getMin();
		double finalNumber = (value * scale) + offset;
		int calcScale = (int) Math.pow(10, decimalPrecision);
		return (int) Math.round(finalNumber * calcScale) / calcScale;
	}

	/**
	 * Given a certain value, the range of possible values, and a different range, converts the given value
	 * from its range to the given second range.
	 * <p></p>
	 * For example let's say I have a value of 0 that can go from -100 to 100 and I want to convert the
	 * value to a range of 0 to 100, the converted value will be 50 (0 is at the middle in the -100-100 range, and
	 * 50 is at the middle in the 0-100 range).
	 */
	public static long mapOneRangeToAnother(long value, NumberRange<Long> fromRange, NumberRange<Long> toRange, int decimalPrecision) {
		double deltaA = fromRange.getMax() - fromRange.getMin();
		double deltaB = toRange.getMax() - toRange.getMin();
		double scale = deltaB / deltaA;
		double negA = -1 * fromRange.getMin();
		double offset = (negA * scale) + toRange.getMin();
		double finalNumber = (value * scale) + offset;
		int calcScale = (int) Math.pow(10, decimalPrecision);
		return Math.round(finalNumber * calcScale) / calcScale;
	}

	/**
	 * Given a certain value, finds the closest value in the given numbers list.
	 */
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

	/**
	 * Given a certain value, finds the closest value in the given numbers list.
	 */
	public static float closestValueTo(float val, List<Float> list) {
		if (list.isEmpty()) {
			return 0;
		}

		float res = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			if (Math.abs(val - res) >
					Math.abs(val - list.get(i))) {
				res = list.get(i);
			}
		}

		return res;
	}

	/**
	 * Given a certain value, finds the closest value in the given numbers list.
	 */
	public static int closestValueTo(int val, List<Integer> list) {
		if (list.isEmpty()) {
			return 0;
		}

		int res = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			if (Math.abs(val - res) >
					Math.abs(val - list.get(i))) {
				res = list.get(i);
			}
		}

		return res;
	}

	/**
	 * Given a certain value, finds the closest value in the given numbers list.
	 */
	public static long closestValueTo(long val, List<Long> list) {
		if (list.isEmpty()) {
			return 0;
		}

		long res = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			if (Math.abs(val - res) >
					Math.abs(val - list.get(i))) {
				res = list.get(i);
			}
		}

		return res;
	}

	/**
	 * Formats the given double value to have the given number of decimal places.
	 */
	public static double formatTo(double value, int decimalPrecision) {
		int calcScale = (int) Math.pow(10, decimalPrecision);
		return (double) Math.round(value * calcScale) / calcScale;
	}

	/**
	 * Returns the given value as a string the specified number of decimal places.
	 */
	public static String formatToString(double value, int decimalPrecision) {
		return String.format("%." + decimalPrecision + "f", value);
	}

	/**
	 * Returns a random double between the specified min-max range.
	 * <p></p>
	 * Uses {@link ThreadLocalRandom#nextDouble(double, double)}.
	 */
	public static double getRandomDoubleBetween(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	/**
	 * Returns a random float value between 0 and 1.
	 * <p></p>
	 * Uses {@link ThreadLocalRandom#nextFloat()}
	 */
	public static float getRandomFloat() {
		return ThreadLocalRandom.current().nextFloat();
	}

	/**
	 * Returns a random int value between the specified min-max range.
	 * <p></p>
	 * Uses {@link ThreadLocalRandom#nextInt(int, int)}.
	 */
	public static int getRandomIntBetween(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	/**
	 * Returns a random long value between the specified min-max range.
	 * <p></p>
	 * Uses {@link ThreadLocalRandom#nextLong(long, long)}.
	 */
	public static long getRandomLongBetween(long min, long max) {
		return ThreadLocalRandom.current().nextLong(min, max);
	}

	/**
	 * Checks if the given number is even or odd, just a convenience method for aesthetic.
	 */
	public static boolean isEven(int number) {
		return (number % 2 == 0);
	}
}
