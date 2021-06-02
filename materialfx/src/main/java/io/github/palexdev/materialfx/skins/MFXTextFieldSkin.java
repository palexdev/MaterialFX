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
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTextField}.
 * <p></p>
 * A little note on the icon positioning and the text field width.
 * If you use the control in SceneBuilder you will immediately notice that the width of the text field doesn't take into
 * account the icon. The icon is placed "outside" the control because otherwise the input text would end under the icon,
 * and that's not a pleasant view.
 * <p>
 * Another solution would be to entirely recreate the TextFieldSkin using an HBox to contain the field and the icon,
 * but I don't think it's necessary since this strategy seems to work fine. Also don't forget that you can position the icon manually,
 * you should be able to put the icon "inside" the control by specifying a right inset equal to the icon's width, see {@link MFXTextField#iconInsetsProperty()}.
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
        unfocusedLine.setManaged(false);
        unfocusedLine.strokeWidthProperty().bind(textField.lineStrokeWidthProperty());
        unfocusedLine.strokeLineCapProperty().bind(textField.lineStrokeCapProperty());
        unfocusedLine.strokeProperty().bind(Bindings.createObjectBinding(
                () -> {
                    List<PseudoClass> pseudoClasses = new ArrayList<>(textField.getPseudoClassStates());
                    return pseudoClasses.stream().map(PseudoClass::getPseudoClassName).collect(Collectors.toList()).contains("invalid") ? textField.getInvalidLineColor() : textField.getUnfocusedLineColor();
                }, textField.focusedProperty(), textField.getPseudoClassStates(), textField.unfocusedLineColorProperty()
        ));
        unfocusedLine.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Node icon = textField.getIcon();
            if (icon != null) {
                return textField.getWidth() + icon.getLayoutBounds().getWidth() +
                        textField.getIconInsets().getLeft() - textField.getIconInsets().getRight();
            }
            return textField.getWidth();
        }, textField.widthProperty(), textField.iconProperty()));
        unfocusedLine.setSmooth(true);
        unfocusedLine.setManaged(false);

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setManaged(false);
        focusedLine.strokeWidthProperty().bind(textField.lineStrokeWidthProperty());
        focusedLine.strokeLineCapProperty().bind(textField.lineStrokeCapProperty());
        focusedLine.strokeProperty().bind(Bindings.createObjectBinding(
                () -> {
                    List<PseudoClass> pseudoClasses = new ArrayList<>(textField.getPseudoClassStates());
                    return pseudoClasses.stream().map(PseudoClass::getPseudoClassName).collect(Collectors.toList()).contains("invalid") ? textField.getInvalidLineColor() : textField.getLineColor();
                }, textField.focusedProperty(), textField.getPseudoClassStates(), textField.lineColorProperty()
        ));
        focusedLine.setSmooth(true);
        focusedLine.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Node icon = textField.getIcon();
            if (icon != null) {
                return textField.getWidth() + icon.getLayoutBounds().getWidth() +
                        textField.getIconInsets().getLeft() - textField.getIconInsets().getRight();
            }
            return textField.getWidth();
        }, textField.widthProperty(), textField.iconProperty()));
        focusedLine.setScaleX(0.0);
        focusedLine.setManaged(false);

        MFXFontIcon warnIcon = new MFXFontIcon("mfx-exclamation-triangle", Color.RED);
        MFXIconWrapper warnWrapper = new MFXIconWrapper(warnIcon, 10);

        validate = new Label();
        validate.setGraphic(warnWrapper);
        validate.getStyleClass().add("validate-label");
        validate.getStylesheets().setAll(textField.getUserAgentStylesheet());
        validate.textProperty().bind(textField.getValidator().validatorMessageProperty());
        validate.setGraphicTextGap(padding);
        validate.setVisible(false);
        validate.setManaged(false);

        if (textField.isValidated() && textField.getValidator().isInitControlValidation()) {
            validate.setVisible(!textField.isValid());
        }

        getChildren().addAll(unfocusedLine, focusedLine, validate);
        Node icon = textField.getIcon();
        if (icon != null) {
            icon.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> icon.setCursor(Cursor.DEFAULT));
            getChildren().add(icon);
        }

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: icon, icon insets, line, focus, disabled and validator properties.
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

        textField.iconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                getChildren().remove(oldValue);
            } else {
                getChildren().remove(oldValue);
                newValue.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> newValue.setCursor(Cursor.DEFAULT));
                getChildren().add(newValue);
            }
        });

        textField.iconInsetsProperty().addListener(invalidated -> textField.requestLayout());

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && textField.isValidated()) {
                textField.getValidator().update();
                validate.setVisible(!textField.isValid());
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
                validate.setVisible(!textField.isValid());
            }
        });

        validator.addListener(invalidated -> {
            if (textField.isValidated()) {
                validate.setVisible(!textField.isValid());
            }
        });

        validate.textProperty().addListener(invalidated -> textField.requestLayout());
        validate.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> validate.setCursor(Cursor.DEFAULT));
        validate.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validator.showModal(textField.getScene().getWindow()));
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
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset), 120);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        MFXTextField textField = (MFXTextField) getSkinnable();

        double lw = snapSizeX(LabelUtils.computeLabelWidth(validate));
        double lh = snapSizeY(LabelUtils.computeTextHeight(validate.getFont(), validate.getText())); // TODO Check
        double lx = 0;
        double ly = h + lh;

        validate.resizeRelocate(lx, ly, lw, lh);
        focusedLine.relocate(0, h + padding * 0.7);
        unfocusedLine.relocate(0, h + padding * 0.7);

        Node icon = textField.getIcon();
        if (icon != null) {
            icon.setManaged(false);

            double iX = snapPositionX(w +
                    textField.getIconInsets().getLeft() -
                    textField.getIconInsets().getRight()
            );
            double iY = snapPositionY(h - (icon.getLayoutBounds().getHeight() / 2.0) +
                    textField.getIconInsets().getTop() -
                    textField.getIconInsets().getBottom() -
                    padding * 0.7
            );
            icon.relocate(iX, iY);
        }
    }
}
