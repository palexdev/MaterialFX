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

package io.github.palexdev.mfxcore.popups.notifications;

import java.util.function.Supplier;

import io.github.palexdev.mfxcore.observables.When;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.util.Duration;

/// Minimal API to represent notifications. Implementations are solely responsible for providing the visual representation
/// of the notification. The rest is handled by the user or by the provided notification system: [MFXNotificationPane].
///
/// There are two optional hooks: [#onShow()] and [#onHide()]. <br >
/// As well as a way to dismiss the notification indirectly via JavaFX events: [#requestDismiss()].
///
/// **Warn on timed notifications: **<br >
/// Timed notifications created by [#timed(Duration)] or [#timed(double)] have a nasty issue, although very specific.
/// They convert a standard notification to a timed one by creating a new instance and overriding the methods to reference
/// the original notification. Ultimately, what is used or sent to the notification system is the new instance.<br >
/// In my testings, I had an event handler dismiss the notification when right-clicking on it. That stopped working when
/// I was testing timed notifications for this exact reason. The dismissal request was referencing the original notification,
/// but the notification system knew only about the timed one. In the end, I put a patch on this by storing the visible
/// notifications in a map by their node.
public interface Notification {

    /// @return the node which is the visual representation of the notification
    Node toNode();

    /// Hook to be called when the notification is shown.
    ///
    /// Example: timed notifications use this hook to start the countdown
    default void onShow() {}

    /// Hook to be called when the notification is about to be hidden.
    ///
    /// Example: timed notifications use this hook to stop and dispose of the countdown
    default void onHide() {}

    /// Fires a [NotificationEvent#DISMISS] event from the notification's node. The interceptor should execute the
    /// actions needed to hide the notification.
    default void requestDismiss() {
        Node target = toNode();
        target.fireEvent(new NotificationEvent(this, NotificationEvent.DISMISS));
    }

    /// Delegates to [#timed(Duration)] with the given duration in milliseconds.
    default Notification timed(Duration duration) {
        return timed(duration.toMillis());
    }


    /// Converts itself to a timed notification that automatically dismisses itself after the specified duration.<br >
    /// The timer starts when the notification is shown and pauses when the notification is hovered.
    ///
    /// Make sure to read the cons of this [here][Notification].
    default Notification timed(double millis) {
        return new Notification() {
            private final PauseTransition timer;
            private When<?> hoverWhen;

            {
                timer = new PauseTransition();
                timer.setDelay(Duration.seconds(1));
                timer.setDuration(Duration.millis(millis));
                timer.setOnFinished(_ -> requestDismiss());
            }

            @Override
            public Node toNode() {
                return Notification.this.toNode();
            }

            @Override
            public void onShow() {
                Notification.this.onShow();
                initTimer();
            }

            @Override
            public void onHide() {
                timer.stop();
                if (hoverWhen != null) hoverWhen.dispose();
                Notification.this.onHide();
            }

            private void initTimer() {
                Node node = toNode();
                hoverWhen = When.onInvalidated(node.hoverProperty())
                    .then(h -> {
                        if (h) timer.stop();
                        else timer.playFromStart();
                    })
                    .executeNow()
                    .listen();
            }
        };
    }

    /// Utility to create a notification from the given node;
    static Notification wrap(Node content) {
        return () -> content;
    }

    /// Utility to create a notification from the result of the given supplier.
    static Notification wrap(Supplier<Node> contentSupplier) {
        return wrap(contentSupplier.get());
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    class NotificationEvent extends Event {
        public static final EventType<NotificationEvent> ANY = new EventType<>(Event.ANY, "NOTIFICATION_EVENT");
        public static final EventType<NotificationEvent> DISMISS = new EventType<>("NOTIFICATION_DISMISS");

        private final Notification notification;

        public NotificationEvent(Notification notification, EventType<? extends Event> eventType) {
            super(eventType);
            this.notification = notification;
        }

        public Notification getNotification() {
            return notification;
        }
    }
}
