package io.github.palexdev.mfxcore.events.bus;

import java.util.function.Consumer;

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

	/**
	 * @return the priority of this subscriber, by default 0
	 * @see IEventBus#subscribe(Class, Consumer, int)
	 * @see SimpleEventBus#notifySubscribers(Event)
	 */
	default int priority() {
		return 0;
	}
}
