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

package io.github.palexdev.mfxeffects.beans.properties;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.palexdev.mfxeffects.beans.Size;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.*;

/// Simple extension of [ReadOnlyObjectWrapper] for [Size] objects.
public class SizeProperty extends ReadOnlyObjectWrapper<Size> {

    //================================================================================
    // Constructors
    //================================================================================
    public SizeProperty() {}

    public SizeProperty(Size initialValue) {
        super(initialValue);
    }

    public SizeProperty(Object bean, String name) {
        super(bean, name);
    }

    public SizeProperty(Object bean, String name, Size initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to create a new [Size] object with the given parameters and set it
    /// as the new value of this property.
    public void setSize(double w, double h) {
        set(Size.of(w, h));
    }

    /// Convenience method to set only the width using [Size#withWidth(double)]
    public void setWidth(double w) {
        set(
            Optional.ofNullable(get())
                .map(s -> s.withWidth(w))
                .orElseGet(() -> Size.of(w, 0.0))
        );
    }

    /// Convenience method to set only the height using [Size#withHeight(double)].
    public void setHeight(double h) {
        set(
            Optional.ofNullable(get())
                .map(s -> s.withHeight(h))
                .orElseGet(() -> Size.of(0.0, h))
        );
    }

    /// Null-safe alternative to `get().width()`, if the value is `null` returns an invalid width of 0.0.
    public double getWidth() {
        return Optional.ofNullable(get())
            .map(Size::width)
            .orElse(0.0);
    }

    /// Null-safe alternative to `get().height()`, if the value is `null` returns an invalid height of 0.0.
    public double getHeight() {
        return Optional.ofNullable(get())
            .map(Size::height)
            .orElse(0.0);
    }

    //================================================================================
    // CSS
    //================================================================================

    /// Convenience method to create a [StyleableProperty] version of this property.
    ///
    /// Now you can set the size without wrapping the values in quotes as I finally found the way to work around the
    /// shitty JavaFX CSS system, seriously, to whatever wrote that spaghetti shit, fuck you.
    ///
    /// Valid values:
    /// - <number>        -> will be used as both the `width` and the `height`
    /// - <number number> -> the first is the `width` the second is the `height` <br >
    /// If you specify more than two values, only the first two will be taken and a warning will be printed.
    ///
    /// The `onInvalidated` parameter is a convenience parameter run every time the property changes, without any listeners,
    /// by overriding the [StyleableObjectProperty#invalidated()] method directly.
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static StyleableObjectProperty<Size> styleableProperty(
        CssMetaData<? extends Styleable, Size> metaData,
        Object bean,
        String name,
        Size initialValue,
        Consumer<Size> onInvalidated
    ) {
        return new SimpleStyleableObjectProperty(metaData, bean, name, initialValue) {
            @Override
            public void applyStyle(StyleOrigin origin, Object newValue) {
                newValue = switch (newValue) {
                    case Size s -> s;
                    case Number n -> Size.of(n.doubleValue(), n.doubleValue());
                    case Number[] arr -> {
                        if (arr.length > 2)
                            System.err.println("Expected 2 or less values for size, got " + Arrays.toString(arr) + " instead.");
                        yield Size.of(arr[0].doubleValue(), arr[1].doubleValue());
                    }
                    default ->
                        throw new IllegalArgumentException("Expected number or array for Size, got " + newValue + " instead.");
                };
                super.applyStyle(origin, newValue);
            }

            @Override
            protected void invalidated() {
                onInvalidated.accept(((Size) get()));
            }

            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };
    }

    /// Delegates to [#styleableProperty(CssMetaData, Object, String, Size, Consumer)] with an empty consumer.
    public static StyleableObjectProperty<Size> styleableProperty(
        CssMetaData<? extends Styleable, Size> metaData,
        Object bean,
        String name,
        Size initialValue
    ) {
        return styleableProperty(metaData, bean, name, initialValue, _ -> {});
    }

    /// Convenience method to create [CssMetaData] for a property generated by [#styleableProperty(CssMetaData, Object, String, Size)].
    public static <S extends Styleable> CssMetaData<S, Size> cssMetaData(
        String property,
        Function<S, StyleableObjectProperty<Size>> styleableProperty,
        Size initialValue
    ) {
        return new CssMetaData<>(property, SizeConverter.instance(), initialValue) {
            @Override
            public boolean isSettable(S styleable) {
                return !styleableProperty.apply(styleable).isBound();
            }

            @Override
            public StyleableProperty<Size> getStyleableProperty(S styleable) {
                return styleableProperty.apply(styleable);
            }
        };
    }

    /// The [CssMetaData] needs a converter, otherwise the JavaFX shit system complains, but this does nothing :)
    @SuppressWarnings("rawtypes")
    public static class SizeConverter extends StyleConverter<ParsedValue[], Size> {
        private static final SizeConverter INSTANCE = new SizeConverter();

        public static SizeConverter instance() {
            return INSTANCE;
        }
    }
}
