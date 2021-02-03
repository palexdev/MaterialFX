package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressSpinnersDemoController implements Initializable {

    @FXML
    private MFXProgressSpinner progress1;

    @FXML
    private MFXProgressSpinner progress2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(progress1.progressProperty(), 0),
                        new KeyValue(progress2.progressProperty(), 0)
                ),
                new KeyFrame(
                        Duration.seconds(0.5),
                        new KeyValue(progress1.progressProperty(), 0.5)
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(progress1.progressProperty(), 1),
                        new KeyValue(progress2.progressProperty(), 1)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
