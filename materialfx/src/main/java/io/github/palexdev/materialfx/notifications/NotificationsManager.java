package io.github.palexdev.materialfx.notifications;

import io.github.palexdev.materialfx.collections.CircularQueue;
import io.github.palexdev.materialfx.controls.MFXNotification;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a notification system, its job is to manage the incoming notifications
 * by sending them to the correct position. It also keeps track of the sent notifications
 * by storing them in a {@link CircularQueue} with the default size of 20.
 */
public class NotificationsManager {
    //================================================================================
    // Properties
    //================================================================================
    private static final Rectangle2D screenBounds;
    private static final Window window;
    private static final Map<NotificationPos, PositionManager> notifications = new HashMap<>();
    private static final CircularQueue<MFXNotification> notificationsHistory = new CircularQueue<>(20);

    //================================================================================
    // Init
    //================================================================================
    static {
        screenBounds = Screen.getPrimary().getVisualBounds();
        window = getWindow();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sends a {@code MFXNotification} to the designated {@code PositionManager}
     *
     * @param pos          The notifications' position on screen
     * @param notification The notification
     */
    public static void send(NotificationPos pos, MFXNotification notification) {
        notifications.computeIfAbsent(pos, notificationPos -> new PositionManager(screenBounds, window, notificationPos));
        notifications.get(pos).show(notification);
        notificationsHistory.add(notification);
    }

    /**
     * Sends a {@code MFXNotification} to the designated {@code PositionManager} with the specified spacing.
     *
     * @param pos          The notifications' position on screen
     * @param notification The notification
     * @param spacing      The number of pixels between each shown notification and from screen's left and right borders
     */
    public static void send(NotificationPos pos, MFXNotification notification, double spacing) {
        notifications.computeIfAbsent(pos, notificationPos -> new PositionManager(screenBounds, window, notificationPos));
        notifications.get(pos).setSpacing(spacing).show(notification);
        notificationsHistory.add(notification);
    }

    /**
     * Sends a {@code MFXNotification} to the designated {@code PositionManager} with the specified limit.
     *
     * @param pos          The notifications' position on screen
     * @param notification The notification
     * @param limit        The maximum number of notifications to show, if limit is exceeded they will be queued
     */
    public static void send(NotificationPos pos, MFXNotification notification, int limit) {
        notifications.computeIfAbsent(pos, notificationPos -> new PositionManager(screenBounds, window, notificationPos));
        notifications.get(pos).setLimit(limit).show(notification);
        notificationsHistory.add(notification);
    }

    /**
     * Sends a {@code MFXNotification} to the designated {@code PositionManager} with the specified spacing and limit.
     *
     * @param pos          The notifications' position on screen
     * @param notification The notification
     * @param spacing      The number of pixels between each shown notification and from screen's left and right borders
     * @param limit        The maximum number of notifications to show, if limit is exceeded they will be queued
     */
    public static void send(NotificationPos pos, MFXNotification notification, double spacing, int limit) {
        notifications.computeIfAbsent(pos, notificationPos -> new PositionManager(screenBounds, window, notificationPos));
        notifications.get(pos).setSpacing(spacing).setLimit(limit).show(notification);
        notificationsHistory.add(notification);
    }

    public static PositionManager getPositionManager(NotificationPos pos) {
        return notifications.get(pos);
    }

    public static void setHistoryLimit(int size) {
        notificationsHistory.setSize(size);
    }

    private static Window getWindow() {
        for (Window w : Window.getWindows()) {
            if (w.isFocused()) {
                return w;
            }
        }
        return null;
    }

    public CircularQueue<MFXNotification> getNotificationsHistory() {
        return notificationsHistory;
    }
}
