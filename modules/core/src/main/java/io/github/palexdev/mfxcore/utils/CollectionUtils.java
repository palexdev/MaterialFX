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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
