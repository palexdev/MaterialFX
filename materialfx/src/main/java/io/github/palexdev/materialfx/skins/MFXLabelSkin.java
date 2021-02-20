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
    private final HBox container;
    private final Label text;

    private final Line unfocusedLine;
    private final Line focusedLine;

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

        text = new Label();
        text.getStyleClass().add("text-node");
        text.setText(label.getText().isEmpty() ? label.getPromptText() : label.getText());
        text.fontProperty().bind(label.fontProperty());
        text.alignmentProperty().bind(label.labelAlignmentProperty());
        text.minWidthProperty().bind(Bindings.createDoubleBinding(() -> NodeUtils.computeTextWidth(text.getFont(), text.getText()),
                label.textProperty(), label.fontProperty())
        );

        container = new HBox(text);
        container.alignmentProperty().bind(label.alignmentProperty());
        container.prefWidthProperty().bind(label.widthProperty());
        container.prefHeightProperty().bind(label.heightProperty());
        container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.spacingProperty().bind(label.graphicTextGapProperty());
        container.setPadding(new Insets(0, 10, 0, 10));

        if (label.getLeadingIcon() != null) {
            container.getChildren().add(0, label.getLeadingIcon());
        }
        if (label.getTrailingIcon() != null) {
            container.getChildren().add(label.getTrailingIcon());
        }
        setListeners();

        if (label.getLabelStyle() == Styles.LabelStyles.STYLE1) {
            getChildren().addAll(container, unfocusedLine, focusedLine);
        } else {
            getChildren().add(container);
        }

        label.setPadding(new Insets(0, 10, 0, 10));
    }

    private void setListeners() {
        MFXLabel label = getSkinnable();


        label.labelStyleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Styles.LabelStyles.STYLE2) {
                getChildren().removeAll(unfocusedLine, focusedLine);
            } else {
                getChildren().addAll(unfocusedLine, focusedLine);
            }
        });

        label.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                text.setText(label.getPromptText());
            } else {
                text.setText(label.getText());
            }
        });

        label.leadingIconProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                int index = container.getChildren().indexOf(oldValue);
                container.getChildren().set(index, newValue);
            } else {
                container.getChildren().add(0, newValue);
            }
        });

        label.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
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
            if (event.getClickCount() >= 2 && label.isEditable() && !containsEditor()) {
                showEditor();
            }
            event.consume();
        });

    }

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

    private void showEditor() {
        text.setVisible(false);
        MFXTextField textField = new MFXTextField(text.getText());
        textField.setId("editor-node");
        textField.setUnfocusedLineColor(Color.TRANSPARENT);
        textField.setLineColor(Color.TRANSPARENT);
        textField.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        textField.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        textField.prefWidthProperty().bind(text.widthProperty());
        textField.prefHeightProperty().bind(text.heightProperty());

        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getSkinnable().setText(textField.getText());
                getChildren().remove(textField);
                text.setVisible(true);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                getChildren().remove(textField);
                text.setVisible(true);
            }
        });

        getChildren().add(textField);
    }

    private boolean containsEditor() {
        Node editor = getChildren().stream()
                .filter(node -> node.getId() != null && node.getId().equals("editor-node"))
                .findFirst()
                .orElse(null);
        return editor != null;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 30;
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
