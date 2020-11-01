package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

import java.util.Random;

public class TogglesController {
    private final Random random = new Random(System.currentTimeMillis());

    @FXML
    private MFXToggleButton toggleButton;

    @FXML
    private void handleButtonClick() {
        toggleButton.setToggleColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        toggleButton.setSelected(false);
    }
}
