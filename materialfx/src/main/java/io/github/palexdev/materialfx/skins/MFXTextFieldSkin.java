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

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXTextField}.
 */
public class MFXTextFieldSkin extends TextFieldSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final double padding = 11;

    private final Line unfocusedLine;
    private final Line focusedLine;
    private final Label validate;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTextFieldSkin(MFXTextField textField) {
        super(textField);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.setStroke(textField.getUnfocusedLineColor());
        unfocusedLine.setStrokeWidth(textField.getLineStrokeWidth());
        unfocusedLine.setSmooth(true);

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setStroke(textField.getLineColor());
        focusedLine.setStrokeWidth(textField.getLineStrokeWidth());
        focusedLine.setSmooth(true);
        focusedLine.setScaleX(0.0);

        unfocusedLine.endXProperty().bind(textField.widthProperty());
        focusedLine.endXProperty().bind(textField.widthProperty());
        unfocusedLine.setManaged(false);
        focusedLine.setManaged(false);

        MFXFontIcon warnIcon = new MFXFontIcon("mfx-exclamation-triangle", Color.RED);
        MFXIconWrapper warnWrapper = new MFXIconWrapper(warnIcon, 10);

        validate = new Label("", warnWrapper);
        validate.getStyleClass().add("validate-label");
        validate.textProperty().bind(textField.getValidator().validatorMessageProperty());
        validate.setGraphicTextGap(padding);
        validate.setVisible(false);

        getChildren().addAll(unfocusedLine, focusedLine, validate);

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: line, focus, disabled and validator properties.
     * <p>
     * Validator: when the control is not focused, and of course if {@code isValidated} is true,
     * all the conditions in the validator are evaluated and if one is false the {@code validate} label is shown.
     * The label text is bound to the {@code validatorMessage} property so if you want to change it you can do it
     * by getting the instance with {@code getValidator()}.
     * <p>
     * There's also another listener to keep track of validator changes.
     */
    private void setListeners() {
        MFXTextField textField = (MFXTextField) getSkinnable();
        MFXDialogValidator validator = textField.getValidator();

        textField.lineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                focusedLine.setStroke(newValue);
            }
        });

        textField.unfocusedLineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                unfocusedLine.setStroke(newValue);
            }
        });

        textField.lineStrokeWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() != oldValue.doubleValue()) {
                unfocusedLine.setStrokeWidth(newValue.doubleValue());
                focusedLine.setStrokeWidth(newValue.doubleValue() * 1.3);
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && textField.isValidated()) {
                validate.setVisible(!validator.isValid());
            }

            if (textField.isAnimateLines()) {
                buildAndPlayAnimation(newValue);
                return;
            }

            if (newValue) {
                focusedLine.setScaleX(1.0);
            } else {
                focusedLine.setScaleX(0.0);
            }
        });

        textField.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (textField.isAnimateLines() && focusedLine.getScaleX() != 1.0) {
                buildAndPlayAnimation(true);
                return;
            }

            focusedLine.setScaleX(1.0);
        });

        textField.isValidatedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validate.setVisible(false);
            }
        });

        textField.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validate.setVisible(false);
            }
        });

        validator.addChangeListener((observable, oldValue, newValue) -> {
            if (textField.isValidated()) {
                validate.setVisible(!newValue);
            }
        });

        validate.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> validator.show());
    }

    /**
     * Builds and play the lines animation if {@code animateLines} is true.
     */
    private void buildAndPlayAnimation(boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(350), focusedLine);
        if (focused) {
            scaleTransition.setFromX(0.0);
            scaleTransition.setToX(1.0);
        } else {
            scaleTransition.setFromX(1.0);
            scaleTransition.setToX(0.0);
        }
        scaleTransition.setInterpolator(MFXAnimationFactory.getInterpolatorV2());
        scaleTransition.play();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        final double size = padding / 2.5;

        focusedLine.setTranslateY(h + padding * 0.7);
        unfocusedLine.setTranslateY(h + padding * 0.7);
        validate.resize(w * 1.5, h - size);
        validate.setTranslateY(focusedLine.getTranslateY() + size);
    }
}
