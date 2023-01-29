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

package io.github.palexdev.mfxeffects.ripple;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.base.Ripple;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import javafx.animation.*;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

import static javafx.animation.Interpolator.LINEAR;
import static javafx.util.Duration.millis;

/**
 * Most common type of ripple, extends {@link Circle} and implements {@link Ripple}.
 * <p>
 * Starts with a radius of 0 and goes all the way up to the value computed by {@link #computeTargetRadius(RippleGenerator)}.
 */
public class CircleRipple extends Circle implements Ripple<Circle> {

	//================================================================================
	// Constructors
	//================================================================================
	public CircleRipple() {
		super(0);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Circle getNode() {
		return this;
	}

	/**
	 * Binds the {@link #centerXProperty()} and {@link #centerYProperty()} properties to the
	 * {@link Position#xProperty()} and {@link Position#yProperty()} properties.
	 */
	@Override
	public void position(Position pos) {
		centerXProperty().bind(pos.xProperty());
		centerYProperty().bind(pos.yProperty());
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The animation of this ripple is simple. A {@link Timeline} composed of two key moments:
	 * <p> 1) The ripple radius is changed from 0 to the value computed by {@link #computeTargetRadius(RippleGenerator)}
	 * (400ms default)
	 * <p> 2) The ripple opacity goes to 0 (700ms default)
	 */
	@Override
	public Animation animation(RippleGenerator rg) {
		return new Timeline(
				new KeyFrame(millis(400), new KeyValue(radiusProperty(), computeTargetRadius(rg), LINEAR)),
				new KeyFrame(millis(700), new KeyValue(opacityProperty(), 0.0, LINEAR))
		);
	}

	/**
	 * This is responsible for computing the radius the ripple will reach at the end of the animation.
	 * <p></p>
	 * There are two possible results:
	 * <p> 1) The value specified by the {@link RippleGenerator#ripplePrefSizeProperty()}
	 * <p> 2) The radius is computed as the maximum between the region's width and height, divided by 2.0
	 * <p></p>
	 * The last is computed only when the {@link RippleGenerator#ripplePrefSizeProperty()} is set to {@link Region#USE_COMPUTED_SIZE}.
	 * <p></p>
	 * Note that both the possible outcomes are multiplied by the value specified by {@link RippleGenerator#rippleSizeMultiplierProperty()},
	 * this is a way to have a smoother animation.
	 */
	protected double computeTargetRadius(RippleGenerator rg) {
		if (rg.getRipplePrefSize() >= 0) {
			return rg.getRipplePrefSize() * rg.getRippleSizeMultiplier();
		}
		Region region = rg.getRegion();
		double diameter = Math.max(region.getWidth(), region.getHeight());
		return (diameter / 2.0) * rg.getRippleSizeMultiplier();
	}
}
