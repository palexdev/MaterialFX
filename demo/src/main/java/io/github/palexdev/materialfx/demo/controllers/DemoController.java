/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.beans.MFXLoaderBean.Builder;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.demo.MFXDemoResourcesLoader;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DemoController implements Initializable {
    private final Stage primaryStage;
    private final HostServices hostServices;

    private MFXButton opNavButton;
    private ParallelTransition openNav;
    private ParallelTransition closeNav;
    private boolean isNavShown = false;

    private final MediaPlayer m1;
    private final MediaPlayer m2;

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

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox logoPane;

    @FXML
    private ImageView logo;

    @FXML
    private Label splashLabel1;

    @FXML
    private Label splashLabel2;

    @FXML
    private Label splashLabel3;

    @FXML
    private TextFlow version;

    public DemoController(Stage primaryStage, HostServices hostServices) {
        m1 = new MediaPlayer(new Media(MFXDemoResourcesLoader.load("assets/welcome1.wav")));
        m2 = new MediaPlayer(new Media(MFXDemoResourcesLoader.load("assets/welcome2.wav")));

        m1.setVolume(0.3);
        m2.setVolume(0.2);

        this.primaryStage = primaryStage;
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Icons
        MFXFontIcon angleIcon = new MFXFontIcon("mfx-angle-right", 20);

        // Buttons
        MFXIconWrapper closeButton = new MFXIconWrapper("mfx-x-circle", 16, 22);
        closeButton.setId("closeButton");
        closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.close());

        MFXIconWrapper minimizeButton = new MFXIconWrapper("mfx-minus-circle", 16, 22);
        minimizeButton.setId("minimizeButton");
        minimizeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setIconified(true));

        MFXIconWrapper expandButton = new MFXIconWrapper("mfx-expand", 12.5, 22);
        expandButton.setId("expandButton");
        expandButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        MFXIconWrapper infoButton = new MFXIconWrapper("mfx-info-circle", 30, Color.rgb(75, 181, 255), 30).defaultRippleGeneratorBehavior();
        infoButton.setId("infoButton");
        infoButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> showInfo());

        opNavButton = new MFXButton("");
        opNavButton.setOpacity(0.0);
        opNavButton.setDisable(true);
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
        vLoader.addItem("BUTTONS", Builder.build(new MFXRectangleToggleNode("BUTTONS"), MFXDemoResourcesLoader.loadURL("ButtonsDemo.fxml")).setDefaultRoot(true));
        vLoader.addItem("CHECKBOXES", Builder.build(new MFXRectangleToggleNode("CHECKBOXES"), MFXDemoResourcesLoader.loadURL("CheckBoxesDemo.fxml")));
        vLoader.addItem("COMBOBOXES", Builder.build(new MFXRectangleToggleNode("COMBOBOXES"), MFXDemoResourcesLoader.loadURL("ComboBoxesDemo.fxml")));
        vLoader.addItem("DATEPICKERS", Builder.build(new MFXRectangleToggleNode("DATEPICKERS"), MFXDemoResourcesLoader.loadURL("DatePickersDemo.fxml")));
        vLoader.addItem("DIALOGS", Builder.build(new MFXRectangleToggleNode("DIALOGS"), MFXDemoResourcesLoader.loadURL("DialogsDemo.fxml")).setControllerFactory(controller -> new DialogsController(demoPane)));
        vLoader.addItem("LABELS", Builder.build(new MFXRectangleToggleNode("LABELS"), MFXDemoResourcesLoader.loadURL("LabelsDemo.fxml")));
        vLoader.addItem("LISTVIEWS", Builder.build(new MFXRectangleToggleNode("LISTVIEWS"), MFXDemoResourcesLoader.loadURL("ListViewsDemo.fxml")));
        vLoader.addItem("NOTIFICATIONS", Builder.build(new MFXRectangleToggleNode("NOTIFICATIONS"), MFXDemoResourcesLoader.loadURL("NotificationsDemo.fxml")));
        vLoader.addItem("PROGRESS_BARS", Builder.build(new MFXRectangleToggleNode("PROGRESS BARS"), MFXDemoResourcesLoader.loadURL("ProgressBarsDemo.fxml")));
        vLoader.addItem("PROGRESS_SPINNERS", Builder.build(new MFXRectangleToggleNode("PROGRESS SPINNERS"), MFXDemoResourcesLoader.loadURL("ProgressSpinnersDemo.fxml")));
        vLoader.addItem("RADIOBUTTONS", Builder.build(new MFXRectangleToggleNode("RADIOBUTTONS"), MFXDemoResourcesLoader.loadURL("RadioButtonsDemo.fxml")));
        vLoader.addItem("SCROLLPANES", Builder.build(new MFXRectangleToggleNode("SCROLLPANES"), MFXDemoResourcesLoader.loadURL("ScrollPanesDemo.fxml")));
        vLoader.addItem("SLIDERS", Builder.build(new MFXRectangleToggleNode("SLIDERS"), MFXDemoResourcesLoader.loadURL("SlidersDemo.fxml")));
        vLoader.addItem("STEPPER", Builder.build(new MFXRectangleToggleNode("STEPPER"), MFXDemoResourcesLoader.loadURL("StepperDemo.fxml")));
        vLoader.addItem("TABLEVIEWS", Builder.build(new MFXRectangleToggleNode("TABLEVIEWS"), MFXDemoResourcesLoader.loadURL("TableViewsDemo.fxml")));
        vLoader.addItem("TEXTFIELDS", Builder.build(new MFXRectangleToggleNode("TEXTFIELDS"), MFXDemoResourcesLoader.loadURL("TextFieldsDemo.fxml")));
        vLoader.addItem("TOGGLES", Builder.build(new MFXRectangleToggleNode("TOGGLES"), MFXDemoResourcesLoader.loadURL("ToggleButtonsDemo.fxml")));
        vLoader.addItem("TREEVIEWS", Builder.build(new MFXRectangleToggleNode("TREEVIEWS"), MFXDemoResourcesLoader.loadURL("TreeViewsDemo.fxml")));
        vLoader.addItem("FONTRESOURCES", Builder.build(new MFXRectangleToggleNode("FONTRESOURCES"), MFXDemoResourcesLoader.loadURL("FontResourcesDemo.fxml")));
        vLoader.start();

        // Others
        ScrollUtils.addSmoothScrolling(scrollPane, 2);
        ScrollUtils.animateScrollBars(scrollPane, 500, 500);
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

        demoPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> demoPane.requestFocus());

        primaryStage.setOnShown(event -> presentation());
    }

    private void presentation() {
        AnimationUtils.SequentialBuilder.build()
                .add(KeyFrames.of(Duration.ONE, event -> m1.play()))
                .add(AnimationUtils.ParallelBuilder.build().show(1000, logo, version).getAnimation())
                .add(AnimationUtils.TimelineBuilder.build().show(450, splashLabel1).setDelay(200).getAnimation())
                .add(AnimationUtils.TimelineBuilder.build().show(450, splashLabel2).setDelay(50).getAnimation())
                .add(AnimationUtils.TimelineBuilder.build().show(450, splashLabel3).setDelay(50).getAnimation())
                .setOnFinished(event -> AnimationUtils.SequentialBuilder.build()
                        .add(KeyFrames.of(300, end -> m2.play()))
                        .add(AnimationUtils.TimelineBuilder.build().hide(300, logoPane).setOnFinished(end -> logoPane.setVisible(false)).getAnimation())
                        .add(AnimationUtils.ParallelBuilder.build().show(800, contentPane, opNavButton).setOnFinished(end -> opNavButton.setDisable(false)).getAnimation())
                        .setDelay(750)
                        .getAnimation().play())
                .setDelay(750)
                .getAnimation().play();

    }

    private void initAnimations() {
        openNav = (ParallelTransition) AnimationUtils.ParallelBuilder.build()
                .show(400, navBar)
                .add(new KeyFrame(Duration.millis(300), new KeyValue(navBar.translateXProperty(), 5)))
                .add(new KeyFrame(Duration.millis(200), new KeyValue(opNavButton.rotateProperty(), -180)))
                .setOnFinished(event -> isNavShown = true)
                .getAnimation();

        closeNav = (ParallelTransition) AnimationUtils.ParallelBuilder.build()
                .hide(50, navBar)
                .add(new KeyFrame(Duration.millis(300), new KeyValue(navBar.translateXProperty(), -240)))
                .add(new KeyFrame(Duration.millis(200), new KeyValue(opNavButton.rotateProperty(), 0)))
                .setOnFinished(event -> isNavShown = false)
                .getAnimation();
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
            FXMLLoader loader = new FXMLLoader(MFXDemoResourcesLoader.loadURL("InfoDialog.fxml"));
            loader.setControllerFactory(controller -> new InfoController(hostServices));
            infoDialog = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Close Button
        StackPane header = (StackPane) infoDialog.lookup("#headerNode");

        MFXIconWrapper closeButton = new MFXIconWrapper("mfx-x", 8, 22).defaultRippleGeneratorBehavior();
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
