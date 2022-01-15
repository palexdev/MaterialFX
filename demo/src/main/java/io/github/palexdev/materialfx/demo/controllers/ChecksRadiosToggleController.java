package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ChecksRadiosToggleController implements Initializable {

	@FXML
	private MFXToggleButton customToggle;

	@FXML
	private MFXRectangleToggleNode r1;

	@FXML
	private MFXRectangleToggleNode r2;

	@FXML
	private MFXRectangleToggleNode r3;

	@FXML
	private void changeColors(ActionEvent event) {
		customToggle.setColors(ColorUtils.getRandomColor(), ColorUtils.getRandomColor());
		customToggle.setSelected(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		r1.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));
		r1.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));

		r2.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));
		r2.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));

		r3.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));
		r3.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, Color.BLACK));
	}
}
