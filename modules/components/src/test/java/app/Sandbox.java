/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package app;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.progress.MFXProgressIndicator;
import io.github.palexdev.mfxcomponents.controls.progress.ProgressDisplayMode;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import static io.github.palexdev.mfxcore.utils.RandomUtils.random;

public class Sandbox extends Application {

    @Override
    public void start(Stage stage) {
        VBox box = new VBox(30);
        box.setAlignment(Pos.CENTER);
        box.setBackground(Background.fill(Color.WHITE));

        MFXProgressIndicator indicator = new MFXProgressIndicator();
        indicator.setDisplayMode(ProgressDisplayMode.LINEAR);
        //indicator.prefWidthProperty().bind(box.widthProperty().subtract(200));

        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMinSize(200, 40);
        label.textProperty().bind(indicator.progressProperty()
            .map(p -> p.doubleValue() < 0 ? "INDETERMINATE" : p.doubleValue() * 100 + "%")
        );

        MFXIconButton minus = new MFXIconButton(new MFXFontIcon(FontAwesomeSolid.MINUS));
        minus.setOnAction(e -> indicator.setProgress(indicator.getProgress() - random.nextFloat(0, 0.3f)));
        MFXIconButton plus = new MFXIconButton(new MFXFontIcon(FontAwesomeSolid.PLUS));
        plus.setOnAction(e -> indicator.setProgress(Math.max(0, indicator.getProgress()) + random.nextFloat(0, 0.3f)));

        HBox actions = new HBox(30, minus, plus);
        actions.setAlignment(Pos.CENTER);

        box.getChildren().addAll(label, indicator, actions);
        Scene scene = new Scene(box, 600, 600);
        MaterialThemes.INDIGO_LIGHT.applyOn(scene);
        stage.setScene(scene);
        stage.show();
        ScenicView.show(scene);
    }
}
