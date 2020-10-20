package it.paprojects.materialfx.demo;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        CSSFX.start();

        AnchorPane anchorPane = FXMLLoader.load(MFXResources.load("buttons_demo.fxml"));

        primaryStage.setTitle("HELLO THERE");
        primaryStage.setScene(new Scene(anchorPane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
