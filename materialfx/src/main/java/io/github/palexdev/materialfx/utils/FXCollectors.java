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

package io.github.palexdev.materialfx.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience class that offers some methods useful on combination with Java {@link Stream}
 * to collect to JavaFX's collections.
 */
public class FXCollectors {

	private FXCollectors() {}

	/**
	 * @return a collector that returns an {@link ObservableSet}
	 */
	public static <T> Collector<T, ?, ObservableSet<T>> toSet() {
		return Collectors.collectingAndThen(Collectors.toSet(), FXCollections::observableSet);
	}

	/**
	 * @return a collector that returns an {@link ObservableList}
	 */
	public static <T> Collector<T, ?, ObservableList<T>> toList() {
		return Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList);
	}

	/**
	 * @return a collector that returns an {@link ObservableMap}
	 */
	public static <T, K, U> Collector<T, ?, ObservableMap<K, U>> toMap(
			Function<T, K> keyMapper,
			Function<T, U> valueMapper
	) {
		return Collectors.collectingAndThen(Collectors.toMap(keyMapper, valueMapper), FXCollections::observableMap);
	}
}
