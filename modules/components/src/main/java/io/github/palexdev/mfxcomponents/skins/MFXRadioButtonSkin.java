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

import io.github.palexdev.mfxcomponents.controls.MFXRadioButton;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.*;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.uniform;

/// Default skin implementation for all [MFXRadioButtons][MFXRadioButton]. Extends [MFXLabeledSkin].
///
/// The layout is delegated almost entirely to the label:
/// - the label shows the component's text
/// - the `box`, which is the node showing the selection state, is set as the label's graphic, see [#buildLabelNode()]
///
/// The advantage of this approach is that properties such as [MFXLabeled#contentDisplayProperty()] and
/// [MFXLabeled#graphicTextGapProperty()] are supported out of the box, no extra code needed to position the text
/// relative to the `box`.
///
/// The `box` is a [StackPane] holding three nodes:
/// - a [MFXSurface] to show the various interaction states with the component
/// - a [MFXRippleGenerator] for the ripple effect, clipped to a circle
/// - the [Indicator], which is the actual "radio mark"
public class MFXRadioButtonSkin extends MFXLabeledSkin {

    //================================================================================
    // Properties
    //================================================================================

    private final MFXSurface surface;
    private final MFXRippleGenerator rg;
    private final StackPane box;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXRadioButtonSkin(MFXRadioButton button) {
        box = new StackPane();
        super(button);

        // Init
        surface = new MFXSurface(button);
        rg = new MFXRippleGenerator(button);
        rg.getStyleClass().add("surface-ripple");
        rg.setClipSupplier(() -> {
            Region clip = new Region();
            clip.setBackground(new Background(new BackgroundFill(
                Color.WHITE, uniform(999.0).toRadius(false), Insets.EMPTY))
            );
            return clip;
        });
        rg.setMeToPosConverter(me ->
            (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null);
        Indicator indicator = new Indicator();
        box.getChildren().setAll(surface, rg, indicator);
        box.getStyleClass().add("box");

        initTextMeasurementCache();

        // Finalize
        getChildren().setAll(label);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void registerBehavior() {
        super.registerBehavior();
        MFXLabeled rb = getSkinnable();
        MFXBehavior<? extends Node> behavior = getBehavior();
        events(
            intercept(rb, MouseEvent.MOUSE_PRESSED).handle(e -> behavior.mousePressed(e, () -> rg.generate(e))),
            intercept(rb, MouseEvent.MOUSE_RELEASED).handle(_ -> rg.release()),
            intercept(rb, MouseEvent.MOUSE_EXITED).handle(_ -> rg.release()),
            intercept(rb, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked),
            intercept(rb, KeyEvent.KEY_PRESSED).handle(e -> behavior.keyPressed(e, () -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    Bounds b = box.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    /// {@inheritDoc}
    ///
    /// Overridden to unbind the graphic property and set the `box` as the label's graphic.
    @Override
    protected BoundLabel buildLabelNode() {
        BoundLabel bl = super.buildLabelNode();
        // radio button does not have a graphic (it's the "mark")
        bl.graphicProperty().unbind();
        bl.setGraphic(box);
        return bl;
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + tmCache.getSnappedWidth() + rightInset;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + tmCache.getSnappedHeight() + bottomInset;
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
        layoutInArea(label, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
        rg.resizeRelocate(0, 0, box.getWidth(), box.getHeight());
        surface.resizeRelocate(0, 0, box.getWidth(), box.getHeight());
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        super.dispose();
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// The node which visually conveys the selection state of a [MFXRadioButton].
    ///
    /// It's a simple [StackPane], with the '.outer' style class, wrapping a [Region] which has the '.inner' style class.
    /// The former is the radio's ring, the latter is the dot at its center.
    ///
    /// There is no logic here, the transition between the two states is entirely done in CSS by scaling the '.inner'
    /// region on the ':selected' pseudo class. For this reason, the region is also cached with [CacheHint#SCALE].
    protected static class Indicator extends StackPane {
        {
            Region inner = new Region();
            inner.getStyleClass().add("inner");
            inner.setCache(true);
            inner.setCacheHint(CacheHint.SCALE);
            getStyleClass().add("outer");
            // Snapping is computed in local coordinates, as if the node was not transformed. Since the '.inner' region
            // is scaled, that correction gets multiplied by the scale factor and lands back off the pixel grid, asymmetrically.
            // The ring is painted unscaled, so the dot ends up visibly off its center.
            // Only noticeable at fractional render scales (e.g. 125%).
            setSnapToPixel(false);
            getChildren().setAll(inner);
        }
    }
}
