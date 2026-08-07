/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.utils.StringUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LabelTestApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label(StringUtils.randAlphanumeric(100));
        label.setMaxWidth(200);
        label.setFont(Font.font(16));
        label.setDisableTruncation(true);
        StackPane root = new StackPane(label);

        label.setStyle("-fx-border-color: red");

        Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene);
        stage.show();
    }
}
