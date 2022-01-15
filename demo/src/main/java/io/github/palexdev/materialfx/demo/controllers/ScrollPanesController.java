package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ScrollPanesController implements Initializable {

	@FXML
	private MFXScrollPane scroll1;

	@FXML
	private MFXScrollPane scroll2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Label l1 = new Label(Model.ipsum);
		l1.setWrapText(true);
		Label l2 = new Label(Model.ipsum);
		l2.setMaxSize(400, Double.MAX_VALUE);
		l2.setWrapText(true);

		scroll1.setContent(l1);
		scroll2.setContent(l2);

		ScrollUtils.addSmoothScrolling(scroll1);
		ScrollUtils.addSmoothScrolling(scroll2);
	}

	@FXML
	private void setRandomTrackColor() {
		scroll1.setTrackColor(ColorUtils.getRandomColor());
		scroll2.setTrackColor(ColorUtils.getRandomColor());
	}

	@FXML
	private void setRandomThumbColor() {
		scroll1.setThumbColor(ColorUtils.getRandomColor());
		scroll2.setThumbColor(ColorUtils.getRandomColor());
	}

	@FXML
	private void setRandomThumbHoverColor() {
		scroll1.setThumbHoverColor(ColorUtils.getRandomColor());
		scroll2.setThumbHoverColor(ColorUtils.getRandomColor());
	}
}
