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

package io.github.palexdev.materialfx.controls.factories;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Factory class to build specific {@code MFXDialogs} and generic {@code MFXDialogs}.
 * <p></p>
 * Also provides convenience methods to convert a specific dialog to another one
 * and from generic to specific (the reverse is not true).
 * These methods are important because rather than building new dialogs it's
 * way better performance-wise to create a single dialog and add it to the desired pane,
 * then convert it to another specific one or generic and reset its title and content if needed.
 * <p></p>
 * This is an advise you don't have to follow, however, keep in mind that creating a new dialog
 * every time you need one and add it to the pane could cause a little bit of lag.
 * <p></p>
 * For a proper use see the demo code in {@code DialogsController}
 */
public class MFXDialogFactory {

    private MFXDialogFactory() {
    }

    /**
     * Sets the header node of the given dialog to the given type.
     *
     * @param type   The desired type
     * @param dialog The dialog reference
     */
    public static void setHeaderNode(DialogType type, AbstractMFXDialog dialog) {
        String color;
        MFXFontIcon icon;

        switch (type) {
            case ERROR:
                icon = new MFXFontIcon("mfx-x-circle-light");
                color = "#ff9e9e";
                break;
            case WARNING:
                icon = new MFXFontIcon("mfx-exclamation-triangle");
                color = "#ffa57f";
                break;
            case INFO:
                icon = new MFXFontIcon("mfx-info-circle");
                color = "#61caff";
                break;
            default:
                return;
        }

        icon.setColor(Color.WHITE);
        icon.setSize(96);
        dialog.setTop(buildHeader(dialog, color, icon));
        dialog.setType(type);
    }

    /**
     * Sets the content node of the given dialog with a new one.
     *
     * @param dialog  The dialog reference
     * @param title   The dialog's title
     * @param content The dialog's content
     */
    public static void setContentNode(AbstractMFXDialog dialog, String title, String content) {
        dialog.setCenter(buildContent(dialog, title, content));
    }

    /**
     * Builds an MFXDialog
     *
     * @param type    The dialog's type
     * @param title   The dialog's title
     * @param content The dialog's content
     * @return A new MFXDialog
     */
    public static MFXDialog buildDialog(DialogType type, String title, String content) {
        MFXDialog dialog = new MFXDialog();
        setHeaderNode(type, dialog);
        setContentNode(dialog, title, content);
        dialog.setType(type);
        return dialog;
    }

    /**
     * Builds a generic MFXDialog
     *
     * @param title   The dialog's title
     * @param content The dialog's content
     * @return A new generic MFXDialog
     */
    public static MFXDialog buildGenericDialog(String title, String content) {
        MFXDialog dialog = new MFXDialog();
        dialog.setPrefSize(480, 120);

        StackPane headerNode = buildGenericHeader(dialog, title);
        StackPane contentNode = buildGenericContent(dialog, content);
        HBox buttonsBox = buildButtonsBox(dialog);

        dialog.setTop(headerNode);
        dialog.setCenter(contentNode);
        dialog.setBottom(buttonsBox);
        dialog.setType(DialogType.GENERIC);

        return dialog;
    }

    /**
     * Converts a given dialog to the desired type.
     *
     * @param type   The desired type
     * @param dialog The dialog reference
     */
    public static void convertToSpecific(DialogType type, AbstractMFXDialog dialog) {
        if (dialog.getType().equals(DialogType.GENERIC)) {
            dialog.setPrefSize(400, 300);
        }
        setContentNode(dialog, dialog.getTitle(), dialog.getContent());
        setHeaderNode(type, dialog);
        dialog.setBottom(null);
        dialog.setType(type);
    }

    /**
     * Common code for building specific dialog's header node.
     *
     * @param dialog The dialog reference
     * @param color  The header color
     * @param icon   The header icon
     * @return A new header node
     */
    private static StackPane buildHeader(AbstractMFXDialog dialog, String color, MFXFontIcon icon) {
        StackPane headerNode = new StackPane();
        headerNode.setPrefSize(dialog.getPrefWidth(), dialog.getPrefHeight() * 0.45);
        headerNode.getStyleClass().add("header-node");
        headerNode.setStyle("-fx-background-color: " + color + ";\n");

        MFXFontIcon closeIcon = new MFXFontIcon("mfx-x-alt", 16, Color.WHITE);

        if (dialog.getType() != null && dialog.getType().equals(DialogType.GENERIC)) {
            dialog.setCloseButtons(new MFXButton(""));
        }

        MFXButton closeButton = new MFXButton("");
        closeButton.setPrefSize(20, 20);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.setGraphic(closeIcon);
        closeButton.setRippleAnimationSpeed(1.5);
        closeButton.setRippleColor(Color.rgb(255, 0, 0, 0.1));
        closeButton.setRippleRadius(15);
        closeButton.setButtonType(ButtonType.FLAT);
        dialog.setCloseButtons(closeButton);

        NodeUtils.makeRegionCircular(closeButton);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(7, 7, 0, 0));
        headerNode.getChildren().addAll(icon, closeButton);

