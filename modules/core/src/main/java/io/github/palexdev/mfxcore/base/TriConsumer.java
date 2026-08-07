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

package io.github.palexdev.mfxcore.base;

import java.util.Objects;
import java.util.function.Consumer;

/// Represents an operation that accepts three input arguments and returns no result. This is the tri-arity specialization of [Consumer].
/// Unlike most other functional interfaces, `TriConsumer` is expected to operate via side effects.
///
/// This is a <a href="package-summary.html">functional interface</a> whose functional method is [#accept(Object, Object, Object)].
///
/// @param <A> the type of the first argument to the operation
/// @param <B> the type of the second argument to the operation
/// @param <C> the type of the third argument to the operation
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    /// Performs this operation on the given arguments.
    ///
    /// @param a the first input argument
    /// @param b the second input argument
    /// @param c the third input argument
    void accept(A a, B b, C c);

    /// Returns a composed `TriConsumer` that performs, in sequence, this
    /// operation followed by the `after` operation. If performing either
    /// operation throws an exception, it is relayed to the caller of the
    /// composed operation. If performing this operation throws an exception,
    /// the `after` operation will not be performed.
    ///
    /// @param after the operation to perform after this operation
    /// @return a composed `TriConsumer` that performs in sequence this
    /// operation followed by the `after` operation
    /// @throws NullPointerException if `after` is null
    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);

        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
