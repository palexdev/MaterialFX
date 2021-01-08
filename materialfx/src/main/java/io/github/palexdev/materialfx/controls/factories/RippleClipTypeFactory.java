package io.github.palexdev.materialfx.controls.factories;

import io.github.palexdev.materialfx.effects.RippleClipType;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Convenience class for building Ripple clip shapes.
 */
public class RippleClipTypeFactory {
    private RippleClipType rippleClipType = RippleClipType.NOCLIP;
    private double arcW = 0;
    private double arcH = 0;

    public RippleClipTypeFactory() {
    }

    public RippleClipTypeFactory(RippleClipType rippleClipType) {
        this.rippleClipType = rippleClipType;
    }

    public RippleClipTypeFactory(RippleClipType rippleClipType, double arcW, double arcH) {
        this.rippleClipType = rippleClipType;
        this.arcW = arcW;
        this.arcH = arcH;
    }

    public Shape build(Region region) {
        double w = region.getWidth() - 0.5;
        double h = region.getHeight() - 0.5;

        switch (rippleClipType) {
            case CIRCLE:
                double radius = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2)) / 2;
                Circle circle = new Circle(radius);
                circle.setTranslateX(w / 2);
                circle.setTranslateY(h / 2);
                return circle;
            case RECTANGLE:
                return new Rectangle(w, h);
            case ROUNDED_RECTANGLE:
                Rectangle rectangle = new Rectangle(w, h);
                rectangle.setArcWidth(arcW);
                rectangle.setArcHeight(arcH);
                return rectangle;
            default:
                return null;
        }
    }

    public RippleClipTypeFactory setArcs(double arcW, double arcH) {
        this.arcW = arcW;
        this.arcH = arcH;
        return this;
    }

    public RippleClipTypeFactory setRippleClipType(RippleClipType rippleClipType) {
        this.rippleClipType = rippleClipType;
        return this;
    }
}
