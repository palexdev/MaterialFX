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

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXSplitButton;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.popups.MFXPopups;
import io.github.palexdev.mfxcore.popups.PopupState;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.control.ContentDisplay;

import static io.github.palexdev.mfxcore.observables.When.observe;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Default skin implementation for all [MFXSplitButtons][MFXSplitButton].
///
/// The split button is not a single node with a fancy shape, but rather a container for two plain [MFXButtons][MFXButton]
/// built and entirely managed by this skin. They can be selected in CSS with '.leading' and '.trailing', which is what
/// the themes use to give each half the proper asymmetric radius.
///
/// The leading button is the one carrying the primary action: its text, graphic and `onAction` properties are bound to
/// the corresponding ones of the [MFXSplitButton]. Since the handler is invoked by the leading button itself, the
/// [ActionEvents][ActionEvent] fired by both halves are consumed by this skin, so that they never bubble up to the
/// [MFXSplitButton] and cause a second, spurious invocation. This is also why [MFXSplitButton#trigger()] is a no-op.
///
/// The trailing button only shows a [MFXFontIcon] and acts as the owner of the [MFXMenu] built by [#buildMenu()].
/// The menu is configured by [MFXSplitButton#menuConfigProperty()], and its entries are kept in sync with the
/// [MFXSplitButton#getMenuItems()] list through a content binding. While the menu is open, the ':open' pseudo state is
/// activated on the [MFXSplitButton], which is what the themes use to rotate the icon by 180deg.
///
/// Finally, since the two halves are standard buttons, they know nothing about the variants applied on the
/// [MFXSplitButton]. Keeping them in sync is the job of [#updateVariants()].
///
/// The layout is trivial: the leading button is laid out at the left, the trailing one at the right, separated by [#GAP].
public class MFXSplitButtonSkin extends MFXSkinBase<MFXSplitButton> {
    //================================================================================
    // Properties
    //================================================================================

    private final MFXButton lead;
    private final MFXButton trail;
    private final MFXMenu menu;

    private EventHandler<ActionEvent> consumeHandler = Event::consume;

    protected double GAP = 2.0;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSplitButtonSkin(MFXSplitButton button) {
        super(button);

        // Init
        lead = new MFXButton();
        lead.textProperty().bind(button.textProperty());
        lead.graphicProperty().bind(button.graphicProperty());
        lead.onActionProperty().bind(button.onActionProperty());
        lead.addEventHandler(ActionEvent.ACTION, consumeHandler);
        lead.getStyleClass().add("leading");

        MFXFontIcon trailIcon = new MFXFontIcon();
        trailIcon.setCache(true);
        trailIcon.setCacheHint(CacheHint.ROTATE);
        trail = new MFXButton();
        trail.setGraphic(trailIcon);
        trail.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        trail.addEventHandler(ActionEvent.ACTION, consumeHandler);
        trail.getStyleClass().add("trailing");

        if ((menu = buildMenu()) != null) {
            menu.install(trail);
        }

        // Finalize
        addListeners();
        getChildren().setAll(lead, trail);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds listeners to the following properties:
    /// - [MFXSplitButton#menuConfigProperty()] to re-apply the config on the menu
    /// - [MFXMenu#stateProperty()] to de-/activate the ':open' pseudo state on the [MFXSplitButton]
    /// - [MFXSplitButton#getAppliedVariants()] to call [#updateVariants()]
    protected void addListeners() {
        MFXSplitButton button = getSkinnable();
        listeners(
            onInvalidated(button.menuConfigProperty())
                .then(cfg -> cfg.apply(menu)),
            onInvalidated(menu.stateProperty())
                .then(s -> PseudoClasses.OPEN.setOn(button, s == PopupState.SHOWING || s == PopupState.SHOWN)),
            observe(this::updateVariants, button.getAppliedVariants()).executeNow()
        );
    }

    /// Responsible for mirroring the [StyleVariant] and [SizeVariant] applied on the [MFXSplitButton] onto the leading
    /// and trailing buttons, since they are standard [MFXButtons][MFXButton] and thus have their own variants.
    protected void updateVariants() {
        MFXSplitButton button = getControl();
        StyleVariant style = button.getAppliedVariant(StyleVariant.class);
        SizeVariant size = button.getAppliedVariant(SizeVariant.class);
        lead.setStyle(style);
        lead.setSize(size);
        trail.setStyle(style);
        trail.setSize(size);
    }

    /// Builds the [MFXMenu] shown by the trailing button, applying the [MFXSplitButton#menuConfigProperty()] on it and
    /// keeping its entries in sync with the [MFXSplitButton#getMenuItems()] list through a content binding.
    ///
    /// @return the newly built menu
    protected MFXMenu buildMenu() {
        MFXSplitButton button = getSkinnable();
        MFXMenu menu = MFXPopups.menu().get();
        button.getMenuConfig().apply(menu);
        Bindings.bindContent(menu.getItems(), button.getMenuItems());
        return menu;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset +
               LayoutUtils.snappedBoundWidth(lead) +
               GAP +
               LayoutUtils.snappedBoundWidth(trail) +
               rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset +
               Math.max(
                   LayoutUtils.snappedBoundHeight(lead),
                   LayoutUtils.snappedBoundHeight(trail)
               ) +
               bottomInset;
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
        layoutInArea(lead, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);
        layoutInArea(trail, x + GAP, y, w, h, 0, HPos.RIGHT, VPos.CENTER);
    }

    @Override
    public void dispose() {
        MFXSplitButton button = getControl();
        lead.removeEventHandler(ActionEvent.ACTION, consumeHandler);
        trail.removeEventHandler(ActionEvent.ACTION, consumeHandler);
        consumeHandler = null;

        Bindings.unbindContent(menu.getItems(), button.getMenuItems());
        menu.uninstall();

        super.dispose();
    }
}
