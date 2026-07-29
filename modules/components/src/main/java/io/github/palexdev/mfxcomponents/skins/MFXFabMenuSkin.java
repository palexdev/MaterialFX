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

import java.util.ArrayList;
import java.util.List;

import io.github.palexdev.mfxcomponents.behaviors.MFXFabMenuBehavior;
import io.github.palexdev.mfxcomponents.controls.MFXFab;
import io.github.palexdev.mfxcomponents.controls.MFXFabMenu;
import io.github.palexdev.mfxcomponents.variants.FABVariants.StyleVariant;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.enums.Corner;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.PivotUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion.MotionPreset;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.observe;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Default skin implementation for all [MFXFabMenus][MFXFabMenu], expects behaviors of type [MFXFabMenuBehavior].
///
/// The menu is implemented in a tricky way. Contrary to what the name might suggest, we do not use a popup to show the
/// entries. Rather, we position all the FABs in the same region and just animate them in/out according to the
/// [MFXFabMenu#openProperty()].
///
/// This makes the layout logic extremely simple. The 'entry' FAB (which is the one responsible for opening/closing) the menu
/// is the only one that accounts for the width and height of the menu. The others are positioned above or below it,
/// one after the other. The [MFXFabMenu#menuCornerProperty()] determines the vertical positioning and order of the FABs,
/// see [#getOrderedButtons()].<br >
/// The 'entry' FAB can be selected in CSS with '.entry'.
///
/// Why such choices, you may ask? One word: animations.
/// 1) Because of animations, a popup would need to be repositioned during the entire duration of the transition.
/// This is indeed possible, but it's quite a hit on performance.
/// 2) FABs are not 'unmanaged', thus the size computation methods need to be properly overridden, because otherwise
/// the extend/collapse animation would not update the layout.
///
/// The only downside of not using a popup is not having a proper automatic close of the menu. However, the skin adds
/// listeners and handlers to reproduce such behavior. If you find a case in which it should close, and it does not, report
/// back, and I'll try to fix it.
///
/// #### Scaling
///
/// Material Design specs show the 'entry' FAB to shrinking a bit when the menu is open. This is implemented by this skin
/// and the relative CSS by scaling down the node. However, since the specs also show that the scale may have different
/// pivots, depending on the context/position, and because in JavaFX CSS this is not possible, we need to apply another
/// [Scale] to the 'entry'. This one allows setting the pivot as specified by the [MFXFabMenu#scalePivotProperty()].
/// The downside of this is having the risk of scaling down the node too much. So, my recommendation is to tweak the scale
/// values appropriately from CSS.
public class MFXFabMenuSkin extends MFXSkinBase<MFXFabMenu> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXFab entry;

    private final Scale scale;

    private Animation animation;
    protected double ANIMATIONS_DELAY = 40.0;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXFabMenuSkin(MFXFabMenu menu) {
        super(menu);

        // Init
        entry = new MFXFab();
        entry.getStyleClass().add("entry");
        entry.onActionProperty().bind(menu.onActionProperty());

        scale = new Scale();
        scale.xProperty().bind(entry.scaleXProperty());
        scale.yProperty().bind(entry.scaleYProperty());
        entry.getTransforms().add(scale);

        // Finalize
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds listeners to the following properties:
    /// - [MFXFabMenu#openProperty()] to call [#animateOpenClose()]
    /// - [MFXFabMenu#focusedProperty()] and [MFXFabMenu#focusWithinProperty()] to close the menu if focus is lost
    /// - [MFXFabMenu#getButtons()] to call [#updateChildren()]
    /// - [MFXFabMenu#getAppliedVariants()] to call [#updateStyle()]
    protected void addListeners() {
        MFXFabMenu menu = getSkinnable();
        listeners(
            onInvalidated(menu.openProperty())
                .then(_ -> animateOpenClose())
                .executeNow(menu::isOpen),
            onInvalidated(menu.focusedProperty())
                .condition(f -> !f || !menu.isFocusWithin())
                .then(_ -> getBehavior().close())
                .invalidating(menu.focusWithinProperty()),
            observe(menu::requestLayout, menu.scalePivotProperty()),
            observe(this::updateChildren, menu.getButtons()).executeNow(),
            observe(this::updateStyle, menu.getAppliedVariants())
        );
    }

    /// Updates the children's list of this component with the 'entry' FAB from this skin, and the FABs contained in
    /// [MFXFabMenu#getButtons()].
    ///
    /// Before doing so, ensures that the menu is closed.
    ///
    /// On each FAB from [MFXFabMenu#getButtons()] these operations are performed:
    /// - Adds the style class '.item' for differentiating from the 'entry' FAB
    /// - Sets the opacity to `0.0` (later for the animation) and visibility to `false`
    /// - Sets them to be collapsed, [MFXFab#extendedProperty()]
    /// - Disallows focus traversal if the menu is not open
    protected void updateChildren() {
        MFXFabMenu menu = getSkinnable();
        menu.setOpen(false); // Make sure the menu is closed

        List<Node> newList = new ArrayList<>();
        newList.add(entry);
        for (MFXFab f : menu.getButtons()) {
            f.setVisible(false);
            if (!f.getStyleClass().contains("item"))
                f.getStyleClass().add("item");
            f.setOpacity(0.0);
            f.setExtended(false);
            // Make them focus traversable only if the menu is open
            f.focusTraversableProperty().bind(menu.openProperty());
            newList.add(f);
        }
        updateStyle();
        getChildren().setAll(newList);
    }

    /// Responsible for updating the FABs [StyleVariant] when it's changed by [MFXFabMenu#setStyle(StyleVariant)].
    ///
    /// The 'entry' gets the currently applied variant. The other FABs get the corresponding tonal variant instead.
    protected void updateStyle() {
        MFXFabMenu menu = getSkinnable();
        StyleVariant variant = menu.getAppliedVariant(StyleVariant.class);
        entry.setStyle(variant);

        variant = switch (variant) {
            case PRIMARY -> StyleVariant.TONAL_PRIMARY;
            case SECONDARY -> StyleVariant.TONAL_SECONDARY;
            case TERTIARY -> StyleVariant.TONAL_TERTIARY;
            default -> throw new IllegalStateException("Unexpected variant for buttons: " + variant);
        };
        for (MFXFab f : menu.getButtons()) f.setStyle(variant);
    }

    /// Responsible for animating the menu when opening/closing.
    ///
    /// The animation is a [ParallelTransition] with a series of [Timelines][Timeline],
    /// one for each FAB in [MFXFabMenu#getButtons()]. For a more pleasant effect, each timeline is slightly delayed
    /// by a `delayAdvance` specified by [#ANIMATIONS_DELAY] (only when opening!).
    ///
    /// The opacity and [MFXFab#extendedProperty()] properties are adjusted according to the open state.
    protected void animateOpenClose() {
        MFXFabMenu menu = getSkinnable();
        List<MFXFab> buttons = getOrderedButtons();
        boolean open = menu.isOpen();

        if (Animations.isPlaying(animation))
            animation.stop();

        double delayAdvance = open ? ANIMATIONS_DELAY : 0.0;
        double delay = delayAdvance;
        double opacity = open ? 1.0 : 0.0;
        MotionPreset om = M3Motion.STANDARD_FAST_EFFECTS;

        ParallelBuilder builder = ParallelBuilder.build();
        for (MFXFab f : buttons) {
            f.setVisible(true);
            builder.add(
                TimelineBuilder.build()
                    .setDelay(delay)
                    .add(KeyFrames.of(Duration.ZERO, _ -> f.setExtended(open)))
                    .add(KeyFrames.of(om.millis(), f.opacityProperty(), opacity, om.curve()))
                    .setOnFinished(_ -> f.setVisible(open))
                    .getAnimation()
            );
            delay += delayAdvance;
        }
        animation = builder.getAnimation();
        animation.play();
    }

    /// @return the FABs list ([MFXFabMenu#getButtons()]) ordered according to the [MFXFabMenu#menuCornerProperty()].
    /// For top corners the order is reversed.
    protected List<MFXFab> getOrderedButtons() {
        MFXFabMenu menu = getSkinnable();
        return menu.getMenuCorner().isTop() ? menu.getButtons().reversed() : menu.getButtons();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void registerBehavior() {
        super.registerBehavior();
        MFXFabMenu menu = getSkinnable();
        MFXFabMenuBehavior behavior = getBehavior();
        events(
            intercept(entry, ActionEvent.ACTION)
                .handle(e -> {
                    if (menu.isOpen()) {
                        behavior.close();
                    } else {
                        behavior.open();
                    }
                    e.consume();
                }),
            // Close if mouse pressed outside
            intercept(menu.getScene(), MouseEvent.MOUSE_PRESSED)
                .condition(_ -> menu.isOpen())
                .handle(_ -> behavior.close()),
            // Actioning one of the buttons should close the menu
            intercept(menu, ActionEvent.ACTION)
                .handle(_ -> behavior.close())
        );
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(-1);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(-1);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + LayoutUtils.snappedBoundWidth(entry) + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + LayoutUtils.snappedBoundHeight(entry) + bottomInset;
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
        MFXFabMenu menu = getSkinnable();
        double gap = menu.getGap();
        Corner corner = menu.getMenuCorner();
        Pos pos = corner.toPos();

        layoutInArea(entry, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);

        List<MFXFab> buttons = getOrderedButtons();
        double advanceMul = corner.isTop() ? -1.0 : 1.0;
        double advance = (entry.getHeight() + gap) * advanceMul;

        for (MFXFab f : buttons) {
            f.autosize();
            double fX = LayoutUtils.computeXPosition(
                menu, f,
                x, w, Insets.EMPTY, false, pos.getHpos(),
                true, false
            );
            f.relocate(fX, advance);
            advance += advanceMul * (f.getHeight() + (gap / 2.0));
        }

        // Update scale pivot
        Pos scalePivot = menu.getScalePivot();
        Position scalePos = PivotUtils.pivotPosition(entry.getLayoutBounds(), scalePivot);
        scale.setPivotX(scalePos.x());
        scale.setPivotY(scalePos.y());
    }

    @Override
    public void dispose() {
        scale.xProperty().unbind();
        scale.yProperty().unbind();
        super.dispose();
    }

    @Override
    protected MFXFabMenuBehavior getBehavior() {
        return (MFXFabMenuBehavior) super.getBehavior();
    }
}
