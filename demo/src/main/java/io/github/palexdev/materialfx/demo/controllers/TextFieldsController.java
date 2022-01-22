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

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;

public class TextFieldsController implements Initializable {
	private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
	// Because fuck regex, stupid shit
	private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
	private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
	private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
	private static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");

	@FXML
	private MFXTextField textField;

	@FXML
	private MFXPasswordField passwordField;

	@FXML
	private Label validationLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		textField.setTextLimit(10);

		Constraint lengthConstraint = Constraint.Builder.build()
				.setSeverity(Severity.ERROR)
				.setMessage("Password must be at least 8 characters long")
				.setCondition(passwordField.textProperty().length().greaterThanOrEqualTo(8))
				.get();

		Constraint digitConstraint = Constraint.Builder.build()
				.setSeverity(Severity.ERROR)
				.setMessage("Password must contain at least one digit")
				.setCondition(Bindings.createBooleanBinding(
						() -> containsAny(passwordField.getText(), "", digits),
						passwordField.textProperty()
				))
				.get();

		Constraint charactersConstraint = Constraint.Builder.build()
				.setSeverity(Severity.ERROR)
				.setMessage("Password must contain at least one lowercase and one uppercase characters")
				.setCondition(Bindings.createBooleanBinding(
						() -> containsAny(passwordField.getText(), "", upperChar) && containsAny(passwordField.getText(), "", lowerChar),
						passwordField.textProperty()
				))
				.get();

		Constraint specialCharactersConstraint = Constraint.Builder.build()
				.setSeverity(Severity.ERROR)
				.setMessage("Password must contain at least one special character")
				.setCondition(Bindings.createBooleanBinding(
						() -> containsAny(passwordField.getText(), "", specialCharacters),
						passwordField.textProperty()
				))
				.get();

		passwordField.getValidator()
				.constraint(digitConstraint)
				.constraint(charactersConstraint)
				.constraint(specialCharactersConstraint)
				.constraint(lengthConstraint);

		passwordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				validationLabel.setVisible(false);
				passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
			}
		});

		passwordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue && !newValue) {
				List<Constraint> constraints = passwordField.validate();
				if (!constraints.isEmpty()) {
					passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
					validationLabel.setText(constraints.get(0).getMessage());
					validationLabel.setVisible(true);
				}
			}
		});
	}
}
