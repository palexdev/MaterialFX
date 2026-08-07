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

package io.github.palexdev.mfxcore.events;

import io.github.palexdev.mfxcore.events.bus.IEvent;
import io.github.palexdev.mfxcore.events.bus.Subscriber;

/// Basic implementation of [IEvent] and starting point to implement real events.
///
/// Many frameworks with similar capabilities use events that can carry some data. While this is not strictly necessary
/// for an event bus, it is indeed nice to have. The data being carried can provide valuable information to the [Subscriber].
/// It's not limited solely to context-explaining data; it can also be used to transmit instances throughout an application,
/// for example.
///
/// @see IEvent
public abstract class Event implements IEvent {
    //================================================================================
    // Properties
    //================================================================================
    private final Object data;

    //================================================================================
    // Constructors
    //================================================================================
    protected Event() {
        this(null);
    }

    public Event(Object data) {
        this.data = data;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Object data() {
        return data;
    }
}
