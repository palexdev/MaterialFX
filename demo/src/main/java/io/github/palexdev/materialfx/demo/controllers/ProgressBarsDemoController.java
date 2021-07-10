/*
 * Copyright (C) 2021 Parisi Alessandro
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

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.Formatter;
import java.util.ResourceBundle;

public class ProgressBarsDemoController implements Initializable {

    @FXML
    private MFXProgressBar determinate;

    @FXML
    private Label progressLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressLabel.textProperty().bind(Bindings.createStringBinding(
                () -> new Formatter().format("%.2f", determinate.getProgress()).toString().replace(",", "."),
                determinate.progressProperty()
        ));
        progressLabel.textFillProperty().bind(Bindings.createObjectBinding(
                () -> progressLabel.getText().equals("1.00") ? Color.web("#85CB33") : Color.BLACK,
                progressLabel.textProperty()
        ));

        Timeline timeline2 = new Timeline(
                new KeyFrame(Duration.seconds(1), new KeyValue(determinate.progressProperty(), 0, MFXAnimationFactory.getInterpolatorV2()))
        );

        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(determinate.progressProperty(), 0.3, MFXAnimationFactory.getInterpolatorV1())),
                new KeyFrame(Duration.seconds(4), new KeyValue(determinate.progressProperty(), 0.6, MFXAnimationFactory.getInterpolatorV1())),
                new KeyFrame(Duration.seconds(6), new KeyValue(determinate.progressProperty(), 1.0, MFXAnimationFactory.getInterpolatorV1()))
        );

        timeline1.setOnFinished(event -> {
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
            pauseTransition.setOnFinished(end -> timeline2.playFromStart());
            pauseTransition.play();
        });

        timeline2.setOnFinished(event -> {
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
            pauseTransition.setOnFinished(end -> timeline1.playFromStart());
            pauseTransition.play();
        });

        timeline1.play();
    }
}
