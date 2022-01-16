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

package io.github.palexdev.materialfx.notifications.base;

import javafx.stage.Window;

/**
 * Defines the public API of every notification system.
 */
public interface INotificationSystem {

	/**
	 * A notification system should be initialized to honor the behavior of a owner Window.
	 * (for example if the owner closes that the notification system closes too)
	 */
	INotificationSystem initOwner(Window owner);

	/**
	 * Method to send a new notification to be shown, it's up to an implementation
	 * to decide how to manage and show it.
	 */
	INotificationSystem publish(INotification notification);

	/**
	 * Closes and disposes the notification system if not needed anymore.
	 */
	INotificationSystem dispose();
}
