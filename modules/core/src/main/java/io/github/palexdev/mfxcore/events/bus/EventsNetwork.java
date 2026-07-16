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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.Event;
import io.github.palexdev.mfxcore.events.bus.TaggedEventBus.BusTag;

import static io.github.palexdev.mfxcore.events.bus.TaggedEventBus.BusTag.tag;

/// A static, utility-style event bus network.
///
/// The network always has a `ROOT` bus, created on first-class load. Convenience methods ([#event(Event)],
/// [#onEvent(Class, Subscriber)], [#unsubscribe(Class, Subscriber)]) operate on this `ROOT` bus by default,
/// covering the vast majority of use cases.
///
/// Additional buses can be created via [#taggedBus(String)] for isolated event channels. Buses are lazy and
/// **never automatically removed** — call [#removeBus(String)] to clean them up.
///
/// ```java
/// EventsNetwork.event(new MyEvent());
/// EventsNetwork.onEvent(MyEvent.class, e -> handle(e));
/// TaggedEventBus uiBus = EventsNetwork.taggedBus("UI");
/// ```
///
/// @see TaggedEventBus
/// @see SimpleEventBus
public class EventsNetwork {

    //================================================================================
    // Properties
    //================================================================================

    private static final TaggedEventBus ROOT;
    private static final Map<BusTag, TaggedEventBus> network = new HashMap<>();

    //================================================================================
    // Constructors
    //================================================================================

    private EventsNetwork() {}

    static {
        ROOT = taggedBus("ROOT");
    }

    //================================================================================
    // Methods
    //================================================================================

    /// @return the `ROOT` tagged bus, shared across the entire network
    public static TaggedEventBus root() {
        return ROOT;
    }

    /// Retrieves an existing tagged bus by name, or creates a new one if none exists yet.
    /// Buses are backed by a [SimpleEventBus] wrapped in a [TaggedEventBus].
    public static TaggedEventBus taggedBus(String name) {
        return network.computeIfAbsent(tag(name), t -> TaggedEventBus.extend(t, new SimpleEventBus()));
    }

    /// Convenience shorthand for [TaggedEventBus#publish(Event)] on the `ROOT` bus.
    public static <E extends Event> void event(E event) {
        ROOT.publish(event);
    }

    /// Convenience shorthand for [TaggedEventBus#subscribe(Class, Subscriber)] on the `ROOT` bus.
    public static <E extends Event> void onEvent(Class<E> evt, Subscriber<E> subscriber) {
        ROOT.subscribe(evt, subscriber);
    }

    /// Convenience shorthand for [TaggedEventBus#subscribe(Class, Consumer, int)] on the `ROOT` bus.
    public static <E extends Event> void onEvent(Class<E> evt, Consumer<E> subscriber, int priority) {
        ROOT.subscribe(evt, subscriber, priority);
    }

    /// Convenience shorthand for [TaggedEventBus#unsubscribe(Class, Subscriber)] on the `ROOT` bus.
    public static <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber) {
        ROOT.unsubscribe(evt, subscriber);
    }

    /// Removes a tagged bus from the network by its name and clears all its subscribers.
    public static void removeBus(String name) {
        BusTag toTag = tag(name);
        if (ROOT.tag().equals(toTag)) throw new IllegalArgumentException("Cannot remove the root bus");
        EventBus bus = network.remove(toTag);
        if (bus != null) bus.clear();
    }

    /// @return an unmodifiable view of all currently active (registered) buses in the network
    public static Map<BusTag, TaggedEventBus> getActiveBuses() {
        return Collections.unmodifiableMap(network);
    }
}
