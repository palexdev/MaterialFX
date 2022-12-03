/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import io.github.palexdev.mfxcore.base.TriFunction;
import io.github.palexdev.mfxcore.base.properties.functional.*;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import javafx.beans.property.*;

import java.util.Comparator;
import java.util.function.*;

/**
 * Convenience methods related to properties.
 */
public class PropUtils {

	private PropUtils() {
	}

	public static DoubleProperty mappedDoubleProperty(Function<Double, Double> mapper) {
		return new SimpleDoubleProperty() {
			@Override
			public void set(double newValue) {
				super.set(mapper.apply(newValue));
			}
		};
	}

	public static FloatProperty mappedFloatProperty(Function<Float, Float> valMapper) {
		return new SimpleFloatProperty() {
			@Override
			public void set(float newValue) {
				super.set(valMapper.apply(newValue));
			}
		};
	}

	public static IntegerProperty mappedIntProperty(Function<Integer, Integer> valMapper) {
		return new SimpleIntegerProperty() {
			@Override
			public void set(int newValue) {
				super.set(valMapper.apply(newValue));
			}
		};
	}

	public static LongProperty mappedLongProperty(Function<Long, Long> valMapper) {
		return new SimpleLongProperty() {
			@Override
			public void set(long newValue) {
				super.set(valMapper.apply(newValue));
			}
		};
	}

	public static StringProperty mappedStringProperty(Function<String, String> valMapper) {
		return new SimpleStringProperty() {
			@Override
			public void set(String newValue) {
				super.set(valMapper.apply(newValue));
			}
		};
	}

	public static <T> ObjectProperty<T> mappedObjectProperty(Function<T, T> valMapper) {
		return new SimpleObjectProperty<>() {
			@Override
			public void set(T newValue) {
				super.set(valMapper.apply(newValue));
			}
		};
	}

	public static DoubleProperty clampedDoubleProperty(Supplier<Double> min, Supplier<Double> max) {
		return mappedDoubleProperty(val -> NumberUtils.clamp(val, min.get(), max.get()));
	}

	public static FloatProperty clampedFloatProperty(Supplier<Float> min, Supplier<Float> max) {
		return mappedFloatProperty(val -> NumberUtils.clamp(val, min.get(), max.get()));
	}

	public static IntegerProperty clampedIntProperty(Supplier<Integer> min, Supplier<Integer> max) {
		return mappedIntProperty(val -> NumberUtils.clamp(val, min.get(), max.get()));
	}

	public static LongProperty clampedLongProperty(Supplier<Long> min, Supplier<Long> max) {
		return mappedLongProperty(val -> NumberUtils.clamp(val, min.get(), max.get()));
	}

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
}
