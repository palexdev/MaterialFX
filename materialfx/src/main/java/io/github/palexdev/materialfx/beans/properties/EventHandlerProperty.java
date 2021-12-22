package io.github.palexdev.materialfx.beans.properties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * Simply an {@link ObjectProperty} that wraps an {@link EventHandler} for an {@link Event} of type T.
 *
 * @param <T> the type of {@link Event}
 */
public class EventHandlerProperty<T extends Event> extends SimpleObjectProperty<EventHandler<T>> { // TODO replace everywhere

	//================================================================================
	// Constructors
	//================================================================================
	public EventHandlerProperty() {
	}

	public EventHandlerProperty(EventHandler<T> initialValue) {
		super(initialValue);
	}

	public EventHandlerProperty(Object bean, String name) {
		super(bean, name);
	}

	public EventHandlerProperty(Object bean, String name, EventHandler<T> initialValue) {
		super(bean, name, initialValue);
	}
}
