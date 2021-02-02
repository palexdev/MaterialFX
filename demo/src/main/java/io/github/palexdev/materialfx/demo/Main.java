package io.github.palexdev.materialfx.demo;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    private double xOffset;
    private double yOffset;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        CSSFX.start();

        FXMLLoader fxmlLoader = new FXMLLoader(ResourcesLoader.load("demo.fxml"));
        fxmlLoader.setControllerFactory(controller -> new io.github.palexdev.materialfx.demo.controllers.Demo(primaryStage, getHostServices()));
        StackPane demoPane = fxmlLoader.load();

        demoPane.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        demoPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });

        primaryStage.setTitle("MaterialFX Main - Features Preview");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(demoPane);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
