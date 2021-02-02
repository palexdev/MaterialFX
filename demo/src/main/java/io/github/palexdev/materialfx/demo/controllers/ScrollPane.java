package io.github.palexdev.materialfx.demo.controllers;


import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ScrollPane implements Initializable {

    @FXML
    private MFXScrollPane scrollPaneV;

    @FXML
    private MFXScrollPane scrollPaneVH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXScrollPane.smoothVScrolling(scrollPaneV);
        MFXScrollPane.smoothVScrolling(scrollPaneVH);
    }

    @FXML
    void setRandomTrackColor() {
        scrollPaneV.setTrackColor(ColorUtils.getRandomColor());
        scrollPaneVH.setTrackColor(ColorUtils.getRandomColor());
    }

    @FXML
    void setRandomThumbColor() {
        scrollPaneV.setThumbColor(ColorUtils.getRandomColor());
        scrollPaneVH.setThumbColor(ColorUtils.getRandomColor());
    }

    @FXML
    void setRandomThumbHoverColor() {
        scrollPaneV.setThumbHoverColor(ColorUtils.getRandomColor());
        scrollPaneVH.setThumbHoverColor(ColorUtils.getRandomColor());
    }
}
