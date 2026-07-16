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

package io.github.palexdev.mfxcore.events.bus;

import java.util.*;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.Event;

/// A basic implementation of an event bus. Takes inspiration from many DI frameworks that use similar mechanisms to
/// dispatch events across the app.
///
/// The basic features include:
///  - The infrastructure makes use of [IEvent] and [Subscriber] interfaces
///  - Subscribe/Unsubscribe mechanisms based on the aforementioned classes. You can subscribe to events of type
///  `IEvent` and specify a `Subscriber` which represents the action to perform when such events occur.
///  The removal of subscribers requires both the event type and the subscriber itself, as it is allowed to register multiple
///  subscribers for any single event type.
///
/// **Trivia: Why this?**
///
/// Long story short. I transitioned one of my projects from Spring to another framework that didn't have events functionality.
/// I could not use JavaFX ones, of course, so I developed my own simple solution.
///
/// @see EventBus
/// @see IEvent
/// @see Event
/// @see Subscriber
public class SimpleEventBus implements EventBus {
    //================================================================================
    // Properties
    //===============================================================================
    private static final Comparator<Subscriber<Event>> SUBSCRIBER_COMPARATOR = Comparator.comparingInt(Subscriber::priority);
    private final Map<Class<? extends Event>, PriorityQueue<Subscriber<Event>>> subscribers = new HashMap<>();

    //================================================================================
    // Methods
    //================================================================================

    /// When an event is published by [#publish(Event)] this is called. After getting all the `Subscribers` added by
    /// [#subscribe(Class,Subscriber)] for the given event's type, loops over all of them passing the given event,
    /// so [Subscriber#handle(Event)] is triggered.
    ///
    /// [One-shot subscribers][Subscriber#isOneShot()] are removed as the processing goes.
    protected <E extends Event> void notifySubscribers(E event) {
        Queue<Subscriber<Event>> subscribers = this.subscribers.get(event.getClass());
        if (subscribers == null || subscribers.isEmpty()) return;
        Iterator<Subscriber<Event>> it = subscribers.iterator();
        while (it.hasNext()) {
            Subscriber<Event> s = it.next();
            s.handle(event);
            if (s.isOneShot()) it.remove();
        }
        if (subscribers.isEmpty()) this.subscribers.remove(event.getClass());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @SuppressWarnings("unchecked")
    @Override
    public <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber) {
        Queue<Subscriber<Event>> queue = subscribers.computeIfAbsent(
            evt,
            _ -> new PriorityQueue<>(SUBSCRIBER_COMPARATOR)
        );
        queue.add((Subscriber<Event>) subscriber);
    }

    /// {@inheritDoc}
    ///
    /// Subscribers in this bus are stored in a [PriorityQueue], which automatically sorts them by their
    /// [Subscriber#priority()]. This handy mechanism allows the user to prioritize certain actions over others.
    /// The **lesser** the `priority` value, the **more important** the subscriber is.
    ///
    /// For subscribers with the same priority, the order is undefined!
    @Override
    public <E extends Event> void subscribe(Class<E> evt, Consumer<E> subscriber, int priority) {
        subscribe(evt, new Subscriber<>() {
            @Override
            public void handle(E event) {
                subscriber.accept(event);
            }

            @Override
            public int priority() {
                return priority;
            }
        });
    }

    @Override
    public <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber) {
        Queue<Subscriber<Event>> queue = subscribers.get(evt);
        if (queue == null) return;
        queue.remove(subscriber);
        if (queue.isEmpty()) subscribers.remove(evt);
    }

    @Override
    public <E extends Event> void publish(E event) {
        notifySubscribers(event);
    }

    @Override
    public boolean hasSubscribers() {
        return !subscribers.isEmpty();
    }

    @Override
    public void clear() {
        subscribers.clear();
    }
}
