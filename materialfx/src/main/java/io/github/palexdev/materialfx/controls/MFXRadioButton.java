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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXRadioButtonSkin;
import javafx.css.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * This is the implementation of a radio button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code RadioButton}, redefines the style class to "mfx-radio-button" for usage in CSS and
 * includes a {@code RippleGenerator} to generate ripple effects on click.
 */
public class MFXRadioButton extends RadioButton {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXRadioButton> FACTORY = new StyleablePropertyFactory<>(RadioButton.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-radio-button";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-radiobutton.css").toString();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRadioButton() {
        setText("RadioButton");
        initialize();
    }

    public MFXRadioButton(String s) {
        super(s);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_COLOR,
            this,
            "selectedColor",
            Color.rgb(15, 157, 88)
    );

    private final StyleableObjectProperty<Paint> unSelectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_COLOR,
            this,
            "unSelectedColor",
            Color.rgb(90, 90, 90)
    );

    private final StyleableObjectProperty<Paint> selectedTextColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_TEXT_COLOR,
            this,
            "selectedTextColor",
            Color.rgb(15, 157, 88)
    );

    private final StyleableObjectProperty<Paint> unSelectedTextColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_TEXT_COLOR,
            this,
            "unSelectedTextColor",
            Color.rgb(0, 0, 0)
    );

    private final StyleableBooleanProperty changeTextColor = new SimpleStyleableBooleanProperty(
            StyleableProperties.CHANGE_TEXT_COLOR,
            this,
            "changeTextColor",
            true
    );

    public Paint getSelectedColor() {
        return selectedColor.get();
    }

    public StyleableObjectProperty<Paint> selectedColorProperty() {
        return selectedColor;
    }

    public void setSelectedColor(Paint selectedColor) {
        this.selectedColor.set(selectedColor);
    }

    public Paint getUnSelectedColor() {
        return unSelectedColor.get();
    }

    public StyleableObjectProperty<Paint> unSelectedColorProperty() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(Paint unSelectedColor) {
        this.unSelectedColor.set(unSelectedColor);
    }

    public Paint getSelectedTextColor() {
        return selectedTextColor.get();
    }

    public StyleableObjectProperty<Paint> selectedTextColorProperty() {
        return selectedTextColor;
    }

    public void setSelectedTextColor(Paint selectedTextColor) {
        this.selectedTextColor.set(selectedTextColor);
    }

    public Paint getUnSelectedTextColor() {
        return unSelectedTextColor.get();
    }

    public StyleableObjectProperty<Paint> unSelectedTextColorProperty() {
        return unSelectedTextColor;
    }

    public void setUnSelectedTextColor(Paint unSelectedTextColor) {
        this.unSelectedTextColor.set(unSelectedTextColor);
    }

    public boolean isChangeTextColor() {
        return changeTextColor.get();
    }

    public StyleableBooleanProperty changeTextColorProperty() {
        return changeTextColor;
    }

    public void setChangeTextColor(boolean changeTextColor) {
        this.changeTextColor.set(changeTextColor);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXRadioButton, Paint> SELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-color",
                        MFXRadioButton::selectedColorProperty,
                        Color.rgb(15, 157, 88)
                );

        private static final CssMetaData<MFXRadioButton, Paint> UNSELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-color",
                        MFXRadioButton::unSelectedColorProperty,
                        Color.rgb(90, 90, 90)
                );

        private static final CssMetaData<MFXRadioButton, Paint> SELECTED_TEXT_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-text-color",
                        MFXRadioButton::selectedTextColorProperty,
                        Color.rgb(15, 157, 88)
                );

        private static final CssMetaData<MFXRadioButton, Paint> UNSELECTED_TEXT_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-text-color",
                        MFXRadioButton::unSelectedTextColorProperty,
                        Color.rgb(0, 0, 0)
                );

        private static final CssMetaData<MFXRadioButton, Boolean> CHANGE_TEXT_COLOR =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-change-text-color",
                        MFXRadioButton::changeTextColorProperty,
                        true
                );

        static {
            cssMetaDataList = List.of(
                    SELECTED_COLOR, UNSELECTED_COLOR,
                    SELECTED_TEXT_COLOR, UNSELECTED_TEXT_COLOR,
                    CHANGE_TEXT_COLOR
            );
        }

    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXRadioButtonSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXRadioButton.getControlCssMetaDataList();
    }
}
