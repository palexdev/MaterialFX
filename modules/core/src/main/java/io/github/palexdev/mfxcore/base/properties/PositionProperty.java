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

package io.github.palexdev.mfxcore.base.properties;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.*;

import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.x;
import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.y;
import static io.github.palexdev.mfxcore.base.beans.Position.position;

/// Simple extension of [ReadOnlyObjectWrapper] for [Position] objects.
public class PositionProperty extends ReadOnlyObjectWrapper<Position> {

    //================================================================================
    // Constructors
    //================================================================================
    public PositionProperty() {}

    public PositionProperty(Position initialValue) {
        super(initialValue);
    }

    public PositionProperty(Object bean, String name) {
        super(bean, name);
    }

    public PositionProperty(Object bean, String name, Position initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to create a new [Position] object with the given parameters and set it
    /// as the new value of this property.
    public void setPosition(double x, double y) {
        set(position(x, y));
    }

    /// Convenience method to set only the x value using [Position#x(double)].
    public void setX(double x) {
        set(
            Optional.ofNullable(get())
                .map(p -> p.x(x))
                .orElseGet(() -> x(x))
        );
    }

    /// Convenience method to set only the y value using [Position#y(double)]
    public void setY(double y) {
        set(
            Optional.ofNullable(get())
                .map(p -> p.y(y))
                .orElseGet(() -> y(y))
        );
    }

    /// Null-safe alternative to `get().x()`, if the value is `null` returns 0.0.
    public double getX() {
        return Optional.ofNullable(get())
            .map(Position::x)
            .orElse(0.0);
    }

    /// Null-safe alternative to `get().x()`, if the value is `null` returns the given value.
    public double getX(double or) {
        return Optional.ofNullable(get())
            .map(Position::x)
            .orElse(or);
    }

    /// Null-safe alternative to `get().getY()`, if the value is `null` returns 0.0.
    public double getY() {
        return Optional.ofNullable(get())
            .map(Position::y)
            .orElse(0.0);
    }

    /// Null-safe alternative to `get().getY()`, if the value is `null` returns the given value.
    public double getY(double or) {
        return Optional.ofNullable(get())
            .map(Position::y)
            .orElse(or);
    }

    //================================================================================
    // CSS
    //================================================================================

    /// Convenience method to create a [StyleableProperty] version of this property.
    ///
    /// Now you can set the position without wrapping the values in quotes as I finally found the way to work around the
    /// shitty JavaFX CSS system, seriously, to whatever wrote that spaghetti shit, fuck you.
    ///
    /// Valid values:
    /// - <number>        -> will be used as both the `x` and the `y`
    /// - <number number> -> the first is the `x` the second is the `y` <br >
    /// If you specify more than two values, only the first two will be taken and a warning will be printed.
    ///
    /// The `onInvalidated` parameter is a convenience parameter run every time the property changes, without any listeners,
    /// by overriding the [StyleableObjectProperty#invalidated()] method directly.
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static StyleableObjectProperty<Position> styleableProperty(
        CssMetaData<? extends Styleable, Position> metaData,
        Object bean,
        String name,
        Position initialValue,
        Consumer<Position> onInvalidated
    ) {
        return PropUtils.objectProperty()
            .bean(bean)
            .name(name)
            .initialValue(initialValue)
            .onInvalidated(o -> Optional.ofNullable(onInvalidated)
                .ifPresent(c -> c.accept(((Position) o)))
            )
            .asStyleable(((CssMetaData) metaData), (_, newValue) ->
                switch (newValue) {
                    case Position p -> p;
                    case Number n -> position(n.doubleValue(), n.doubleValue());
                    case Number[] arr -> {
                        if (arr.length > 2)
                            System.err.println("Expected 2 or less values for position, got " + Arrays.toString(arr) + " instead.");
                        yield position(arr[0].doubleValue(), arr[1].doubleValue());
                    }
                    default ->
                        throw new IllegalArgumentException("Expected number or array for Position, got " + newValue + " instead.");
                }
            );
    }

    /// Delegates to [#styleableProperty(CssMetaData, Object, String, Position, Consumer)] with an empty consumer.
    public static StyleableObjectProperty<Position> styleableProperty(
        CssMetaData<? extends Styleable, Position> metaData,
        Object bean,
        String name,
        Position initialValue
    ) {
        return styleableProperty(metaData, bean, name, initialValue, null);
    }

    /// Convenience method to create [CssMetaData] for a property generated by [#styleableProperty(CssMetaData, Object, String, Position)].
    public static <S extends Styleable> CssMetaData<S, Position> cssMetaData(
        String property,
        Function<S, StyleableObjectProperty<Position>> styleableProperty,
        Position initialValue
    ) {
        return new CssMetaData<>(property, PositionConverter.instance(), initialValue) {
            @Override
            public boolean isSettable(S styleable) {
                return !styleableProperty.apply(styleable).isBound();
            }

            @Override
            public StyleableProperty<Position> getStyleableProperty(S styleable) {
                return styleableProperty.apply(styleable);
            }
        };
    }

    /// The [CssMetaData] needs a converter, otherwise the JavaFX shit system complains, but this does nothing :)
    @SuppressWarnings("rawtypes")
    public static class PositionConverter extends StyleConverter<ParsedValue[], Position> {
        private static final PositionConverter INSTANCE = new PositionConverter();

        public static PositionConverter instance() {
            return INSTANCE;
        }
    }
}
