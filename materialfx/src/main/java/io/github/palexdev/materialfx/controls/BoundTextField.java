package io.github.palexdev.materialfx.controls;

import javafx.scene.control.IndexRange;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

/**
 * Similar to {@link BoundLabel}.
 * <p>
 * A special text field used by custom controls which has all its properties
 * bound to another {@link TextField} control.
 * <p></p>
 * Note: JavaFX's text fields do not allow to also bind the selection and the caret position
 * (thank you very much for making everything close/private you donkeys devs), as a workaround for
 * that two listeners and a boolean flag are used to update both the text fields when needed.
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
	private boolean updatingField = false;

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
		setTextFormatter(textField.getTextFormatter());
		selectRange(textField.getSelection().getStart(), textField.getSelection().getEnd());
		positionCaret(textField.getCaretPosition());

		// Binding
		promptTextProperty().bind(textField.promptTextProperty());
		textProperty().bindBidirectional(textField.textProperty());
		fontProperty().bind(textField.fontProperty());
		editableProperty().bind(textField.editableProperty());
		alignmentProperty().bind(textField.alignmentProperty());
		textFormatterProperty().bind(textField.textFormatterProperty());

		// Update selection/caret via listeners
		textField.selectionProperty().addListener(invalidated -> {
			if (updatingField) {
				updatingField = false;
				return;
			}
			updateField(this, textField.getSelection(), textField.getCaretPosition());
		});
		selectedTextProperty().addListener(invalidated -> {
			updatingField = true;
			updateField(textField, getSelection(), getCaretPosition());
		});
	}

	private void updateField(TextField textField, IndexRange selection, int caretPosition) {
		if (selection.getEnd() == caretPosition) {
			textField.selectRange(selection.getStart(), selection.getEnd());
		} else {
			textField.selectRange(selection.getEnd(), selection.getStart());
		}
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CustomTextFieldSkin(this);
	}

	public void dispose() {
		textField = null;
	}

	private class CustomTextFieldSkin extends TextFieldSkin {
		public CustomTextFieldSkin(TextField field) {
			super(field);
			setCaretAnimating(textField.getCaretVisible());
			textField.caretVisibleProperty().addListener(invalidated -> setCaretAnimating(textField.getCaretVisible()));
		}

		@Override
		public void setCaretAnimating(boolean value) {
			super.setCaretAnimating(value && textField.getCaretVisible());
		}
	}
}
