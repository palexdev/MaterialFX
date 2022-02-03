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

package io.github.palexdev.materialfx.builders.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LabeledBuilder<L extends Labeled> extends ControlBuilder<L> {

	//================================================================================
	// Constructors
	//================================================================================
	public LabeledBuilder(L labeled) {
		super(labeled);
	}

	public static LabeledBuilder<Labeled> control(Labeled labeled) {
		return new LabeledBuilder<>(labeled);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public LabeledBuilder<L> setText(String value) {
		node.setText(value);
		return this;
	}

	public LabeledBuilder<L> setAlignment(Pos value) {
		node.setAlignment(value);
		return this;
	}

	public LabeledBuilder<L> setTextAlignment(TextAlignment value) {
		node.setTextAlignment(value);
		return this;
	}

	public LabeledBuilder<L> setTextOverrun(OverrunStyle value) {
		node.setTextOverrun(value);
		return this;
	}

	public LabeledBuilder<L> setEllipsisString(String value) {
		node.setEllipsisString(value);
		return this;
	}

	public LabeledBuilder<L> setWrapText(boolean value) {
		node.setWrapText(value);
		return this;
	}

	public LabeledBuilder<L> setFont(Font value) {
		node.setFont(value);
		return this;
	}

	public LabeledBuilder<L> setGraphic(Node value) {
		node.setGraphic(value);
		return this;
	}

	public LabeledBuilder<L> setUnderline(boolean value) {
		node.setUnderline(value);
		return this;
	}

	public LabeledBuilder<L> setLineSpacing(double value) {
		node.setLineSpacing(value);
		return this;
	}

	public LabeledBuilder<L> setContentDisplay(ContentDisplay value) {
		node.setContentDisplay(value);
		return this;
	}

	public LabeledBuilder<L> setGraphicTextGap(double value) {
		node.setGraphicTextGap(value);
		return this;
	}

	public LabeledBuilder<L> setTextFill(Paint value) {
		node.setTextFill(value);
		return this;
	}

	public LabeledBuilder<L> setMnemonicParsing(boolean value) {
		node.setMnemonicParsing(value);
		return this;
	}
}
