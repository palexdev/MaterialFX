package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.fxml.FXML;

public class TogglesController {

    @FXML
    private MFXToggleButton toggleButton;

    @FXML
    private void handleButtonClick() {
        toggleButton.setToggleColor(ColorUtils.getRandomColor());
        toggleButton.setSelected(false);
    }
}
