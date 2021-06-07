/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.validation;

import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * This is an extension of the {@link MFXPriorityValidator}, basically adds the capability
 * to shown a dialog showing all the unmet conditions of the validator using the {@link #getUnmetMessages()} method.
 * <p>
 * The dialog used is a {@link MFXStageDialog} so it can also be modal by calling
 * {@link #showModal(Window)} and specifying the owner.
 */
public class MFXDialogValidator extends MFXPriorityValidator {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<DialogType> dialogType = new SimpleObjectProperty<>(DialogType.WARNING);
    private final StringProperty title = new SimpleStringProperty();
    private MFXStageDialog stageDialog;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDialogValidator(String title) {
        setTitle(title);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        dialogType.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && stageDialog != null) {
                MFXDialogFactory.convertToSpecific(newValue, stageDialog.getDialog());
                Label label = (Label) stageDialog.getDialog().lookup(".content-label");
                label.setAlignment(Pos.CENTER);
            }
        });

        title.addListener((observable, oldValue, newValue) -> {
            if (stageDialog != null) {
                stageDialog.getDialog().setTitle(newValue);
            }
        });
    }

    /**
     * Shows this validator's dialog. The dialog is not modal and doesn't scrim the background.
     */
    public void show() {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), getTitle(), "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getUnmetMessages());
        stageDialog.setOwner(null);
        stageDialog.setModality(Modality.NONE);
        stageDialog.setCenterInOwner(false);
        stageDialog.setScrimBackground(false);
        stageDialog.show();
    }

    /**
     * Shows this validator's dialog. The dialog is modal and scrims the background.
     *
     * @param owner The dialog's owner.
     */
    public void showModal(Window owner) {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), getTitle(), "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getUnmetMessages());
        stageDialog.setOwner(owner);
        stageDialog.setModality(Modality.WINDOW_MODAL);
        stageDialog.setCenterInOwner(true);
        stageDialog.setScrimBackground(true);
        stageDialog.show();

    }

    public DialogType getDialogType() {
        return dialogType.get();
    }

    /**
     * Specifies the dialog's type.
     */
    public ObjectProperty<DialogType> dialogTypeProperty() {
        return dialogType;
    }

    public void setDialogType(DialogType dialogType) {
        this.dialogType.set(dialogType);
    }

    public String getTitle() {
        return title.get();
    }

    /**
     * Specifies the dialog's title.
     */
    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}
