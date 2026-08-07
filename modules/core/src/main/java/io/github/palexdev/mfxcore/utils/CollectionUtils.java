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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// Utilities for Java collections.
public class CollectionUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private CollectionUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// I still can't believe Java does not offer a single method to create a modifiable list from an array of objects.
    @SafeVarargs
    public static <T> List<T> list(T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }

    public static <K, V> Map<K, V> map(Object... mappings) {
        return map(HashMap::new, mappings);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> map(Supplier<? extends Map<K, V>> mapFactory, Object... mappings) {
        if (mappings.length % 2 != 0)
            throw new IllegalArgumentException("Mappings must be in pairs of key and value");
        Map<K, V> map = mapFactory.get();
        for (int i = 0; i < mappings.length; i += 2) {
            map.put((K) mappings[i], (V) mappings[i + 1]);
        }
        return map;
    }

    public static <T> Set<T> set(T... ts) {
        return set(HashSet::new, ts);
    }

    public static <T> Set<T> set(Supplier<? extends Set<T>> setFactory, T... ts) {
        Set<T> set = setFactory.get();
        Collections.addAll(set, ts);
        return set;
    }

    /// Flattens a tree structure into a sequential stream of nodes using depth-first traversal.
    ///
    /// Example usage:
    ///
    /// ```
    /// // Flatten a custom Node tree and collect foo properties
    /// List<String> foos = flatten(root, Node::getChildren)
    ///         .map(Node::getFoo)
    ///         .toList();
    /// ```
    public static <T> Stream<T> flatten(T node, Function<T, ? extends Collection<T>> children) {
        return Stream.concat(
            Stream.of(node),
            children.apply(node).stream().flatMap(c -> flatten(c, children))
        );
    }
}
