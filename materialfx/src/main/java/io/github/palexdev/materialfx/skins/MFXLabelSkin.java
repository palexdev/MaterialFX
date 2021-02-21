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
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
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

public class MFXLabelSkin extends SkinBase<MFXLabel> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label textNode;

    private final Line unfocusedLine;
    private final Line focusedLine;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabelSkin(MFXLabel label) {
        super(label);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.setManaged(false);
        unfocusedLine.strokeProperty().bind(label.unfocusedLineColorProperty());
        unfocusedLine.setSmooth(true);
        unfocusedLine.endXProperty().bind(label.widthProperty().subtract(1));

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setManaged(false);
        focusedLine.strokeProperty().bind(label.lineColorProperty());
        focusedLine.setSmooth(true);
        focusedLine.endXProperty().bind(label.widthProperty().subtract(1));
        focusedLine.setScaleX(0.0);

        textNode = new Label();
        textNode.getStyleClass().add("text-node");
        textNode.textProperty().bind(Bindings.createStringBinding(() -> {
            if (label.getText().isEmpty()) {
                return label.getPromptText();
            }
            return label.getText();
        }, label.textProperty(), label.promptTextProperty()));
        textNode.fontProperty().bind(label.fontProperty());
        textNode.alignmentProperty().bind(label.labelAlignmentProperty());

        container = new HBox(textNode);
        container.alignmentProperty().bind(label.alignmentProperty());
        container.spacingProperty().bind(label.graphicTextGapProperty());
        container.setPadding(new Insets(0, 10, 0, 10));

        if (label.getLeadingIcon() != null) {
            container.getChildren().add(0, label.getLeadingIcon());
        }
        if (label.getTrailingIcon() != null) {
            container.getChildren().add(label.getTrailingIcon());
        }

        if (label.getLabelStyle() == Styles.LabelStyles.STYLE1) {
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
            } else {
                getChildren().addAll(unfocusedLine, focusedLine);
            }
        });

        label.leadingIconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                container.getChildren().remove(oldValue);
                return;
            }
            if (oldValue != null) {
                container.getChildren().set(0, newValue);
            } else {
                container.getChildren().add(0, newValue);
            }
        });

        label.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                container.getChildren().remove(oldValue);
                return;
            }
            if (oldValue != null) {
                int index = container.getChildren().indexOf(oldValue);
                container.getChildren().set(index, newValue);
            } else {
                container.getChildren().add(newValue);
            }
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

        label.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            label.requestFocus();
            if (event.getClickCount() >= 2 && label.isEditable() && !containsEditor() &&
                    NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), textNode)) {
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
     * the changes.
     */
    private void showEditor() {
        MFXLabel label = getSkinnable();

        textNode.setVisible(false);
        MFXTextField textField = new MFXTextField(textNode.getText());
        textField.setId("editor-node");
        textField.setUnfocusedLineColor(Color.TRANSPARENT);
        textField.setLineColor(Color.TRANSPARENT);
        textField.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        textField.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        textField.prefWidthProperty().bind(textNode.widthProperty());
        textField.prefHeightProperty().bind(textNode.heightProperty());

        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getSkinnable().setText(textField.getText());
                getChildren().remove(textField);
                textNode.setVisible(true);
                label.requestFocus();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                getChildren().remove(textField);
                textNode.setVisible(true);
                label.requestFocus();
            }
        });

        getChildren().add(textField);
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
        return Math.max(100, super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset));
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        focusedLine.relocate(0, contentHeight);
        unfocusedLine.relocate(0, contentHeight);
    }
}
