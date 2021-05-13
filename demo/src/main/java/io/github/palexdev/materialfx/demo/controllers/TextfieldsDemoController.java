package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.BindingUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

public class TextfieldsDemoController implements Initializable {

    @FXML
    private MFXTextField validated;

    @FXML
    private MFXCheckbox checkbox;

    @FXML
    private MFXDatePicker picker;

    @FXML
    private Label label;

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

        label.visibleProperty().bind(validated.getValidator().validProperty());
    }
}
