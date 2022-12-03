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

package io.github.palexdev.mfxcore.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Simple implementation of a Memoizer.
 * <p></p>
 * Memoization is a technique to store the results of expensive function calls and returning the cached result
 * when the same inputs occur again.
 * <p></p>
 * Given a {@code Function<T, U> fn}, use {@code Memoizer.memoize(fn)} to obtain a new function {@code Function<T, U> mfn}
 * which you can use from now on to store/cache the results.
 */
public class Memoizer<T, U> {
	public final Map<T, U> cache = new HashMap<>();

	public Function<T, U> doMemoize(final Function<T, U> function) {
		return value -> cache.computeIfAbsent(value, function);
	}

	public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
		return new Memoizer<T, U>().doMemoize(function);
	}
}