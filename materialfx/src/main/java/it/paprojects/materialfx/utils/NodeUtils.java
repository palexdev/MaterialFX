package it.paprojects.materialfx.utils;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class which provides convenience methods for working with Nodes
 */
public class NodeUtils {

    private NodeUtils() {
    }

    /**
     * Changes the background color of a {@code Region} to the desired one.
     * @param region The region to change the background color to
     * @param fill The desired color
     */
    public static void updateBackground(Region region, Paint fill) {
        final Background background = region.getBackground();
        if (background == null || background.getFills().isEmpty()) {
            return;
        }

        final List<BackgroundFill> fills = new ArrayList<>();
        for (BackgroundFill bf : background.getFills()) {
            fills.add(new BackgroundFill(fill, bf.getRadii(), bf.getInsets()));
        }

        region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
    }

    /**
     * Changes the background color of a {@code Region} to the desired one and lets specify the background insets.
     * @param region The region to change the background color to
     * @param fill The desired color
     * @param backgroundInsets The background insets to use
     */
    public static void updateBackground(Region region, Paint fill, Insets backgroundInsets) {
        final Background background = region.getBackground();
        if (background == null || background.getFills().isEmpty()) {
            return;
        }

        final List<BackgroundFill> fills = new ArrayList<>();
        for (BackgroundFill bf : background.getFills()) {
            fills.add(new BackgroundFill(fill, bf.getRadii(), backgroundInsets));
        }

        region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
    }

    /* The next two methods are copied from com.sun.javafx.scene.control.skin.Utils class
     * It's a private module, so to avoid adding exports and opens I copied them
     */
    public static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch (hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
        }
        return 0;
    }

    public static double computeYOffset(double height, double contentHeight, VPos vpos) {

        switch (vpos) {
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                return 0;
        }
    }
}
