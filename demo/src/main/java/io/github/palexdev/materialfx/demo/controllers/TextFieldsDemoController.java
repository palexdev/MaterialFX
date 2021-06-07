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

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.BindingUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

public class TextFieldsDemoController implements Initializable {

    @FXML
    private MFXTextField validated;

    @FXML
    private MFXCheckbox checkbox;

    @FXML
    private MFXDatePicker picker;

    @FXML
    private MFXPasswordField passwordValidated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanProperty checkboxValidation = BindingUtils.toProperty(
                Bindings.createBooleanBinding(
                        () -> checkbox.isSelected(),
                        checkbox.selectedProperty()
                )
        );
        BooleanProperty datePickerValidation = BindingUtils.toProperty(
                Bindings.createBooleanBinding(
                        () -> {
                            LocalDate value = picker.getDatePicker().getValue();
                            if (value != null) {
                                return value.equals(LocalDate.of(1911, Month.OCTOBER, 3));
                            } else {
                                return false;
                            }
                        },
                        picker.getDatePicker().valueProperty()
                )
        );
        validated.getValidator().add(checkboxValidation, "Checkbox must be selected");
        validated.getValidator().add(datePickerValidation, "Selected date must be 03/10/1911");
        validated.setValidated(true);
        validated.setIcon(new MFXFontIcon("mfx-variant7-mark", 16, Color.web("#8FF7A7")));
        validated.getIcon().visibleProperty().bind(validated.getValidator().validProperty());

        passwordValidated.setValidated(true);
        passwordValidated.getValidator().add(
                BindingUtils.toProperty(passwordValidated.passwordProperty().length().greaterThanOrEqualTo(8)),
                "Password must be at least 8 characters long"
        );
        passwordValidated.getValidator().add(BindingUtils.toProperty(
                Bindings.createBooleanBinding(() -> passwordValidated.getPassword().matches(".*\\d.*"), passwordValidated.passwordProperty())),
                "Password must contain at least one digit"
        );
        passwordValidated.getValidator().add(BindingUtils.toProperty(
                Bindings.createBooleanBinding(() -> StringUtils.containsAny(passwordValidated.getPassword(), "", "?", "!", "@", "(", ")", "[", "]", "{", "}", "-", "_"), passwordValidated.passwordProperty())),
                "Password must contain at least one special character among these: ?!@()[]{}-_"
        );
    }
}
