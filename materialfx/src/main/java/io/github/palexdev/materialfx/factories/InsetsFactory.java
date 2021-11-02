package io.github.palexdev.materialfx.factories;

import javafx.geometry.Insets;

/**
 * Convenience class to build {@link Insets} objects.
 */
public class InsetsFactory {

    //================================================================================
    // Constructors
    //================================================================================
    private InsetsFactory() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static Insets all(double topRightBottomLeft) {
        return new Insets(topRightBottomLeft);
    }

    public static Insets none() {
        return Insets.EMPTY;
    }

    public static Insets top(double top) {
        return new Insets(top, 0, 0, 0);
    }

    public static Insets right(double right) {
        return new Insets(0, right, 0, 0);
    }

    public static Insets bottom(double bottom) {
        return new Insets(0, 0, bottom, 0);
    }

    public static Insets left(double left) {
        return new Insets(0, 0, 0, left);
    }

    public static Insets of(double top, double right) {
        return new Insets(top, right, 0, 0);
    }

    public static Insets of(double top, double right, double bottom) {
        return new Insets(top, right, bottom, 0);
    }

    public static Insets of(double top, double right, double bottom, double left) {
        return new Insets(top, right, bottom, left);
    }
}
