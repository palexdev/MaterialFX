package io.github.palexdev.materialfx.effects;

import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public enum RippleClipType {
    CIRCLE {
        @Override
        public Shape buildClip(Region region) {
            double radius = Math.sqrt(Math.pow(region.getWidth(), 2) + Math.pow(region.getHeight(), 2)) / 2;
            Circle circle = new Circle(radius);
            circle.setTranslateX(region.getWidth() / 2);
            circle.setTranslateY(region.getHeight() / 2);
            return circle;
        }
    },
    RECTANGLE {
        @Override
        public Shape buildClip(Region region) {
            return new Rectangle(region.getWidth(), region.getHeight());
        }
    },
    NOCLIP {
        @Override
        public Shape buildClip(Region region) {
            return null;
        }
    };

    public static Circle buildClip(double radius) {
        return new Circle(radius);
    }

    public static Rectangle buildClip(double width, double height) {
        return new Rectangle(width, height);
    }

    public abstract Shape buildClip(Region region);
}
