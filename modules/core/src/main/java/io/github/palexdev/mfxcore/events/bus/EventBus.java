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

import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.Event;

/// Public API to implement a basic event bus.
///
/// @see SimpleEventBus
public interface EventBus {

    /// Registers the given [Subscriber] for the given event type.
    <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber);

    /// Register a new [Subscriber] from the given [Consumer] with the specified priority.
    <E extends Event> void subscribe(Class<E> evt, Consumer<E> subscriber, int priority);

    /// Unregisters the given [Subscriber] for the given event type.
    <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber);

    /// Publishes the given event. What this exactly means depends on the implementations. Typically, this results in a
    /// loop running which passes the event to all the subscribers for the event type, added by [#subscribe(Class, Subscriber)].
    <E extends Event> void publish(E event);

    /// @return whether there are registered subscribers in the event bus currently or not.
    boolean hasSubscribers();

    /// Clears the event bus by removing all registered subscribers.
    void clear();
}
