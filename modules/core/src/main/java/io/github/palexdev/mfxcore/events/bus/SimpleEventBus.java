package io.github.palexdev.mfxcore.events.bus;

import java.util.*;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.Event;

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
	private final Map<Class<? extends Event>, PriorityQueue<Subscriber<Event>>> subscribers = new HashMap<>();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * When an event is published by {@link #publish(Event)} this is called. After getting all the
	 * {@code Subscribers} added by {@link #subscribe(Class, Subscriber)} for the given event's type, loops over
	 * all of them passing the given event, so {@link Subscriber#handle(Event)} is triggered.
	 */
	protected <E extends Event> void notifySubscribers(E event) {
		Queue<Subscriber<Event>> subscribers = this.subscribers.get(event.getClass());
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
		Queue<Subscriber<Event>> queue = subscribers.computeIfAbsent(
			evt,
			c -> new PriorityQueue<>(Comparator.comparingInt(Subscriber::priority))
		);
		queue.add((Subscriber<Event>) subscriber);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Subscribers in this bus are stored in a {@link PriorityQueue}, which automatically sorts them by their
	 * {@link Subscriber#priority()}. This handy mechanism allows user to priority certain actions over others.
	 * The lesser the {@code priority} value, the more important the subscriber is.
	 * <p>
	 * For subscribers with the same priority, the order is undefined!
	 */
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
		if (queue == null || queue.isEmpty()) return;
		queue.remove(subscriber);
	}

	@Override
	public <E extends Event> void publish(E event) {
		notifySubscribers(event);
	}
}
