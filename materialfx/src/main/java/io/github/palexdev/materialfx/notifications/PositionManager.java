package io.github.palexdev.materialfx.notifications;

import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.utils.LoggingUtils;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Support class for the {@code NotificationManager}.
 * <p>
 * Each {@code PositionManager} is responsible for showing the incoming notifications according to its position ({@link #pos}),
 * and according to its limit ({@link #limit}), which by default is 3. Each notification is distant from each other and from
 * the left and right borders according to the {@link #spacing} property which by default is 15.
 * <p>
 * To simulate the behavior of a queue, a {@link ThreadPoolExecutor} and a {@link Semaphore} are used,
 * each notification show is committed to a JavaFX's {@link Task} and before any other operation a permit is acquired
 * from the semaphore. The semaphore has a number of permits equals to the {@link #limit} property of this class,
 * when the limit is reached and there are no more permits available all other sent notifications are queued and
 * waiting for a semaphore {@code release()}. The semaphore is released every time a notification is being hidden.
 */
public class PositionManager {
    //================================================================================
    // Properties
    //================================================================================
    private final ThreadPoolExecutor service;
    private final Semaphore semaphore;
    private final Rectangle2D screenBounds;
    private final Window owner;

    private final List<MFXNotification> notifications = new ArrayList<>();
    private double spacing = 15;
    private int limit = 3;

    private final NotificationPos pos;
    private double anchorX;
    private double anchorY;

    //================================================================================
    // Constructors
    //================================================================================
    public PositionManager(Rectangle2D screenBounds, Window owner, NotificationPos pos) {
        this.service = new ThreadPoolExecutor(
                1,
                2,
                2,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                runnable -> {
                    Thread t = Executors.defaultThreadFactory().newThread(runnable);
                    t.setName("MFXNotificationsThread - " + pos.name());
                    t.setDaemon(true);
                    return t;
                }
        );
        this.service.allowCoreThreadTimeOut(true);
        this.semaphore = new Semaphore(limit);

        this.screenBounds = screenBounds;
        this.owner = owner;
        this.pos = pos;
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Shows the specified notification on screen.
     * <p>
     * The show mechanism uses a {@link ThreadPoolExecutor} with JavaFX's {@link Task}s and
     * a {@link Semaphore} to make threads wait when the notifications limit is reached,
     * in a sense it simulates the operation of a queue.
     * <p>
     * After the new notification has been added to the list, all others notifications are repositioned,
     * then anchorX and anchorY are calculated according to the {@link #pos} property.
     * <p>
     * After that the notification's show method is called on the JavaFX's thread with the specified owner and anchors.
     * On hidden the notification is removed from the list and the semaphore is released.
     * <p></p>
     * On failed task, logs the exception.
     *
     * @param newNotification The notification to show
     */
    public void show(MFXNotification newNotification) {
        Task<Void> showTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                semaphore.acquire();

                notifications.add(newNotification);
                repositionNotifications(newNotification);

                newNotification.setOnHidden(event -> {
                    notifications.remove(newNotification);
                    semaphore.release();
                });
                computePosition(newNotification);
                Platform.runLater(() -> newNotification.show(owner, anchorX, anchorY));

                return null;
            }
        };
        showTask.setOnFailed(event -> LoggingUtils.logException(showTask.getException()));
        service.submit(showTask);
    }

    public PositionManager setSpacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public PositionManager setLimit(int limit) {
        this.limit = limit;
        this.semaphore.release(limit);
        return this;
    }

    /**
     * Repositions every notification in the list, except the most recent one, with a {@code Transition} animation.
     */
    private void repositionNotifications(MFXNotification newNotification) {
        for (int i = 0; i < notifications.indexOf(newNotification); i++) {
            MFXNotification oldNotification = notifications.get(i);
            buildRepositionAnimation(newNotification, oldNotification).play();
        }
    }

    /**
     * Builds the repositioning animation.
     * <p>
     * The new anchorY is calculated using the current value and the new notification's content prefHeight.
     * <b>Note: this works only if the notification's content has it's pref height set</b>
     * @param newNotification The new notification
     * @param oldNotification The already showing notification
     * @return The animation
     */
    private Transition buildRepositionAnimation(MFXNotification newNotification, MFXNotification oldNotification) {
        final double notificationHeight = newNotification.getNotificationContent().getPrefHeight();
        final double oldAnchorY = oldNotification.getAnchorY();

        switch (pos) {
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                return new Transition() {
                    {
                        setCycleDuration(Duration.millis(350));
                        setInterpolator(Interpolator.EASE_BOTH);
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double newAnchorY = (oldAnchorY - notificationHeight) - (spacing * frac);
                        oldNotification.setAnchorY(newAnchorY);
                    }
                };
            default:
                return new Transition() {
                    {
                        setCycleDuration(Duration.millis(350));
                        setInterpolator(Interpolator.EASE_BOTH);
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double newAnchorY = (oldAnchorY + notificationHeight) + (spacing * frac);
                        oldNotification.setAnchorY(newAnchorY);
                    }
                };
        }
    }

    /**
     * Computes the notification coordinates according to the {@link #pos} property.
     */
    private void computePosition(MFXNotification notification) {
        switch (pos) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                anchorY = spacing;
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                anchorY = screenBounds.getHeight() - notification.getNotificationContent().getPrefHeight() - spacing;
                break;
        }

        switch (pos) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                anchorX = spacing;
                break;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                anchorX = screenBounds.getWidth() - notification.getNotificationContent().getPrefWidth() - spacing;
                break;
            default:
                anchorX = (screenBounds.getWidth() / 2) - (notification.getNotificationContent().getPrefWidth() / 2);
        }
    }
}