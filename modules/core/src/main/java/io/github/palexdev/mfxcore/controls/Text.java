/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.controls;

import java.util.List;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.text.FontSmoothingType;

/// Simple extension of [javafx.scene.text.Text] to allow setting the wrapping width property in CSS, as well
/// as setting the default style class to '.text', and the default font smoothing to [FontSmoothingType#LCD].
public class Text extends javafx.scene.text.Text {

    //================================================================================
    // Constructors
    //================================================================================
    public Text() {
        this("");
    }

    public Text(String text) {
        super(text);
        getStyleClass().setAll("text");
        wrappingWidthProperty().bind(cssWrappingWidthProperty());
        StyleUtils.initProperty(fontSmoothingTypeProperty(), FontSmoothingType.LCD);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty cssWrappingWidth = new StyleableDoubleProperty(
        StyleableProperties.WRAPPING_WIDTH,
        this,
        "cssWrappingWidth",
        0.0
    );

    public double getCssWrappingWidth() {
        return cssWrappingWidth.get();
    }

    /// Allows setting the wrapping width in CSS with the property: '-fx-wrapping-width'.
    ///
    ///
    /// It's named like this to avoid conflicts with [#wrappingWidthProperty()], which won't be settable anymore
    /// since it's being bound to this one instead.
    public StyleableDoubleProperty cssWrappingWidthProperty() {
        return cssWrappingWidth;
    }

    public void setCssWrappingWidth(double cssWrappingWidth) {
        this.cssWrappingWidth.set(cssWrappingWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<Text> FACTORY = new StyleablePropertyFactory<>(javafx.scene.text.Text.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<Text, Number> WRAPPING_WIDTH =
            FACTORY.createSizeCssMetaData(
                "-fx-wrapping-width",
                Text::cssWrappingWidthProperty,
                0.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                javafx.scene.text.Text.getClassCssMetaData(),
                WRAPPING_WIDTH
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
