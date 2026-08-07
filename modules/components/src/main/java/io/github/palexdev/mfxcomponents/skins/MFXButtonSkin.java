/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;

/// Base skin implementation for all components of type [MFXButtonBase].
///
/// The layout is simple, there is just the label to show the text, the [MFXSurface] responsible for showing the various
/// interaction states (applying an overlay background) and the [MFXRippleGenerator] for the ripple effects.
public class MFXButtonSkin extends MFXLabeledSkin {
    //================================================================================
    // Properties
    //================================================================================
    protected final MFXSurface surface;
    protected final MFXRippleGenerator rg;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXButtonSkin(MFXButtonBase button) {
        super(button);

        // Init
        initTextMeasurementCache();
        surface = new MFXSurface(button);
        rg = new MFXRippleGenerator(button);
        rg.getStyleClass().add("surface-ripple");
        rg.setMeToPosConverter(me ->
            (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
        );

        // Finalize
        updateChildren();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Responsible for updating the control's children list.
    protected void updateChildren() {
        getChildren().setAll(surface, rg, label);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void registerBehavior() {
        MFXButtonBase button = getControl();
        MFXBehavior<? extends Node> behavior = getBehavior();
        events(
            intercept(button, MouseEvent.MOUSE_PRESSED).handle(e -> behavior.mousePressed(e, () -> rg.generate(e))),
            intercept(button, MouseEvent.MOUSE_RELEASED).handle(_ -> rg.release()),
            intercept(button, MouseEvent.MOUSE_EXITED).handle(_ -> rg.release()),
            intercept(button, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked),
            intercept(button, KeyEvent.KEY_PRESSED).handle(e -> behavior.keyPressed(e, () -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    Bounds b = button.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXButtonBase button = getControl();
        Node graphic = button.getGraphic();
        return leftInset +
               ((graphic != null) ? LayoutUtils.snappedBoundWidth(graphic) + button.getGraphicTextGap() : 0.0) +
               ((button.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) ? 0.0 : textWidth()) +
               rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXButtonBase button = getControl();
        Node graphic = button.getGraphic();
        return topInset +
               Math.max(
                   ((graphic != null) ? LayoutUtils.snappedBoundHeight(graphic) : 0.0),
                   textHeight()
               ) + bottomInset;
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
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXButtonBase button = getControl();
        Pos align = button.getAlignment();
        layoutInArea(label, x, y, w, h, 0.0, align.getHpos(), align.getVpos());
        surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        rg.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        super.dispose();
    }

    @Override
    protected MFXButtonBase getControl() {
        return (MFXButtonBase) super.getControl();
    }
}
