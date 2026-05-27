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

package io.github.palexdev.mfxcore.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/// A bunch of functional utilities.
public class Functions {

    //================================================================================
    // Constructors
    //================================================================================

    private Functions() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// @return a new function that caches and returns the results of the given input function
    /// (not thread-safe!)
    public static <T, R> Function<T, R> cachedFunction(Function<T, R> fn) {
        return new Function<>() {
            private final Map<T, R> cache = new HashMap<>();

            @Override
            public R apply(T t) {
                return cache.computeIfAbsent(t, fn);
            }
        };
    }

    /// Same as [#cachedFunction(Function)] but allows specifying a custom cache (in case you need thread safety
    /// or eviction policies for example).
    public static <T, R, M extends Map<T, R>> Function<T, R> cachedFunction(Function<T, R> fn, Supplier<M> cache) {
        return new Function<>() {
            private final M map = cache.get();

            @Override
            public R apply(T t) {
                return map.computeIfAbsent(t, fn);
            }
        };
    }

    /// @return a new supplier that caches and returns the results of the given input supplier. `Null` results are allowed.
    /// (not thread-safe!)
    public static <T> Supplier<T> cachedSupplier(Supplier<T> sup) {
        return new Supplier<>() {
            private T value;
            private boolean computed = false;

            @Override
            public T get() {
                if (!computed) {
                    value = sup.get();
                    computed = true;
                }
                return value;
            }
        };
    }

    /// Same as [#cachedSupplier(Supplier)] but thread-safe.
    public static <T> Supplier<T> threadSafeCachedSupplier(Supplier<T> sup) {
        return new Supplier<>() {
            private volatile T value;
            private volatile boolean computed = false;

            @Override
            public T get() {
                if (!computed) {
                    synchronized (this) {
                        if (!computed) {
                            value = sup.get();
                            computed = true;
                        }
                    }
                }
                return value;
            }
        };
    }
}
