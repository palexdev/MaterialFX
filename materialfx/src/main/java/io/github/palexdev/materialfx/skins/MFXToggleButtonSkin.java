package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXToggleButton}.
 */
public class MFXToggleButtonSkin extends ToggleButtonSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final Circle circle;
    final double circleRadius;
    private final Line line;
    private final RippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXToggleButtonSkin(MFXToggleButton toggleButton) {
        super(toggleButton);

         circleRadius = toggleButton.getSize();

        line = new Line();
        line.setStroke(toggleButton.isSelected() ? toggleButton.getToggleLineColor() : toggleButton.getUnToggleLineColor());
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(circleRadius * 2 + 4);
        line.setEndY(0);
        line.setStrokeWidth(circleRadius * 1.5);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setSmooth(true);

        circle = new Circle(circleRadius);
        circle.setFill(toggleButton.isSelected() ? toggleButton.getToggleColor() : toggleButton.getUnToggleColor());
        circle.setTranslateX(-circleRadius);
        circle.setSmooth(true);
        circle.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL1));

        container = new StackPane();
        container.getStyleClass().setAll("container");
        container.getChildren().addAll(line, circle);
        container.setCursor(Cursor.HAND);
        container.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.setPrefSize(50, 40);

        rippleGenerator = new RippleGenerator(container, new RippleClipTypeFactory());
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setRippleColor((Color) (toggleButton.isSelected() ? toggleButton.getUnToggleLineColor() : toggleButton.getToggleLineColor()));
        rippleGenerator.setRippleRadius(circleRadius * 1.2);
        rippleGenerator.setInDuration(Duration.millis(400));
        rippleGenerator.setTranslateX(-circleRadius);
        container.getChildren().add(0, rippleGenerator);

        toggleButton.setGraphic(container);

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: selected, size and skin(workaround) properties.
     */
    private void setListeners() {
        MFXToggleButton toggleButton = (MFXToggleButton) getSkinnable();

        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                line.setStroke(toggleButton.getToggleLineColor());
                rippleGenerator.setRippleColor((Color) toggleButton.getToggleLineColor());
                circle.setFill(toggleButton.getToggleColor());
            } else {
                line.setStroke(toggleButton.getUnToggleLineColor());
                rippleGenerator.setRippleColor((Color) toggleButton.getUnToggleLineColor());
                circle.setFill(toggleButton.getUnToggleColor());
            }
        });

        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> buildAndPlayAnimation(newValue));

        toggleButton.sizeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < oldValue.doubleValue()) {
                double translateX = newValue.doubleValue() + oldValue.doubleValue();
                circle.setTranslateX(translateX + 2);
            }
        });

        /*
         * Workaround
         * When the control is created the Skin is still null, so if the ToggleButton is set
         * to be selected the animation won't be played. To fix this add a listener to the
         * control's skinProperty, when the skin is not null and the ToggleButton isSelected,
         * play the animation.
         */
        toggleButton.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && toggleButton.isSelected()) {
                buildAndPlayAnimation(true);
            }
        });
    }

    /**
     * Re-builds and plays the translation animation every time the control is selected/unselected.
     * @param isSelected The control's state
     */
    private void buildAndPlayAnimation(boolean isSelected) {
        KeyValue circleTranslateXKey;
        KeyValue rippleTranslateXKey;
        KeyFrame circleTranslateXFrame;
        KeyFrame rippleTranslateXFrame;
        KeyFrame rippleAnimationFrame;

        circleTranslateXKey = new KeyValue(circle.translateXProperty(), computeTranslateX(isSelected), Interpolator.EASE_BOTH);
        rippleTranslateXKey = new KeyValue(rippleGenerator.translateXProperty(), computeTranslateX(isSelected), Interpolator.EASE_BOTH);

        circleTranslateXFrame = new KeyFrame(Duration.millis(150), circleTranslateXKey);
        rippleTranslateXFrame = new KeyFrame(Duration.millis(150), rippleTranslateXKey);
        rippleAnimationFrame = new KeyFrame(Duration.ZERO, event -> rippleGenerator.createRipple());
        Timeline timeline = new Timeline(circleTranslateXFrame, rippleTranslateXFrame, rippleAnimationFrame);
        timeline.play();
    }

    /**
     * Computes the final x coordinate of the translate animation.
     * @param isSelected The control's state
     * @return The final x coordinate.
     */
    private double computeTranslateX(boolean isSelected) {
        return isSelected ? (line.getEndX() - circleRadius) : (-circleRadius);
    }
}
