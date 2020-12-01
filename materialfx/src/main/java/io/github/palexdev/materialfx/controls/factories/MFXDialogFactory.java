package io.github.palexdev.materialfx.controls.factories;

import io.github.palexdev.materialfx.MFXResourcesManager.SVGResources;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * Factory class to build specific {@code MFXDialog}s and generic {@code MFXDialog}s.
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
     * @param type The desired type
     * @param dialog The dialog reference
     */
    public static void setHeaderNode(DialogType type, AbstractMFXDialog dialog) {
        String color;
        SVGPath icon;

        switch (type) {
            case ERROR:
                icon = SVGResources.CROSS.getSvgPath();
                color = "#ff9e9e";
                break;
            case WARNING:
                icon = SVGResources.EXCLAMATION_TRIANGLE.getSvgPath();
                color = "#ffa57f";
                break;
            case INFO:
                icon = SVGResources.INFO.getSvgPath();
                color = "#61caff";
                break;
            default:
                return;
        }

        icon.setFill(Color.WHITE);
        dialog.setTop(buildHeader(dialog, color, icon));
        dialog.setType(type);
    }

    /**
     * Sets the content node of the given dialog with a new one.
     * @param dialog The dialog reference
     * @param title The dialog's title
     * @param content The dialog's content
     */
    public static void setContentNode(AbstractMFXDialog dialog, String title, String content)  {
        dialog.setCenter(buildContent(dialog, title, content));
    }

    /**
     * Builds an MFXDialog
     * @param type The dialog's type
     * @param title The dialog's title
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
     * @param title The dialog's title
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
     * @param type The desired type
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
     * @param dialog The dialog reference
     * @param color The header color
     * @param icon The header icon
     * @return A new header node
     */
    private static StackPane buildHeader(AbstractMFXDialog dialog, String color, SVGPath icon) {
        StackPane headerNode = new StackPane();
        headerNode.setPrefSize(dialog.getPrefWidth(), dialog.getPrefHeight() * 0.45);
        headerNode.getStyleClass().add("header-node");
        headerNode.setStyle("-fx-background-color: " + color + ";\n");

        SVGPath closeSvg = SVGResources.X.getSvgPath();
        closeSvg.setScaleX(0.17);
        closeSvg.setScaleY(0.17);
        closeSvg.setFill(Color.WHITE);

        if (dialog.getType() != null && dialog.getType().equals(DialogType.GENERIC)) {
            dialog.setCloseButton(new MFXButton(""));
        }

        MFXButton closeButton = dialog.getCloseButton();
        closeButton.setPrefSize(20, 20);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        closeButton.setGraphic(closeSvg);
        closeButton.setRippleRadius(15);
        closeButton.setRippleColor(Color.rgb(255, 0, 0, 0.1));
        closeButton.setRippleInDuration(Duration.millis(500));
        closeButton.setButtonType(ButtonType.FLAT);

        NodeUtils.makeRegionCircular(closeButton);

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(7, 7, 0, 0));
        headerNode.getChildren().addAll(icon, closeButton);

        return headerNode;
    }

    /**
     * Common code for building specific dialog's content node.
     * @param dialog The dialog reference
     * @param title The dialog's title
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
     * @param dialog The dialog reference
     * @param title The dialog's title.
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
     * @param dialog The dialog reference
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
     * @param dialog The dialog instance
     * @return A new button box
     */
    private static HBox buildButtonsBox(AbstractMFXDialog dialog) {
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(20);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(0, 15, 10, 0));
        buttonsBox.getStyleClass().add("buttons-box");

        MFXButton closeButton = dialog.getCloseButton();
        closeButton.setText("OK");
        closeButton.setPrefSize(55, 20);
        closeButton.setTextFill(Color.rgb(120, 66, 245));
        closeButton.setRippleRadius(30);
        closeButton.setRippleInDuration(Duration.millis(500));
        closeButton.setRippleColor(Color.rgb(120, 66, 245, 0.3));

        HBox.setMargin(closeButton, new Insets(5, 10, 0, 0));
        buttonsBox.getChildren().add(closeButton);

        return buttonsBox;
    }
}
