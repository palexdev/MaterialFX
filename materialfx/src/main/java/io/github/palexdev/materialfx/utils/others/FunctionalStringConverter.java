/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.utils.others;

import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * A functional alternative to {@link javafx.util.StringConverter}.
 */
@FunctionalInterface
public interface FunctionalStringConverter<T> {
	T fromString(String s);

	default String toString(T t) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a new {@link StringConverter} which uses the given function
	 * to convert a String to an object of type T
	 */
	static <T> StringConverter<T> converter(Function<String, T> fsFunction) {
		return new StringConverter<>() {
			@Override
			public String toString(T t) {
				throw new UnsupportedOperationException();
			}

			@Override
			public T fromString(String string) {
				return fsFunction.apply(string);
			}
		};
	}

	/**
	 * @return a new {@link StringConverter} which uses the given functions
	 * to convert a String to an object of type T and vice versa.
	 */
	static <T> StringConverter<T> converter(Function<String, T> fsFunction, Function<T, String> tsFunction) {
		return new StringConverter<>() {
			@Override
			public String toString(T t) {
				return t != null ? tsFunction.apply(t) : "";
			}

			@Override
			public T fromString(String string) {
				return fsFunction.apply(string);
			}
		};
	}

	/**
	 * @return a new {@link StringConverter} which is only capable of converting a String
	 * to an object of type T
	 * @throws UnsupportedOperationException when using the toString(T) method
	 */
	static <T> StringConverter<T> from(Function<String, T> fsFunction) {
		return new StringConverter<>() {
			@Override
			public String toString(T t) {
				throw new UnsupportedOperationException();
			}

			@Override
			public T fromString(String string) {
				return fsFunction.apply(string);
			}
		};
	}

	/**
	 * @return a new {@link StringConverter} which is only capable of converting an object
	 * of type T to a String
	 * @throws UnsupportedOperationException when using the fromString(String) method
	 */
	static <T> StringConverter<T> to(Function<T, String> tsFunction) {
		return new StringConverter<>() {
			@Override
			public String toString(T t) {
				return tsFunction.apply(t);
			}

			@Override
			public T fromString(String string) {
				throw new UnsupportedOperationException();
			}
		};
	}
}
