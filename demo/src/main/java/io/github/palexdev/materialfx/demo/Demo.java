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

        FXMLLoader fxmlLoader = new FXMLLoader(MFXResourcesLoader.load("Demo.fxml"));
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
