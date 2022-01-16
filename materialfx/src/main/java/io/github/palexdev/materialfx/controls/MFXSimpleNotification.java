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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Simple implementation of {@link INotification}.
 * <p></p>
 * By default the {@link INotification#getTimeToStringConverter()} function is set to use {@link StringUtils#timeToHumanReadable(long)}.
 * By default the {@link INotification#setOnUpdateElapsed(BiConsumer)} function is set to do nothing.
 * <p></p>
 * Offers a Builder to build a notification with fluent design.
 */
public class MFXSimpleNotification implements INotification {
	//================================================================================
	// Properties
	//================================================================================
	private Region content;

	private final ObjectProperty<NotificationState> state = new SimpleObjectProperty<>(NotificationState.UNREAD);
	private final long createdTime;
	private Function<Long, String> timeToStringConverter = StringUtils::timeToHumanReadable;
	private BiConsumer<Long, String> onUpdate = (elapsedLong, elapsedString) -> {};

	//================================================================================
	// Constructors
	//================================================================================
	protected MFXSimpleNotification() {
		this(new AnchorPane());
	}

	public MFXSimpleNotification(Region content) {
		createdTime = Instant.now().getEpochSecond();
		if (content == null) {
			throw new IllegalArgumentException("Content cannot be null!");
		}
		this.content = content;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	@Override
	public Region getContent() {
		return content;
	}

	protected void setContent(Region content) {
		this.content = content;
	}

	@Override
	public NotificationState getState() {
		return state.get();
	}

	@Override
	public ObjectProperty<NotificationState> notificationStateProperty() {
		return state;
	}

	@Override
	public void setNotificationState(NotificationState state) {
		this.state.set(state);
	}

	@Override
	public long getTime() {
		return createdTime;
	}

	@Override
	public long getElapsedTime() {
		return Instant.now().getEpochSecond() - createdTime;
	}

	@Override
	public Function<Long, String> getTimeToStringConverter() {
		return timeToStringConverter;
	}

	@Override
	public void setTimeToStringConverter(Function<Long, String> converter) {
		this.timeToStringConverter = converter;
	}

	@Override
	public void updateElapsed() {
		long elapsedTime = getElapsedTime();
		onUpdate.accept(elapsedTime, timeToStringConverter.apply(elapsedTime));
	}

	@Override
	public void setOnUpdateElapsed(BiConsumer<Long, String> elapsedConsumer) {
		this.onUpdate = elapsedConsumer;
	}

	//================================================================================
	// Builder
	//================================================================================
	public static class Builder {
		private final MFXSimpleNotification notification = new MFXSimpleNotification();

		protected Builder() {}

		public static Builder build() {
			return new Builder();
		}

		public Builder setContent(Region content) {
			if (content == null) {
				throw new IllegalArgumentException("Content cannot be null!");
			}
			notification.setContent(content);
			return this;
		}

		public Builder setState(NotificationState state) {
			notification.setNotificationState(state);
			return this;
		}

		public Builder setTimeToStringConverter(Function<Long, String> converter) {
			notification.setTimeToStringConverter(converter);
			return this;
		}

		public Builder setOnUpdateElapsed(BiConsumer<Long, String> elapsedConsumer) {
			notification.setOnUpdateElapsed(elapsedConsumer);
			return this;
		}

		public MFXSimpleNotification get() {
			return notification;
		}
	}
}
