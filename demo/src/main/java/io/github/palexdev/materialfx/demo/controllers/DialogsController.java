package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogsController implements Initializable {
    private final Pane pane;
    private final AbstractMFXDialog dialog;
    private final AbstractMFXDialog animateDialog;
    private final String text =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                    "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                    "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
                    "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
                    "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
    @FXML
    private MFXButton pError;
    @FXML
    private MFXButton pWarning;
    @FXML
    private MFXButton pInfo;
    @FXML
    private MFXButton pGeneric;
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

    public DialogsController(Pane pane) {
        this.pane = pane;
        this.dialog = MFXDialogFactory.buildDialog(DialogType.INFO, "MFXDialog - Generic Dialog", text);
        this.animateDialog = MFXDialogFactory.buildDialog(DialogType.INFO, "", text);
        this.animateDialog.setAnimateIn(true);
        this.animateDialog.setAnimateOut(true);
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
}
