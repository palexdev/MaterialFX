package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.MFXResourcesManager.SVGResources;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.demo.MFXResourcesLoader;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DemoController implements Initializable {
    private final Stage primaryStage;
    private final HostServices hostServices;

    private ParallelTransition openNav;
    private ParallelTransition closeNav;
    private boolean isNavShown = false;

    @FXML
    private StackPane demoPane;

    @FXML
    private StackPane navBar;

    @FXML
    private MFXVLoader vLoader;

    private MFXButton opNavButton;

    @FXML
    private StackPane contentPane;

    public DemoController(Stage primaryStage, HostServices hostServices) {
        this.primaryStage = primaryStage;
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Resources
        SVGPath x = SVGResources.X.getSvgPath();
        x.setScaleX(0.14);
        x.setScaleY(0.14);
        SVGPath minus = SVGResources.MINUS.getSvgPath();
        minus.setScaleX(0.03);
        minus.setScaleY(0.05);
        SVGPath expand = SVGResources.EXPAND.getSvgPath();
        expand.setScaleX(0.5);
        expand.setScaleY(0.5);
        SVGPath info = SVGResources.INFO.getSvgPath();
        info.setScaleX(0.4);
        info.setScaleY(0.4);
        info.setFill(Color.rgb(75, 181, 255));
        FontIcon angle = new FontIcon("fas-angle-right");
        angle.setIconSize(20);

        // Buttons
        MFXButton closeButton = new MFXButton("");
        closeButton.setId("closeButton");
        closeButton.setPrefSize(25, 25);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.close());

        MFXButton minimizeButton = new MFXButton("");
        minimizeButton.setId("minimizeButton");
        minimizeButton.setPrefSize(22, 22);
        minimizeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minimizeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setIconified(true));

        MFXButton expandButton = new MFXButton("");
        expandButton.setId("expandButton");
        expandButton.setPrefSize(22, 22);
        expandButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        expandButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        MFXButton infoButton = new MFXButton("");
        infoButton.setId("infoButton");
        infoButton.setPrefSize(30, 30);
        infoButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        infoButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> showInfo());

        opNavButton = new MFXButton("");
        opNavButton.setId("navButton");
        opNavButton.setPrefSize(25, 25);
        opNavButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        opNavButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> animate());


        // Graphics
        closeButton.setGraphic(x);
        minimizeButton.setGraphic(minus);
        expandButton.setGraphic(expand);
        infoButton.setGraphic(info);
        opNavButton.setGraphic(angle);

        // Layout and Utils
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(8, 8, 0, 0));
        StackPane.setAlignment(minimizeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(minimizeButton, new Insets(10, 34, 0, 0));
        StackPane.setAlignment(expandButton, Pos.TOP_RIGHT);
        StackPane.setMargin(expandButton, new Insets(10, 60, 0, 0));
        StackPane.setAlignment(infoButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(infoButton, new Insets(0, 8, 8, 0));
        StackPane.setAlignment(opNavButton, Pos.CENTER_LEFT);
        StackPane.setMargin(opNavButton, new Insets(0, 0, 0, 9));

        NodeUtils.makeRegionCircular(closeButton);
        NodeUtils.makeRegionCircular(minimizeButton);
        NodeUtils.makeRegionCircular(expandButton);
        NodeUtils.makeRegionCircular(infoButton);
        NodeUtils.makeRegionCircular(opNavButton);

        // Add all
        demoPane.getChildren().addAll(closeButton, minimizeButton, expandButton, infoButton, opNavButton);

        // VLoader
        vLoader.setContentPane(contentPane);
        vLoader.addItem(0, "BUTTONS", new MFXToggleNode("BUTTONS"), MFXResourcesLoader.load("buttons_demo.fxml"));
        vLoader.addItem(1, "CHECKBOXES", new MFXToggleNode("CHECKBOXES"), MFXResourcesLoader.load("checkboxes_demo.fxml"));
        vLoader.addItem(2, "TOGGLES", new MFXToggleNode("TOGGLES"), MFXResourcesLoader.load("toggle_buttons_demo.fxml"));
        vLoader.addItem(3, "DIALOGS", new MFXToggleNode("DIALOGS"), MFXResourcesLoader.load("dialogs_demo.fxml"), controller -> new DialogsController(demoPane));
        vLoader.addItem(4, "NOTIFICATIONS", new MFXToggleNode("NOTIFICATIONS"), MFXResourcesLoader.load("notifications_demo.fxml"));
        vLoader.addItem(5, "SCROLLPANE", new MFXToggleNode("SCROLLPANE"), MFXResourcesLoader.load("scrollpane_demo.fxml"));
        vLoader.setDefault("BUTTONS");

        // Others
        primaryStage.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Scene scene = primaryStage.getScene();
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.F11) {
                        primaryStage.setFullScreen(!primaryStage.isFullScreen());
                    }
                });
                scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    if (isNavShown) {
                        animate();
                    }
                });
            }
        });
        navBar.setVisible(false);
        initAnimations();
    }

    private void initAnimations() {
        Timeline fadeIn = MFXAnimationFactory.FADE_IN.build(navBar, 400);
        Timeline show = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(navBar.translateXProperty(), 5))
        );
        Timeline left = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(opNavButton.rotateProperty(), -180))
        );

        Timeline fadeOut = MFXAnimationFactory.FADE_OUT.build(navBar, 50);
        Timeline close = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(navBar.translateXProperty(), -200))
        );
        Timeline right = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(opNavButton.rotateProperty(), 0))
        );

        openNav = new ParallelTransition(fadeIn, show, left);
        openNav.setOnFinished(event -> isNavShown = true);
        closeNav = new ParallelTransition(fadeOut, close, right);
        closeNav.setOnFinished(event -> isNavShown = false);
    }

    private void animate() {
        if (!isNavShown) {
            navBar.setVisible(true);
            openNav.play();
        } else {
            closeNav.play();
        }
    }

    private void showInfo() {
        MFXDialog infoDialog;
        MFXStageDialog stageDialog;
        try {
            FXMLLoader loader = new FXMLLoader(MFXResourcesLoader.load("info_dialog.fxml"));
            loader.setControllerFactory(controller -> new InfoController(hostServices));
            infoDialog = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Close Button
        StackPane header = (StackPane) infoDialog.lookup("#headerNode");

        SVGPath x = SVGResources.X.getSvgPath();
        x.setScaleX(0.14);
        x.setScaleY(0.14);

        MFXButton closeButton = new MFXButton("");
        closeButton.setId("closeButton");
        closeButton.setPrefSize(25, 25);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.setGraphic(x);
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(4, 4, 0, 0));
        NodeUtils.makeRegionCircular(closeButton);
        header.getChildren().add(closeButton);

        stageDialog = new MFXStageDialog(infoDialog);
        stageDialog.setScrimBackground(true);
        stageDialog.setOwner(primaryStage);
        stageDialog.setModality(Modality.APPLICATION_MODAL);
        closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> stageDialog.close());
        stageDialog.setCenterInOwner(true);
        stageDialog.show();
    }

}
