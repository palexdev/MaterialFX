/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.skin.RadioButtonSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXRadioButton}.
 */
public class MFXRadioButtonSkin extends RadioButtonSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final Circle radio;
    private final Circle dot;
    private final double padding = 6;

    private final RippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRadioButtonSkin(MFXRadioButton radioButton) {
        super(radioButton);

        final double radius = 8;
        radio = new Circle(radius);
        radio.getStyleClass().setAll("radio");
        radio.setStrokeWidth(2);
        radio.setFill(Color.web("#f4f4f4"));
        radio.setSmooth(true);

        dot = new Circle(radius);
        dot.getStyleClass().setAll("dot");
        dot.fillProperty().bind(radioButton.selectedColorProperty());
        dot.setScaleX(0);
        dot.setScaleY(0);
        dot.setSmooth(true);

        container = new StackPane();
        container.getStyleClass().add("radio-container");

        rippleGenerator = new RippleGenerator(container, new RippleClipTypeFactory());
        rippleGenerator.setRippleRadius(radius * 1.2);
        rippleGenerator.setInDuration(Duration.millis(350));
        rippleGenerator.setAnimateBackground(false);

        container.getChildren().addAll(rippleGenerator, radio, dot);

        radioButton.setCursor(Cursor.HAND);
        updateChildren();
        updateColors();
        radio.setStroke(radioButton.isSelected() ? radioButton.getSelectedColor() : radioButton.getUnSelectedColor());
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a listener to the selected property for animations, colors and ripples.
     */
    private void setListeners() {
        MFXRadioButton radioButton = (MFXRadioButton) getSkinnable();

        radioButton.selectedColorProperty().addListener((observable, oldValue, newValue) -> updateColors());
        radioButton.selectedTextColorProperty().addListener((observable, oldValue, newValue) -> updateColors());
        radioButton.unSelectedColorProperty().addListener((observable, oldValue, newValue) -> updateColors());
        radioButton.unSelectedTextColorProperty().addListener((observable, oldValue, newValue) -> updateColors());

        radioButton.selectedProperty().addListener((observableValue, oldValue, newValue) ->  {
            buildAndPlayAnimation();
            updateColors();
            rippleGenerator.setGeneratorCenterX(container.getWidth() / 2.0 - 0.3);
            rippleGenerator.setGeneratorCenterY(container.getHeight() / 2.0 - 0.3);
            rippleGenerator.createRipple();
        });

        /*
         * Workaround
         * When the control is created the Skin is still null, so if the RadioButton is set
         * to be selected the animation won't be played. To fix this add a listener to the
         * control's skinProperty, when the skin is not null and the RadioButton isSelected,
         * play the animation.
         */
        radioButton.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && radioButton.isSelected()) {
                buildAndPlayAnimation();
            }
        });
    }

    /**
     * Changes the ripples color according to the selected property and
     * the text color if {@code changeTextColor} property is true.
     */
    private void updateColors() {
        final MFXRadioButton radioButton = (MFXRadioButton) getSkinnable();
        Color selectedColor = (Color) radioButton.getSelectedColor();
        Color unSelectedColor = (Color) radioButton.getUnSelectedColor();
        rippleGenerator.setRippleColor(radioButton.isSelected() ? selectedColor : unSelectedColor);

        if (radioButton.isChangeTextColor()) {
            Color selectedTextColor = (Color) radioButton.getSelectedTextColor();
            Color unSelectedTextColor = (Color) radioButton.getUnSelectedTextColor();

            Text text = (Text) radioButton.lookup(".text");
            String color = radioButton.isSelected() ? ColorUtils.rgb(selectedTextColor) : ColorUtils.rgb(unSelectedTextColor);
            text.setStyle("-fx-fill: " + color + ";\n");
        }
    }

    /**
     * Builds and play the animation to show/hide the radio dot and change the stroke color.
     */
    private void buildAndPlayAnimation() {
        final MFXRadioButton radioButton = (MFXRadioButton) getSkinnable();
        final Duration duration = Duration.millis(200);

        KeyValue keyValue1 = new KeyValue(dot.scaleXProperty(), radioButton.isSelected() ? 0.55 : 0, Interpolator.EASE_BOTH);
        KeyValue keyValue2 = new KeyValue(dot.scaleYProperty(), radioButton.isSelected() ? 0.55 : 0, Interpolator.EASE_BOTH);
        KeyValue keyValue3 = new KeyValue(radio.strokeProperty(), radioButton.isSelected() ? radioButton.getSelectedColor() : radioButton.getUnSelectedColor(), Interpolator.EASE_BOTH);

        KeyFrame keyFrame = new KeyFrame(duration, keyValue1, keyValue2, keyValue3);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private void removeRadio() {
        getChildren().removeIf(node -> node.getStyleClass().contains("radio"));
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (radio != null) {
            removeRadio();
            getChildren().addAll(rippleGenerator, container);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final RadioButton radioButton = getSkinnable();

        final double contWidth = container.prefWidth(-1);
        final double contHeight = container.prefHeight(-1);
        final double computeWidth = Math.max(radioButton.prefWidth(-1), radioButton.minWidth(-1));
        final double labelWidth = Math.min(computeWidth - contWidth, w - snapSizeX(contWidth));
        final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(contHeight, labelHeight);
        final double xOffset = NodeUtils.computeXOffset(w, labelWidth + computeWidth, radioButton.getAlignment().getHpos()) + x;
        final double yOffset = NodeUtils.computeYOffset(h, maxHeight, radioButton.getAlignment().getVpos()) + y;

        layoutLabelInArea(xOffset + contWidth + padding, yOffset, labelWidth, maxHeight, radioButton.getAlignment());
        container.resize(snapSizeX(contWidth), snapSizeY(contHeight));
        positionInArea(container, xOffset, yOffset, contWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computeMinWidth(height,
                topInset,
                rightInset,
                bottomInset,
                leftInset) + snapSizeX(radio.minWidth(-1)) + padding;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height,
                topInset,
                rightInset,
                bottomInset,
                leftInset) + snapSizeX(radio.prefWidth(-1)) + padding;
    }
}
