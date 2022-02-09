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

public class BackgroundBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final List<BackgroundFill> fills = new ArrayList<>();
	private final List<BackgroundImage> images = new ArrayList<>();

	//================================================================================
	// Constructors
	//================================================================================
	public static BackgroundBuilder build() {
		return new BackgroundBuilder();
	}

	//================================================================================
	// Methods
	//================================================================================
	public BackgroundBuilder addFill(BackgroundFill fill) {
		fills.add(fill);
		return this;
	}

	public BackgroundBuilder addFill(Paint fill) {
		return addFill(fill, CornerRadii.EMPTY);
	}

	public BackgroundBuilder addFill(Paint fill, CornerRadii cornerRadii) {
		return addFill(fill, cornerRadii, Insets.EMPTY);
	}

	public BackgroundBuilder addFill(Paint fill, CornerRadii cornerRadii, Insets insets) {
		fills.add(new BackgroundFill(fill, cornerRadii, insets));
		return this;
	}

	public BackgroundBuilder addImage(BackgroundImage image) {
		images.add(image);
		return this;
	}

	public BackgroundBuilder addImage(Image image, BackgroundRepeat repeatX, BackgroundRepeat repeatY, BackgroundPosition position, BackgroundSize size) {
		images.add(new BackgroundImage(image, repeatX, repeatY, position, size));
		return this;
	}

	public Background get() {
		return new Background(fills, images);
	}
}
