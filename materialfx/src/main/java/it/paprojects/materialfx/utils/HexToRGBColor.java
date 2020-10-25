package it.paprojects.materialfx.utils;

import javafx.scene.paint.Color;

public class HexToRGBColor {

    private HexToRGBColor() {
    }

    public static String rgb(Color color) {
        return String.format("#%02x%02x%02x",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }

    public static String rgba(Color color) {
        return String.format("rgba(%d, %d, %d, %f)",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()),
                color.getOpacity());
    }
}
