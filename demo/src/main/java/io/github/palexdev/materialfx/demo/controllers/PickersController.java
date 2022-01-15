package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PickersController implements Initializable {

	@FXML
	private MFXDatePicker custDatePicker;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		custDatePicker.setGridAlgorithm(DateTimeUtils::partialIntMonthMatrix);
		custDatePicker.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", custDatePicker.getLocale()));
	}
}