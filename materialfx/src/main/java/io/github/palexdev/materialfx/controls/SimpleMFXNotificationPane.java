package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesManager.SVGResources;
import io.github.palexdev.materialfx.controls.base.AbstractMFXNotificationPane;
import io.github.palexdev.materialfx.utils.LoggingUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * This class extends {@code AbstractMFXNotificationPane} and it serves as an
 * example of a basic pane for a {@code MFXNotification}.
 */
public class SimpleMFXNotificationPane extends AbstractMFXNotificationPane {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane headerNode;
    private final Label headerLabel;
    private final Label titleLabel;
    private final MFXScrollPane contentScroll;
    private final Label contentLabel;

    private final MFXButton closeButton;
    private final MFXButton okButton;
    private final HBox buttonsBox;

    private EventHandler<MouseEvent> closeHandler;

    //================================================================================
    // Constructors
    //================================================================================
    public SimpleMFXNotificationPane(String header, String title, String content) {
        this(null, header, title, content);
    }

    public SimpleMFXNotificationPane(Node icon, String header, String title, String content) {
        // Header
        headerNode = new StackPane();
        headerLabel = new Label();
        headerLabel.textProperty().bind(headerProperty);
        headerLabel.getStyleClass().add("header-label");
        headerLabel.setGraphic(icon);
        headerLabel.setGraphicTextGap(7);
        headerLabel.setPadding(new Insets(15, 15, 0, 15));
        headerProperty.set(header);

        closeButton = new MFXButton("");
        closeButton.setPrefSize(18, 18);
        closeButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        SVGPath x = SVGResources.X.getSvgPath();
        x.setScaleX(0.14);
        x.setScaleY(0.14);
        closeButton.setGraphic(x);
        closeButton.setRippleRadius(12);
        closeButton.setRippleColor(Color.rgb(255, 0, 0, 0.1));

        NodeUtils.makeRegionCircular(closeButton);

        headerNode.getChildren().addAll(headerLabel, closeButton);
        StackPane.setMargin(headerLabel, new Insets(0, 0, 10, 0));
        StackPane.setAlignment(headerLabel, Pos.CENTER_LEFT);
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(6, 6, 8, 8));

        // Title
        titleLabel = new Label();
        titleLabel.textProperty().bind(titleProperty);
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setPadding(new Insets(0, 0, 0, 15));
        titleProperty.set(title);

        // Content
        contentScroll = new MFXScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setPrefSize(200, 200);

        contentLabel = new Label();
        contentLabel.textProperty().bind(contentProperty);
        contentLabel.getStyleClass().add("content-label");
        contentLabel.setWrapText(true);
        contentProperty.set(content);

        contentScroll.setContent(contentLabel);
        contentScroll.setPadding(new Insets(5, 10, 5, 10));
        VBox.setMargin(contentScroll, new Insets(5, 10, 5, 10));

        // Buttons
        buttonsBox = new HBox();
        buttonsBox.getStyleClass().add("buttons-box");
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setSpacing(20);
        okButton = new MFXButton("OK");
        okButton.setPrefWidth(50);
        buttonsBox.getChildren().add(okButton);
        VBox.setMargin(buttonsBox, new Insets(2, 10, 2, 0));

        getChildren().addAll(headerNode, titleLabel, contentScroll, buttonsBox);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setPrefSize(360, 160);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    /**
     * Adds the specified button the the HBox at the bottom of the VBox.
     */
    public void addButton(Button button) {
        this.buttonsBox.getChildren().add(button);
    }

    /**
     * Adds the specified button to the HBox at the bottom of the VBox at the specified index.
     */
    public void addButton(int index, Button button) {
        try {
            this.buttonsBox.getChildren().add(index, button);
        } catch (IndexOutOfBoundsException ex) {
            LoggingUtils.logException(
                    "Could not add button at index:" + index +
                            ", list size is:" + this.buttonsBox.getChildren().size(),
                    ex
            );
        }
    }

    /**
     *  Since this class has no references to {@code MFXNotification} because they are two distinct and separate concepts,
     *  the close button action must be set after instantiating a {@code MFXNotification}.
     */
    public void setCloseHandler(EventHandler<MouseEvent> closeHandler) {
        if (this.closeHandler != null) {
            this.closeButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.closeHandler);
        }

        this.closeHandler = closeHandler;
        this.closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
    }

    public StackPane getHeaderNode() {
        return headerNode;
    }

    public Label getHeaderLabel() {
        return headerLabel;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public MFXScrollPane getContentScroll() {
        return contentScroll;
    }

    public Label getContentLabel() {
        return contentLabel;
    }

    public MFXButton getCloseButton() {
        return closeButton;
    }

    public MFXButton getOkButton() {
        return okButton;
    }

    public HBox getButtonsBox() {
        return buttonsBox;
    }

    /*
     * Unused code.
     * Before using the scroll pane for the content label the header had an extra button,
     * an expand button similar to Android's notifications, that button was set to be visible
     * only if the content was truncated and on click the prefHeight was incremented by the specified value with
     * a Transition, however the problem with this approach was the PositionManager system because as you can see in the following code,
     * if the content was still truncated at the end of the transition the method was executed again and again until the isTruncated property
     * was false. The PositionManager had two extra methods, repositionNotifications and buildRepositionAnimation with the expandValue as parameter,
     * the reposition method had to be recalled every time too with the same frequency as the expandNotificationMethod but as you can see this class
     * has no references to PositionManager because of course they are two distinct and separate concepts.
     *
     * I don't want to delete this code because I still believe it can be implemented in some way, but in the end I opted for a scroll pane
     * because it was way easier.
     */
    /*
    public void expandNotification(double expandValue) {
        final double currHeight = getPrefHeight();
        Transition expand = new Transition() {
            {
                setCycleDuration(Duration.millis(0.1));
            }

            @Override
            protected void interpolate(double frac) {
                setPrefHeight(currHeight + (expandValue * frac));
            }
        };
        expand.setOnFinished(event -> {
            if (isTruncated.get()) {
                expandNotification(expandValue);
            }
        });
        expand.play();
    }
    */
}