        return headerNode;
    }

    /**
     * Common code for building specific dialog's content node.
     *
     * @param dialog  The dialog reference
     * @param title   The dialog's title
     * @param content The dialog's content
     * @return A new header node
     */
    private static StackPane buildContent(AbstractMFXDialog dialog, String title, String content) {
        StackPane contentNode = new StackPane();

        Label titleLabel = new Label();
        titleLabel.getStyleClass().setAll("title-label");
        titleLabel.textProperty().bind(dialog.titleProperty());
        dialog.setTitle(title);
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        StackPane.setMargin(titleLabel, new Insets(15, 0, 0, 0));

        Label contentLabel = new Label();
        contentLabel.getStyleClass().setAll("content-label");
        contentLabel.setMinHeight(Region.USE_PREF_SIZE);
        contentLabel.setPrefWidth(dialog.getPrefWidth() * 0.9);
        contentLabel.setWrapText(true);
        contentLabel.textProperty().bind(dialog.contentProperty());
        dialog.setContent(content);
        StackPane.setAlignment(contentLabel, Pos.TOP_CENTER);
        StackPane.setMargin(contentLabel, new Insets(40, 20, 20, 20));

        contentNode.getChildren().addAll(titleLabel, contentLabel);
        return contentNode;
    }

    /**
     * Builds an header node for generic dialogs.
     *
     * @param dialog The dialog reference
     * @param title  The dialog's title.
     * @return A new generic header node
     */
    private static StackPane buildGenericHeader(AbstractMFXDialog dialog, String title) {
        StackPane headerNode = new StackPane();
        headerNode.getStyleClass().add("header-node");
        Label titleLabel = new Label();
        titleLabel.getStyleClass().setAll("title-label");
        titleLabel.textProperty().bind(dialog.titleProperty());
        dialog.setTitle(title);
        headerNode.getChildren().add(titleLabel);
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        StackPane.setMargin(titleLabel, new Insets(15, 0, 0, 0));

        return headerNode;
    }

    /**
     * Builds a content node for generic dialogs.
     *
     * @param dialog  The dialog reference
     * @param content The dialog's content
     * @return A new generic content node
     */
    private static StackPane buildGenericContent(AbstractMFXDialog dialog, String content) {
        StackPane contentNode = new StackPane();
        contentNode.getStyleClass().add("content-node");
        Label contentLabel = new Label();
        contentLabel.getStyleClass().setAll("content-label");
        contentLabel.setMinHeight(Region.USE_PREF_SIZE);
        contentLabel.setPrefWidth(dialog.getPrefWidth() * 0.9);
        contentLabel.setWrapText(true);
        contentLabel.textProperty().bind(dialog.contentProperty());
        dialog.setContent(content);
        contentNode.getChildren().add(contentLabel);
        StackPane.setAlignment(contentLabel, Pos.TOP_CENTER);
        StackPane.setMargin(contentLabel, new Insets(7, 0, 0, 0));

        return contentNode;
    }

    /**
     * Builds a button box for generic dialogs.
     *
     * @param dialog The dialog instance
     * @return A new button box
     */
    private static HBox buildButtonsBox(AbstractMFXDialog dialog) {
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(20);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(0, 15, 10, 0));
        buttonsBox.getStyleClass().add("buttons-box");

        MFXButton closeButton = new MFXButton("");
        closeButton.setText("OK");
        closeButton.setPrefSize(55, 20);
        closeButton.setTextFill(Color.rgb(120, 66, 245));
        closeButton.setRippleAnimationSpeed(1.5);
        closeButton.setRippleColor(Color.rgb(120, 66, 245, 0.3));
        closeButton.setRippleRadius(30);
        dialog.setCloseButtons(closeButton);

        HBox.setMargin(closeButton, new Insets(5, 10, 0, 0));
        buttonsBox.getChildren().add(closeButton);

        return buttonsBox;
    }
}
