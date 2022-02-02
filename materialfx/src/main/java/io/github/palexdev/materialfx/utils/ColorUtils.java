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

import javafx.scene.paint.*;

/**
 * Utils class for JavaFX's {@code Colors} and CSS.
 */
public class ColorUtils {

	private ColorUtils() {
	}

	/**
	 * Converts a JavaFX Paint object to the right CSS string.
	 * <p>
	 * Supports: {@link Color}, {@link LinearGradient}, {@link RadialGradient}.
	 */
	public static String toCss(Paint paint) {
		if (paint == null) return "";

		if (paint instanceof LinearGradient) {
			LinearGradient gradient = (LinearGradient) paint;
			return linearGradientToString(gradient);
		}

		if (paint instanceof RadialGradient) {
			RadialGradient gradient = (RadialGradient) paint;
			return radialGradientToString(gradient);
		}

		return rgb((Color) paint);
	}

	/**
	 * Converts a JavaFX's {@code Color} to CSS corresponding rgb function.
	 *
	 * @return the rgb function as a string
	 */
	public static String rgb(Color color) {
		if (color == null) return "";
		return String.format("rgb(%d, %d, %d)",
				(int) (255 * color.getRed()),
				(int) (255 * color.getGreen()),
				(int) (255 * color.getBlue()));
	}

	/**
	 * Converts a JavaFX's {@code Color} to CSS corresponding rgba function.
	 *
	 * @return the rgba function as a string
	 */
	public static String rgba(Color color) {
		if (color == null) return "";
		return String.format("rgba(%d, %d, %d, %s)",
				(int) (255 * color.getRed()),
				(int) (255 * color.getGreen()),
				(int) (255 * color.getBlue()),
				color.getOpacity());
	}

	/**
	 * Generates a random {@code Color} using java.util.Random.
	 */
	public static Color getRandomColor() {
		return Color.rgb(
				RandomUtils.random.nextInt(256),
				RandomUtils.random.nextInt(256),
				RandomUtils.random.nextInt(256)
		);
	}

	/**
	 * Util method to convert {@link LinearGradient} to a CSS string.
	 * <p></p>
	 * This is partly a copy of {@link LinearGradient#toString()} but {@code Stops} are correctly converted
	 * for CSS.
	 *
	 * @param gradient the linear gradient to convert
	 * @see Stop
	 */
	public static String linearGradientToString(LinearGradient gradient) {
		if (gradient == null) return "";
		final StringBuilder s = new StringBuilder("linear-gradient(from ")
				.append(lengthToString(gradient.getStartX(), gradient.isProportional()))
				.append(" ").append(lengthToString(gradient.getStartY(), gradient.isProportional()))
				.append(" to ").append(lengthToString(gradient.getEndX(), gradient.isProportional()))
				.append(" ").append(lengthToString(gradient.getEndY(), gradient.isProportional()))
				.append(", ");

		switch (gradient.getCycleMethod()) {
			case REFLECT:
				s.append("reflect").append(", ");
				break;
			case REPEAT:
				s.append("repeat").append(", ");
				break;
		}

		for (Stop stop : gradient.getStops()) {
			s.append(stopToString(stop)).append(", ");
		}

		s.delete(s.length() - 2, s.length());
		s.append(")");

		return s.toString();
	}

	/**
	 * Util method to convert {@link RadialGradient} to a CSS string.
	 * <p></p>
	 * This is partly a copy of {@link RadialGradient#toString()} but {@code Stops} are correctly converted
	 * for CSS.
	 *
	 * @param gradient the radial gradient to convert
	 * @see Stop
	 */
	public static String radialGradientToString(RadialGradient gradient) {
		if (gradient == null) return "";
		final StringBuilder s = new StringBuilder("radial-gradient(focus-angle ").append(gradient.getFocusAngle())
				.append("deg, focus-distance ").append(gradient.getFocusDistance() * 100)
				.append("% , center ").append(lengthToString(gradient.getCenterX(), gradient.isProportional()))
				.append(" ").append(lengthToString(gradient.getCenterY(), gradient.isProportional()))
				.append(", radius ").append(lengthToString(gradient.getRadius(), gradient.isProportional()))
				.append(", ");

		switch (gradient.getCycleMethod()) {
			case REFLECT:
				s.append("reflect").append(", ");
				break;
			case REPEAT:
				s.append("repeat").append(", ");
				break;
		}

		for (Stop stop : gradient.getStops()) {
			s.append(stopToString(stop)).append(", ");
		}

		s.delete(s.length() - 2, s.length());
		s.append(")");

		return s.toString();

	}

