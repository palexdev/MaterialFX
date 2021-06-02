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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.LabelUtils;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXLabelSkin}.
 * <p>
 * This skin simply wrappers a normal JavaFX {@link Label} in an {@link HBox}.
 * Why? Because designing a new label entirely from scratch would be too much work, plus I'm not
 * entirely sure it could be done because lots of apis for the JavaFX label are part of the com.sun.javafx package,
 * so they are private.
 * <p>
 * This leads to the loss of some base features of the JavaFX {@link Label}, you can get the wrapper label
 * by using the {@link MFXLabel#getTextNode()} method, but I don't guarantee that all options are working.
 * <p>
 * That said it's important to remember that {@link MFXLabel} also introduces new features and fixes. For example
 * you can have two icons, one leading and one trailing. Also, the alignment of the icons with the text should be way better
 * and you can also control it by setting the margin of the icons using {@link HBox#setMargin(Node, Insets)} since the icons
 * are added to the {@link HBox}. The label can also be edited like a text field by setting {@link MFXLabel#editableProperty()} to true
 * and double clicking it.
 */
public class MFXLabelSkin extends SkinBase<MFXLabel> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label textNode;
    private boolean promptIsUsed = false;

    private final Line unfocusedLine;
    private final Line focusedLine;

    private EventHandler<MouseEvent> iconEditorHandler = event -> {
        if (event.getClickCount() > 1) {
            event.consume();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabelSkin(MFXLabel label) {
        super(label);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.setManaged(false);
        unfocusedLine.strokeWidthProperty().bind(label.lineStrokeWidthProperty());
        unfocusedLine.strokeProperty().bind(label.unfocusedLineColorProperty());
        unfocusedLine.setSmooth(true);
        unfocusedLine.endXProperty().bind(label.widthProperty().subtract(1));

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setManaged(false);
        focusedLine.strokeWidthProperty().bind(label.lineStrokeWidthProperty());
        focusedLine.strokeProperty().bind(label.lineColorProperty());
        focusedLine.setSmooth(true);
        focusedLine.endXProperty().bind(label.widthProperty().subtract(1));
        focusedLine.setScaleX(0.0);

        textNode = new Label();
        textNode.getStyleClass().add("text-node");
        textNode.textProperty().bind(Bindings.createStringBinding(() -> {
            if (label.getText().isEmpty()) {
                promptIsUsed = true;
                return label.getPromptText();
            } else {
                promptIsUsed = false;
                return label.getText();
            }
        }, label.textProperty(), label.promptTextProperty()));
        textNode.fontProperty().bind(label.fontProperty());
        textNode.textFillProperty().bind(label.textFillProperty());
        textNode.alignmentProperty().bind(label.labelAlignmentProperty());

        container = new HBox(textNode);
        container.alignmentProperty().bind(label.alignmentProperty());
        container.spacingProperty().bind(label.graphicTextGapProperty());
        container.paddingProperty().bind(label.containerPaddingProperty());

        if (label.getLeadingIcon() != null) {
            container.getChildren().add(0, label.getLeadingIcon());
            label.getLeadingIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
        }
        if (label.getTrailingIcon() != null) {
            container.getChildren().add(label.getTrailingIcon());
            label.getTrailingIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
        }

        if (label.getLabelStyle() != Styles.LabelStyles.STYLE2) {
            getChildren().addAll(container, unfocusedLine, focusedLine);
        } else {
            getChildren().add(container);
        }

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: label style, leading and trailing icons, focus.
     * <p>
     * Adds handlers for: focus, show editor.
     */
    private void setListeners() {
        MFXLabel label = getSkinnable();

        label.labelStyleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Styles.LabelStyles.STYLE2) {
                getChildren().removeAll(unfocusedLine, focusedLine);
            } else if (!getChildren().contains(focusedLine)) {
                getChildren().addAll(unfocusedLine, focusedLine);
            }
        });

        label.promptTextShowingProperty().bind(textNode.textProperty().isEqualTo(label.getPromptText()));
        label.promptTextShowingProperty().bind(Bindings.createBooleanBinding(
                () -> textNode.getText().equals(label.getPromptText()) && promptIsUsed,
                textNode.textProperty(), label.promptTextProperty()
        ));

        label.leadingIconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                container.getChildren().remove(oldValue);
                return;
            }
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
                container.getChildren().set(0, newValue);
            } else {
                container.getChildren().add(0, newValue);
            }

            newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
        });

        label.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                container.getChildren().remove(oldValue);
                return;
            }
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
                int index = container.getChildren().indexOf(oldValue);
                container.getChildren().set(index, newValue);
            } else {
                container.getChildren().add(newValue);
            }

            newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, iconEditorHandler);
        });

        label.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (label.isAnimateLines()) {
                buildAndPlayAnimation(newValue);
                return;
            }

            if (newValue) {
                focusedLine.setScaleX(1.0);
            } else {
                focusedLine.setScaleX(0.0);
            }
        });

        label.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (label.isAnimateLines() && focusedLine.getScaleX() != 1.0) {
                buildAndPlayAnimation(true);
                return;
            }

            focusedLine.setScaleX(1.0);
        });

        container.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            label.requestFocus();
            if (event.getClickCount() >= 2 && label.isEditable() && !containsEditor()) {
                showEditor();
            }
            event.consume();
        });
    }


    /**
     * Builds the focus animation.
     */
    private void buildAndPlayAnimation(boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(350), focusedLine);
        if (focused) {
            scaleTransition.setFromX(0.0);
            scaleTransition.setToX(1.0);
        } else {
            scaleTransition.setFromX(1.0);
            scaleTransition.setToX(0.0);
        }
        scaleTransition.setInterpolator(MFXAnimationFactory.getInterpolatorV2());
        scaleTransition.play();
    }

    /**
     * If {@link MFXLabel#editableProperty()} is set to true shows the editor,
     * ESCAPE hides the editor canceling any change, ENTER hides the editor and confirms
     * the changes, if the editor looses focus (for example by clicking on another component that requests the focus)
     * the editor is hidden and any changes are confirmed.
     */
    private void showEditor() {
        MFXLabel label = getSkinnable();

        textNode.setVisible(false);
        MFXTextField textField = new MFXTextField(label.getText());
        label.editorFocusedProperty().bind(textField.focusedProperty());
        textField.setId("editor-node");
        textField.setManaged(false);
        textField.setUnfocusedLineColor(Color.TRANSPARENT);
        textField.setLineColor(Color.TRANSPARENT);

        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                label.setText(textField.getText());
                container.getChildren().remove(textField);
                textNode.setVisible(true);
                textNode.setPrefWidth(Region.USE_COMPUTED_SIZE);
                label.requestFocus();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                container.getChildren().remove(textField);
                textNode.setVisible(true);
                textNode.setPrefWidth(Region.USE_COMPUTED_SIZE);
                label.requestFocus();
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                label.setText(textField.getText());
                container.getChildren().remove(textField);
                textNode.setVisible(true);
                textNode.setPrefWidth(Region.USE_COMPUTED_SIZE);
                label.requestFocus();
            }
        });

        container.getChildren().add(textField);
        computeEditorPosition(textField);
        textField.requestFocus();
    }

    /**
     * Responsible for showing the editor correctly, handles its size and location.
     * <p>
     * Note that when the editor width is computed we set that same width as the textNode's prefWidth as well,
     * by doing so the trailing icon position will be automatically managed by the container. When the editor is removed
     * the textNode's prefWidth is set back to USE_COMPUTED_SIZE.
     */
    private void computeEditorPosition(MFXTextField textField) {
        MFXLabel label = getSkinnable();

        double posX = textNode.getBoundsInParent().getMinX();
        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();
        double leadingWidth = label.getLeadingIcon() != null ? label.getLeadingIcon().getLayoutBounds().getWidth() : 0;
        double trailingWidth = label.getTrailingIcon() != null ? label.getTrailingIcon().getLayoutBounds().getWidth() : 0;
        double editorWidth = containerWidth -
                (
                        label.getContainerPadding().getLeft() +
                        leadingWidth + label.getGraphicTextGap() +
                        trailingWidth + label.getGraphicTextGap() +
                        label.getContainerPadding().getRight()
                );
        textNode.setPrefWidth(editorWidth);
        textField.resizeRelocate(posX, 0, editorWidth, containerHeight);
    }

    /**
     * Checks if the editor is already shown.
     */
    private boolean containsEditor() {
        Node editor = getChildren().stream()
                .filter(node -> node.getId() != null && node.getId().equals("editor-node"))
                .findFirst()
                .orElse(null);
        return editor != null;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(27, super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset));
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(100, LabelUtils.computeMFXLabelWidth(getSkinnable()));
    }


    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }


    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    public void dispose() {
        super.dispose();
        iconEditorHandler = null;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        focusedLine.relocate(0, contentHeight);
        unfocusedLine.relocate(0, contentHeight);
    }
}
