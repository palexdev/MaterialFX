package io.github.palexdev.mfxcore.base.properties.styleable;

import io.github.palexdev.mfxcore.base.beans.Position;
import javafx.css.*;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Convenience {@link StyleableObjectProperty} for {@link io.github.palexdev.mfxcore.base.beans.Position}, settable via CSS thanks to
 * {@link StyleablePositionProperty.PositionConverter}.
 */
public class StyleablePositionProperty extends StyleableObjectProperty<Position> {

    //================================================================================
    // Constructors
    //================================================================================
    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData) {
        super(cssMetaData);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Position initialValue) {
        super(cssMetaData, initialValue);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Object bean, String name) {
        super(cssMetaData, bean, name);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Object bean, String name, Position initialValue) {
        super(cssMetaData, bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void setPosition(double x, double y) {
        set(Position.of(x, y));
    }

    @Override
    public void applyStyle(StyleOrigin origin, Position v) {
        if (v == null) return;
        super.applyStyle(origin, v);
    }

    public static <S extends Styleable> CssMetaData<S, Position> metaDataFor(
        String propId, Function<S, StyleablePositionProperty> property, Position initialValue
    ) {
        return new CssMetaData<>(propId, PositionConverter.getInstance(), initialValue) {
            @Override
            public boolean isSettable(S styleable) {
                return !property.apply(styleable).isBound();
            }

            @Override
            public StyleableProperty<Position> getStyleableProperty(S styleable) {
                return property.apply(styleable);
            }
        };
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * Style converter implementation to make {@link Position} settable via CSS.
     * The related property is {@link StyleablePositionProperty}.
     * <p>
     * For this to properly work, you must use a specific format. The converter expects a string value,
     * with two double numbers which will be in order the x and the y for the new {@code Position}, so:
     * <pre>
     * {@code
     * .node {
     *     -fx-property-name: "100 30";
     * }
     * }
     * </pre>
     */

    public static class PositionConverter extends StyleConverter<String, Position> {

        // lazy, thread-safe instantiation
        private static class Holder {
            static final PositionConverter INSTANCE = new PositionConverter();
        }

        /**
         * Gets the {@code SizeConverter} instance.
         *
         * @return the {@code SizeConverter} instance
         */
        public static StyleConverter<String, Position> getInstance() {
            return Holder.INSTANCE;
        }

        private PositionConverter() {
            super();
        }

        @Override
        public Position convert(ParsedValue<String, Position> value, Font font) {
            try {
                double[] sizes = Arrays.stream(value.getValue().split(" "))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
                return Position.of(sizes[0], sizes[1]);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return "PositionConverter";
        }
    }
}
