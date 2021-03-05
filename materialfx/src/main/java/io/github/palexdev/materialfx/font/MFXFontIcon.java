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

package io.github.palexdev.materialfx.font;

import javafx.css.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Class used for MaterialFX font icon resources.
 */
public class MFXFontIcon extends Text {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXFontIcon> FACTORY = new StyleablePropertyFactory<>(Text.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-font-icon";

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFontIcon() {
        initialize();
    }

    public MFXFontIcon(String description) {
        this(description, 10);
    }

    public MFXFontIcon(String description, Color color) {
        this(description, 10, color);
    }

    public MFXFontIcon(String description, double size) {
        this(description, size, Color.rgb(117, 117, 117));
    }

    public MFXFontIcon(String description, double size, Color color) {
        initialize();

        setDescription(description);
        setSize(size);
        setColor(color);

        setText(String.valueOf(FontHandler.getCode(description)));
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setFont(FontHandler.getResources());
        setFontSmoothingType(FontSmoothingType.GRAY);

        sizeProperty().addListener((observable, oldValue, newValue) -> {
            Font font = getFont();
            setFont(Font.font(font.getFamily(), newValue.doubleValue()));
        });

        colorProperty().addListener((observable, oldValue, newValue) -> setFill(newValue));

        descriptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                final char character = FontHandler.getCode(newValue);
                setText(String.valueOf(character));
            }
        });
    }

    /**
     * Specifies the icon code of the icon.
     */
    private final StyleableStringProperty description = new SimpleStyleableStringProperty(
            StyleableProperties.DESCRIPTION,
            this,
            "description"
    );

    /**
     * Specifies the size of the icon.
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            this,
            "size",
            10.0
    );

    /**
     * Specifies the color of the icon.
     */
    private final StyleableObjectProperty<Paint> color = new SimpleStyleableObjectProperty<>(
            StyleableProperties.COLOR,
            this,
            "color",
            Color.rgb(117, 117, 117)
    );

    public String getDescription() {
        return description.get();
    }

    public StyleableStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String code) {
        this.description.set(code);
    }

    public double getSize() {
        return size.get();
    }

    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    public Paint getColor() {
        return color.get();
    }

    public StyleableObjectProperty<Paint> colorProperty() {
        return color;
    }

    public void setColor(Paint color) {
        this.color.set(color);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    public static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXFontIcon, String> DESCRIPTION =
                FACTORY.createStringCssMetaData(
                        "-mfx-icon-code",
                        MFXFontIcon::descriptionProperty
                );

        private static final CssMetaData<MFXFontIcon, Number> SIZE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-size",
                        MFXFontIcon::sizeProperty,
                        10
                );

        private static final CssMetaData<MFXFontIcon, Paint> COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-color",
                        MFXFontIcon::colorProperty,
                        Color.rgb(117, 117, 117)
                );

        static {
            cssMetaDataList = List.of(DESCRIPTION, SIZE, COLOR);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return MFXFontIcon.getClassCssMetaDataList();
    }
}
