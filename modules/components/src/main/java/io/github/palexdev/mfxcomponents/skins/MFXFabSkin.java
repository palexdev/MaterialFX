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

import io.github.palexdev.mfxcomponents.controls.MFXFab;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxcore.utils.fx.LabelMeasurementCache;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextMeasurementCache;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion.MotionPreset;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import javafx.animation.Animation;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Experimental alternative to [MFXFabSkin] that trades the reactive, listener-driven layout for a layout-pass-driven one.
///
/// The original skin computes the FAB's size and the label's position inside the property listeners (extended, icon,
/// variants, ...). Those listeners can fire before the CSS of the FAB's subtree is applied, so the measurements they read
/// ([LabelMeasurementCache], the icon wrapper bounds, the label's position) are frequently stale and have to be forced
/// fresh with scattered [Node#applyCss()]/[Parent#layout()] calls, one quirk at a time.
///
/// This skin follows a single invariant instead:
///
/// > All sizing and positioning is computed **only** inside [#layoutChildren(double, double, double, double)], from live
/// > measurements. Listeners never measure; they only record an *intent* and request a layout.
///
/// Because [#layoutChildren(double, double, double, double)] always runs after the CSS pass, the measurements are always
/// fresh, so no [Node#applyCss()] hack is needed anywhere. It also makes the layout **self-healing**: every layout pass
/// re-syncs the FAB to the correct target, so it can never get stuck in a stale state (which is exactly why, in the old
/// skin, an unrelated event like a hover would "fix" the layout).
///
/// As a consequence, most of the old listeners disappear: variant, min-size and text/label changes naturally dirty the
/// layout and are healed for free. Only two intents remain:
/// - [#animateNext]: set when [MFXFab#extendedProperty()] toggles, to animate the next re-sync
/// - [#iconSwitch]: set when [MFXFab#iconProperty()] changes, to play the collapse-then-re-extend effect
///
/// Everything else (nodes, behavior/ripple wiring, measurements, min/max sizes) is identical to [MFXFabSkin].
public class MFXFabSkin extends MFXLabeledSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXIconWrapper iconWrapper;
    private final MFXSurface surface;
    private final MFXRippleGenerator rg;

    protected boolean init = false;
    protected boolean animateNext = false;
    protected boolean iconSwitch = false;
    protected LabelMeasurementCache lmc;
    protected Animation animation;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXFabSkin(MFXFab fab) {
        super(fab);

        // Init
        iconWrapper = new MFXIconWrapper(fab.getIcon());
        iconWrapper.iconProperty().bindBidirectional(fab.iconProperty());
        iconWrapper.animatedProperty().bind(fab.extendedProperty().not());
        label.setGraphic(iconWrapper);

        surface = new MFXSurface(fab);
        rg = new MFXRippleGenerator(fab);
        rg.getStyleClass().add("surface-ripple");
        rg.setMeToPosConverter(me ->
            (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
        );

        initTextMeasurementCache();
        clip();

        // Finalize
        addListeners();
        getChildren().setAll(surface, rg, label);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// The listeners only record an intent and request a layout; the actual work happens in
    /// [#layoutChildren(double, double, double, double)]:
    /// - [MFXFab#extendedProperty()] sets [#animateNext] so the next re-sync is animated
    /// - [MFXFab#iconProperty()] sets [#iconSwitch] so the next re-sync plays the collapse-then-re-extend effect
    ///
    /// Variant, min-size and text/label changes need no listener: they already dirty the layout and are re-synced by the
    /// self-healing branch of [#layoutChildren(double, double, double, double)].
    protected void addListeners() {
        MFXFab fab = getControl();
        listeners(
            onInvalidated(fab.extendedProperty())
                .then(_ -> {
                    animateNext = true;
                    fab.requestLayout();
                }),
            onInvalidated(fab.iconProperty())
                .then(_ -> {
                    iconSwitch = true;
                    fab.requestLayout();
                })
        );
    }

    /// Instantly sets the FAB's [MFXFab#prefWidthProperty()], the label's [Node#translateXProperty()] and the FAB's
    /// [MFXFab#textOpacityProperty()] to the targets computed for the given state. Does nothing if already on target, which
    /// keeps the self-healing branch of [#layoutChildren(double, double, double, double)] from looping.
    protected void snap(boolean extended) {
        MFXFab fab = getControl();
        double w = computeTargetWidth(extended);
        double x = computeTargetX(extended, w);
        double o = extended ? 1.0 : 0.0;
        if (fab.getPrefWidth() == w && label.getTranslateX() == x && fab.getTextOpacity() == o) return;
        fab.setPrefWidth(w);
        label.setTranslateX(x);
        fab.setTextOpacity(o);
    }

    /// Animates the same three values adjusted by [#snap(boolean)] toward the targets computed for the given state, using
    /// the Material 3 motion presets. Any running animation is stopped first.
    protected void animateTo(boolean extended) {
        stopAnimation();
        MFXFab fab = getControl();
        double w = computeTargetWidth(extended);
        double x = computeTargetX(extended, w);
        double o = extended ? 1.0 : 0.0;

        MotionPreset eMotion = M3Motion.EXPRESSIVE_DEFAULT_EFFECTS;
        MotionPreset sMotion = M3Motion.EXPRESSIVE_FAST_SPATIAL;
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(sMotion.millis(), fab.prefWidthProperty(), w, sMotion.curve()))
            .add(KeyFrames.of(sMotion.millis(), label.translateXProperty(), x, sMotion.curve()))
            .add(KeyFrames.of(eMotion.millis(), fab.textOpacityProperty(), o, eMotion.curve()))
            .getAnimation();
        animation.play();
    }

    protected void stopAnimation() {
        if (Animations.isPlaying(animation)) animation.stop();
    }

    /// Computes the FAB's width according to its extended state.
    ///
    /// The content width is given by:
    /// - Extended: the label's width, which we get from the cache, [LabelMeasurementCache]
    /// - Collapsed: the [#iconWrapper]'s width (the node that is actually displayed)
    ///
    /// The final value, however, is the maximum between: the width given by the [MFXFab#minSizeProperty()] and
    /// the computed value including horizontal padding.
    protected double computeTargetWidth(boolean extended) {
        MFXFab fab = getControl();
        double minW = fab.getMinSize().width();
        double target = contentWidth(extended);
        return snapSizeX(Math.max(minW, snappedLeftInset() + target + snappedRightInset()));
    }

    /// Computes the label's x displacement so that its icon or the label as a whole always appear at the center of the FAB.
    /// Since this is intended to be used in conjunction with [#computeTargetWidth(boolean)], to avoid recomputing such value,
    /// it's accepted as an argument.
    ///
    /// The target is given by `(targetW - contentW) / 2.0 - startX`, where `contentW` is the same measurement used by
    /// [#computeTargetWidth(boolean)] and `startX` is the natural x position of the label. Because the label is laid out
    /// before this runs (see [#layoutChildren(double, double, double, double)]), `startX` is always fresh.
    protected double computeTargetX(boolean extended, double targetW) {
        double startX = label.getLayoutX();
        return (targetW - contentWidth(extended)) / 2.0 - startX;
    }

    /// The width of the FAB's content according to its extended state: the cached label width when extended, otherwise
    /// the [#iconWrapper]'s width. Shared by [#computeTargetWidth(boolean)] and [#computeTargetX(boolean, double)] so that
    /// sizing and centering always agree on the same measurement.
    protected double contentWidth(boolean extended) {
        return extended ? lmc.getSnappedWidth() : LayoutUtils.snappedBoundWidth(iconWrapper);
    }

    /// Clips the FAB's label so that the text does not overflow when animating between extended/collapsed states.
    protected void clip() {
        MFXLabeled fab = getSkinnable();
        Rectangle r = new Rectangle();
        r.widthProperty().bind(fab.widthProperty());
        r.heightProperty().bind(fab.heightProperty());
        label.setClip(r);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void registerBehavior() {
        super.registerBehavior();
        MFXLabeled fab = getSkinnable();
        MFXBehavior<? extends Node> behavior = getBehavior();
        events(
            intercept(fab, MouseEvent.MOUSE_PRESSED).handle(e -> behavior.mousePressed(e, () -> rg.generate(e))),
            intercept(fab, MouseEvent.MOUSE_RELEASED).handle(_ -> rg.release()),
            intercept(fab, MouseEvent.MOUSE_EXITED).handle(_ -> rg.release()),
            intercept(fab, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked),
            intercept(fab, KeyEvent.KEY_PRESSED).handle(e -> behavior.keyPressed(e, () -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    Bounds b = fab.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    /// Overridden to not use [TextMeasurementCache] but rather [LabelMeasurementCache]. In this skin, because of the animations,
    /// it's not enough to have the text's sizes, we need the full size of the label, which includes many more parameters.
    @Override
    protected void initTextMeasurementCache() {
        lmc = new LabelMeasurementCache(label);
    }

    /// {@inheritDoc}
    ///
    /// Overridden to unbind the graphic property as the icon is carried in a [MFXIconWrapper], and to set the
    /// [Label#disableTruncationProperty()] to `true`.
    @Override
    protected BoundLabel buildLabelNode() {
        BoundLabel label = super.buildLabelNode();
        label.graphicProperty().unbind();
        label.setDisableTruncation(true);
        return label;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + getControl().getMinSize().height() + bottomInset;
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + rightInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    /// The single place where sizing and positioning happen. Runs after the CSS pass, so every measurement is fresh.
    ///
    /// The label is laid out first so that [#computeTargetX(boolean, double)] reads a fresh `startX`. Then, in order:
    /// - if an animation is running and there's no new intent, it is left to play
    /// - [#iconSwitch]: instantly collapse, then re-extend with an animation if the FAB is extended (the Material 3 effect
    ///   shown when the icon changes)
    /// - [#animateNext]: animate the re-sync toward the current extended state
    /// - otherwise: [#snap(boolean)] to the current state (the self-healing case, e.g. after a variant/text change)
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXFab fab = getControl();
        surface.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
        rg.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
        layoutInArea(label, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);

        if (!init) {
            init = true;
            animateNext = false;
            iconSwitch = false;
            snap(fab.isExtended());
            return;
        }

        if (iconSwitch) {
            iconSwitch = false;
            animateNext = false;
            stopAnimation();
            snap(false);
            if (fab.isExtended()) animateTo(true);
        } else if (animateNext) {
            animateNext = false;
            animateTo(fab.isExtended());
        } else if (!Animations.isPlaying(animation)) {
            snap(fab.isExtended());
        }
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        lmc.dispose();
        super.dispose();
    }

    @Override
    protected MFXFab getControl() {
        return (MFXFab) super.getControl();
    }
}
