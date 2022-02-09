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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TextFieldBuilder<F extends MFXTextField> extends ControlBuilder<F> {

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("unchecked")
	public TextFieldBuilder() {
		this((F) new MFXTextField());
	}

	public TextFieldBuilder(F control) {
		super(control);
	}

	public static TextFieldBuilder<MFXTextField> textField() {
		return new TextFieldBuilder<>();
	}

	public static TextFieldBuilder<MFXTextField> textField(MFXTextField textField) {
		return new TextFieldBuilder<>(textField);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public TextFieldBuilder<F> setSelectable(boolean selectable) {
		node.setSelectable(selectable);
		return this;
	}

	public TextFieldBuilder<F> setLeadingIcon(Node leadingIcon) {
		node.setLeadingIcon(leadingIcon);
		return this;
	}

	public TextFieldBuilder<F> setTrailingIcon(Node trailingIcon) {
		node.setTrailingIcon(trailingIcon);
		return this;
	}

	public TextFieldBuilder<F> setFloatingText(String floatingText) {
		node.setFloatingText(floatingText);
		return this;
	}

	public TextFieldBuilder<F> setAllowEdit(boolean allowEdit) {
		node.setAllowEdit(allowEdit);
		return this;
	}

	public TextFieldBuilder<F> setAnimated(boolean animated) {
		node.setAnimated(animated);
		return this;
	}

	public TextFieldBuilder<F> setBorderGap(double borderGap) {
		node.setBorderGap(borderGap);
		return this;
	}

	public TextFieldBuilder<F> setCaretVisible(boolean caretVisible) {
		node.setCaretVisible(caretVisible);
		return this;
	}

	public TextFieldBuilder<F> setFloatMode(FloatMode floatMode) {
		node.setFloatMode(floatMode);
		return this;
	}

	public TextFieldBuilder<F> setFloatingTextGap(double floatingTextGap) {
		node.setFloatingTextGap(floatingTextGap);
		return this;
	}

	public TextFieldBuilder<F> setGraphicTextGap(double graphicTextGap) {
		node.setGraphicTextGap(graphicTextGap);
		return this;
	}

	public TextFieldBuilder<F> setScaleOnAbove(boolean scaleOnAbove) {
		node.setScaleOnAbove(scaleOnAbove);
		return this;
	}

	public TextFieldBuilder<F> setTextFill(Color textFill) {
		node.setTextFill(textFill);
		return this;
	}

	public TextFieldBuilder<F> setTextLimit(int textLimit) {
		node.setTextLimit(textLimit);
		return this;
	}

	public TextFieldBuilder<F> setPrefColumnCount(int prefColumnCount) {
		node.setPrefColumnCount(prefColumnCount);
		return this;
	}

	public TextFieldBuilder<F> setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public TextFieldBuilder<F> setFont(Font font) {
		node.setFont(font);
		return this;
	}

	public TextFieldBuilder<F> setPromptText(String promptText) {
		node.setPromptText(promptText);
		return this;
	}

	public TextFieldBuilder<F> setText(String text) {
		node.setText(text);
		return this;
	}
}
