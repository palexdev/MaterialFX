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

package io.github.palexdev.mfxeffects.ripple.base;

import io.github.palexdev.mfxeffects.beans.Position;
import javafx.animation.Animation;
import javafx.scene.shape.Shape;

/**
 * Specifies the public API every type of {@code Ripple} shape should implement.
 *
 * @param <S> the shape of the ripple
 */
public interface Ripple<S extends Shape> {

	/**
	 * @return the ripple's node
	 */
	S getNode();

	/**
	 * This is typically given by a {@link RippleGenerator} during the generation of a ripple.
	 * Should be used to set the position of the ripple.
	 */
	void position(Position pos);

	/**
	 * This is responsible for building the animation, specific for every type of ripple.
	 * <p>
	 * The {@link RippleGenerator} is given as a parameter if the ripple needs some information/properties/settings
	 * from it.
	 */
	Animation animation(RippleGenerator rg);

	@SuppressWarnings("unchecked")
	default Class<S> shapeType() {
		return (Class<S>) getNode().getClass();
	}
}
