package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.events.Event;

/**
 * Public API to implement a basic event bus.
 *
 * @see SimpleEventBus
 */
public interface IEventBus {

	/**
	 * Registers the given {@link Subscriber} for the given event type.
	 */
	<E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber);

	/**
	 * Unregisters the given {@link Subscriber} for the given event type.
	 */
	<E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber);

	/**
	 * Publishes the given event. What this exactly means depends on the implementations. Typically, this results in a
	 * loop running which passes the event to all the subscribers for the event type, added by {@link #subscribe(Class, Subscriber)}.
	 */
	<E extends Event> void publish(E event);
}
