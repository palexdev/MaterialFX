/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.skins.legacy;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.TextUtils;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.MFXValidator;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXLegacyComboBox}.
 */
public class MFXLegacyComboBoxSkin<T> extends ComboBoxListViewSkin<T> {
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
	public MFXLegacyComboBoxSkin(MFXLegacyComboBox<T> comboBox) {
		super(comboBox);

		unfocusedLine = new Line();
		unfocusedLine.getStyleClass().add("unfocused-line");
		unfocusedLine.setManaged(false);
		unfocusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
		unfocusedLine.strokeLineCapProperty().bind(comboBox.lineStrokeCapProperty());
		unfocusedLine.strokeProperty().bind(Bindings.createObjectBinding(
				() -> {
					List<PseudoClass> pseudoClasses = new ArrayList<>(comboBox.getPseudoClassStates());
					return pseudoClasses.stream().map(PseudoClass::getPseudoClassName).collect(Collectors.toList()).contains("invalid") ? comboBox.getInvalidLineColor() : comboBox.getUnfocusedLineColor();
				}, comboBox.focusedProperty(), comboBox.getPseudoClassStates(), comboBox.unfocusedLineColorProperty()
		));
		unfocusedLine.endXProperty().bind(comboBox.widthProperty());
		unfocusedLine.setSmooth(true);
		unfocusedLine.setManaged(false);

		focusedLine = new Line();
		focusedLine.getStyleClass().add("focused-line");
		focusedLine.setManaged(false);
		focusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
		focusedLine.strokeLineCapProperty().bind(comboBox.lineStrokeCapProperty());
		focusedLine.strokeProperty().bind(Bindings.createObjectBinding(
				() -> {
					List<PseudoClass> pseudoClasses = new ArrayList<>(comboBox.getPseudoClassStates());
					return pseudoClasses.stream().map(PseudoClass::getPseudoClassName).collect(Collectors.toList()).contains("invalid") ? comboBox.getInvalidLineColor() : comboBox.getLineColor();
				}, comboBox.focusedProperty(), comboBox.getPseudoClassStates(), comboBox.lineColorProperty()
		));
		focusedLine.endXProperty().bind(comboBox.widthProperty());
		focusedLine.setSmooth(true);
		focusedLine.setScaleX(0.0);
		focusedLine.setManaged(false);

		MFXIconWrapper warnWrapper = new MFXIconWrapper("mfx-exclamation-triangle", 10, Color.RED, 10);

		validate = new Label();
		validate.setGraphic(warnWrapper);
		validate.getStyleClass().add("validate-label");
		validate.getStylesheets().setAll(comboBox.getUserAgentStylesheet());
		validate.setGraphicTextGap(padding);
		validate.setVisible(false);
		validate.setManaged(false);

		getChildren().addAll(unfocusedLine, focusedLine, validate);
		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds listeners for: line, focus, disabled, validator properties and validate label's text.
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
		MFXValidator validator = comboBox.getValidator();

		comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && comboBox.isValidated()) {
				comboBox.getValidator().update();
				validate.setVisible(!comboBox.isValid());
			}

			if (comboBox.isAnimateLines()) {
				buildAndPlayAnimation(newValue);
				return;
			}

			if (newValue) {
				focusedLine.setScaleX(1.0);
			} else {
				focusedLine.setScaleX(0.0);
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

		validator.setOnUpdated((valid, constraints) -> {
			if (!comboBox.isValidated()) return;

			if (!valid) {
				Constraint first = constraints.get(0);
				validate.setText(first.getMessage());
			}
			validate.setVisible(!valid);
		});

		validate.textProperty().addListener(invalidated -> comboBox.requestLayout());
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
		scaleTransition.setInterpolator(MFXAnimationFactory.INTERPOLATOR_V2);
		scaleTransition.play();
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		super.layoutChildren(x, y, w, h);

		double lw = snapSizeX(TextUtils.computeLabelWidth(validate));
		double lh = snapSizeY(TextUtils.computeTextHeight(validate.getFont(), validate.getText()));
		double lx = 0;
		double ly = h + (padding * 0.7);

		validate.resizeRelocate(lx, ly, lw, lh);
		focusedLine.relocate(0, h);
		unfocusedLine.relocate(0, h);
	}
}
