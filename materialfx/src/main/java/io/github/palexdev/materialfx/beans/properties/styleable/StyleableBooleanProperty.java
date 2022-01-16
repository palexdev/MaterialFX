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

package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableBooleanProperty extends SimpleStyleableBooleanProperty {

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, boolean initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, Object bean, String name, boolean initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
