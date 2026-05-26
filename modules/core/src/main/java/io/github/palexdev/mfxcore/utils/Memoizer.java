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

package io.github.palexdev.mfxcore.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/// Simple implementation of a Memoizer.
///
/// Memoization is a technique to store the results of expensive function calls by returning the cached result
/// when the same inputs occur again.
///
/// Given a `Function<T, U> fn`, use `Memoizer.memoize(fn)` to get a new function `Function<T, U> mfn`
/// which you can use from now on to store/cache the results.
@Deprecated(forRemoval = true)
public class Memoizer<T, U> {
    public final Map<T, U> cache = new HashMap<>();

    public Function<T, U> doMemoize(final Function<T, U> function) {
        return value -> cache.computeIfAbsent(value, function);
    }

    public static <T, U> Function<T, U> memoize(Function<T, U> function) {
        return new Memoizer<T, U>().doMemoize(function);
    }

    /// This is here because the mechanism is similar to memoized functions. However, since a [Supplier] has no inputs,
    /// the produced value is simply stored in an anonymous instance of it, and **it's expected to always be the same**.
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<>() {
            T cached;

            @Override
            public T get() {
                if (cached == null) cached = supplier.get();
                return cached;
            }
        };
    }
}