/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcomponents.controls.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.observe;
import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.uniform;

/// Default skin implementation for all [MFXCheckboxes][MFXCheckbox]. Extends [MFXLabeledSkin].
///
/// It is composed of four nodes:
/// - the label to show the text
/// - the box to display the selection state. It's a [MFXIconWrapper] wrapping the font icon.
/// - A [MFXSurface] and a [MFXRippleGenerator] for showing the various interaction states with the component and ripple
/// effects.
///
/// The surface (and the ripple) is designed to always be double in size of the icon. Can be changed by setting the
/// [#SURFACE_SIZE_MULTIPLIER] variable.
///
/// By design, at least for now, the [MFXCheckbox#contentDisplayProperty()] is not entirely supported. Only [ContentDisplay#RIGHT]
/// and [ContentDisplay#LEFT] are managed and determine the position of the label relative to the box.<br >
/// In my opinion, those are the most sensible values used 99% of the time.
public class MFXCheckboxSkin extends MFXLabeledSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXSurface surface;
    private final MFXRippleGenerator rg;
    private final MFXIconWrapper icon;

    protected double SURFACE_SIZE_MULTIPLIER = 2.0;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXCheckboxSkin(MFXCheckbox checkbox) {
        super(checkbox);

        // Init
        icon = new MFXIconWrapper();
        icon.getStyleClass().add("box");

        surface = new MFXSurface(checkbox);
        rg = new MFXRippleGenerator(checkbox);
        rg.getStyleClass().add("surface-ripple");
        rg.setClipSupplier(() -> {
            Region clip = new Region();
            clip.setBackground(new Background(new BackgroundFill(
                Color.WHITE, uniform(999.0).toRadius(false), Insets.EMPTY))
            );
            return clip;
        });
        rg.setMeToPosConverter(me ->
            (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
        );

        initTextMeasurementCache();

        // Finalize
        addListeners();
        getChildren().setAll(surface, rg, icon, label);

    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners:
    ///  - A listener on the [MFXCheckbox#contentDisplayProperty()] to update the layout when it changes
    protected void addListeners() {
        MFXLabeled checkbox = getSkinnable();
        listeners(
            observe(checkbox::requestLayout, checkbox.contentDisplayProperty())
        );
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void registerBehavior() {
        super.registerBehavior();
        MFXLabeled checkbox = getSkinnable();
        MFXBehavior<? extends Node> behavior = getBehavior();
        events(
            intercept(checkbox, MouseEvent.MOUSE_PRESSED).handle(e -> behavior.mousePressed(e, () -> rg.generate(e))),
            intercept(checkbox, MouseEvent.MOUSE_RELEASED).handle(_ -> rg.release()),
            intercept(checkbox, MouseEvent.MOUSE_EXITED).handle(_ -> rg.release()),
            intercept(checkbox, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked),
            intercept(checkbox, KeyEvent.KEY_PRESSED).handle(e -> behavior.keyPressed(e, () -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    Bounds b = checkbox.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    @Override
    protected BoundLabel buildLabelNode() {
        BoundLabel boundLabel = super.buildLabelNode();
        boundLabel.graphicProperty().unbind();
        boundLabel.contentDisplayProperty().unbind();
        boundLabel.setGraphic(null);
        boundLabel.setContentDisplay(ContentDisplay.TEXT_ONLY);
        return boundLabel;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double iconSize = Math.max(
            LayoutUtils.snappedBoundWidth(icon),
            LayoutUtils.snappedBoundHeight(icon)
        );
        double stateLayerSize = iconSize * SURFACE_SIZE_MULTIPLIER;
        double gap = label.getGraphicTextGap();
        return leftInset + stateLayerSize + gap + tmCache.getSnappedWidth() + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        double iconSize = Math.max(
            LayoutUtils.snappedBoundWidth(icon),
            LayoutUtils.snappedBoundHeight(icon)
        );
        double stateLayerSize = iconSize * SURFACE_SIZE_MULTIPLIER;
        return topInset +
               Math.max(stateLayerSize, tmCache.getSnappedHeight()) +
               bottomInset;
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
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXLabeled checkbox = getSkinnable();

        icon.autosize();
        double stateLayerSize = Math.max(icon.getWidth(), icon.getHeight()) * SURFACE_SIZE_MULTIPLIER;

        HPos sPos = checkbox.getContentDisplay() == ContentDisplay.RIGHT ? HPos.RIGHT : HPos.LEFT;
        surface.resize(stateLayerSize, stateLayerSize);
        rg.resize(stateLayerSize, stateLayerSize);
        positionInArea(surface, x, y, w, h, 0, sPos, VPos.CENTER);
        positionInArea(rg, x, y, w, h, 0, sPos, VPos.CENTER);

        icon.relocate(
            surface.getLayoutX() + (stateLayerSize - icon.getWidth()) / 2.0,
            surface.getLayoutY() + (stateLayerSize - icon.getHeight()) / 2.0
        );

        HPos lPos = checkbox.getContentDisplay() == ContentDisplay.RIGHT ? HPos.LEFT : HPos.RIGHT;
        layoutInArea(label, x, y, w, h, 0, lPos, VPos.CENTER);
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        super.dispose();
    }
}
