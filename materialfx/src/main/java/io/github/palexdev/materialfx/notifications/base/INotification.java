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

import io.github.palexdev.materialfx.enums.NotificationState;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Region;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Interface which specifies the features of a notification rather than its content.
 * <p></p>
 * Base features of a notification are:
 * <p> - the content, specified as a generic {@link Region}
 * <p> - the read state property, {@link NotificationState}
 * <p> - a way to tell when the notification was created, how much time has passed since creation,
 * a way to convert the time from a long value to a String, a way to tell that the elapsed time should change
 * and an action to run when this happens
 */
public interface INotification {

	/**
	 * @return the notification's content
	 */
	Region getContent();

	NotificationState getState();

	/**
	 * Specifies the notification's read state.
	 */
	ObjectProperty<NotificationState> notificationStateProperty();

	void setNotificationState(NotificationState state);

	/**
	 * @return the created time as a long value, the number of seconds from the Java epoch
	 */
	long getTime();

	/**
	 * @return the difference between the current number of seconds from the Java epoch and the created time
	 */
	long getElapsedTime();

	/**
	 * @return the function used to convert a time in seconds to String
	 */
	Function<Long, String> getTimeToStringConverter();

	/**
	 * Sets the function used to convert a time in seconds to String.
	 */
	void setTimeToStringConverter(Function<Long, String> converter);

	/**
	 * Should be called by a periodic task to inform "someone" that the elapsed time should be updated
	 */
	void updateElapsed();

	/**
	 * This action is automatically called by {@link #updateElapsed()}, use this to inform "someone" that
	 * the elapsed time should be updated. The action is a {@link BiConsumer} and the inputs are the elapsed seconds
	 * as a long value and the elapsed seconds converted to a String by using the {@link #getTimeToStringConverter()}.
	 */
	void setOnUpdateElapsed(BiConsumer<Long, String> elapsedConsumer);
}
