package io.github.palexdev.materialfx.beans;

import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 * JavaFX allows you to create custom {@code Bounds} objects, see {@link BoundingBox}, the thing is
 * that it automatically computes the max X/Y/Z values. This can be quite unfortunate in some rare
 * cases because maybe you need some kind of special bounds, this bean is specifically for those
 * cases, it allows creating custom bounds.
 * <p>
 * An example of that is in the {@link MFXNotificationCenterSystem} class, there custom bounds
 * are created to take into account the coordinates of the bell icon and the entire width/height of the
 * notification center. Like I said tough, cases like that are quite rare.
 */
public class CustomBounds {
    //================================================================================
    // Properties
    //================================================================================
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;
    private final double width;
    private final double height;

    //================================================================================
    // Constructors
    //================================================================================
    public CustomBounds(double minX, double minY, double maxX, double maxY, double width, double height) {
        this(minX, minY, 0, maxX, maxY, 0, width, height);
    }

    public CustomBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double width, double height) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.width = width;
        this.height = height;
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static CustomBounds from(Bounds bounds) {
        return new CustomBounds(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getMinZ(),
                bounds.getMaxX(),
                bounds.getMaxY(),
                bounds.getMaxY(),
                bounds.getWidth(),
                bounds.getHeight()
        );
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
