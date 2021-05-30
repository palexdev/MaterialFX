package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TogglesController implements Initializable {

    @FXML
    private MFXToggleButton toggleButton;

    @FXML
    private MFXRectangleToggleNode rec1;

    @FXML
    private MFXRectangleToggleNode rec2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rec1.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec1.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec2.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
        rec2.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(16, ColorUtils.getRandomColor()));
    }

    @FXML
    private void handleButtonClick() {
        toggleButton.setToggleColor(ColorUtils.getRandomColor());
        toggleButton.setSelected(false);
    }
}
