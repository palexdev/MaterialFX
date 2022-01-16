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

import io.github.palexdev.materialfx.beans.TransitionPositionBean;
import io.github.palexdev.materialfx.beans.properties.resettable.ResettableBooleanProperty;
import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.*;
import javafx.util.Duration;

/**
 * Base class to define a notification system.
 * <p></p>
 * A notification system has the following info and features:
 * <p> - The {@link Screen} on which to show the notification
 * <p> - The {@link Window} on which the notification system's stage depends (more on that later)
 * <p> - A {@link MFXPopup} which contains the notification
 * <p> - The {@link NotificationPos} to compute the shown notifications' position
 * <p> - Allows to specify some extra spacing between the screen's borders and the notification
 * <p> - By default is animated but it can also be disabled
 * <p> - By default notifications will close automatically after 3 seconds (by default)
 * <p></p>
 * To fix issue #80 and similar, the notification system do not uses the focused window anymore but
 * it has its own "dummy" stage. It is a UTILITY stage, so that no icon appear in the taskbar, and its opacity is 0.
 * While this is a great improvement over the old design there's also a downside.
 * When the JavaFX's primary stage is closed the app won't close because the "dummy" stage is still open,
 * to fix this a notification system must be initialized with an owner window, so that when owner is closed
 * the "dummy" stage is closed too.
 * <p></p>
 * Any notification system ideally must implement the following behaviors:
 * <p> - publish: the method that accepts and manages new notifications
 * <p> - show: ideally called by the publish method, serves to specify how to show the new notification
 * <p> - close: serves to specify how a notification should be closed
 * <p> - scheduleReopen: this method specifies how the notification system behaves when
 * a new notification is published but it already is in a showing/closing state. Ideally this method
 * should instruct the notification system to ignore the new notification and recall publish as soon as the current
 * notification is being closed.
 * <p> - computePosition: computes the position of the notification as a {@link TransitionPositionBean} for use
 * with animations too. (for non animated systems just subtract the deltas from their respective coordinates)
 * <p></p>
 * Side note on the close method: it's highly recommended to also close the popup since it is not transparent
 * and mouse won't work through. (may block OS windows)
 * <p></p>
 * Side notes on the scheduleReopen mechanism: this base class offers two {@link ResettableBooleanProperty} to
 * specify that the notification is showing/closing a notification. Show and hide methods should set these properties
 * to true as the first operation, and then reset them once the notification is closed (see implementations source code for examples).
 * The scheduleReopen should instruct the system to call publish as soon as the closing property becomes false.
 * <p></p>
 * Last note: notification systems ideally should be singletons.
 */
public abstract class AbstractMFXNotificationSystem implements INotificationSystem {
	//================================================================================
	// Properties
	//================================================================================
	protected Screen screen = Screen.getPrimary();
	protected Window owner;
	protected final Stage dummyStage;
	protected final EventHandler<WindowEvent> onClose;
	protected final MFXPopup popup;
	protected NotificationPos position;
	protected Insets spacing;

	protected boolean animated = true;
	protected boolean closeAutomatically = true;
	protected Duration closeAfter = Duration.seconds(3);
	protected final PauseTransition closeAfterTransition;

	protected final ResettableBooleanProperty showing = new ResettableBooleanProperty(false, false);
	protected final ResettableBooleanProperty closing = new ResettableBooleanProperty(false, false);

	//================================================================================
	// Constructors
	//================================================================================
	protected AbstractMFXNotificationSystem() {
		position = NotificationPos.BOTTOM_RIGHT;
		spacing = InsetsFactory.all(15);

		dummyStage = new Stage();
		dummyStage.initStyle(StageStyle.UTILITY);
		dummyStage.setOpacity(0.0);
		dummyStage.show();
		onClose = event -> dummyStage.close();

		popup = new MFXPopup();
		popup.setAnimated(false);

		closeAfterTransition = PauseBuilder.build()
				.setDelay(100)
				.setDuration(closeAfter)
				.setOnFinished(event -> close())
				.getAnimation();

		closing.setFireChangeOnReset(true);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Shows a notification by manipulating the popup's coordinates and content.
	 */
	protected abstract void show();

	/**
	 * Closes a notification by manipulating the popup's coordinates and content.
	 * <p>
	 * The popup should be closed as well!
	 */
	protected abstract void close();

	/**
	 * Instructs the notification system to shown the specified notification when possible.
	 */
	protected abstract void scheduleReopen(INotification notification);

	/**
	 * Responsible for computing the popup's coordinates.
	 */
	protected abstract TransitionPositionBean computePosition();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Default implementation is empty.
	 */
	protected void init() {}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the screen on which to show the notifications
	 */
	public Screen getScreen() {
		return screen;
	}

	/**
	 * Sets the screen on which to show the notifications.
	 */
	public AbstractMFXNotificationSystem setScreen(Screen screen) {
		this.screen = screen;
		return this;
	}

	/**
	 * @return the position at which notifications will be shown
	 */
	public NotificationPos getPosition() {
		return position;
	}

	/**
	 * Sets the position at which notifications will be shown.
	 */
	public AbstractMFXNotificationSystem setPosition(NotificationPos position) {
		this.position = position;
		return this;
	}

	/**
	 * Safer version of {@link #setPosition(NotificationPos)}. If the notification system is currently showing
	 * it's not a good idea to change the position as the close method could then misbehave, this method
	 * changes the position as soon as the notification system has been closed. If it's already closed then
	 * the position is set immediately.
	 */
	public AbstractMFXNotificationSystem delaySetPosition(NotificationPos position) {
		if (isShowing()) {
			ExecutionUtils.executeWhen(
					closing,
					(oldValue, newValue) -> setPosition(position),
					false,
					(oldValue, newValue) -> !newValue,
					true
			);
		} else {
			setPosition(position);
		}
		return this;
	}

	/**
	 * @return the Insets object that specifies the spacing between notifications and the screen borders
	 */
	public Insets getSpacing() {
		return spacing;
	}

	/**
	 * Sets the Insets object that specifies the spacing between notifications and the screen borders.
	 */
	public AbstractMFXNotificationSystem setSpacing(Insets spacing) {
		this.spacing = spacing;
		return this;
	}

	/**
	 * @return whether the notification system is animated
	 */
	public boolean isAnimated() {
		return animated;
	}

	/**
	 * Enables/Disables animations.
	 */
	public AbstractMFXNotificationSystem setAnimated(boolean animated) {
		this.animated = animated;
		return this;
	}

	/**
	 * @return whether notifications should close automatically
	 */
	public boolean isCloseAutomatically() {
		return closeAutomatically;
	}

	/**
	 * Enables/Disables notifications automatic close.
	 */
	public AbstractMFXNotificationSystem setCloseAutomatically(boolean closeAutomatically) {
		this.closeAutomatically = closeAutomatically;
		return this;
	}

	/**
	 * @return the duration of time after which the notifications are automatically closed
	 * if {@link #isCloseAutomatically()} is true
	 */
	public Duration getCloseAfter() {
		return closeAfter;
	}

	/**
	 * Sets the duration of time after which the notifications are automatically closed
	 * if {@link #isCloseAutomatically()} is true
	 */
	public AbstractMFXNotificationSystem setCloseAfter(Duration closeAfter) {
		this.closeAfter = closeAfter;
		closeAfterTransition.setDuration(closeAfter);
		return this;
	}

	/**
	 * @return whether the notification system is showing a notification
	 */
	public boolean isShowing() {
		return showing.get();
	}

	/**
	 * @return whether the notification system is closing
	 */
	public boolean isClosing() {
		return closing.get();
	}
}
