package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

public class DialogsController {
	private MFXGenericDialog dialogContent;
	private MFXStageDialog dialog;

	public DialogsController(Stage stage) {

		Platform.runLater(() -> {
			this.dialogContent = MFXGenericDialogBuilder.build()
					.setContentText(Model.ipsum)
					.makeScrollable(true)
					.get();
			this.dialog = MFXGenericDialogBuilder.build(dialogContent)
					.toStageDialogBuilder()
					.initOwner(stage)
					.initModality(Modality.APPLICATION_MODAL)
					.setDraggable(true)
					.setTitle("Dialogs Preview")
					.setScrimOwner(true)
					.get();

			dialogContent.addActions(
					Map.entry(new MFXButton("Confirm"), event -> {}),
					Map.entry(new MFXButton("Cancel"), event -> dialog.close())
			);

			dialogContent.setMaxSize(400, 200);
		});
	}

	@FXML
	private void openInfo(ActionEvent event) {
		MFXFontIcon infoIcon = new MFXFontIcon("mfx-info-circle-filled", 18);
		dialogContent.setHeaderIcon(infoIcon);
		dialogContent.setHeaderText("This is a generic info dialog");
		convertDialogTo("mfx-info-dialog");
		dialog.showDialog();
	}

	@FXML
	private void openWarning(ActionEvent event) {
		MFXFontIcon warnIcon = new MFXFontIcon("mfx-do-not-enter-circle", 18);
		dialogContent.setHeaderIcon(warnIcon);
		dialogContent.setHeaderText("This is a warning info dialog");
		convertDialogTo("mfx-warn-dialog");
		dialog.showDialog();
	}

	@FXML
	private void openError(ActionEvent event) {
		MFXFontIcon errorIcon = new MFXFontIcon("mfx-exclamation-circle-filled", 18);
		dialogContent.setHeaderIcon(errorIcon);
		dialogContent.setHeaderText("This is a error info dialog");
		convertDialogTo("mfx-error-dialog");
		dialog.showDialog();
	}

	@FXML
	private void openGeneric(ActionEvent event) {
		dialogContent.setHeaderIcon(null);
		dialogContent.setHeaderText("This is a generic dialog");
		convertDialogTo(null);
		dialog.showDialog();
	}

	private void convertDialogTo(String styleClass) {
		dialogContent.getStyleClass().removeIf(
				s -> s.equals("mfx-info-dialog") || s.equals("mfx-warn-dialog") || s.equals("mfx-error-dialog")
		);

		if (styleClass != null)
		dialogContent.getStyleClass().add(styleClass);
	}
}
