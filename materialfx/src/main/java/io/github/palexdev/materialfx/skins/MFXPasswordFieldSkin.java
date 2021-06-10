/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXContextMenu;
import io.github.palexdev.materialfx.controls.MFXContextMenuItem;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MFXPasswordFieldSkin extends MFXTextFieldSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final StringBuilder sb = new StringBuilder();
    private final StringProperty fakeText = new SimpleStringProperty("");
    private final StringProperty password;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPasswordFieldSkin(MFXPasswordField passwordField, StringProperty password) {
        super(passwordField);
        this.password = password;

        if (!passwordField.getText().isEmpty()) {
            sb.append(passwordField.getText());
            for (int i = 0; i < passwordField.getText().length(); i++) {
                setFakeText(getFakeText().concat(passwordField.getHideCharacter()));
                setPassword(sb.toString());
            }
            passwordField.selectedTextProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.isEmpty()) {
                        passwordField.deselect();
                        passwordField.positionCaret(passwordField.getText().length());
                        passwordField.selectedTextProperty().removeListener(this);
                    }
                }
            });
        }

        setContextMenu();
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXPasswordField#hideCharacterProperty()}: to change the mask character when changes.
     * <p></p>
     * Adds bindings for:
     * <p>
     * <p> - text property: to update the field text when {@link MFXPasswordField#showPasswordProperty()} changes
     * and when the user inputs new characters.
     * <p></p>
     * Adds event filters/handlers for:
     * <p>
     * <p> - MOUSE_PRESSED: to select all the text when double click occurs, consumes the event.
     * <p> - KEY_TYPED: to listen to the input characters and update the password and the shown text.
     * <p> - KEY_PRESSED: to allow "navigation" with arrows, and make all the shortcuts work.
     */
    private void setListeners() {
        MFXPasswordField passwordField = (MFXPasswordField) getSkinnable();

        passwordField.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!passwordField.isShowPassword()) {
                if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
                    passwordField.selectAll();
                    passwordField.requestFocus();
                    event.consume();
                }
            }
        });

        passwordField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!isInvalidCharacter(keyEvent.getCharacter().charAt(0))) {
                if (passwordField.getSelection().getLength() > 0) {
                    handleDeletion(passwordField.getText().length());
                }

                sb.append(keyEvent.getCharacter());
                setFakeText(getFakeText().concat(passwordField.getHideCharacter()));
                setPassword(sb.toString());
                passwordField.positionCaret(passwordField.getText().length());
            }
            keyEvent.consume();
        });

        passwordField.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCode code = keyEvent.getCode();

            if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                int pos = passwordField.getCaretPosition();
                int removeIndex = pos - 1;
                handleDeletion(removeIndex);
            }
            if (keyEvent.getCode() == KeyCode.DELETE) {
                int pos = passwordField.getCaretPosition();
                handleDeletion(pos);
            }

            switch (code) {
                case UP:
                case END:
                    passwordField.positionCaret(getPassword().length());
                    break;
                case DOWN:
                case HOME:
                    passwordField.positionCaret(0);
                    break;
                case LEFT:
                    if (keyEvent.isShiftDown()) {
                        passwordField.selectBackward();
                    } else {
                        passwordField.positionCaret(passwordField.getCaretPosition() - 1);
                    }
                    break;
                case RIGHT:
                    if (keyEvent.isShiftDown()) {
                        passwordField.selectForward();
                    } else {
                        passwordField.positionCaret(passwordField.getCaretPosition() + 1);
                    }
                    break;
                case A: {
                    if (keyEvent.isControlDown()) {
                        passwordField.selectAll();
                    }
                    break;
                }
                case C:
                    if (keyEvent.isControlDown() && passwordField.isAllowCopy()) {
                        passwordField.copy();
                    }
                    break;
                case D:
                    if (keyEvent.isControlDown()) {
                        handleDeletion(passwordField.getText().length());
                    }
                    break;
                case V:
                    if (keyEvent.isControlDown() && passwordField.isAllowPaste()) {
                        handlePaste();
                    }
                    break;
                case X:
                    if (keyEvent.isControlDown() && passwordField.isAllowCut()) {
                        passwordField.cut();
                        handleDeletion(passwordField.getText().length());
                    }
                default:
                    break;
            }

            keyEvent.consume();
        });

        passwordField.textProperty().bind(Bindings.createStringBinding(
                () -> passwordField.isShowPassword() ? getPassword() : getFakeText(),
                passwordField.showPasswordProperty(), fakeText, password
        ));
        passwordField.hideCharacterProperty().addListener((observable, oldValue, newValue) -> fakeText.set(getFakeText().replace(oldValue, newValue)));
    }

    /**
     * Handles the past action by checking that the clipboard is not empty,
     * by removing the selected text (if there is), ensures that the pasted text
     * is inserted at the right position and ensures that the caret is at the right index.
     */
    private void handlePaste() {
        MFXPasswordField passwordField = (MFXPasswordField) getSkinnable();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = clipboard.getString();
        if (!content.trim().isEmpty()) {
            if (passwordField.getSelection().getLength() > 0) {
                handleDeletion(passwordField.getText().length());
            }

            int caretPos = passwordField.getCaretPosition();
            int end = caretPos + content.length();
            sb.insert(caretPos, content);
            for (int i = 0; i < content.length(); i++) {
                setFakeText(getFakeText().concat(passwordField.getHideCharacter()));
                setPassword(sb.toString());
            }
            passwordField.positionCaret(end);
        }
    }

    /**
     * Handles the deletion of text.
     */
    private void handleDeletion(int pos) {
        MFXPasswordField passwordField = (MFXPasswordField) getSkinnable();

        if (!passwordField.getSelectedText().isEmpty()) {
            IndexRange range = passwordField.getSelection();
            int start = range.getStart();
            int end = range.getEnd();

            setPassword(sb.delete(start, end).toString());
            StringBuilder tmp = new StringBuilder(getFakeText());
            setFakeText(tmp.delete(start, end).toString());
            passwordField.positionCaret(pos);
            return;
        }

        if (pos >= 0 && pos < getPassword().length()) {
            setPassword(sb.deleteCharAt(pos).toString());
            StringBuilder tmp = new StringBuilder(getFakeText());
            setFakeText(tmp.deleteCharAt(pos).toString());
            passwordField.positionCaret(pos);
        }
    }

    /**
     * Checks if the typed character is valid.
     */
    private static boolean isInvalidCharacter(char c) {
        if (c == 0x7F) return true;
        if (c == 0xA) return true;
        if (c == 0x9) return true;
        return c < 0x20;
    }

    /**
     * Sets the default {@link MFXContextMenu} for the password field.
     */
    protected void setContextMenu() {
        MFXPasswordField passwordField = (MFXPasswordField) getSkinnable();

        MFXContextMenuItem copy = new MFXContextMenuItem()
                .setIcon(new MFXFontIcon("mfx-content-copy", 14))
                .setText("Copy")
                .setAccelerator("Ctrl + C")
                .setAction(event -> {
                    if (passwordField.isAllowCopy()) {
                        passwordField.copy();
                    }
                });

        MFXContextMenuItem cut = new MFXContextMenuItem()
                .setIcon(new MFXFontIcon("mfx-content-cut", 14))
                .setText("Cut")
                .setAccelerator("Ctrl + X")
                .setAction(event -> {
                    if (passwordField.isAllowCut()) {
                        passwordField.cut();
                        handleDeletion(passwordField.getText().length());
                    }
                });

        MFXContextMenuItem paste = new MFXContextMenuItem()
                .setIcon(new MFXFontIcon("mfx-content-paste", 14))
                .setText("Paste")
                .setAccelerator("Ctrl + V")
                .setAction(event -> {
                    if (passwordField.isAllowPaste()) {
                        handlePaste();
                    }
                });

        MFXContextMenuItem delete = new MFXContextMenuItem()
                .setIcon(new MFXFontIcon("mfx-delete-alt", 16))
                .setText("Delete")
                .setAccelerator("Ctrl + D")
                .setAction(event -> handleDeletion(passwordField.getText().length()));

        MFXContextMenuItem selectAll = new MFXContextMenuItem()
                .setIcon(new MFXFontIcon("mfx-select-all", 16))
                .setText("Select All")
                .setAccelerator("Ctrl + A")
                .setAction(event -> passwordField.selectAll());

        passwordField.setMFXContextMenu(
                MFXContextMenu.Builder.build(passwordField)
                        .addMenuItem(copy)
                        .addMenuItem(cut)
                        .addMenuItem(paste)
                        .addMenuItem(delete)
                        .addSeparator()
                        .addMenuItem(selectAll)
                        .install()
        );
    }

    /**
     * @return the masked text
     */
    public String getFakeText() {
        return fakeText.get();
    }

    /**
     * Sets the masked text
     */
    public void setFakeText(String fakeText) {
        this.fakeText.set(fakeText);
    }

    /**
     * @return the password/un-masked text
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Sets the password
     */
    public void setPassword(String password) {
        this.password.set(password);
    }
}