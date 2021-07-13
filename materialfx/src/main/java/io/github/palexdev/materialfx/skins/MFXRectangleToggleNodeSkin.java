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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * This is the default skin for every {@link MFXRectangleToggleNode}.
 * <p></p>
 * The base container is a {@link StackPane} which contains: a {@link MFXLabel} to show the toggle's text.
 * <p></p>
 * Includes a {@link MFXCircleRippleGenerator} to generate ripple effects on mouse pressed.
 */
public class MFXRectangleToggleNodeSkin extends SkinBase<MFXRectangleToggleNode> {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final MFXLabel label;
    private final MFXCircleRippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRectangleToggleNodeSkin(MFXRectangleToggleNode toggleNode) {
        super(toggleNode);

        label = new MFXLabel();
        label.setId("textNode");
        label.textProperty().bind(toggleNode.textProperty());
        label.graphicProperty().bind(toggleNode.graphicProperty());
        label.graphicTextGapProperty().bind(toggleNode.labelTextGapProperty());
        label.getStylesheets().setAll(toggleNode.getUserAgentStylesheet());
        label.setLeadingIcon(toggleNode.getLabelLeadingIcon());
        label.setTrailingIcon(toggleNode.getLabelTrailingIcon());

        container = new StackPane();
        container.getStyleClass().setAll("container");
        container.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.alignmentProperty().bind(toggleNode.alignmentProperty());
        container.prefWidthProperty().bind(toggleNode.widthProperty());
        container.prefHeightProperty().bind(toggleNode.heightProperty());

        rippleGenerator = new MFXCircleRippleGenerator(toggleNode);

        container.getChildren().setAll(rippleGenerator, label);

        setupRippleGenerator();
        setListeners();
        getChildren().setAll(container);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sets up the ripple generator.
     */
    protected void setupRippleGenerator() {
        MFXRectangleToggleNode toggleNode = getSkinnable();

        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setClipSupplier(() -> toggleNode.getRippleClipTypeFactory().build(container));
        rippleGenerator.setRipplePositionFunction(event -> new RipplePosition(event.getX(), event.getY()));
        rippleGenerator.rippleRadiusProperty().bind(toggleNode.widthProperty().divide(2.0));
    }

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXRectangleToggleNode}: to update the ripple generator's clip supplier.
     * <p></p>
     * Adds bindings for:
     * <p>
     * <p> - Binds the {@link MFXLabel} icons properties to the corresponding toggle's properties.
     * <p></p>
     * Adds event filters/handlers for:
     * <p>
     * <p> - MOUSE_PRESSED: to not change the selection state if the mouse is pressed on the label's icons, to update the selection state, and to generate ripple effects.
     * <p> - MOUSE_PRESSED on the label: to not change the selection state if the mouse is pressed on the label's icons and pass the mouse event to the toggle.
     */
    private void setListeners() {
        MFXRectangleToggleNode toggleNode = getSkinnable();

        toggleNode.rippleClipTypeFactoryProperty().addListener((observable, oldValue, newValue) -> rippleGenerator.setClipSupplier(() -> newValue.build(container)));
        label.leadingIconProperty().bind(toggleNode.labelLeadingIconProperty());
        label.trailingIconProperty().bind(toggleNode.labelTrailingIconProperty());

        container.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node leadingIcon = label.getLeadingIcon();
            Node trailingIcon = label.getTrailingIcon();

            if (leadingIcon != null && NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), leadingIcon)) {
                return;
            }
            if (trailingIcon != null && NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), trailingIcon)) {
                return;
            }

            toggleNode.setSelected(!toggleNode.isSelected());
            rippleGenerator.generateRipple(event);
        });

        label.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node leadingIcon = label.getLeadingIcon();
            Node trailingIcon = label.getTrailingIcon();


            if (leadingIcon != null && NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), leadingIcon)) {
                return;
            }
            if (trailingIcon != null && NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), trailingIcon)) {
                return;
            }

            Event.fireEvent(toggleNode, event);
        });
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + LabelUtils.computeMFXLabelWidth(label) + rightInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
}
