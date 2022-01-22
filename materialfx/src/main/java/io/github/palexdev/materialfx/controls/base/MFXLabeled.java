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

package io.github.palexdev.materialfx.controls.base;

import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;

/**
 * Interface that specifies all the features MaterialFX controls with a text must have.
 */
public interface MFXLabeled {
	ContentDisplay getContentDisposition();

	/**
	 * Specifies how the control is positioned relative to its text.
	 */
	StyleableObjectProperty<ContentDisplay> contentDispositionProperty();

	void setContentDisposition(ContentDisplay contentDisposition);

	double getGap();

	/**
	 * Specifies the spacing between the control and its text.
	 */
	StyleableDoubleProperty gapProperty();

	void setGap(double gap);

	boolean isTextExpand();

	/**
	 * When setting a specific size for the control (by using setPrefSize for example, and this
	 * is true for SceneBuilder too), this flag will tell the control's label to take all the
	 * space available.
	 * <p>
	 * This allows, in combination with the {@link #contentDispositionProperty()}, to layout
	 * the control's content in many interesting ways. When the text is expanded (this property is true)
	 * use {@link Labeled#alignmentProperty()} to position the text.
	 */
	StyleableBooleanProperty textExpandProperty();

	void setTextExpand(boolean textExpand);
}
