package it.paprojects.materialfx.demo;

import fr.brouillard.oss.cssfx.CSSFX;
import it.paprojects.materialfx.MFXResources;
import it.paprojects.materialfx.controls.MFXButton;
import it.paprojects.materialfx.utils.ButtonType;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) {
        CSSFX.start();

        StackPane pane = new StackPane();
        pane.getStylesheets().add(MFXResources.load("css/mfx-button.css").toString());

        MFXButton button = new MFXButton("MFXButton");
        button.setPrefWidth(500);
        button.setPrefHeight(200);
        button.setButtonType(ButtonType.RAISED);
        pane.getChildren().add(button);
        StackPane.setAlignment(button, Pos.CENTER);

        button.setRippleColor(Color.rgb(10, 120, 200));
        button.setRippleRadius(255);
        button.setRippleInDuration(Duration.millis(600));

        primaryStage.setTitle("HELLO THERE");
        primaryStage.setScene(new Scene(pane, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
