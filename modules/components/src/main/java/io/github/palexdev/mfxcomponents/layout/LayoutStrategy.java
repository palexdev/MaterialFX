/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.layout;

import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.base.TriFunction;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code LayoutStrategy} defines a series of {@link Function}s that are responsible for computing:
 * <p> 1) The minimum width
 * <p> 2) The minimum height
 * <p> 3) The preferred width
 * <p> 4) The preferred height
 * <p> 5) The maximum width
 * <p> 6) The maximum height
 * <p>
 * ...of a component that implements {@link MFXResizable}.
 * <p></p>
 * An internal class, {@link Defaults}, offers a series of default layout functions that replicate the JavaFX's
 * algorithm.
 * <p></p>
 * A good usage of this is to start from the defaults and then extend them using the {@link Function#andThen(Function)}
 * feature.
 * <p></p>
 * When creating a new {@code LayoutStrategy} object through no-arg constructor or {@link #defaultStrategy()}, the six
 * functions are set to the one in {@link Defaults} (JavaFX algorithm).
 */
public class LayoutStrategy {
	//================================================================================
	// Properties
	//================================================================================
	private Function<MFXResizable, Double> minWidthFunction = Defaults.DEF_MIN_WIDTH_FUNCTION;
	private Function<MFXResizable, Double> minHeightFunction = Defaults.DEF_MIN_HEIGHT_FUNCTION;
	private Function<MFXResizable, Double> prefWidthFunction = Defaults.DEF_PREF_WIDTH_FUNCTION;
	private Function<MFXResizable, Double> prefHeightFunction = Defaults.DEF_PREF_HEIGHT_FUNCTION;
	private Function<MFXResizable, Double> maxWidthFunction = Defaults.DEF_MAX_WIDTH_FUNCTION;
	private Function<MFXResizable, Double> maxHeightFunction = Defaults.DEF_MAX_HEIGHT_FUNCTION;

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * @return a new {@code LayoutStrategy} instance that uses JavaFX's algorithm for all sizes
	 */
	public static LayoutStrategy defaultStrategy() {
		return new LayoutStrategy();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Computes the minimum width for the specified {@link MFXResizable} by invoking the set {@link #getMinWidthFunction()}.
	 *
	 * @return the computed width or 0.0 if the function is null
	 */
	public double computeMinWidth(MFXResizable resizable) {
		return minWidthFunction != null ? minWidthFunction.apply(resizable) : 0.0;
	}

	/**
	 * Computes the minimum height for the specified {@link MFXResizable} by invoking the set {@link #getMinHeightFunction()}.
	 *
	 * @return the computed height or 0.0 if the function is null
	 */
	public double computeMinHeight(MFXResizable resizable) {
		return minHeightFunction != null ? minHeightFunction.apply(resizable) : 0.0;
	}

	/**
	 * Computes the preferred width for the specified {@link MFXResizable} by invoking the set {@link #getPrefWidthFunction()}.
	 *
	 * @return the computed width or 0.0 if the function is null
	 */
	public double computePrefWidth(MFXResizable resizable) {
		return prefWidthFunction != null ? prefWidthFunction.apply(resizable) : 0.0;
	}

	/**
	 * Computes the preferred height for the specified {@link MFXResizable} by invoking the set {@link #getPrefHeightFunction()}.
	 *
	 * @return the computed height or 0.0 if the function is null
	 */
	public double computePrefHeight(MFXResizable resizable) {
		return prefHeightFunction != null ? prefHeightFunction.apply(resizable) : 0.0;
	}

	/**
	 * Computes the maximum width for the specified {@link MFXResizable} by invoking the set {@link #getMaxWidthFunction()}.
	 *
	 * @return the computed width or 0.0 if the function is null
	 */
	public double computeMaxWidth(MFXResizable resizable) {
		return maxWidthFunction != null ? maxWidthFunction.apply(resizable) : 0.0;
	}

	/**
	 * Computes the maximum height for the specified {@link MFXResizable} by invoking the set {@link #getMaxHeightFunction()}.
	 *
	 * @return the computed height or 0.0 if the function is null
	 */
	public double computeMaxHeight(MFXResizable resizable) {
		return maxHeightFunction != null ? maxHeightFunction.apply(resizable) : 0.0;
	}

	/**
	 * Fluent API to set this {@code LayoutStrategy} on the given {@link MFXResizable}.
	 *
	 * @see MFXResizable#setLayoutStrategy(LayoutStrategy)
	 */
	public LayoutStrategy setOn(MFXResizable resizable) {
		resizable.setLayoutStrategy(this);
		return this;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LayoutStrategy that = (LayoutStrategy) o;
		return Objects.equals(getMinWidthFunction(), that.getMinWidthFunction()) &&
				Objects.equals(getMinHeightFunction(), that.getMinHeightFunction()) &&
				Objects.equals(getPrefWidthFunction(), that.getPrefWidthFunction()) &&
				Objects.equals(getPrefHeightFunction(), that.getPrefHeightFunction()) &&
				Objects.equals(getMaxWidthFunction(), that.getMaxWidthFunction()) &&
				Objects.equals(getMaxHeightFunction(), that.getMaxHeightFunction());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				getMinWidthFunction(), getMinHeightFunction(),
				getPrefWidthFunction(), getPrefHeightFunction(),
				getMaxWidthFunction(), getMaxHeightFunction()
		);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Function<MFXResizable, Double> getMinWidthFunction() {
		return minWidthFunction;
	}

	public LayoutStrategy setMinWidthFunction(Function<MFXResizable, Double> minWidthFunction) {
		this.minWidthFunction = minWidthFunction;
		return this;
	}

	public Function<MFXResizable, Double> getMinHeightFunction() {
		return minHeightFunction;
	}

	public LayoutStrategy setMinHeightFunction(Function<MFXResizable, Double> minHeightFunction) {
		this.minHeightFunction = minHeightFunction;
		return this;
	}

	public Function<MFXResizable, Double> getPrefWidthFunction() {
		return prefWidthFunction;
	}

	public LayoutStrategy setPrefWidthFunction(Function<MFXResizable, Double> prefWidthFunction) {
		this.prefWidthFunction = prefWidthFunction;
		return this;
	}

	public Function<MFXResizable, Double> getPrefHeightFunction() {
		return prefHeightFunction;
	}

	public LayoutStrategy setPrefHeightFunction(Function<MFXResizable, Double> prefHeightFunction) {
		this.prefHeightFunction = prefHeightFunction;
		return this;
	}

	public Function<MFXResizable, Double> getMaxWidthFunction() {
		return maxWidthFunction;
	}

	public LayoutStrategy setMaxWidthFunction(Function<MFXResizable, Double> maxWidthFunction) {
		this.maxWidthFunction = maxWidthFunction;
		return this;
	}

	public Function<MFXResizable, Double> getMaxHeightFunction() {
		return maxHeightFunction;
	}

	public LayoutStrategy setMaxHeightFunction(Function<MFXResizable, Double> maxHeightFunction) {
		this.maxHeightFunction = maxHeightFunction;
		return this;
	}

	public static class Defaults {
		public static final Function<MFXResizable, Double> DEF_MIN_WIDTH_FUNCTION;
		public static final Function<MFXResizable, Double> DEF_MIN_HEIGHT_FUNCTION;
		public static final Function<MFXResizable, Double> DEF_PREF_WIDTH_FUNCTION;
		public static final Function<MFXResizable, Double> DEF_PREF_HEIGHT_FUNCTION;
		public static final Function<MFXResizable, Double> DEF_MAX_WIDTH_FUNCTION;
		public static final Function<MFXResizable, Double> DEF_MAX_HEIGHT_FUNCTION;

		static {
			DEF_MIN_WIDTH_FUNCTION = createFunction(
					MFXResizable::getHeight,
					(s, h, i) -> s.computeMinWidth(h, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
			DEF_MIN_HEIGHT_FUNCTION = createFunction(
					MFXResizable::getWidth,
					(s, w, i) -> s.computeMinHeight(w, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
			DEF_PREF_WIDTH_FUNCTION = createFunction(
					MFXResizable::getHeight,
					(s, h, i) -> s.computePrefWidth(h, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
			DEF_PREF_HEIGHT_FUNCTION = createFunction(
					MFXResizable::getWidth,
					(s, w, i) -> s.computePrefHeight(w, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
			DEF_MAX_WIDTH_FUNCTION = createFunction(
					MFXResizable::getHeight,
					(s, h, i) -> s.computeMaxWidth(h, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
			DEF_MAX_HEIGHT_FUNCTION = createFunction(
					MFXResizable::getWidth,
					(s, w, i) -> s.computeMaxHeight(w, i.getTop(), i.getRight(), i.getBottom(), i.getLeft())
			);
		}

		private static Function<MFXResizable, Double> createFunction(
				Function<MFXResizable, Double> otherSize,
				TriFunction<MFXSkinBase<?, ?>, Double, Insets, Double> fn) {
			return c -> {
				Skin<?> skin = c.getSkin();
				if (!(skin instanceof MFXSkinBase)) return 0.0;
				Double oSize = otherSize.apply(c);
				return fn.apply(((MFXSkinBase<?, ?>) skin), oSize, getSnappedInsets(c));
			};
		}

		private static Insets getSnappedInsets(MFXResizable res) {
			return InsetsBuilder.of(
					res.snappedTopInset(),
					res.snappedRightInset(),
					res.snappedBottomInset(),
					res.snappedLeftInset()
			);
		}
	}
}
