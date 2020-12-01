package io.github.palexdev.materialfx.utils;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Utils class for JavaFX's {@code Color}s and CSS.
 */
public class ColorUtils {
    private static final Random random = new Random(System.currentTimeMillis());

    private ColorUtils() {
    }

    /**
     * Converts a JavaFX's {@code Color} to CSS corresponding rgb function.
     * @return the rgb function as a string
     */
    public static String rgb(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }

    /**
     * Converts a JavaFX's {@code Color} to CSS corresponding rgba function.
     * @return the rgba function as a string
     */
    public static String rgba(Color color) {
        return String.format("rgba(%d, %d, %d, %s)",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()),
                color.getOpacity());
    }

    /**
     * Generates a random {@code Color} using java.util.Random.
     */
    public static Color getRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
