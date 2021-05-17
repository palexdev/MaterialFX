package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.beans.MFXLoaderBean.Builder;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.demo.MFXResourcesLoader;
import io.github.palexdev.materialfx.font.MFXFontIcon;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private HBox windowButtons;

    @FXML
    private StackPane navBar;

    @FXML
    private MFXScrollPane scrollPane;

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
        // Icons
        MFXFontIcon xIcon = new MFXFontIcon("mfx-x-circle", 16);
        MFXFontIcon minusIcon = new MFXFontIcon("mfx-minus-circle", 16);
        MFXFontIcon expandIcon = new MFXFontIcon("mfx-expand", 12.5);
        MFXFontIcon infoIcon = new MFXFontIcon("mfx-info-circle", 30, Color.rgb(75, 181, 255));
        MFXFontIcon angleIcon = new MFXFontIcon("mfx-angle-right", 20);

        // Buttons
        MFXIconWrapper closeButton = new MFXIconWrapper(xIcon, 22);
        closeButton.setId("closeButton");
        closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.close());

        MFXIconWrapper minimizeButton = new MFXIconWrapper(minusIcon, 22);
        minimizeButton.setId("minimizeButton");
        minimizeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setIconified(true));

        MFXIconWrapper expandButton = new MFXIconWrapper(expandIcon, 22);
        expandButton.setId("expandButton");
        expandButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        MFXIconWrapper infoButton = new MFXIconWrapper(infoIcon, 30).defaultRippleGeneratorBehavior();
        infoButton.setId("infoButton");
        infoButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> showInfo());

        opNavButton = new MFXButton("");
        opNavButton.setId("navButton");
        opNavButton.setPrefSize(25, 25);
        opNavButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        opNavButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> animate());

        // Graphics
        opNavButton.setGraphic(angleIcon);

        // Layout and Utils
        StackPane.setAlignment(infoButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(infoButton, new Insets(0, 8, 8, 0));
        StackPane.setAlignment(opNavButton, Pos.CENTER_LEFT);
        StackPane.setMargin(opNavButton, new Insets(0, 0, 0, 4));

        NodeUtils.makeRegionCircular(closeButton);
        NodeUtils.makeRegionCircular(minimizeButton);
        NodeUtils.makeRegionCircular(expandButton);
        NodeUtils.makeRegionCircular(infoButton);
        NodeUtils.makeRegionCircular(opNavButton);

        // Add all
        windowButtons.getChildren().addAll(expandButton, minimizeButton, closeButton);
        demoPane.getChildren().addAll(infoButton, opNavButton);

        // VLoader
        vLoader.setContentPane(contentPane);
        vLoader.addItem("BUTTONS", Builder.build(new MFXToggleNode("BUTTONS"), MFXResourcesLoader.load("buttons_demo.fxml")).setDefaultRoot(true));
        vLoader.addItem("CHECKBOXES", Builder.build(new MFXToggleNode("CHECKBOXES"), MFXResourcesLoader.load("checkboxes_demo.fxml")));
        vLoader.addItem("COMBOBOXES", Builder.build(new MFXToggleNode("COMBOBOXES"), MFXResourcesLoader.load("combo_boxes_demo.fxml")));
        vLoader.addItem("DATEPICKERS", Builder.build(new MFXToggleNode("DATEPICKERS"), MFXResourcesLoader.load("datepickers_demo.fxml")));
        vLoader.addItem("DIALOGS", Builder.build(new MFXToggleNode("DIALOGS"), MFXResourcesLoader.load("dialogs_demo.fxml")).setControllerFactory(controller -> new DialogsController(demoPane)));
        vLoader.addItem("LABELS", Builder.build(new MFXToggleNode("LABELS"), MFXResourcesLoader.load("labels_demo.fxml")));
        vLoader.addItem("LISTVIEWS", Builder.build(new MFXToggleNode("LISTVIEWS"), MFXResourcesLoader.load("listviews_demo.fxml")));
        vLoader.addItem("NOTIFICATIONS", Builder.build(new MFXToggleNode("NOTIFICATIONS"), MFXResourcesLoader.load("notifications_demo.fxml")));
        vLoader.addItem("PROGRESS_SPINNERS", Builder.build(new MFXToggleNode("PROGRESS_SPINNERS"), MFXResourcesLoader.load("progress_spinners_demo.fxml")));
        vLoader.addItem("RADIOBUTTONS", Builder.build(new MFXToggleNode("RADIOBUTTONS"), MFXResourcesLoader.load("radio_buttons_demo.fxml")));
        vLoader.addItem("SCROLLPANES", Builder.build(new MFXToggleNode("SCROLLPANES"), MFXResourcesLoader.load("scrollpanes_demo.fxml")));
        vLoader.addItem("TABLEVIEWS", Builder.build(new MFXToggleNode("TABLEVIEWS"), MFXResourcesLoader.load("tableviews_demo.fxml")));
        vLoader.addItem("TEXTFIELDS", Builder.build(new MFXToggleNode("TEXTFIELDS"), MFXResourcesLoader.load("textfields_demo.fxml")));
        vLoader.addItem("TOGGLES", Builder.build(new MFXToggleNode("TOGGLES"), MFXResourcesLoader.load("toggle_buttons_demo.fxml")));
        vLoader.addItem("TREEVIEWS", Builder.build(new MFXToggleNode("TREEVIEWS"), MFXResourcesLoader.load("treeviews_demo.fxml")));
        vLoader.start();

        // Others
        MFXScrollPane.smoothVScrolling(scrollPane);
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

        demoPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> demoPane.requestFocus());
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

        MFXFontIcon xIcon = new MFXFontIcon("mfx-x", 8);
        MFXIconWrapper closeButton = new MFXIconWrapper(xIcon, 22).defaultRippleGeneratorBehavior();
        closeButton.setId("closeButton");
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
