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

import static java.util.Objects.requireNonNull;

/// An [EventBus] decorated with a [BusTag] to allow routing events to specific buses.
/// Created via [#extend(BusTag, EventBus)], which wraps any [EventBus] implementation.
///
/// @see EventsNetwork
/// @see SimpleEventBus
public interface TaggedEventBus extends EventBus {

    /// @return the tag identifying this bus
    BusTag tag();

    /// Wraps the given [EventBus] with a [BusTag], returning a [TaggedEventBus] that delegates all calls.
    ///
    /// @param tag the tag to associate with this bus
    /// @param bus the underlying bus to delegate to
    static TaggedEventBus extend(BusTag tag, EventBus bus) {
        return new TaggedEventBus() {
            @Override
            public BusTag tag() {
                return tag;
            }

            @Override
            public <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber) {
                bus.subscribe(evt, subscriber);
            }

            @Override
            public <E extends Event> void subscribe(Class<E> evt, Consumer<E> subscriber, int priority) {
                bus.subscribe(evt, subscriber, priority);
            }

            @Override
            public <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber) {
                bus.unsubscribe(evt, subscriber);
            }

            @Override
            public <E extends Event> void publish(E event) {
                bus.publish(event);
            }

            @Override
            public boolean hasSubscribers() {
                return bus.hasSubscribers();
            }

            @Override
            public void clear() {
                bus.clear();
            }
        };
    }

    /// Simple record that associates an [EventBus] with a name.
    ///
    /// @param name the tag's name, must not be null
    record BusTag(String name) {
        public BusTag {
            requireNonNull(name, "Bus tag name cannot be null");
        }

        /// Creates a [BusTag] for the given name, caching it for reuse.
        /// @see EventsNetwork#taggedBus(String)
        public static BusTag tag(String name) {
            return new BusTag(name);
        }
    }
}
