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
