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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import javafx.animation.Animation;
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

        Animation a1 = AnimationUtils.TimelineBuilder.build()
                .add(
                        KeyFrames.of(2000, determinate.progressProperty(), 0.3, MFXAnimationFactory.getInterpolatorV1()),
                        KeyFrames.of(4000, determinate.progressProperty(), 0.6, MFXAnimationFactory.getInterpolatorV1()),
                        KeyFrames.of(6000, determinate.progressProperty(), 1.0, MFXAnimationFactory.getInterpolatorV1())
                )
                .getAnimation();

        Animation a2 = AnimationUtils.TimelineBuilder.build()
                .add(
                        KeyFrames.of(1000, determinate.progressProperty(), 0, MFXAnimationFactory.getInterpolatorV2())
                )
                .getAnimation();

        a1.setOnFinished(end -> AnimationUtils.PauseBuilder.build()
                .setDuration(Duration.seconds(1))
                .setOnFinished(event -> a2.playFromStart())
                .getAnimation()
                .play()
        );
        a2.setOnFinished(end -> AnimationUtils.PauseBuilder.build()
                .setDuration(Duration.seconds(1))
                .setOnFinished(event -> a1.playFromStart())
                .getAnimation()
                .play()
        );

        a1.play();

        determinate.getRanges1().add(NumberRange.of(0.0, 0.30));
        determinate.getRanges2().add(NumberRange.of(0.31, 0.60));
        determinate.getRanges3().add(NumberRange.of(0.61, 1.0));
    }
}
