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

package io.github.palexdev.materialfx.factories;

import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.enums.DialogType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Factory class to build {@code MFXStageDialogs}.
 */
public class MFXStageDialogFactory {

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Builds a MFXStageDialog from type, title and content.
     *
     * @param type    The dialog type
     * @param title   The dialog's title
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
     * Builds a MFXStageDialog from an AbstractMFXDialog or subclasses.
     *
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
     * Creates a TRANSPARENT {@code Scene}.
     *
     * @param pane The dialog
     * @return The MFXStageDialog scene
     */
    private static Scene buildScene(Pane pane) {
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

}
