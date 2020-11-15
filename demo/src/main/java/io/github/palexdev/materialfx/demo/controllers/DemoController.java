package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.MFXResourcesManager.SVGResources;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXHLoader;
import io.github.palexdev.materialfx.controls.MFXToggleNode;
import io.github.palexdev.materialfx.controls.MFXVLoader;
import io.github.palexdev.materialfx.demo.MFXResourcesLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DemoController implements Initializable {
    @FXML
    private AnchorPane demoPane;

    @FXML
    private MFXHLoader hLoader;

    @FXML
    private MFXVLoader vLoader;

    @FXML
    private StackPane contentPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXButton closeButton = new MFXButton("");
        SVGPath x = SVGResources.X.getSvgPath();
        x.setScaleX(0.15);
        x.setScaleY(0.15);
        x.setFill(Color.WHITE);
        closeButton.setPrefSize(20, 20);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.setGraphic(x);
        closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> ((Stage) demoPane.getScene().getWindow()).close());
        closeButton.setStyle("-fx-background-color: transparent");
        demoPane.getChildren().add(closeButton);
        AnchorPane.setTopAnchor(closeButton, 8.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);

        hLoader.setContentPane(contentPane);
        vLoader.setContentPane(contentPane);

        hLoader.addItem(0, "BUTTONS", new MFXToggleNode("BUTTONS"), MFXResourcesLoader.load("buttons_demo.fxml"));
        hLoader.addItem(1, "CHECKBOXES", new MFXToggleNode("CHECKBOXES"), MFXResourcesLoader.load("checkboxes_demo.fxml"));
        hLoader.addItem(2, "TOGGLES", new MFXToggleNode("TOGGLES"), MFXResourcesLoader.load("toggle_buttons_demo.fxml"));
        hLoader.addItem(3, "DIALOGS", new MFXToggleNode("DIALOGS"), MFXResourcesLoader.load("dialogs_demo.fxml"), controller -> new DialogsController(demoPane));
        hLoader.setDefault("BUTTONS");

        /*
        vLoader.addItem(0, new MFXButton("Buttons", 80, 40), MFXResourcesLoader.load("buttons_demo.fxml"));
        vLoader.addItem(1, new MFXButton("Checkboxes", 80, 40), MFXResourcesLoader.load("checkboxes_demo.fxml"));
        vLoader.addItem(2, new MFXButton("Toggles", 80, 40), MFXResourcesLoader.load("toggle_buttons_demo.fxml"));
        vLoader.addItem(3, new MFXButton("Dialogs", 80, 40), MFXResourcesLoader.load("dialogs_demo.fxml"), controller -> new DialogsController(demoPane));
        */
    }
}
