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
