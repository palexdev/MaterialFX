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

package io.github.palexdev.materialfx.builders.layout;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

public class BorderBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final List<BorderStroke> strokes = new ArrayList<>();
	private final List<BorderImage> images = new ArrayList<>();

	//================================================================================
	// Constructors
	//================================================================================
	public static BorderBuilder build() {
		return new BorderBuilder();
	}

	//================================================================================
	// Methods
	//================================================================================
	public BorderBuilder addFill(BorderStroke stroke) {
		strokes.add(stroke);
		return this;
	}

	public BorderBuilder addFill(Paint stroke, BorderStrokeStyle style, CornerRadii cornerRadii, BorderWidths widths, Insets insets) {
		strokes.add(new BorderStroke(stroke, style, cornerRadii, widths, insets));
		return this;
	}

	public BorderBuilder addImage(BorderImage image) {
		images.add(image);
		return this;
	}

	public BorderBuilder addImage(Image image, BorderWidths widths, Insets insets, BorderWidths slices, boolean filled, BorderRepeat repeatX, BorderRepeat repeatY) {
		images.add(new BorderImage(image, widths, insets, slices, filled, repeatX, repeatY));
		return this;
	}

	public Border get() {
		return new Border(strokes, images);
	}
}
