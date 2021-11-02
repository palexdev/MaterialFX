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

import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.enums.TextPosition;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * This is the default skin for every {@link MFXCircleToggleNode}.
 * <p></p>
 * The base container is a {@link StackPane} which contains: a {@link Circle} that represents
 * the toggle, a {@link MFXLabel} to show the toggle's text.
 * <p></p>
 * Includes a {@link MFXCircleRippleGenerator} to generate ripple effects on mouse pressed.
 */
public class MFXCircleToggleNodeSkin extends SkinBase<MFXCircleToggleNode> {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final Circle circle;
    private final MFXLabel label;
    private final MFXCircleRippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCircleToggleNodeSkin(MFXCircleToggleNode toggleNode) {
        super(toggleNode);

        circle = new Circle();
        circle.setId("circle");
        circle.radiusProperty().bind(toggleNode.sizeProperty());
        circle.strokeWidthProperty().bind(toggleNode.strokeWidthProperty());
        circle.strokeTypeProperty().bind(toggleNode.strokeTypeProperty());

        label = new MFXLabel();
        label.setId("textNode");
        label.setManaged(false);
        label.textProperty().bind(toggleNode.textProperty());
        label.setLeadingIcon(toggleNode.getLabelLeadingIcon());
        label.setTrailingIcon(toggleNode.getLabelTrailingIcon());
        label.getStylesheets().setAll(toggleNode.getUserAgentStylesheet());

        container = new StackPane();
        container.getStyleClass().setAll("container");
        container.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        rippleGenerator = new MFXCircleRippleGenerator(container);
        rippleGenerator.setMouseTransparent(true);

        if (toggleNode.getGraphic() != null) {
            toggleNode.getGraphic().setMouseTransparent(true);
            container.getChildren().setAll(circle, rippleGenerator, label, toggleNode.getGraphic());
        } else {
            container.getChildren().setAll(circle, rippleGenerator, label);
        }

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
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setAnimationSpeed(1.3);
        rippleGenerator.setClipSupplier(() -> {
            Circle clip = new Circle();
            clip.radiusProperty().bind(circle.radiusProperty());
            clip.centerXProperty().bind(Bindings.createDoubleBinding(
                    () -> circle.getBoundsInParent().getCenterX(),
                    circle.boundsInParentProperty()
            ));
            clip.centerYProperty().bind(Bindings.createDoubleBinding(
                    () -> circle.getBoundsInParent().getCenterY(),
                    circle.boundsInParentProperty()
            ));
            return clip;
        });
        rippleGenerator.setRipplePositionFunction(event -> new PositionBean(event.getX(), event.getY()));
        rippleGenerator.rippleRadiusProperty().bind(circle.radiusProperty().add(5));
    }

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXCircleToggleNode#graphicProperty()} ()}: to update the toggle's icon when changes.
     * <p> - {@link MFXCircleToggleNode#labelTextGapProperty()} and {@link MFXCircleToggleNode#textPositionProperty()}: to update the layout when they change.
     * <p></p>
     * Adds bindings for:
     * <p>
     * <p> - Binds the {@link MFXLabel} icons properties to the corresponding toggle's properties.
     * <p></p>
     * Adds event filters/handlers for:
     * <p>
     * <p> - MOUSE_PRESSED: to consume the check if the mouse was pressed inside tje circle,
     * to ignore mouse pressed on label and its icons, to update the selection state and create riffle effects.
     * (Unfortunately, JavaFX is not very accurate for circles).
     */
    private void setListeners() {
        MFXCircleToggleNode toggleNode = getSkinnable();

        toggleNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!NodeUtils.inHierarchy(event, circle)) {
                return;
            }

            Node leadingIcon = label.getLeadingIcon();
            Node trailingIcon = label.getTrailingIcon();

            if (leadingIcon != null && NodeUtils.inHierarchy(event, leadingIcon)) {
                return;
            }
            if (trailingIcon != null && NodeUtils.inHierarchy(event, trailingIcon)) {
                return;
            }
            if (NodeUtils.inHierarchy(event, label)) {
                return;
            }

            toggleNode.setSelected(!toggleNode.isSelected());
            rippleGenerator.generateRipple(event);
            event.consume();
        });

        toggleNode.graphicProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                container.getChildren().remove(oldValue);
            }
            if (newValue != null) {
                newValue.setMouseTransparent(true);
                container.getChildren().add(newValue);
            }
        });

        toggleNode.labelTextGapProperty().addListener(invalidated -> toggleNode.requestLayout());
        toggleNode.textPositionProperty().addListener(invalidated -> toggleNode.requestLayout());
        label.leadingIconProperty().bind(toggleNode.labelLeadingIconProperty());
        label.trailingIconProperty().bind(toggleNode.labelTrailingIconProperty());
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        MFXCircleToggleNode toggleNode = getSkinnable();

        double lw = snapSizeX(LabelUtils.computeMFXLabelWidth(label));
        double lh = label.prefHeight(lw);
        double lx = snapPositionX(circle.getBoundsInParent().getCenterX() - (lw / 2.0));
        double ly = 0;

        if (toggleNode.getTextPosition() == TextPosition.BOTTOM) {
            label.setTranslateY(0);
            ly = snapPositionY(circle.getBoundsInParent().getMaxY() + toggleNode.getLabelTextGap());
            label.resizeRelocate(lx, ly, lw, lh);
        } else {
            label.resizeRelocate(lx, ly, lw, lh);
            label.setTranslateY(-toggleNode.getLabelTextGap() - lh);
        }
    }
}
