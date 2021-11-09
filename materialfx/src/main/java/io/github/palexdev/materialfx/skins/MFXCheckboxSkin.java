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
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXCheckbox}.
 */
public class MFXCheckboxSkin extends SkinBase<MFXCheckbox> {
    //================================================================================
    // Properties
    //================================================================================
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
        rippleContainer.setManaged(false);

        rippleContainerClip = new Circle();
        rippleContainerClip.centerXProperty().bind(rippleContainer.widthProperty().divide(2.0));
        rippleContainerClip.centerYProperty().bind(rippleContainer.heightProperty().divide(2.0));
        rippleContainer.setClip(rippleContainerClip);

        text = new LabeledControlWrapper(checkbox);

        getChildren().setAll(rippleContainer, text);
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

        checkbox.contentDispositionProperty().addListener(invalidated -> checkbox.requestLayout());
        checkbox.gapProperty().addListener(invalidated -> checkbox.requestLayout());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXCheckbox checkbox = getSkinnable();
        ContentDisplay disposition = checkbox.getContentDisposition();
        double gap = checkbox.getGap();

        double minW;
        switch (disposition) {
            case LEFT:
            case RIGHT:
            case TEXT_ONLY:
                minW = leftInset + rippleContainer.prefWidth(-1) + gap + text.prefWidth(-1) + rightInset;
                break;
            case TOP:
            case BOTTOM:
                minW = leftInset + Math.max(rippleContainer.prefWidth(-1), text.prefWidth(-1)) + rightInset;
                break;
            case CENTER:
            case GRAPHIC_ONLY:
                minW = leftInset + rippleContainer.prefWidth(-1) + rightInset;
                break;
            default:
                minW = super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        return minW;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXCheckbox checkbox = getSkinnable();
        ContentDisplay disposition = checkbox.getContentDisposition();
        double gap = checkbox.getGap();

        double minH;
        switch (disposition) {
            case LEFT:
            case RIGHT:
            case TEXT_ONLY:
                minH = topInset + Math.max(rippleContainer.prefHeight(-1), text.prefHeight(-1)) + bottomInset;
                break;
            case TOP:
            case BOTTOM:
                minH = topInset + rippleContainer.prefHeight(-1) + gap + text.prefHeight(-1) + bottomInset;
                break;
            case CENTER:
            case GRAPHIC_ONLY:
                minH = leftInset + rippleContainer.prefHeight(-1) + rightInset;
                break;
            default:
                minH = super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
        }
        return minH;
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
        MFXCheckbox checkbox = getSkinnable();
        ContentDisplay disposition = checkbox.getContentDisposition();
        Insets padding = checkbox.getPadding();
        double gap = checkbox.getGap();

        double rcW = rippleContainer.prefWidth(-1);
        double rcH = rippleContainer.prefHeight(-1);
        double rcX = 0;
        double rcY = 0;

        double txW = text.prefWidth(-1);
        double txH = text.prefHeight(-1);
        double txX = 0;
        double txY = 0;

        switch (disposition) {
            case TOP: {
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = 0;
                txX = (contentWidth / 2) - (txW / 2);
                txY = rcH + gap;
                break;
            }
            case RIGHT: {
                rcX = contentWidth - rcW;
                rcY = (contentHeight / 2) - (rcH / 2);
                txX = rcX - txW - gap;
                txY = (contentHeight / 2) - (txH / 2);
                break;
            }
            case BOTTOM: {
                txX = (contentWidth / 2) - (txW / 2);
                txY = 0;
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = txH + gap;
                break;
            }
            case TEXT_ONLY:
            case LEFT: {
                rcX = 0;
                rcY = (contentHeight / 2) - (rcH / 2);
                txX = rcW + gap;
                txY = (contentHeight / 2) - (txH / 2);
                break;
            }
            case CENTER:
            case GRAPHIC_ONLY: {
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = (contentHeight / 2) - (rcH / 2);
                txW = 0;
                txH = 0;
                break;
            }
        }

        rippleContainer.resizeRelocate(
                snapPositionX(rcX + padding.getLeft()),
                snapPositionY(rcY + padding.getTop()),
                rcW,
                rcH
        );
        text.resizeRelocate(
                snapPositionX(txX + padding.getLeft()),
                snapPositionY(txY + padding.getTop()),
                txW,
                txH
        );

        double boxSize = box.getSize();
        Insets boxPadding = box.getPadding();
        double boxClipRadius = boxPadding.getLeft() + boxSize / 2 + boxPadding.getRight();
        rippleContainerClip.setRadius(boxClipRadius);
    }
}
