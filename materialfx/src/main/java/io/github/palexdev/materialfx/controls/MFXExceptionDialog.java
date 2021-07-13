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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ExceptionUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Specific dialog to show an exception's stack trace in a text area using {@link ExceptionUtils}
 * <p></p>
 * Extends {@link MFXDialog}
 */
public class MFXExceptionDialog extends MFXDialog {
    private final TextArea exceptionArea;

    public MFXExceptionDialog() {
        setPrefSize(500, 350);
        setPadding(new Insets(3));

        StackPane headerNode = new StackPane();
        headerNode.setPrefSize(getPrefWidth(), getPrefHeight() * 0.45);
        headerNode.getStyleClass().add("header-node");
        headerNode.setStyle("-fx-background-color: #EF6E6B;\n" + "-fx-background-insets: -3 -3 0 -3");

        MFXFontIcon exceptionIcon = new MFXFontIcon("mfx-x-circle-light");
        exceptionIcon.setColor(Color.WHITE);
        exceptionIcon.setSize(96);
        MFXFontIcon closeIcon = new MFXFontIcon("mfx-x", Color.WHITE);

        MFXButton closeButton = new MFXButton("");
        closeButton.setPrefSize(20, 20);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.setGraphic(closeIcon);
        closeButton.setRippleAnimationSpeed(1.5);
        closeButton.setRippleColor(Color.rgb(255, 0, 0, 0.1));
        closeButton.setRippleRadius(15);
        closeButton.setButtonType(ButtonType.FLAT);

        NodeUtils.makeRegionCircular(closeButton);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(7, 7, 0, 0));
        headerNode.getChildren().addAll(exceptionIcon, closeButton);

        exceptionArea = new TextArea();
        exceptionArea.setEditable(false);
        exceptionArea.setWrapText(true);
        exceptionArea.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        StackPane scrollContent = new StackPane();
        scrollContent.setPadding(new Insets(-5));
        scrollContent.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        scrollContent.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        scrollContent.setPrefSize(590, 240);
        scrollContent.getChildren().setAll(exceptionArea);
        MFXScrollPane scrollPane = new MFXScrollPane(scrollContent);
        scrollPane.setPadding(new Insets(10, 10, 0, 10));
        scrollPane.setFitToWidth(true);

        setTop(headerNode);
        setCenter(scrollPane);
        setCloseButtons(closeButton);
    }

    /**
     * Sets the textarea text to the specified exception's stack trace.
     */
    public void setException(Throwable th) {
        exceptionArea.setText(ExceptionUtils.getStackTraceString(th));
    }
}
