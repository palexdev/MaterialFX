package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.events.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of an event bus. Takes inspiration from many DI frameworks that use similar mechanisms to
 * dispatch events across the app.
 * <p></p>
 * The basic features include:
 * <p> - The infrastructure makes use of {@link IEvent} and {@link Subscriber} interfaces
 * <p> - Subscribe/Unsubscribe mechanisms based on the aforementioned classes. You can subscribe to events of type
 * {@code IEvent} and specify a {@code Subscriber} which represents the action to perform when such events occur.
 * The removal of subscribers requires both the event type and the subscriber itself, as it is allowed to register multiple
 * subscribers for any single event type.
 * <p></p>
 * <b>Trivia: Why this?</b>
 * <p>
 * Long story short. I transitioned one of my projects from Spring to another framework that didn't have events functionality.
 * I could not use JavaFX ones of course, so I developed my own simple solution.
 *
 * @see IEventBus
 * @see IEvent
 * @see Event
 * @see Subscriber
 */
public class SimpleEventBus implements IEventBus {
	//================================================================================
	// Properties
	//===============================================================================
	private final Map<Class<? extends Event>, Set<Subscriber<Event>>> subscribers = new HashMap<>();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * When an event is published by {@link #publish(Event)} this is called. After getting all the
	 * {@code Subscribers} added by {@link #subscribe(Class, Subscriber)} for the given event's type, loops over
	 * all of them passing the given event, so {@link Subscriber#handle(Event)} is triggered.
	 */
	protected <E extends Event> void notifySubscribers(E event) {
		Set<Subscriber<Event>> subscribers = this.subscribers.get(event.getClass());
		if (subscribers == null || subscribers.isEmpty()) return;
		for (Subscriber<Event> s : subscribers) {
			s.handle(event);
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Event> void subscribe(Class<E> evt, Subscriber<E> subscriber) {
		Set<Subscriber<Event>> set = subscribers.computeIfAbsent(evt, c -> new WeakHashSet<>());
		set.add((Subscriber<Event>) subscriber);
	}

	@Override
	public <E extends Event> void unsubscribe(Class<E> evt, Subscriber<E> subscriber) {
		Set<Subscriber<Event>> set = subscribers.get(evt);
		if (set == null || set.isEmpty()) return;
		set.remove(subscriber);
	}

	@Override
	public <E extends Event> void publish(E event) {
		notifySubscribers(event);
	}
}
