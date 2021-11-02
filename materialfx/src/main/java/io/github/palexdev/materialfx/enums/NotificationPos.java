package io.github.palexdev.materialfx.enums;

import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;

/**
 * Enumeration to specify where a notification has to be shown.
 * <p>
 * Used by {@link MFXNotificationCenterSystem} and {@link MFXNotificationSystem}.
 */
public enum NotificationPos {
    TOP_CENTER,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_CENTER,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
    }

    public boolean isCenter() {
        return this == TOP_CENTER || this == BOTTOM_CENTER;
    }

    public boolean isRight() {
        return this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }
}
