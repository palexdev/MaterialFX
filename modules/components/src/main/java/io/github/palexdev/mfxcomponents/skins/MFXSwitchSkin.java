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
import io.github.palexdev.mfxcomponents.controls.MFXSwitch;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.uniform;
import static java.util.Optional.ofNullable;

public class MFXSwitchSkin extends MFXLabeledSkin {

    //================================================================================
    // Properties
    //================================================================================

    private final MFXSurface surface;
    private final MFXRippleGenerator rg;
    private final Pane track;
    private final StackPane box;
    private final MFXIconWrapper icon;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSwitchSkin(MFXSwitch sw) {
        box = new StackPane();
        track = new Pane() {
            @Override
            protected void layoutChildren() {
                if (getChildren().isEmpty()) return;
                Node box = getChildren().getFirst();
                box.autosize();
                positionInArea(box, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.CENTER);
            }
        };
        super(sw);

        // Init
        surface = new MFXSurface(sw);
        rg = new MFXRippleGenerator(sw);
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

        Region handle = new Region();
        handle.getStyleClass().add("handle");
        icon = new MFXIconWrapper(null);
        box.getChildren().addAll(surface, rg, handle, icon);
        box.getStyleClass().add("box");
        track.getChildren().add(box);
        track.getStyleClass().add("track");

        initTextMeasurementCache();

        // Finalize
        listeners(
            onInvalidated(icon.iconProperty())
                .then(i -> {
                    boolean present = ofNullable(i).map(MFXFontIcon::getIconName).filter(s -> !s.isBlank()).isPresent();
                    PseudoClasses.setOn(box, "with-icon", present);
                }).executeNow()
        );
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

    @Override
    protected BoundLabel buildLabelNode() {
        BoundLabel bl = super.buildLabelNode();
        // switches do not have a graphic (it's the "track")
        bl.graphicProperty().unbind();
        bl.setGraphic(track);
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
}
