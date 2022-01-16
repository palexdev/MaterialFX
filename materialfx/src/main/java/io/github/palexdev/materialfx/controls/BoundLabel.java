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

package io.github.palexdev.materialfx.controls;

import javafx.scene.control.Label;
import javafx.scene.control.Labeled;

/**
 * This is a special Label which has all its main properties bound to
 * another {@link Labeled} control. This is especially useful for custom
 * controls and skins that have text, as the text properties are set on the control
 * and not on the text node itself, and that's why all properties are bound.
 */
public class BoundLabel extends Label {

	public BoundLabel(Labeled labeled) {
		super();

		// Init
		setText(labeled.getText());
		setFont(labeled.getFont());
		setTextFill(labeled.getTextFill());
		setWrapText(labeled.isWrapText());
		setTextAlignment(labeled.getTextAlignment());
		setTextOverrun(labeled.getTextOverrun());
		setEllipsisString(labeled.getEllipsisString());
		setUnderline(labeled.isUnderline());
		setLineSpacing(labeled.getLineSpacing());
		setGraphicTextGap(labeled.getGraphicTextGap());
		setContentDisplay(labeled.getContentDisplay());
		setGraphic(labeled.getGraphic());
		setAlignment(labeled.getAlignment());

		// Bindings
		textProperty().bind(labeled.textProperty());
		fontProperty().bind(labeled.fontProperty());
		textFillProperty().bind(labeled.textFillProperty());
		wrapTextProperty().bind(labeled.wrapTextProperty());
		textAlignmentProperty().bind(labeled.textAlignmentProperty());
		textOverrunProperty().bind(labeled.textOverrunProperty());
		ellipsisStringProperty().bind(labeled.ellipsisStringProperty());
		underlineProperty().bind(labeled.underlineProperty());
		lineSpacingProperty().bind(labeled.lineSpacingProperty());
		graphicTextGapProperty().bind(labeled.graphicTextGapProperty());
		contentDisplayProperty().bind(labeled.contentDisplayProperty());
		graphicProperty().bind(labeled.graphicProperty());
		alignmentProperty().bind(labeled.alignmentProperty());
	}
}
