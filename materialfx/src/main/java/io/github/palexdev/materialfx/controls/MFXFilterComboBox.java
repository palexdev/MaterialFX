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
import io.github.palexdev.materialfx.skins.MFXFilterComboBoxSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;

/**
 * This combo box allows to filter the items shown in the popup's listview.
 * <p>
 * Extends {@code MFXComboBox} and redefines the style class to "mfx-filter-combo-box".
 *
 * @param <T> The type of the value that has been selected
 * @see MFXComboBox
 */
public class MFXFilterComboBox<T> extends MFXComboBox<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-filter-combo-box";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFilterComboBox.css");

    private static final PseudoClass EDITOR_FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("editor");
    private final BooleanProperty editorFocused = new SimpleBooleanProperty();

    private final MFXFilterComboBoxSkin<T> mfxFilterComboBoxSkin = new MFXFilterComboBoxSkin<>(this);
    /**
     * When the popup is shown and the text field is added to the scene the text field is not focused,
     * to change this behavior and force it to be focused you can set this boolean to true.
     * <p>
     * For more details see {@link MFXFilterComboBoxSkin}
     */
    private boolean forceFieldFocusOnShow = false;
    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterComboBox() {
        initialize();
    }

    public MFXFilterComboBox(ObservableList<T> items) {
        super(items);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        editorFocused.addListener(invalidated -> pseudoClassStateChanged(EDITOR_FOCUSED_PSEUDO_CLASS, editorFocused.get()));
    }

    public boolean isForceFieldFocusOnShow() {
        return forceFieldFocusOnShow;
    }

    public void setForceFieldFocusOnShow(boolean forceFieldFocusOnShow) {
        this.forceFieldFocusOnShow = forceFieldFocusOnShow;
    }

    public boolean isEditorFocused() {
        return editorFocused.get();
    }

    public MFXFilterComboBoxSkin<T> getComboBoxSkin() {
        return mfxFilterComboBoxSkin;
    }
    /**
     * Bound to the editor focus property. This allows to keep the focused style specified
     * by css when the focus is acquired by the editor. The PseudoClass to use in css is ":editor"
     */
    public BooleanProperty editorFocusedProperty() {
        return editorFocused;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return mfxFilterComboBoxSkin;
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
