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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/// A custom implementation of [ObjectBinding] which can be useful for nodes that have text.
///
/// At the time of writing this, and honestly, I don't believe one bit this will change in the future, the only way to
/// measure the width and height of text in JavaFX is to use a dummy Scene with a dummy Text node and make a container
/// layout the text, thus allowing to retrieve its sizes.
/// It sounds like a good workaround for the lack of public APIs from JavaFX
/// (I really don't understand why they keep hiding stuff, it's so stupid), but the reality is that it is a quite expensive
/// operation. Using such a technique in layout methods (e.g., computePrefWidth(), computePrefHeight(), layoutChildren(),...)
/// can have drastic effects on performance.
///
/// Leveraging the fact that JavaFX's bindings are lazy and that the sizes of a text node change only when:
/// 1) the text changes
/// 2) the font changes, we can cache the measurements once and only update them when requested and not valid anymore.
///
///
/// Since computing text sizes will almost always return "raw" values, this cache also offers the possibility of retrieving
/// values that are "pixel snapped". The functions responsible for that are: [#setXSnappingFunction(Function)] and
/// [#setYSnappingFunction(Function)].
public class TextMeasurementCache extends ObjectBinding<Size> {
    //================================================================================
    // Properties
    //================================================================================
    private ObservableValue<String> text;
    private ObservableValue<Font> font;
    private Function<Double, Double> xSnappingFunction = v -> v;
    private Function<Double, Double> ySnappingFunction = v -> v;

    //================================================================================
    // Constructors
    //================================================================================

    /// Constructs a new cache from the given [Labeled] node, using its [Labeled#textProperty()] and
    /// [Labeled#fontProperty()] as dependencies of this binding. The functions responsible for rounding the
    /// computed values are set to: [Region#snapSizeX(double)] and [Region#snapSizeY(double)].
    public TextMeasurementCache(Labeled labeled) {
        this(labeled.textProperty(), labeled.fontProperty());
        xSnappingFunction = labeled::snapSizeX;
        ySnappingFunction = labeled::snapSizeY;
    }

    /// Constructs a new cache from the given [Text] node, using its [Text#textProperty()] and
    /// [Text#fontProperty()] as dependencies of this binding. The functions responsible for rounding the
    /// computed values are not set implicitly since [Text] is not a region.
    public TextMeasurementCache(Text text) {
        this(text.textProperty(), text.fontProperty());
    }

    /// Constructs a new cache from the given properties, which will be then set as the dependencies of this binding.
    /// In this case too, the function responsible for rounding the computed values must be set explicitly.
    public TextMeasurementCache(ObservableValue<String> textProperty, ObservableValue<Font> fontProperty) {
        this.text = textProperty;
        this.font = fontProperty;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        bind(text, font);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Size computeValue() {
        return TextUtils.computeTextSizes(font.getValue(), text.getValue());
    }

    @Override
    public void dispose() {
        unbind(text, font);
        text = null;
        font = null;
        super.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// Applies the [#getXSnappingFunction()] on the [Size#width()] value of this binding.
    public double getSnappedWidth() {
        return xSnappingFunction.apply(getValue().width());
    }

    /// Applies the [#getYSnappingFunction()] on the [Size#height()] value of this binding.
    public double getSnappedHeight() {
        return ySnappingFunction.apply(getValue().height());
    }

    /// @return the function responsible for rounding the computed width values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel
    public Function<Double, Double> getXSnappingFunction() {
        return xSnappingFunction;
    }

    /// Sets the function responsible for rounding the computed width values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel.
    public void setXSnappingFunction(Function<Double, Double> xSnappingFunction) {
        this.xSnappingFunction = xSnappingFunction;
    }

    /// @return the function responsible for rounding the computed height values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel
    public Function<Double, Double> getYSnappingFunction() {
        return ySnappingFunction;
    }

    /// Sets the function responsible for rounding the computed height values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel.
    public void setYSnappingFunction(Function<Double, Double> ySnappingFunction) {
        this.ySnappingFunction = ySnappingFunction;
    }
}
