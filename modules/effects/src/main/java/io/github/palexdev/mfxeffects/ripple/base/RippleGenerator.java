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
import io.github.palexdev.mfxeffects.builders.RippleClipTypeBuilder;
import io.github.palexdev.mfxeffects.enums.RippleClipType;
import io.github.palexdev.mfxeffects.ripple.CircleRipple;
import javafx.animation.Animation;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Public API that all ripple generators should expose, this is also a connector between the
 * generator and {@link Ripple}.
 */
public interface RippleGenerator {

	/**
	 * By default, does nothing.
	 * <p>
	 * Optionally generators can implement this convenience method to enable the generation of effects
	 * on the target region.
	 */
	default void enable() {
	}

	/**
	 * By default, does nothing.
	 * <p>
	 * Optionally generators can implement this convenience method to disable the generation of effects
	 * on the target region.
	 */
	default void disable() {
	}

	/**
	 * This is the core method responsible for generating ripple effects.
	 */
	void generate(MouseEvent me);

	/**
	 * By default, returns null.
	 * <p>
	 * Optionally generators can create a secondary animation for the background to 'accompany' the
	 * main ripple effect.
	 * <p></p>
	 * As far as I know the original specs do not mention a secondary animation, and in fact it should not be needed,
	 * the 'effect' can be replicated by using just the main one, check {@link CircleRipple#animation(RippleGenerator)}.
	 */
	default Animation backgroundAnimation() {
		return null;
	}

	/**
	 * @return the target region on which the ripple effect is applied
	 */
	Region getRegion();

	/**
	 * @return the {@link Supplier} used by the generator to clip itself, thus avoiding ripples from
	 * overflowing
	 */
	Supplier<Shape> getClipSupplier();

	/**
	 * Sets the {@link Supplier} used by the generator to clip itself, thus avoiding ripples from
	 * overflowing.
	 */
	void setClipSupplier(Supplier<Shape> clipSupplier);

	/**
	 * @return the {@link Supplier} used by the generator to create ripples
	 */
	Supplier<? extends Ripple<?>> getRippleSupplier();

	/**
	 * Sets the {@link Supplier} used by the generator to create ripples.
	 */
	void setRippleSupplier(Supplier<? extends Ripple<?>> rippleSupplier);

	/**
	 * @return the {@link Function} used by the generator to convert a {@link MouseEvent} to a {@link Position}
	 * bean, which will be used as the coordinates at which create the ripple
	 */
	Function<MouseEvent, Position> getPositionFunction();

	/**
	 * Sets the {@link Function} used by the generator to convert a {@link MouseEvent} to a {@link Position}
	 * bean, which will be used as the coordinates at which create the ripple
	 */
	void setPositionFunction(Function<MouseEvent, Position> positionFunction);

	Paint getRippleColor();

	/**
	 * Specifies the color of the ripples.
	 */
	StyleableObjectProperty<Paint> rippleColorProperty();

	void setRippleColor(Paint rippleColor);

	double getRippleOpacity();

	/**
	 * Specifies the starting opacity of the ripples.
	 */
	StyleableDoubleProperty rippleOpacityProperty();

	void setRippleOpacity(double rippleOpacity);

	double getRipplePrefSize();

	/**
	 * Specifies the preferred size of the ripples.
	 */
	StyleableDoubleProperty ripplePrefSizeProperty();

	void setRipplePrefSize(double ripplePrefSize);

	double getRippleSizeMultiplier();

	/**
	 * Specifies by how much the ripples should be "enlarged" by the animation.
	 */
	StyleableDoubleProperty rippleSizeMultiplierProperty();

	void setRippleSizeMultiplier(double rippleSizeMultiplier);

	/**
	 * @return a default {@link Supplier} for the ripples shape
	 */
	default Supplier<? extends Ripple<?>> defaultRippleSupplier() {
		return CircleRipple::new;
	}

	/**
	 * @return a default {@link Supplier} for the generator's clip shape
	 * @see #setClipSupplier(Supplier)
	 */
	default Supplier<Shape> defaultClipSupplier() {
		return () -> new RippleClipTypeBuilder(RippleClipType.RECTANGLE).build(getRegion());
	}

	/**
	 * @return a default {@link Function} for the conversion of {@link MouseEvent}s to positions
	 * at which ripples are places. This default function uses {@link MouseEvent#getX()} and {@link MouseEvent#getY()}.
	 * @see #setPositionFunction(Function)
	 */
	default Function<MouseEvent, Position> defaultPositionFunction() {
		return e -> Position.of(e.getX(), e.getY());
	}
}
