/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class EventHandlerProperty<T extends Event> extends SimpleObjectProperty<EventHandler<T>> {

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
