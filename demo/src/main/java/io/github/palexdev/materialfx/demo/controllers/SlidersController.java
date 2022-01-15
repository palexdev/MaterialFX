package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SlidersController implements Initializable {

	@FXML
	private MFXSlider customSlider;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		customSlider.getRanges1().add(NumberRange.of(customSlider.getMin(), 33.0));
		customSlider.getRanges2().add(NumberRange.of(34.0, 66.0));
		customSlider.getRanges3().add(NumberRange.of(67.0, customSlider.getMax()));
	}
}