	/**
	 * Properly converts a {@link Stop} to string. Partly copied from
	 * {@link Stop#toString()} but the color is converted using {@link #rgba(Color)}.
	 */
	public static String stopToString(Stop stop) {
		return rgba(stop.getColor()) + " " + stop.getOffset() * 100 + "%";
	}

	/**
	 * Converts the given color to a String
	 * in hexadecimal format.
	 */
	public static String toWeb(Color color) {
		if (color == null) return "";
		String cs = color.toString();
		return "#" + cs.substring(2, cs.length() - 2);
	}

	/**
	 * Converts the given color to a String
	 * in hexadecimal format, also includes the color's opacity.
	 */
	public static String toWebAlpha(Color color) {
		if (color == null) return "";
		return "#" + color.toString().substring(2);
	}

	/**
	 * Converts the given color to a String
	 * in HSL format.
	 */
	public static String toHSL(Color color) {
		if (color == null) return "";

		double[] rgb = colorToArray(color);
		double r = rgb[0];
		double g = rgb[1];
		double b = rgb[2];

		double min = Math.min(r, Math.min(g, b));
		double max = Math.max(r, Math.max(g, b));

		// Hue
		double h = 0;
		if (max == min) {
			h = 0;
		} else if (max == r) {
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		} else if (max == g) {
			h = (60 * (b - r) / (max - min)) + 120;
		} else if (max == b) {
			h = (60 * (r - g) / (max - min)) + 240;
		}

		// Luminance
		double l = (max + min) / 2;

		// Saturation
		double s = 0;
		if (max == min) {
			s = 0;
		} else if (l <= 0.5f) {
			s = (max - min) / (max + min);
		} else {
			s = (max - min) / (2 - max - min);
		}

		return "hsl(" + Math.round(h) + "deg, " + Math.round(s * 100) + "%, " + Math.round(l * 100) + "%)";
	}

	/**
	 * Converts the given color to a String
	 * in HSB format.
	 */
	public static String toHSB(Color color) {
		if (color == null) return "";

		double[] rgb = colorToArray(color);
		double r = rgb[0];
		double g = rgb[1];
		double b = rgb[2];

		double max = Math.max(Math.max(r, g), b);
		double min = Math.min(Math.min(r, g), b);
		double delta = max - min;

		double brightness;
		double saturation;
		double hue;

		// Brightness
		brightness = max;

		// Saturation
		if (max != 0) {
			saturation = delta / max;
		} else {
			saturation = 0;
		}

		// Hue
		if (saturation == 0) {
			hue = 0;
		} else {
			double tmpR = (max - r) / delta;
			double tmpG = (max - g) / delta;
			double tmpB = (max - b) / delta;
			if (r == max)
				hue = tmpB - tmpG;
			else if (g == max)
				hue = 2.0 + tmpR - tmpB;
			else
				hue = 4.0 + tmpG - tmpR;
			hue = hue / 6.0;
			if (hue < 0)
				hue = hue + 1.0;
		}
		hue *= 360;

		return "hsb(" + Math.round(hue) + "deg, " + Math.round(saturation * 100) + "%, " + Math.round(brightness * 100) + "%)";
	}

	private static double[] colorToArray(Color color) {
		return new double[]{
				color.getRed(),
				color.getGreen(),
				color.getBlue()
		};
	}

	private static String lengthToString(double value, boolean proportional) {
		if (proportional) {
			return (value * 100) + "%";
		} else {
			return value + "px";
		}
	}

}
