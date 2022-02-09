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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableStringProperty;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.binding.Bindings;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * This is my implementation of a password field, a TextField which masks the given input text.
 * <p></p>
 * Extends {@link MFXTextField}, starts with a default trailing icon which allows to show/hide the password and it's
 * defined by the {@link #defaultTrailingIcon()} method so it can be changed after instantiation or by overriding the method.
 * <p></p>
 * Specific features:
 * <p>
 * <p> - Allows to change the "mask" character, even at runtime
 * <p> - Allows to show/hide the password.
 * <p> - Allows to copy the selected text (Ctrl + C)
 * <p> - Allows to cut the selected text (Ctrl + X)
 * <p> - Allows to paste the text in the clipboard to the field (Ctrl + V)
 * <p> - Allows to enable/disable copy, cut and paste at any time
 * <p> - Introduces a new PseudoClass, ":masked" that activates when the text is masked
 */
public class MFXPasswordField extends MFXTextField {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-password-field";
	private final String STYLE_SHEET = MFXResourcesLoader.load("css/MFXPasswordField.css");

	public static final String BULLET = "\u25cf";
	protected static final PseudoClass MASKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("masked");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPasswordField() {
		this("");
	}

	public MFXPasswordField(String text) {
		this(text, "");
	}

	public MFXPasswordField(String text, String promptText) {
		this(text, promptText, "");
	}

	public MFXPasswordField(String text, String promptText, String floatingText) {
		super(text, promptText, floatingText);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setBehavior();
		defaultTrailingIcon();
		defaultContextMenu();
	}

	/**
	 * Sets the default behavior for the password field such:
	 * <p> - Avoid worlds selection and only allowing selectAll()
	 * <p> - Managing the ":masked" PseudoClass
	 */
	protected void setBehavior() {
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
				selectAll();
				event.consume();
			}
		});
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case LEFT:
				case RIGHT:
					if (event.isControlDown() && event.isShiftDown()) {
						boundField.selectAll();
						event.consume();
					}
					break;
			}
		});

		if (!isShowPassword()) pseudoClassStateChanged(MASKED_PSEUDO_CLASS, true);
		showPassword.addListener((observable, oldValue, newValue) -> pseudoClassStateChanged(MASKED_PSEUDO_CLASS, !newValue));
	}

	/**
	 * Sets the default trailing icon for the password field.
	 * <p>
	 * An eye to show/hide the password.
	 */
	protected void defaultTrailingIcon() {
		MFXFontIcon icon = new MFXFontIcon("mfx-eye", 16, Color.web("#4D4D4D"));
		icon.getStyleClass().add("eye-icon");
		icon.descriptionProperty().bind(Bindings.createStringBinding(
				() -> isShowPassword() ? "mfx-eye-slash" : "mfx-eye",
				showPasswordProperty()
		));
		MFXIconWrapper showPasswordIcon = new MFXIconWrapper(icon, 24).defaultRippleGeneratorBehavior();
		NodeUtils.makeRegionCircular(showPasswordIcon);
		showPasswordIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			setShowPassword(!isShowPassword());

			// Workaround for caret being positioned (only visually) wrongly
			int currPos = delegateGetCaretPosition();
			positionCaret(0);
			positionCaret(currPos);

			event.consume();
		});

		setTrailingIcon(showPasswordIcon);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void defaultContextMenu() {
		if (allowCopy == null ||
				allowCut == null ||
				allowPaste == null)
			return;

		MFXContextMenuItem copyItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-copy", 14))
				.setText("Copy")
				.setAccelerator("Ctrl + C")
				.setOnAction(event -> copy())
				.get();
		copyItem.disableProperty().bind(allowCopyProperty().not());

		MFXContextMenuItem cutItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-cut", 14))
				.setText("Cut")
				.setAccelerator("Ctrl + X")
				.setOnAction(event -> cut())
				.get();
		cutItem.disableProperty().bind(allowCutProperty().not());

		MFXContextMenuItem pasteItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-paste", 14))
				.setText("Paste")
				.setAccelerator("Ctrl + V")
				.setOnAction(event -> paste())
				.get();
		pasteItem.disableProperty().bind(allowPasteProperty().not());

		MFXContextMenuItem deleteItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-delete-alt", 16))
				.setText("Delete")
				.setAccelerator("Ctrl + D")
				.setOnAction(event -> deleteText(getSelection()))
				.get();

		MFXContextMenuItem selectAllItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-select-all", 16))
				.setText("Select All")
				.setAccelerator("Ctrl + A")
				.setOnAction(event -> selectAll())
				.get();

		MFXContextMenuItem redoItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-redo", 12))
				.setText("Redo")
				.setAccelerator("Ctrl + Y")
				.setOnAction(event -> redo())
				.get();

		MFXContextMenuItem undoItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-undo", 12))
				.setText("Undo")
				.setAccelerator("Ctrl + Z")
				.setOnAction(event -> undo())
				.get();

		contextMenu = MFXContextMenu.Builder.build(this)
				.addItems(copyItem, cutItem, pasteItem, deleteItem, selectAllItem)
				.addLineSeparator()
				.addItems(redoItem, undoItem)
				.setPopupStyleableParent(this)
				.installAndGet();
	}

	@Override
	public void copy() {
		if (!isAllowCopy()) return;
		super.copy();
	}

	@Override
	public void cut() {
		if (!isAllowCut()) return;
		super.cut();
	}

	@Override
	public void paste() {
		if (!isAllowPaste()) return;
		super.paste();
	}

	@Override
	public void previousWord() {
		boundField.selectAll();
	}

	@Override
	public void nextWord() {
		boundField.selectAll();
	}

	@Override
	public void endOfNextWord() {
		boundField.selectAll();
	}

	@Override
	public void selectPreviousWord() {
		boundField.selectAll();
	}

	@Override
	public void selectNextWord() {
		boundField.selectAll();
	}

	@Override
	public void selectEndOfNextWord() {
		boundField.selectAll();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLE_SHEET;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty allowCopy = new StyleableBooleanProperty(
			StyleableProperties.ALLOW_COPY,
			this,
			"allowCopy",
			false
	);

	private final StyleableBooleanProperty allowCut = new StyleableBooleanProperty(
			StyleableProperties.ALLOW_CUT,
			this,
			"allowCut",
			false
	);

	private final StyleableBooleanProperty allowPaste = new StyleableBooleanProperty(
			StyleableProperties.ALLOW_PASTE,
			this,
			"allowPaste",
			false
	);

	private final StyleableBooleanProperty showPassword = new StyleableBooleanProperty(
			StyleableProperties.SHOW_PASSWORD,
			this,
			"showPassword",
			false
	);

	private final StyleableStringProperty hideCharacter = new StyleableStringProperty(
			StyleableProperties.HIDE_CHARACTER,
			this,
			"hideCharacter",
			BULLET
	) {
		@Override
		public void set(String newValue) {
			if (newValue.trim().isEmpty()) {
				return;
			}
			super.set(newValue.length() > 1 ? newValue.substring(0, 1) : newValue);
		}
	};

	public boolean isAllowCopy() {
		return allowCopy.get();
	}

	/**
	 * Specifies if copying the password field text is allowed.
	 */
	public StyleableBooleanProperty allowCopyProperty() {
		return allowCopy;
	}

	public void setAllowCopy(boolean allowCopy) {
		this.allowCopy.set(allowCopy);
	}

	public boolean isAllowCut() {
		return allowCut.get();
	}

	/**
	 * Specifies if it's allowed to cut text from the password field.
	 */
	public StyleableBooleanProperty allowCutProperty() {
		return allowCut;
	}

	public void setAllowCut(boolean allowCut) {
		this.allowCut.set(allowCut);
	}

	public boolean isAllowPaste() {
		return allowPaste.get();
	}

	/**
	 * Specifies if it's allowed to paste text from the clipboard to the field.
	 */
	public StyleableBooleanProperty allowPasteProperty() {
		return allowPaste;
	}

	public void setAllowPaste(boolean allowPaste) {
		this.allowPaste.set(allowPaste);
	}

	public boolean isShowPassword() {
		return showPassword.get();
	}

	/**
	 * Specifies if the text should be un-masked to show the password.
	 */
	public StyleableBooleanProperty showPasswordProperty() {
		return showPassword;
	}

	public void setShowPassword(boolean showPassword) {
		this.showPassword.set(showPassword);
	}

	public String getHideCharacter() {
		return hideCharacter.get();
	}

	/**
	 * Specifies the character used to mask the text.
	 */
	public StyleableStringProperty hideCharacterProperty() {
		return hideCharacter;
	}

	public void setHideCharacter(String hideCharacter) {
		this.hideCharacter.set(hideCharacter);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXPasswordField> FACTORY = new StyleablePropertyFactory<>(MFXTextField.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXPasswordField, Boolean> ALLOW_COPY =
				FACTORY.createBooleanCssMetaData(
						"-mfx-allow-copy",
						MFXPasswordField::allowCopyProperty,
						false
				);

		private static final CssMetaData<MFXPasswordField, Boolean> ALLOW_CUT =
				FACTORY.createBooleanCssMetaData(
						"-mfx-allow-cut",
						MFXPasswordField::allowCutProperty,
						false
				);

		private static final CssMetaData<MFXPasswordField, Boolean> ALLOW_PASTE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-allow-paste",
						MFXPasswordField::allowPasteProperty,
						false
				);

		private static final CssMetaData<MFXPasswordField, Boolean> SHOW_PASSWORD =
				FACTORY.createBooleanCssMetaData(
						"-mfx-show-password",
						MFXPasswordField::showPasswordProperty,
						false
				);

		private static final CssMetaData<MFXPasswordField, String> HIDE_CHARACTER =
				FACTORY.createStringCssMetaData(
						"-mfx-hide-character",
						MFXPasswordField::hideCharacterProperty,
						BULLET
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					MFXTextField.getClassCssMetaData(),
					ALLOW_COPY, ALLOW_CUT, ALLOW_PASTE,
					SHOW_PASSWORD, HIDE_CHARACTER
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXPasswordField.getClassCssMetaData();
	}
}
