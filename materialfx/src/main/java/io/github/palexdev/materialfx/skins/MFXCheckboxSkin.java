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

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.LabeledControlWrapper;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXCheckbox}.
 */
public class MFXCheckboxSkin extends SkinBase<MFXCheckbox> {
    //================================================================================
    // Properties
    //================================================================================
    private final BorderPane container;
    private final MFXIconWrapper box;
    private final LabeledControlWrapper text;

    private final StackPane rippleContainer;
    private final Circle rippleContainerClip;
    private final MFXCircleRippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckboxSkin(MFXCheckbox checkbox) {
        super(checkbox);

        MFXFontIcon mark = new MFXFontIcon();
        mark.getStyleClass().add("mark");
        box = new MFXIconWrapper(mark, -1);
        box.getStyleClass().add("box");

        rippleContainer = new StackPane();
        rippleGenerator = new MFXCircleRippleGenerator(rippleContainer);
        rippleGenerator.setManaged(false);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setCheckBounds(false);
        rippleGenerator.setClipSupplier(() -> null);
        rippleGenerator.setRipplePositionFunction(event -> {
            PositionBean position = new PositionBean();
            position.setX(Math.min(event.getX(), rippleContainer.getWidth()));
            position.setY(Math.min(event.getY(), rippleContainer.getHeight()));
            return position;
        });

        rippleContainer.getChildren().addAll(rippleGenerator, box);
        rippleContainer.getStyleClass().add("ripple-container");

        rippleContainerClip = new Circle();
        rippleContainerClip.centerXProperty().bind(rippleContainer.widthProperty().divide(2.0));
        rippleContainerClip.centerYProperty().bind(rippleContainer.heightProperty().divide(2.0));
        rippleContainer.setClip(rippleContainerClip);

        text = new LabeledControlWrapper(checkbox);
        if (checkbox.isTextExpand()) text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        container = new BorderPane();
        initPane();
        updateAlignment();

        getChildren().setAll(container);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void addListeners() {
        MFXCheckbox checkbox = getSkinnable();

        checkbox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            rippleGenerator.generateRipple(event);
            checkbox.fire();
        });

        checkbox.alignmentProperty().addListener((observable, oldValue, newValue) -> updateAlignment());
        checkbox.contentDispositionProperty().addListener(invalidated -> initPane());
        checkbox.gapProperty().addListener(invalidated -> initPane());
        checkbox.textExpandProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            } else {
                text.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            }
        });
    }

    protected void initPane() {
        MFXCheckbox checkbox = getSkinnable();
        ContentDisplay disposition = checkbox.getContentDisposition();
        double gap = checkbox.getGap();

        container.getChildren().clear();
        container.setCenter(text);
        switch (disposition) {
            case TOP: {
                container.setTop(rippleContainer);
                BorderPane.setMargin(text, InsetsFactory.top(gap));
                break;
            }
            case RIGHT: {
                container.setRight(rippleContainer);
                BorderPane.setMargin(text, InsetsFactory.right(gap));
                break;
            }
            case BOTTOM: {
                container.setBottom(rippleContainer);
                BorderPane.setMargin(text, InsetsFactory.bottom(gap));
                break;
            }
            case TEXT_ONLY:
            case LEFT: {
                container.setLeft(rippleContainer);
                BorderPane.setMargin(text, InsetsFactory.left(gap));
                break;
            }
            case GRAPHIC_ONLY:
            case CENTER: {
                container.setCenter(rippleContainer);
                BorderPane.setMargin(text, InsetsFactory.none());
                break;
            }
        }
    }

    protected void updateAlignment() {
        MFXCheckbox checkbox = getSkinnable();
        Pos alignment = checkbox.getAlignment();

        if (PositionUtils.isTop(alignment)) {
            BorderPane.setAlignment(rippleContainer, Pos.TOP_CENTER);
        } else if (PositionUtils.isCenter(alignment)) {
            BorderPane.setAlignment(rippleContainer, Pos.CENTER);
        } else if (PositionUtils.isBottom(alignment)) {
            BorderPane.setAlignment(rippleContainer, Pos.BOTTOM_CENTER);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + Math.max(rippleContainer.prefWidth(-1), text.prefWidth(-1)) + rightInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(-1);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(-1);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        double boxSize = box.getSize();
        Insets boxPadding = box.getPadding();
        double boxClipRadius = boxPadding.getLeft() + boxSize / 2 + boxPadding.getRight();
        rippleContainerClip.setRadius(boxClipRadius);
    }
}
