/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.demo.controllers.DemoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Demo extends Application {
    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage primaryStage) throws IOException {
        CSSFX.start();

        FXMLLoader fxmlLoader = new FXMLLoader(MFXDemoResourcesLoader.loadURL("Demo.fxml"));
        fxmlLoader.setControllerFactory(controller -> new DemoController(primaryStage, getHostServices()));
        StackPane demoPane = fxmlLoader.load();

        demoPane.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        demoPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });

        primaryStage.setTitle("MaterialFX Demo - Features Preview");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(demoPane);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
