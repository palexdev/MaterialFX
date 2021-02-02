package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.demo.ResourcesLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DatePickers implements Initializable {

    @FXML
    private MFXDatePicker customPicker;

    @FXML
    private StackPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ResourcesLoader.load("customcss/custom-datepicker.css").toString();
        customPicker.getContent().getStylesheets().add(css);

        MFXDatePicker initialized = new MFXDatePicker(LocalDate.now());
        initialized.setColorText(true);
        pane.getChildren().add(initialized);
        StackPane.setMargin(initialized, new Insets(10, 0, 0, 0));
    }
}
