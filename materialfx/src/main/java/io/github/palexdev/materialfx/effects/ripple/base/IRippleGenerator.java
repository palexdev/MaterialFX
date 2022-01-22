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

package io.github.palexdev.materialfx.effects.ripple.base;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Public API for every ripple generator.
 *
 * @param <T> the type of accepted ripples
 */
public interface IRippleGenerator<T extends IRipple> {
	/**
	 * @return the region on which the ripple will be generated
	 */
	Region getRegion();

	/**
	 * Every ripple generator should have a default clip supplier.
	 */
	void defaultClipSupplier();

	/**
	 * @return the current generator's clip supplier
	 */
	Supplier<Shape> getClipSupplier();

	/**
	 * Sets the generator's clip supplier to the specified one.
	 * <p>
	 * This is responsible for creating the clip node of the generator, which is built and set
	 * everytime the ripple is generated, before the animation is started, and defines
	 * the bounds beyond which the ripple must not go.
	 * <p>
	 * Although the supplier accepts any {@link Shape} it is highly recommended to build clips
	 * using {@link RippleClipTypeFactory}.
	 */
	void setClipSupplier(Supplier<Shape> clipSupplier);

	/**
	 * Every ripple generator should have a default position for the ripples.
	 */
	void defaultPositionFunction();

	/**
	 * @return the current generator's position function
	 */
	Function<MouseEvent, PositionBean> getRipplePositionFunction();

	/**
	 * Sets the generator's ripple position function to the specified one.
	 * <p>
	 * This {@link Function} is responsible for computing the ripple's x and y
	 * coordinates before the animation is played. The function takes a MouseEvent as the input
	 * (since in most controls the coordinates are the x and y coordinates of the mouse event)
	 * and returns a {@link PositionBean} bean.
	 */
	void setRipplePositionFunction(Function<MouseEvent, PositionBean> positionFunction);

	/**
	 * Every ripple generator should have a default ripple supplier.
	 */
	void defaultRippleSupplier();

	/**
	 * @return the current generator's ripple supplier
	 */
	Supplier<T> getRippleSupplier();

	/**
	 * Sets the generator's ripple supplier to the specified one.
	 * <p>
	 * This {@link Supplier} is responsible for creating the ripple shape before the animation
	 * is played.
	 */
	void setRippleSupplier(Supplier<T> rippleSupplier);
}
