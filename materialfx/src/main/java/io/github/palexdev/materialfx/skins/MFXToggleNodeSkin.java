package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXToggleNode;
import io.github.palexdev.materialfx.effects.RippleClipType;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Optional;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXToggleNode}.
 */
public class MFXToggleNodeSkin extends ToggleButtonSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final RippleGenerator rippleGenerator;
    private final Circle circle;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXToggleNodeSkin(MFXToggleNode control) {
        super(control);

        container = new StackPane();
        Optional.ofNullable(control.getGraphic()).ifPresent(node -> container.getChildren().add(node));

        circle = new Circle();
        circle.setOpacity(0.0);
        circle.setFill(control.getUnSelectedColor());
        circle.setStrokeWidth(control.getStrokeWidth());

        rippleGenerator = new RippleGenerator(control, RippleClipType.NOCLIP);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setRippleColor(Color.GRAY);
        rippleGenerator.setInDuration(Duration.millis(250));

        updateChildren();
        setListeners(control);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: mouse pressed, selected and skin(workaround) properties.
     * @param control The MFXToggleButton associated to this skin
     */
    private void setListeners(MFXToggleNode control) {
        control.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });

        control.selectedProperty().addListener((observable, oldValue, newValue) -> buildAndPlayAnimation(newValue));

        /*
         * Workaround
         * When the control is created the Skin is still null, so if the ToggleNode is set
         * to be selected the animation won't be played. To fix this add a listener to the
         * control's skinProperty, when the skin is not null and the ToggleNode isSelected,
         * play the animation.
         */
        control.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && control.isSelected()) {
                buildAndPlayAnimation(true);
            }
        });
    }

    /**
     * Re-builds and plays the background animation every time the control is selected/unselected.
     * @param isSelected The control's state
     */
    private void buildAndPlayAnimation(boolean isSelected) {
        MFXToggleNode control = (MFXToggleNode) getSkinnable();

        final KeyValue keyValue1;
        final KeyValue keyValue2;
        final KeyValue keyValue3;
        if (isSelected) {
            keyValue1 = new KeyValue(circle.opacityProperty(), 0.3, Interpolator.EASE_IN);
            keyValue2 = new KeyValue(circle.fillProperty(), control.getSelectedColor(), Interpolator.EASE_IN);
            keyValue3 = new KeyValue(circle.strokeProperty(), ((Color) control.getSelectedColor()).darker(), Interpolator.EASE_IN);
        } else {
            keyValue1 = new KeyValue(circle.opacityProperty(), 0.0, Interpolator.EASE_OUT);
            keyValue2 = new KeyValue(circle.fillProperty(), control.getUnSelectedColor(), Interpolator.EASE_OUT);
            keyValue3 = new KeyValue(circle.strokeProperty(), Color.TRANSPARENT, Interpolator.EASE_OUT);
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100), keyValue1, keyValue2, keyValue3)
        );
        timeline.play();
    }

    /**
     * Computes the radius to be used for the circle. It's either half the width or height
     * of the control depending on which of the two is the smaller.
     * @return The circle radius
     */
    private double computeRadius() {
        return Math.min((getSkinnable().getWidth() / 2), (getSkinnable().getHeight() / 2));
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Adds the circle and the ripple generator to the control as soon as they are not null.
     */
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (circle != null && rippleGenerator != null) {
            getChildren().addAll(circle, rippleGenerator);
        }
    }

    /**
     * Each time the control sizes change, recalculates the circle center coordinates and radius,
     * the clip center coordinates and radius, the ripple generator radius.
     */
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        circle.setCenterX(getSkinnable().getWidth() / 2);
        circle.setCenterY(getSkinnable().getHeight() / 2);
        circle.setRadius(computeRadius() * 1.3);

        rippleGenerator.setRippleRadius(computeRadius() * 1.2);

        Circle clip = new Circle();
        clip.setCenterX(getSkinnable().getWidth() / 2);
        clip.setCenterY(getSkinnable().getHeight() / 2);
        clip.setRadius(computeRadius() * 1.4);
        getSkinnable().setClip(clip);
    }
}
