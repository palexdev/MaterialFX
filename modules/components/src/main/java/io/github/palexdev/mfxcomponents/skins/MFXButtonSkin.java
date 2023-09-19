/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onChanged;

/**
 * Base skin implementation for all components of type {@link MFXButtonBase}.
 * <p>
 * This skin uses behaviors of type {@link MFXButtonBehaviorBase}.
 * <p></p>
 * The layout is simple, there are just the label to show the text and the {@link MaterialSurface} responsible for
 * showing the various interaction states (applying an overlay background) and generating ripple effects.
 */
public class MFXButtonSkin<T extends MFXButtonBase<B>, B extends MFXButtonBehaviorBase<T>> extends MFXLabeledSkin<T, B> {
    //================================================================================
    // Properties
    //================================================================================
    protected final MaterialSurface surface;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonSkin(T button) {
        super(button);
        initTextMeasurementCache();

        // Init surface
        surface = new MaterialSurface(button)
            .initRipple(rg -> rg.setRippleColor(Color.web("#d7d1e7")));

        // Finalize init
        getChildren().addAll(surface, label);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds the following listeners:
     * <p> - A listener on the {@link MFXButton#contentDisplayProperty()} to activate/disable the pseudo classes
     * {@link PseudoClasses#WITH_ICON_LEFT} and {@link PseudoClasses#WITH_ICON_RIGHT} accordingly
     */
    protected void addListeners() {
        T button = getSkinnable();
        listeners(
            onChanged(button.contentDisplayProperty())
                .then((o, n) -> {
                    Node graphic = button.getGraphic();
                    boolean wil = (graphic != null) && (n == ContentDisplay.LEFT);
                    boolean wir = (graphic != null) && (n == ContentDisplay.RIGHT);
                    PseudoClasses.WITH_ICON_LEFT.setOn(button, wil);
                    PseudoClasses.WITH_ICON_RIGHT.setOn(button, wir);
                })
                .executeNow()
                .invalidating(button.graphicProperty())
        );
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * Initializes the given {@link MFXButtonBehaviorBase} to handle events such as: {@link MouseEvent#MOUSE_PRESSED},
     * {@link MouseEvent#MOUSE_RELEASED}, {@link MouseEvent#MOUSE_CLICKED}, {@link MouseEvent#MOUSE_EXITED} and
     * {@link KeyEvent#KEY_PRESSED}.
     */
    @Override
    protected void initBehavior(B behavior) {
        T button = getSkinnable();
        MFXRippleGenerator rg = surface.getRippleGenerator();
        behavior.init();
        events(
            intercept(button, MouseEvent.MOUSE_PRESSED)
                .process(e -> behavior.mousePressed(e, c -> rg.generate(e))),

            intercept(button, MouseEvent.MOUSE_RELEASED)
                .process(e -> behavior.mouseReleased(e, c -> rg.release())),

            intercept(button, MouseEvent.MOUSE_CLICKED)
                .process(behavior::mouseClicked),

            intercept(button, MouseEvent.MOUSE_EXITED)
                .process(e -> behavior.mouseExited(e, c -> rg.release())),

            intercept(button, KeyEvent.KEY_PRESSED)
                .process(e -> behavior.keyPressed(e, c -> {
                    if (e.getCode() == KeyCode.ENTER) {
                        Bounds b = button.getLayoutBounds();
                        rg.generate(b.getCenterX(), b.getCenterY());
                        rg.release();
                    }
                }))
        );
    }

    @Override
    public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        T button = getSkinnable();
        double insets = leftInset + rightInset;
        double tW = getCachedTextWidth();
        if (button.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) tW = 0;
        double gW = (button.getGraphic() != null) ? LayoutUtils.boundWidth(button.getGraphic()) + button.getGraphicTextGap() : 0.0;
        return insets + tW + gW;
    }

    @Override
    public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        T button = getSkinnable();
        double insets = topInset + bottomInset;
        double tH = getCachedTextHeight();
        double gH = button.getGraphic() != null ? LayoutUtils.boundHeight(button.getGraphic()) : 0.0;
        return insets + Math.max(tH, gH);
    }

    @Override
    public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        T button = getSkinnable();
        Pos pos = button.getAlignment();
        layoutInArea(label, x, y, w, h, 0, pos.getHpos(), pos.getVpos());
        surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
    }

    @Override
    public void dispose() {
        surface.dispose();
        super.dispose();
    }
}
