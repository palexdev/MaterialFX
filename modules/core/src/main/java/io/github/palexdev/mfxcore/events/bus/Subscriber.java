package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.events.Event;

/**
 * A {@code Subscriber} is a functional interface, and essentially an action to perform given a certain type of event.
 *
 * @param <E> the event type
 * @see SimpleEventBus
 * @see Event
 */
@FunctionalInterface
public interface Subscriber<E extends Event> {
	void handle(E event);
}
