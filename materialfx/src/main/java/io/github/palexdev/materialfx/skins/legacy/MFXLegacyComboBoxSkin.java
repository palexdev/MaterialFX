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

package io.github.palexdev.materialfx.skins.legacy;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXLegacyComboBox}.
 */
public class MFXLegacyComboBoxSkin<T> extends ComboBoxListViewSkin<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final double padding = 11;

    private final Line line;
    private final Line focusLine;
    private final Label validate;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLegacyComboBoxSkin(MFXLegacyComboBox<T> comboBox) {
        super(comboBox);

        line = new Line();
        line.getStyleClass().add("unfocused-line");
        line.setStroke(comboBox.getUnfocusedLineColor());
        line.setStrokeWidth(comboBox.getLineStrokeWidth());
        line.setSmooth(true);

        focusLine = new Line();
        focusLine.getStyleClass().add("focused-line");
        focusLine.setStroke(comboBox.getLineColor());
        focusLine.setStrokeWidth(comboBox.getLineStrokeWidth());
        focusLine.setSmooth(true);
        focusLine.setScaleX(0.0);

        line.endXProperty().bind(comboBox.widthProperty());
        focusLine.endXProperty().bind(comboBox.widthProperty());

        MFXFontIcon warnIcon = new MFXFontIcon("mfx-exclamation-triangle", Color.RED);
        MFXIconWrapper warnWrapper = new MFXIconWrapper(warnIcon, 10);

        validate = new Label("", warnWrapper);
        validate.getStyleClass().add("validate-label");
        validate.textProperty().bind(comboBox.getValidator().validatorMessageProperty());
        validate.setFont(Font.font(padding));
        validate.setGraphicTextGap(padding / 2);
        validate.setVisible(false);

        getChildren().addAll(line, focusLine, validate);

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
     * There's also another listener to keep track of validator changes and an event handler to show a dialog if you click
     * on the warning label.
     */
    private void setListeners() {
        MFXLegacyComboBox<T> comboBox = (MFXLegacyComboBox<T>) getSkinnable();
        MFXDialogValidator validator = comboBox.getValidator();

        comboBox.lineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                focusLine.setStroke(newValue);
            }
        });

        comboBox.unfocusedLineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                line.setStroke(newValue);
            }
        });

        comboBox.lineStrokeWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() != oldValue.doubleValue()) {
                line.setStrokeWidth(newValue.doubleValue());
                focusLine.setStrokeWidth(newValue.doubleValue() * 1.3);
            }
        });

        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && comboBox.isValidated()) {
                validate.setVisible(!validator.isValid());
            }

            if (comboBox.isAnimateLines()) {
                buildAndPlayAnimation(newValue);
                return;
            }

            if (newValue) {
                focusLine.setScaleX(1.0);
            } else {
                focusLine.setScaleX(0.0);
            }
        });

        comboBox.isValidatedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validate.setVisible(false);
            }
        });

        comboBox.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validate.setVisible(false);
            }
        });

        validator.addChangeListener((observable, oldValue, newValue) -> {
            if (comboBox.isValidated()) {
                validate.setVisible(!newValue);
            }
        });
        validate.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> validator.showModal(comboBox.getScene().getWindow()));
    }

    /**
     * Builds and play the lines animation if {@code animateLines} is true.
     */
    private void buildAndPlayAnimation(boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), focusLine);
        if (focused) {
            scaleTransition.setFromX(0.0);
            scaleTransition.setToX(1.0);
        } else {
            scaleTransition.setFromX(1.0);
            scaleTransition.setToX(0.0);
        }
        scaleTransition.setInterpolator(MFXAnimationFactory.getInterpolatorV1());
        scaleTransition.play();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        final double size = padding / 2.5;
        final double tx = -((w - line.getEndX()) / 2);

        focusLine.setTranslateY(h);
        line.setTranslateY(h);
        validate.resize(w * 1.5, h - size);
        validate.setTranslateY(focusLine.getTranslateY() + size);
        validate.setTranslateX(tx);
    }
}
