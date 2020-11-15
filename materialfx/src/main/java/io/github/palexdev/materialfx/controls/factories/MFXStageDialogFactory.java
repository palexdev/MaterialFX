package io.github.palexdev.materialfx.controls.factories;

import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Factory class to build {@code MFXStageDialog}s
 */
public class MFXStageDialogFactory {

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Builds a MFXStageDialog from type, title and content
     * @param type The dialog type
     * @param title The dialog's title
     * @param content The dialog's content
     * @return The MFXStageDialog's stage
     * @see DialogType
     */
    public static Stage buildDialog(DialogType type, String title, String content) {
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        AbstractMFXDialog dialog;
        if (!type.equals(DialogType.GENERIC)) {
            dialog = MFXDialogFactory.buildDialog(type, title, content);
        } else {
            dialog = MFXDialogFactory.buildGenericDialog(title, content);
        }
        dialog.setVisible(true);

        Scene scene = buildScene(dialog);
        dialogStage.setTitle(title);
        dialogStage.setScene(scene);

        return dialogStage;
    }

    /**
     * Builds a MFXStageDialog from an AbstractMFXDialog or subclasses
     * @param dialog The dialog
     * @return The MFXStageDialog's stage
     */
    public static Stage buildDialog(AbstractMFXDialog dialog) {
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        dialog.setCloseHandler(event -> dialogStage.close());
        dialog.setVisible(true);

        Scene scene = buildScene(dialog);
        dialogStage.setTitle(dialog.getTitle());
        dialogStage.setScene(scene);

        return dialogStage;
    }

    /**
     * Creates a TRANSPARENT {@code Scene} however it doesn't seem to work
     * so the dialog is clipped with a {@code Rectangle} to keep round corners
     * @param pane The dialog
     * @return The MFXStageDialog scene
     */
    private static Scene buildScene(Pane pane) {
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }
    
}
