package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

public class TextFields implements Initializable {

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
        BooleanProperty checkboxValidation = new SimpleBooleanProperty(false);
        BooleanProperty datePickerValidation = new SimpleBooleanProperty(false);
        checkboxValidation.bind(checkbox.selectedProperty());
        datePickerValidation.bind(picker.getDatePicker().valueProperty().isEqualTo(LocalDate.of(1911, Month.OCTOBER, 3)));
        validated.getValidator().add(checkboxValidation, "Checkbox must be selected");
        validated.getValidator().add(datePickerValidation, "Selected date must be 03/10/1911");
        validated.setIsValidated(true);

        label.visibleProperty().bind(validated.getValidator().validationProperty());
    }
}
