/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.Comparator;
import java.util.function.*;

import io.github.palexdev.mfxcore.base.TriFunction;
import io.github.palexdev.mfxcore.base.properties.base.ExtendedProperty;
import io.github.palexdev.mfxcore.base.properties.functional.*;
import io.github.palexdev.mfxcore.base.properties.styleable.*;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

/// Convenience methods related to properties.
public class PropUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private PropUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    // Functional property methods
    public static <T> ComparatorProperty<T> compare(Comparator<T> comparator) {
        return new ComparatorProperty<>(comparator);
    }

    public static <T> ConsumerProperty<T> consume(Consumer<T> consumer) {
        return new ConsumerProperty<>(consumer);
    }

    public static <T, R> FunctionProperty<T, R> function(Function<T, R> function) {
        return new FunctionProperty<>(function);
    }

    public static <T> PredicateProperty<T> predicate(Predicate<T> predicate) {
        return new PredicateProperty<>(predicate);
    }

    public static <T> SupplierProperty<T> supply(Supplier<T> supplier) {
        return new SupplierProperty<>(supplier);
    }

    public static <T, U> BiConsumerProperty<T, U> biConsume(BiConsumer<T, U> biConsumer) {
        return new BiConsumerProperty<>(biConsumer);
    }

    public static <T, U, R> BiFunctionProperty<T, U, R> biFunction(BiFunction<T, U, R> biFunction) {
        return new BiFunctionProperty<>(biFunction);
    }

    public static <T, U> BiPredicateProperty<T, U> biPredicate(BiPredicate<T, U> biPredicate) {
        return new BiPredicateProperty<>(biPredicate);
    }

    public static <T, U, V, R> TriFunctionProperty<T, U, V, R> triFunction(TriFunction<T, U, V, R> function) {
        return new TriFunctionProperty<>(function);
    }

    // Static convenience methods (backwards compatibility)
    public static DoubleProperty mappedDoubleProperty(Function<Double, Double> mapper) {
        return doubleProperty().mapper(mapper).build();
    }

    public static FloatProperty mappedFloatProperty(Function<Float, Float> valMapper) {
        return floatProperty().mapper(valMapper).build();
    }

    public static IntegerProperty mappedIntProperty(Function<Integer, Integer> valMapper) {
        return intProperty().mapper(valMapper).build();
    }

    public static LongProperty mappedLongProperty(Function<Long, Long> valMapper) {
        return longProperty().mapper(valMapper).build();
    }

    public static StringProperty mappedStringProperty(Function<String, String> valMapper) {
        return stringProperty().mapper(valMapper).build();
    }

    public static <T> ObjectProperty<T> mappedObjectProperty(Function<T, T> valMapper) {
        return PropUtils.<T>objectProperty().mapper(valMapper).build();
    }

    public static DoubleProperty clampedDoubleProperty(Supplier<Double> min, Supplier<Double> max) {
        return doubleProperty().mapper(val -> NumberUtils.clamp(val, min.get(), max.get())).build();
    }

    public static FloatProperty clampedFloatProperty(Supplier<Float> min, Supplier<Float> max) {
        return floatProperty().mapper(val -> NumberUtils.clamp(val, min.get(), max.get())).build();
    }

    public static IntegerProperty clampedIntProperty(Supplier<Integer> min, Supplier<Integer> max) {
        return intProperty().mapper(val -> NumberUtils.clamp(val, min.get(), max.get())).build();
    }

    public static LongProperty clampedLongProperty(Supplier<Long> min, Supplier<Long> max) {
        return longProperty().mapper(val -> NumberUtils.clamp(val, min.get(), max.get())).build();
    }

    @SuppressWarnings("unchecked")
    private static <N extends Number> ExtendedProperty<N> wrapNumber(Property<Number> property, N defaultValue) {
        return (ExtendedProperty<N>) ExtendedProperty.wrap(property, defaultValue);
    }

    //================================================================================
    // Builders
    //================================================================================

    public static DoublePropertyBuilder doubleProperty() {
        return new DoublePropertyBuilder();
    }

    public static FloatPropertyBuilder floatProperty() {
        return new FloatPropertyBuilder();
    }

    public static IntegerPropertyBuilder intProperty() {
        return new IntegerPropertyBuilder();
    }

    public static LongPropertyBuilder longProperty() {
        return new LongPropertyBuilder();
    }

    public static StringPropertyBuilder stringProperty() {
        return new StringPropertyBuilder();
    }

    public static <T> ObjectPropertyBuilder<T> objectProperty() {
        return new ObjectPropertyBuilder<>();
    }

    public static class DoublePropertyBuilder {
        private final PropertyConfig<Double> config = new PropertyConfig<>();

        public DoublePropertyBuilder name(String name) {
            config.name = name;
            return this;
        }

        public DoublePropertyBuilder bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public DoublePropertyBuilder initialValue(Double initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public DoublePropertyBuilder mapper(Function<Double, Double> mapper) {
            config.mapper = mapper;
            return this;
        }

        public DoublePropertyBuilder onInvalidated(Consumer<Double> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public DoubleProperty build() {
            return new SimpleDoubleProperty(config.bean, config.name, config.initialValue != null ? config.initialValue : 0.0) {
                @Override
                public void set(double newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<Double> extended(Double defaultValue) {
            return wrapNumber(build(), config.mapped(defaultValue));
        }

        public StyleableDoubleProperty asStyleable(
            CssMetaData<? extends Styleable, Number> metaData, BiFunction<StyleOrigin, Double, Double> applyStyle
        ) {
            return new StyleableDoubleProperty(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(double newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, Number newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue.doubleValue());
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    public static class FloatPropertyBuilder {
        private final PropertyConfig<Float> config = new PropertyConfig<>();

        public FloatPropertyBuilder name(String name) {
            config.name = name;
            return this;
        }

        public FloatPropertyBuilder bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public FloatPropertyBuilder initialValue(Float initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public FloatPropertyBuilder mapper(Function<Float, Float> mapper) {
            config.mapper = mapper;
            return this;
        }

        public FloatPropertyBuilder onInvalidated(Consumer<Float> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public FloatProperty build() {
            return new SimpleFloatProperty(config.bean, config.name, config.initialValue != null ? config.initialValue : 0.0f) {
                @Override
                public void set(float newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<Float> extended(Float defaultValue) {
            return wrapNumber(build(), config.mapped(defaultValue));
        }

        public StyleableFloatProperty asStyleable(
            CssMetaData<? extends Styleable, Number> metaData, BiFunction<StyleOrigin, Float, Float> applyStyle
        ) {
            return new StyleableFloatProperty(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(float newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, Number newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue.floatValue());
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    public static class IntegerPropertyBuilder {
        private final PropertyConfig<Integer> config = new PropertyConfig<>();

        public IntegerPropertyBuilder name(String name) {
            config.name = name;
            return this;
        }

        public IntegerPropertyBuilder bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public IntegerPropertyBuilder initialValue(Integer initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public IntegerPropertyBuilder mapper(Function<Integer, Integer> mapper) {
            config.mapper = mapper;
            return this;
        }

        public IntegerPropertyBuilder onInvalidated(Consumer<Integer> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public IntegerProperty build() {
            return new SimpleIntegerProperty(config.bean, config.name, config.initialValue != null ? config.initialValue : 0) {
                @Override
                public void set(int newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<Integer> extended(Integer defaultValue) {
            return wrapNumber(build(), config.mapped(defaultValue));
        }

        public StyleableIntegerProperty asStyleable(
            CssMetaData<? extends Styleable, Number> metaData, BiFunction<StyleOrigin, Integer, Integer> applyStyle
        ) {
            return new StyleableIntegerProperty(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(int newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, Number newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue.intValue());
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    public static class LongPropertyBuilder {
        private final PropertyConfig<Long> config = new PropertyConfig<>();

        public LongPropertyBuilder name(String name) {
            config.name = name;
            return this;
        }

        public LongPropertyBuilder bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public LongPropertyBuilder initialValue(Long initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public LongPropertyBuilder mapper(Function<Long, Long> mapper) {
            config.mapper = mapper;
            return this;
        }

        public LongPropertyBuilder onInvalidated(Consumer<Long> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public LongProperty build() {
            return new SimpleLongProperty(config.bean, config.name, config.initialValue != null ? config.initialValue : 0L) {
                @Override
                public void set(long newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<Long> extended(Long defaultValue) {
            return wrapNumber(build(), config.mapped(defaultValue));
        }

        public StyleableLongProperty asStyleable(
            CssMetaData<? extends Styleable, Number> metaData, BiFunction<StyleOrigin, Long, Long> applyStyle
        ) {
            return new StyleableLongProperty(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(long newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, Number newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue.longValue());
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    public static class StringPropertyBuilder {
        private final PropertyConfig<String> config = new PropertyConfig<>();

        public StringPropertyBuilder name(String name) {
            config.name = name;
            return this;
        }

        public StringPropertyBuilder bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public StringPropertyBuilder initialValue(String initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public StringPropertyBuilder mapper(Function<String, String> mapper) {
            config.mapper = mapper;
            return this;
        }

        public StringPropertyBuilder onInvalidated(Consumer<String> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public StringProperty build() {
            return new SimpleStringProperty(config.bean, config.name, config.initialValue) {
                @Override
                public void set(String newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<String> extended(String defaultValue) {
            return ExtendedProperty.wrap(build(), config.mapped(defaultValue));
        }

        public StyleableStringProperty asStyleable(
            CssMetaData<? extends Styleable, String> metaData, BiFunction<StyleOrigin, String, String> applyStyle
        ) {
            return new StyleableStringProperty(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(String newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, String newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue);
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    public static class ObjectPropertyBuilder<T> {
        private final PropertyConfig<T> config = new PropertyConfig<>();

        public ObjectPropertyBuilder<T> name(String name) {
            config.name = name;
            return this;
        }

        public ObjectPropertyBuilder<T> bean(Object bean) {
            config.bean = bean;
            return this;
        }

        public ObjectPropertyBuilder<T> initialValue(T initialValue) {
            config.initialValue = initialValue;
            return this;
        }

        public ObjectPropertyBuilder<T> mapper(Function<T, T> mapper) {
            config.mapper = mapper;
            return this;
        }

        public ObjectPropertyBuilder<T> onInvalidated(Consumer<T> onInvalidated) {
            config.onInvalidated = onInvalidated;
            return this;
        }

        public ObjectProperty<T> build() {
            return new SimpleObjectProperty<>(config.bean, config.name, config.initialValue) {
                @Override
                public void set(T newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }
            };
        }

        /// Builds the property and wraps it in an [ExtendedProperty] with the given default value. Note that the
        /// default value goes through the configured mapper too, if any.
        public ExtendedProperty<T> extended(T defaultValue) {
            return ExtendedProperty.wrap(build(), config.mapped(defaultValue));
        }

        public StyleableObjectProperty<T> asStyleable(
            CssMetaData<? extends Styleable, T> metaData, BiFunction<StyleOrigin, T, T> applyStyle) {
            return new StyleableObjectProperty<>(metaData, config.bean, config.name, config.initialValue) {
                @Override
                public void set(T newValue) {
                    super.set(config.mapper != null ? config.mapper.apply(newValue) : newValue);
                }

                @Override
                protected void invalidated() {
                    if (config.onInvalidated != null) config.onInvalidated.accept(get());
                }

                @Override
                public void applyStyle(StyleOrigin origin, T newValue) {
                    if (applyStyle != null) newValue = applyStyle.apply(origin, newValue);
                    super.applyStyle(origin, newValue);
                }
            };
        }
    }

    // Common configuration holder using composition
    protected static class PropertyConfig<T> {
        String name;
        Object bean;
        T initialValue;
        Function<T, T> mapper;
        Consumer<T> onInvalidated;

        T mapped(T value) {
            return mapper != null ? mapper.apply(value) : value;
        }
    }
}