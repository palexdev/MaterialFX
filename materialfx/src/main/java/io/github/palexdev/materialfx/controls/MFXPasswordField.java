/*
 * Copyright (C) 2021 Parisi Alessandro
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
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXPasswordFieldSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This is my implementation of a password field, a TextField which masks the given input text.
 * <p></p>
 * Extends {@link MFXTextField}, defines a default icon which allows to show/hide the password and it's
 * defined by the {@link #defaultIcon()} method so it can be changed after instantiation or by overriding the method.
 * <p></p>
 * Specific features:
 * <p>
 * <p> - Allows to change the "mask" character, event at runtime
 * <p> - Allows to show/hide the password. When the password is hidden the text field {@code getText()} method will return
 * the masked string so to get the password you must use {@link #getPassword()}. When the password is shown you can use both
 * <p> - Allows to quickly position the caret with all four arrows
 * <p> - Allows to selected all the text (Ctrl + A)
 * <p> - Allows to copy the selected text (Ctrl + C), note that if the password is hidden it will copy the masked text
 * <p> - Allows to cut the selected text (Ctrl + X), note that if the password is hidden it will cut the masked text
 * <p> - Allows to paste the text in the clipboard to the field (Ctrl + V)
 * <p> - Allows to enable/disable copy, cut and paste at any time
 * <p> - Allows to delete the selected text with the shortcut (Ctrl + D)
 * <p></p>
 * Note: the context menu is redefined in the skin since some methods are private in the skin.
 */
public class MFXPasswordField extends MFXTextField {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-password-field";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXPasswordField.css");

    private final ReadOnlyStringWrapper password = new ReadOnlyStringWrapper("");
    private final BooleanProperty showPassword = new SimpleBooleanProperty(false);
    private final StringProperty hideCharacter = new SimpleStringProperty("\u25cf") {
        @Override
        public void set(String newValue) {
            if (newValue.trim().isEmpty()) {
                return;
            }
            super.set(newValue.length() > 1 ? newValue.substring(0, 1) : newValue);
        }
    };
    private final BooleanProperty allowCopy = new SimpleBooleanProperty(true);
    private final BooleanProperty allowCut = new SimpleBooleanProperty(true);
    private final BooleanProperty allowPaste = new SimpleBooleanProperty(true);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPasswordField() {
        this("");
    }

    public MFXPasswordField(String text) {
        setText(text);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        defaultIcon();
    }

    /**
     * Installs the default password field icon
     */
    protected void defaultIcon() {
        MFXFontIcon icon = new MFXFontIcon("mfx-eye", 16, Color.web("#4D4D4D"));
        icon.descriptionProperty().bind(Bindings.createStringBinding(
                () -> isShowPassword() ? "mfx-eye-slash" : "mfx-eye",
                showPasswordProperty()
        ));
        MFXIconWrapper showPasswordIcon = new MFXIconWrapper(icon, 24).defaultRippleGeneratorBehavior();
        NodeUtils.makeRegionCircular(showPasswordIcon);
        showPasswordIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            setShowPassword(!isShowPassword());
            positionCaret(getText().length());
            requestFocus();
            event.consume();
        });

        setIcon(showPasswordIcon);
    }

    /**
     * Overridden, does nothing. The context menu is redefined in the skin
     * as some methods are declared in the skin, are private and are needed to
     * make the password field work correctly.
     * <p></p>
     * You can still change it or remove it anyway but keep in mind
     * that functionalities like cut, paste and delete won't work by
     * simply calling JavaFX methods.
     */
    @Override
    protected void defaultContextMenu() {
        setMFXContextMenu(MFXContextMenu.Builder.build(this).install());
    }

    /**
     * @return the un-masked text
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Specifies the un-masked text property.
     */
    public ReadOnlyStringWrapper passwordProperty() {
        return password;
    }

    public String getHideCharacter() {
        return hideCharacter.get();
    }

    /**
     * Specifies the character used to mask the text.
     */
    public StringProperty hideCharacterProperty() {
        return hideCharacter;
    }

    public void setHideCharacter(char hideCharacter) {
        this.hideCharacter.set(String.valueOf(hideCharacter));
    }

    public boolean isShowPassword() {
        return showPassword.get();
    }

    /**
     * Specifies if the text should be un-masked to show the password.
     */
    public BooleanProperty showPasswordProperty() {
        return showPassword;
    }

    public void setShowPassword(boolean showPassword) {
        this.showPassword.set(showPassword);
    }

    public boolean isAllowCopy() {
        return allowCopy.get();
    }

    /**
     * Specifies if copying the password field text is allowed.
     */
    public BooleanProperty allowCopyProperty() {
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
    public BooleanProperty allowCutProperty() {
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
    public BooleanProperty allowPasteProperty() {
        return allowPaste;
    }

    public void setAllowPaste(boolean allowPaste) {
        this.allowPaste.set(allowPaste);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXPasswordFieldSkin(this, passwordProperty());
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
