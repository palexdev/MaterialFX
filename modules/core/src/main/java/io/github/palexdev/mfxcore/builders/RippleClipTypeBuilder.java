/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.builders;

import io.github.palexdev.mfxcore.effects.ripple.RippleClipType;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Convenience class for building Ripple clip shapes.
 */
public class RippleClipTypeBuilder {
	private RippleClipType rippleClipType = RippleClipType.NO_CLIP;
	private double radius = 0;
	private double arcW = 0;
	private double arcH = 0;
	private double offsetW = 0;
	private double offsetH = 0;

	public RippleClipTypeBuilder() {
	}

	public RippleClipTypeBuilder(RippleClipType rippleClipType) {
		this.rippleClipType = rippleClipType;
	}

	public RippleClipTypeBuilder(RippleClipType rippleClipType, double arcW, double arcH) {
		this.rippleClipType = rippleClipType;
		this.arcW = arcW;
		this.arcH = arcH;
	}

	public Shape build(Region region) {
		double w = region.getWidth() + offsetW;
		double h = region.getHeight() + offsetH;

		switch (rippleClipType) {
			case CIRCLE:
				double radius = this.radius == 0 ? Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2)) / 2 : this.radius;
				Circle circle = new Circle(radius);
				circle.setTranslateX(w / 2);
				circle.setTranslateY(h / 2);
				return circle;
			case RECTANGLE:
				return new Rectangle(w, h);
			case ROUNDED_RECTANGLE:
				Rectangle rectangle = new Rectangle(w, h);
				rectangle.setArcWidth(arcW);
				rectangle.setArcHeight(arcH);
				return rectangle;
			default:
				return null;
		}
	}

	public RippleClipTypeBuilder setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public RippleClipTypeBuilder setArcs(double arcs) {
		this.arcW = arcs;
		this.arcH = arcs;
		return this;
	}

	public RippleClipTypeBuilder setArcs(double arcW, double arcH) {
		this.arcW = arcW;
		this.arcH = arcH;
		return this;
	}

	public RippleClipTypeBuilder setOffsetW(double offsetW) {
		this.offsetW = offsetW;
		return this;
	}

	public RippleClipTypeBuilder setOffsetH(double offsetH) {
		this.offsetH = offsetH;
		return this;
	}

	public RippleClipTypeBuilder setRippleClipType(RippleClipType rippleClipType) {
		this.rippleClipType = rippleClipType;
		return this;
	}
}
