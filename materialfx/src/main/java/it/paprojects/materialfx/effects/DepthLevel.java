package it.paprojects.materialfx.effects;

import javafx.scene.paint.Color;

/**
 * Enumerator which defines 5 levels of {@code DropShadow} effects from {@code LEVEL1} to {@code LEVEL5}.
 */
public enum DepthLevel {
    LEVEL0(Color.rgb(0, 0, 0, 0), 0, 0, 0, 0),
    LEVEL1(Color.rgb(0, 0, 0, 0.20), 10, 0.12, -1, 2),
    LEVEL2(Color.rgb(0, 0, 0, 0.20), 15, 0.16, 0, 4),
    LEVEL3(Color.rgb(0, 0, 0, 0.20), 20, 0.19, 0, 6),
    LEVEL4(Color.rgb(0, 0, 0, 0.20), 25, 0.25, 0, 8),
    LEVEL5(Color.rgb(0, 0, 0, 0.20), 30, 0.30, 0, 10);

    private final Color color;
    private final double radius;
    private final double spread;
    private final double offsetX;
    private final double offsetY;
    private static final DepthLevel[] valuesArr = values();

    DepthLevel(Color color, double radius, double spread, double offsetX, double offsetY) {
        this.color = color;
        this.radius = radius;
        this.spread = spread;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Color getColor() {
        return color;
    }

    public double getRadius() {
        return radius;
    }

    public double getSpread() {
        return spread;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Retrieves the next {@code DepthLevel} associated with {@code this} enumerator.
     * @return The next {@code DepthLevel}
     */
    public DepthLevel next() {
        return valuesArr[(this.ordinal()+1) % valuesArr.length];
    }
}
