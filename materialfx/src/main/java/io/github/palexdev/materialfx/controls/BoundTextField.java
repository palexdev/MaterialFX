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

import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to {@link BoundLabel}.
 * <p>
 * A special text field used by custom controls which has all its properties
 * bound to another {@link TextField} control.
 * <p></p>
 * Note: JavaFX's text fields do not allow to also bind the selection and the caret position
 * (thank you very much for making everything close/private you fucking donkeys devs), the
 * only consistent way of doing this is to keep the reference in the custom control and
 * redirect all the related methods to this field.
 * <p></p>
 * Oh, also another feature of this field is that it uses a special skin which allows to
 * hide the caret. Because for some fucking reason the JavaFX's -fx-caret-visible property was not
 * working, and I didn't want to understand why another borked JavaFX feature was not working so, as
 * always, here's a brute force workaround, you're welcome.
 */
public class BoundTextField extends TextField {
	//================================================================================
	// Properties
	//================================================================================
	private MFXTextField textField;

	//================================================================================
	// Constructors
	//================================================================================
	public BoundTextField(MFXTextField textField) {
		this.textField = textField;

		// Initialization
		setPromptText(textField.getPromptText());
		setText(textField.getText());
		setFont(textField.getFont());
		setEditable(textField.isEditable());
		setAlignment(textField.getAlignment());
		setPrefColumnCount(textField.getPrefColumnCount());
		selectRange(textField.getSelection().getStart(), textField.getSelection().getEnd());
		positionCaret(textField.getCaretPosition());

		// Binding
		promptTextProperty().bind(textField.promptTextProperty());
		textProperty().bindBidirectional(textField.textProperty());
		fontProperty().bind(textField.fontProperty());
		editableProperty().bind(textField.editableProperty());
		alignmentProperty().bind(textField.alignmentProperty());
		prefColumnCountProperty().bind(textField.prefColumnCountProperty());
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CustomTextFieldSkin(this);
	}

	public void dispose() {
		textField = null;
	}

	private class CustomTextFieldSkin extends TextFieldSkin {
		private final StringBinding textBinding;

		public CustomTextFieldSkin(TextField field) {
			super(field);

			// This is needed because there's no way to distinguish the text node
			// from the prompt text node (both have .text style class, no id)
			// The prompt text node is present only when the prompt text is not empty
			// and it's always placed at the beginning of the children list
			// so the text node we want is the last
			Text textNode;
			if (!field.getPromptText().isEmpty()) {
				try {
					List<Node> textNodes = new ArrayList<>(lookupAll(".text"));
					textNode = (Text) textNodes.get(textNodes.size() - 1);
				} catch (Exception ex) {
					textNode = (Text) lookup(".text");
				}
			} else {
				textNode = (Text) lookup(".text");
			}

			textBinding = new StringBinding() {
				{
					bind(field.textProperty());
				}

				@Override
				protected String computeValue() {
					return maskText(field.textProperty().getValueSafe());
				}
			};
			textNode.textProperty().bind(textBinding);

			setCaretAnimating(textField.getCaretVisible());
			textField.caretVisibleProperty().addListener(invalidated -> setCaretAnimating(textField.getCaretVisible()));

			if (textField instanceof MFXPasswordField) {
				MFXPasswordField passwordField = (MFXPasswordField) textField;
				passwordField.showPasswordProperty().addListener((observable, oldValue, newValue) -> textBinding.invalidate());
				passwordField.hideCharacterProperty().addListener((observable, oldValue, newValue) -> textBinding.invalidate());
			}
		}

		@Override
		protected String maskText(String txt) {
			if (textField instanceof MFXPasswordField) {
				MFXPasswordField passwordField = (MFXPasswordField) textField;
				if (passwordField.isShowPassword()) return txt;

				int n = txt.length();
				return passwordField.getHideCharacter().repeat(n);
			}
			return txt;
		}

		@Override
		public void setCaretAnimating(boolean value) {
			super.setCaretAnimating(value && textField.getCaretVisible());
		}
	}
}
