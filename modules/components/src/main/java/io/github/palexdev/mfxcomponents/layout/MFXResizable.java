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

import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * This API allows MaterialFX components, descendants of {@link MFXControl} and {@link MFXLabeled}, which also use the
 * new base skin {@link MFXSkinBase}, to define a quick and easy way to change their layout strategy through a property.
 * <p>
 * The pro of such API is that a user doesn't have to necessarily create a custom skin or override the methods inline
 * to change the component sizing, it's enough to define a new strategy, which is more elegant indeed.
 * <p></p>
 * Some methods present in this API are already defined by JavaFX controls, but forces them to be {@code public} since
 * they may be needed by the {@link LayoutStrategy} when computing the sizes.
 */
public interface MFXResizable {

	/**
	 * @return the instance of the current {@link LayoutStrategy}
	 */
	LayoutStrategy getLayoutStrategy();

	/**
	 * Specifies the {@link LayoutStrategy} used by the component to compute its sizes.
	 */
	ObjectProperty<LayoutStrategy> layoutStrategyProperty();

	/**
	 * Sets the {@link LayoutStrategy} used by the component to compute its sizes.
	 */
	void setLayoutStrategy(LayoutStrategy strategy);

	/**
	 * By default, does nothing.
	 * <p>
	 * Implementations of this should perform the actions needed to 'activate' the new layout strategy,
	 * for example a component may invoke a layout request through {@link Control#requestLayout()}.
	 */
	default void onLayoutStrategyChanged() {
	}

	/**
	 * By defaults, returns {@link LayoutStrategy#defaultStrategy()}.
	 * <p></p>
	 * Components may override this to specify what is their default layout strategy.
	 */
	default LayoutStrategy defaultLayoutStrategy() {
		return LayoutStrategy.defaultStrategy();
	}

	/**
	 * Calls {@link #setLayoutStrategy(LayoutStrategy)} with {@link #defaultLayoutStrategy()} as parameter.
	 * In other words, resets the component's {@link LayoutStrategy} to its default one.
	 */
	default void setDefaultLayoutStrategy() {
		setLayoutStrategy(defaultLayoutStrategy());
	}

	/**
	 * Calls {@link #setLayoutStrategy(LayoutStrategy)} with {@link LayoutStrategy#defaultStrategy()} as parameter.
	 * In other words, resets the component's {@link LayoutStrategy} to the one used by JavaFX.
	 * <p>
	 * This may be useful in case {@link #defaultLayoutStrategy()} has been overridden.
	 */
	default void setJavaFXLayoutStrategy() {
		setLayoutStrategy(LayoutStrategy.defaultStrategy());
	}

	double getWidth();

	double getHeight();

	Skin<?> getSkin();

	double snappedTopInset();

	double snappedRightInset();

	double snappedBottomInset();

	double snappedLeftInset();

	double computeMinWidth(double height);

	double computeMinHeight(double width);

	double computePrefWidth(double height);

	double computePrefHeight(double width);

	double computeMaxWidth(double height);

	double computeMaxHeight(double width);

}
