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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.SimpleMFXNotificationPane;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.notifications.NotificationPos;
import io.github.palexdev.materialfx.notifications.NotificationsManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogsController implements Initializable {
    private final Pane pane;

    @FXML
    private MFXButton pError;

    @FXML
    private MFXButton pWarning;

    @FXML
    private MFXButton pInfo;

    @FXML
    private MFXButton pGeneric;

    @FXML
    private MFXButton pGenericActions;

    @FXML
    private MFXButton pFade;

    @FXML
    private MFXButton pSlideLR;

    @FXML
    private MFXButton pSlideTB;

    @FXML
    private MFXButton pMix;

    @FXML
    private MFXButton sError;

    @FXML
    private MFXButton sWarning;

    @FXML
    private MFXButton sInfo;

    @FXML
    private MFXButton sGeneric;

    @FXML
    private MFXButton pDraggable;

    @FXML
    private MFXButton pOverlayClose;

    @FXML
    private MFXButton sModal;

    private final AbstractMFXDialog dialog;
    private final AbstractMFXDialog animateDialog;
    private final String text =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                    "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                    "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
                    "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
                    "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

    public DialogsController(Pane pane) {
        this.pane = pane;

        dialog = MFXDialogFactory.buildDialog(DialogType.INFO, "MFXDialog - Generic Dialog", text);

        animateDialog = MFXDialogFactory.buildDialog(DialogType.INFO, "", text);
        animateDialog.setAnimateIn(true);
        animateDialog.setAnimateOut(true);

        animateDialog.setOnBeforeOpen(event -> System.out.println("BEFORE OPEN"));
        animateDialog.setOnOpened(event -> System.out.println("OPENED"));
        animateDialog.setOnBeforeClose(event -> System.out.println("BEFORE CLOSING"));
        animateDialog.setOnClosed(event -> System.out.println("CLOSED"));

        Platform.runLater(() -> this.pane.getChildren().addAll(dialog, animateDialog));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog.setVisible(false);
        animateDialog.setVisible(false);

        pError.setOnAction(event -> {
            resetDialog();
            MFXDialogFactory.convertToSpecific(DialogType.ERROR, dialog);
            dialog.setTitle("MFXDialog - Error Dialog");
            dialog.show();
        });

        pWarning.setOnAction(event -> {
            resetDialog();
            MFXDialogFactory.convertToSpecific(DialogType.WARNING, dialog);
            dialog.setTitle("MFXDialog - Warning Dialog");
            dialog.show();
        });

        pInfo.setOnAction(event -> {
            resetDialog();
            MFXDialogFactory.convertToSpecific(DialogType.INFO, dialog);
            dialog.setTitle("MFXDialog - Info Dialog");
            dialog.show();
        });

        pGeneric.setOnAction(event -> {
            AbstractMFXDialog genericDialog = MFXDialogFactory.buildGenericDialog("MFXDialog - Generic Dialog", text);
            genericDialog.setCloseHandler(c -> {
                genericDialog.close();
                DialogsController.this.pane.getChildren().remove(genericDialog);
            });
            genericDialog.setVisible(false);
            this.pane.getChildren().add(genericDialog);
            genericDialog.show();
        });

        pGenericActions.setOnAction(event -> {
            AbstractMFXDialog genericDialog = MFXDialogFactory.buildGenericDialog("MFXDialog - Generic Dialog", text);
            genericDialog.setCloseHandler(c -> {
                genericDialog.close();
                DialogsController.this.pane.getChildren().remove(genericDialog);
            });
            genericDialog.setVisible(false);
            this.pane.getChildren().add(genericDialog);
            genericDialog.setActions(createActionsBar(genericDialog));
            genericDialog.show();
        });

        pFade.setOnAction(event -> {
            resetDialog();
            animateDialog.setTitle("MFXDialog - Fade Dialog");
            animateDialog.setInAnimationType(MFXAnimationFactory.FADE_IN);
            animateDialog.setOutAnimationType(MFXAnimationFactory.FADE_OUT);
            animateDialog.show();
        });

        pSlideLR.setOnAction(event -> {
            resetDialog();
            animateDialog.setTitle("MFXDialog - Slide Left/Right Dialog");
            animateDialog.setInAnimationType(MFXAnimationFactory.SLIDE_IN_LEFT);
            animateDialog.setOutAnimationType(MFXAnimationFactory.SLIDE_OUT_RIGHT);
            animateDialog.show();
        });

        pSlideTB.setOnAction(event -> {
            resetDialog();
            animateDialog.setTitle("MFXDialog - Slide Top/Bottom Dialog");
            animateDialog.setInAnimationType(MFXAnimationFactory.SLIDE_IN_TOP);
            animateDialog.setOutAnimationType(MFXAnimationFactory.SLIDE_OUT_BOTTOM);
            animateDialog.show();
        });

        pMix.setOnAction(event -> {
            resetDialog();
            animateDialog.setTitle("MFXDialog - Mix Animation Dialog");
            animateDialog.setInAnimationType(MFXAnimationFactory.SLIDE_IN_TOP);
            animateDialog.setOutAnimationType(MFXAnimationFactory.SLIDE_OUT_RIGHT);
            animateDialog.show();
        });

        pDraggable.setOnAction(event -> {
            resetDialog();
            MFXDialogFactory.convertToSpecific(DialogType.INFO, dialog);
            dialog.setTitle("MFXDialog - Draggable Dialog");
            dialog.setIsDraggable(true);
            dialog.show();
        });

        pOverlayClose.setOnAction(event -> {
            resetDialog();
            MFXDialogFactory.convertToSpecific(DialogType.INFO, dialog);
            dialog.setTitle("MFXDialog - Overlay Close Dialog");
            dialog.setOverlayClose(true);
            dialog.show();
        });

        sError.setOnAction(event -> {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.ERROR, "MFXStageDialog - Error Dialog", text);
            dialog.show();
        });

        sWarning.setOnAction(event -> {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.WARNING, "MFXStageDialog - Warning Dialog", text);
            dialog.show();
        });

        sInfo.setOnAction(event -> {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.INFO, "MFXStageDialog - Info Dialog", text);
            dialog.show();
        });

        sGeneric.setOnAction(event -> {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.GENERIC, "MFXStageDialog - Generic Dialog", text);
            dialog.show();
        });

        sModal.setOnAction(event -> {
            MFXStageDialog dialog = new MFXStageDialog(DialogType.INFO, "MFXStageDialog - Modal Dialog", text);
            dialog.setOwner(pane.getScene().getWindow());
            dialog.setModality(Modality.APPLICATION_MODAL);
            dialog.setScrimBackground(true);
            dialog.setCenterInOwner(true);
            dialog.show();
        });
    }

    private void resetDialog() {
        dialog.setOverlayClose(false);
        dialog.setIsDraggable(false);
    }

    private HBox createActionsBar(AbstractMFXDialog dialog) {
        MFXButton action1 = new MFXButton("Perform Action 1");
        MFXButton action2 = new MFXButton("Perform Action 2");
        MFXButton action3 = new MFXButton("Perform Action 3");
        MFXButton close = new MFXButton("Close");

        action1.setButtonType(ButtonType.RAISED);
        action2.setButtonType(ButtonType.RAISED);
        action3.setButtonType(ButtonType.RAISED);
        close.setButtonType(ButtonType.RAISED);

        action1.setDepthLevel(DepthLevel.LEVEL1);
        action2.setDepthLevel(DepthLevel.LEVEL1);
        action3.setDepthLevel(DepthLevel.LEVEL1);
        close.setDepthLevel(DepthLevel.LEVEL1);

        action1.setOnAction(event -> NotificationsManager.send(NotificationPos.BOTTOM_RIGHT, createNotification("Action 1 Performed")));
        action2.setOnAction(event -> NotificationsManager.send(NotificationPos.BOTTOM_RIGHT, createNotification("Action 2 Performed")));
        action3.setOnAction(event -> NotificationsManager.send(NotificationPos.BOTTOM_RIGHT, createNotification("Action 3 Performed")));
        dialog.addCloseButton(close);

        HBox box = new HBox(20, action1, action2, action3, close);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 5, 20, 5));
        return box;
    }

    private MFXNotification createNotification(String text) {
        Region notificationPane = new SimpleMFXNotificationPane(
                "Dialogs Actions Test",
                "",
                text
        );
        MFXNotification notification = new MFXNotification(notificationPane, true, true);
        notification.setHideAfterDuration(Duration.seconds(3));
        return notification;
    }
}
