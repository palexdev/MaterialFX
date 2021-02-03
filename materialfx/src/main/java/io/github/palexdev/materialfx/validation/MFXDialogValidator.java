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
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a concrete implementation of a validator.
 * <p>
 * This validator has a string message associated with every boolean property in its base class.
 * It can show a {@link MFXStageDialog} containing all warning messages.
 */
public class MFXDialogValidator extends AbstractMFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    private final Map<BooleanProperty, String> messagesMap = new HashMap<>();
    private final ObjectProperty<DialogType> dialogType = new SimpleObjectProperty<>(DialogType.WARNING);
    private String title;
    private MFXStageDialog stageDialog;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDialogValidator(String title) {
        this.title = title;
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
    }

    /**
     * Shows this validator's dialog. The dialog is not modal and doesn't scrim the background.
     */
    public void show() {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), title, "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getMessages());
        stageDialog.setOwner(null);
        stageDialog.setModality(Modality.NONE);
        stageDialog.setCenterInOwner(false);
        stageDialog.setScrimBackground(false);
        stageDialog.show();
    }

    /**
     * Shows this validator's dialog. The dialog is modal and scrims the background.
     * @param owner The dialog's owner.
     */
    public void showModal(Window owner) {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), title, "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getMessages());
        stageDialog.setOwner(owner);
        stageDialog.setModality(Modality.WINDOW_MODAL);
        stageDialog.setCenterInOwner(true);
        stageDialog.setScrimBackground(true);
        stageDialog.show();

    }

    /**
     * Adds a new boolean condition to the list with the corresponding message in case it is false.
     * @param property The new boolean condition
     * @param message The message to show in case it is false
     */
    public void add(BooleanProperty property, String message) {
        super.conditions.add(property);
        this.messagesMap.put(property, message);
    }

    /**
     * Removes the given property and the corresponding message from the list.
     */
    public void remove(BooleanProperty property) {
        messagesMap.remove(property);
        super.conditions.remove(property);
    }

    /**
     * Checks the messages list and if the corresponding boolean condition is false
     * adds the message to the {@code StringBuilder}.
     */
    public String getMessages() {
        StringBuilder sb = new StringBuilder();
        for (BooleanProperty property : messagesMap.keySet()) {
            if (!property.get()) {
                sb.append(messagesMap.get(property)).append(",\n");
            }
        }
        return StringUtils.replaceLast(sb.toString(), ",", ".");
    }

    public DialogType getDialogType() {
        return dialogType.get();
    }

    public ObjectProperty<DialogType> dialogTypeProperty() {
        return dialogType;
    }

    public void setDialogType(DialogType dialogType) {
        this.dialogType.set(dialogType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
