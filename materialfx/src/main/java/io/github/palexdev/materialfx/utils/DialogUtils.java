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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.MFXExceptionDialog;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.enums.DialogType;
import io.github.palexdev.materialfx.factories.MFXDialogFactory;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Utils class to quickly get modal {@code MFXStageDialogs} and other specialized dialogs.
 *
 * @see MFXStageDialog
 */
public class DialogUtils {

    private DialogUtils() {
    }

    /**
     * Calls {@link MFXDialogFactory#buildDialog(DialogType, String, String)} with the specified arguments.
     *
     * @param type    the dialog type
     * @param title   the dialog title
     * @param message the dialog content
     * @return the newly created MFXDialog
     */
    public static MFXDialog getDialog(DialogType type, String title, String message) {
        return MFXDialogFactory.buildDialog(type, title, message);
    }

    /**
     * Creates a new {@link MFXStageDialog} with the given owner and arguments.
     *
     * @param window  the owner
     * @param type    the dialog type
     * @param title   the dialog title
     * @param message the dialog content
     * @return the newly created MFXStageDialog
     */
    public static MFXStageDialog getStageDialog(Window window, DialogType type, String title, String message) {
        MFXStageDialog stageDialog = new MFXStageDialog(type, title, message);
        stageDialog.setOwner(window);
        stageDialog.setModality(Modality.APPLICATION_MODAL);
        return stageDialog;
    }

    /**
     * Creates a new {@link MFXStageDialog} with the given owner and {@link MFXDialog}.
     *
     * @param window the owner
     * @param dialog the MFXDialog
     * @return the newly created MFXStageDialog
     */
    public static MFXStageDialog getStageDialog(Window window, MFXDialog dialog) {
        MFXStageDialog stageDialog = new MFXStageDialog(dialog);
        stageDialog.setOwner(window);
        stageDialog.setModality(Modality.APPLICATION_MODAL);
        return stageDialog;
    }

    /**
     * Creates a new {@link MFXStageDialog} which shows a {@link MFXExceptionDialog}
     * with the given owner, title and exception.
     *
     * @param window the owner
     * @param title  the dialog title
     * @param th     the exception
     * @return the newly created MFXStageDialog
     */
    public static MFXStageDialog getExceptionDialog(Window window, String title, Throwable th) {
        MFXExceptionDialog dialog = new MFXExceptionDialog();
        dialog.setTitle(title);
        dialog.setException(th);
        MFXStageDialog stageDialog = new MFXStageDialog(dialog);
        stageDialog.setOwner(window);
        stageDialog.setModality(Modality.APPLICATION_MODAL);
        return stageDialog;
    }
}
