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

package io.github.palexdev.mfxcore.events.bus;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.Event;

/// A `Subscriber` is a functional interface, and essentially an action to perform given a certain type of event.
///
/// Optionally can specify a priority, which determines order of execution in case of multiple subscribers on the same event
/// (depends on the bus implementation!).
///
/// @param <E> the event type
/// @see SimpleEventBus
/// @see Event
@FunctionalInterface
public interface Subscriber<E extends Event> {
    void handle(E event);

    /// @return a composed `Subscriber` that performs, in sequence, this operation followed by the `after` operation.
    /// Retains the priority of the first, can be easily overridden by using [#withPriority(int)].
    default Subscriber<E> andThen(Subscriber<E> after) {
        Objects.requireNonNull(after);
        return new Subscriber<>() {
            @Override
            public void handle(E event) {
                Subscriber.this.handle(event);
                after.handle(event);
            }

            @Override
            public boolean isOneShot() {
                return Subscriber.this.isOneShot();
            }

            @Override
            public int priority() {
                return Subscriber.this.priority();
            }
        };
    }

    /// @return a new `Subscriber` with an overridden [#priority()] method to return the given `priority`.
    default Subscriber<E> withPriority(int priority) {
        return new Subscriber<>() {
            @Override
            public void handle(E event) {
                Subscriber.this.handle(event);
            }

            @Override
            public boolean isOneShot() {
                return Subscriber.this.isOneShot();
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }

    /// @return a new `Subscriber` with an overridden [#isOneShot()] method to return `true`. Will execute only once
    default Subscriber<E> oneShot() {
        return new Subscriber<>() {
            @Override
            public void handle(E event) {
                Subscriber.this.handle(event);
            }

            @Override
            public boolean isOneShot() {
                return true;
            }

            @Override
            public int priority() {
                return Subscriber.super.priority();
            }
        };
    }

    /// @return whether this subscriber is [#oneShot()], by default `false`
    default boolean isOneShot() {
        return false;
    }

    /// @return the priority of this subscriber, by default 0
    /// @see EventBus#subscribe(Class, Consumer, int)
    /// @see SimpleEventBus#notifySubscribers(Event)
    default int priority() {
        return 0;
    }
}
