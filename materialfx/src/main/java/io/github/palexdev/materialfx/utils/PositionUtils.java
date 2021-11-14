package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.enums.NotificationPos;
import javafx.geometry.Pos;

/**
 * Utilities for JavaFX's {@link Pos} and {@link NotificationPos}.
 */
public class PositionUtils {

    private PositionUtils() {
    }

    public static boolean isTop(Pos pos) {
        return pos == Pos.TOP_LEFT || pos == Pos.TOP_CENTER || pos == Pos.TOP_RIGHT;
    }

    public static boolean isCenter(Pos pos) {
        return pos == Pos.CENTER_LEFT || pos == Pos.CENTER || pos == Pos.CENTER_RIGHT || pos == Pos.TOP_CENTER || pos == Pos.BOTTOM_CENTER;
    }

    public static boolean isBottom(Pos pos) {
        return pos == Pos.BOTTOM_LEFT || pos == Pos.BOTTOM_CENTER || pos == Pos.BOTTOM_RIGHT;
    }

    public static boolean isLeft(Pos pos) {
        return pos == Pos.TOP_LEFT || pos == Pos.CENTER_LEFT || pos == Pos.BOTTOM_LEFT;
    }

    public static boolean isRight(Pos pos) {
        return pos == Pos.TOP_RIGHT || pos == Pos.CENTER_RIGHT || pos == Pos.BOTTOM_RIGHT;
    }

    public static boolean isTop(NotificationPos pos) {
        return pos == NotificationPos.TOP_LEFT || pos == NotificationPos.TOP_CENTER || pos == NotificationPos.TOP_RIGHT;
    }

    public static boolean isCenter(NotificationPos pos) {
        return pos == NotificationPos.TOP_CENTER || pos == NotificationPos.BOTTOM_CENTER;
    }

    public static boolean isRight(NotificationPos pos) {
        return pos == NotificationPos.TOP_RIGHT || pos == NotificationPos.BOTTOM_RIGHT;
    }
}
