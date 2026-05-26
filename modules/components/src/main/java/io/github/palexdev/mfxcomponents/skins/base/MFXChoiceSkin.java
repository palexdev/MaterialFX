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

package io.github.palexdev.mfxcomponents.skins.base;

import java.util.Optional;
import java.util.function.Function;

import io.github.palexdev.mfxcomponents.controls.base.MFXChoice;
import io.github.palexdev.mfxcomponents.popups.ExtendedPopoverConfig;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.popups.MFXPopover;
import io.github.palexdev.mfxcore.popups.MFXPopups;
import io.github.palexdev.mfxcore.popups.PopupState;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.virtualizedfx.base.VFXContext;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.controls.VFXScrollPane;
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums.ScrollBarPolicy;
import io.github.palexdev.virtualizedfx.list.VFXList;
import io.github.palexdev.virtualizedfx.utils.ScrollParams;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Base skin containing common properties and behavior for all controls based on [MFXChoice].
///
/// It manages the 'view cell' (see [#buildViewCell()] and [#updateViewCell()]) and the popup used to display the choices.
public abstract class MFXChoiceSkin<T> extends MFXSkinBase<MFXChoice<T>> {
    //================================================================================
    // Properties
    //================================================================================
    protected VFXCell<T> viewCell;

    protected final MFXPopover popup;
    protected double POPUP_PREF_HEIGHT = 100.0;
    protected ExtendedPopoverConfig popupConfig;

    //================================================================================
    // Constructors
    //================================================================================

    protected MFXChoiceSkin(MFXChoice<T> choice) {
        super(choice);

        popupConfig = choice.getPopupConfig();
        popup = buildPopup();

        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners:
    /// - On [MFXChoice#cellFactoryProperty()] to call [#buildViewCell()]
    /// - On [MFXChoice#popupConfigProperty()] to apply the new configuration to the popup.<br >
    /// Note: the [ExtendedPopoverConfig#styleableParent()] is always overridden to be the [#getSkinnable()] node.
    /// The popup is usually shown/hidden by an icon or a button, but the owner is not the same as the styleable parent.
    /// For styling you want the styleable parent to be the control itself so that in CSS you can do: `.my-control .popup`
    /// - On [MFXChoice#selection()] to call [#updateViewCell()]
    protected void addListeners() {
        MFXChoice<T> choice = getSkinnable();
        listeners(
            onInvalidated(choice.cellFactoryProperty())
                .then(_ -> buildViewCell())
                .executeNow(() -> choice.getCellFactory() != null),
            onInvalidated(choice.popupConfigProperty())
                .then(c -> {
                    // Override styleable parent to always be the control
                    this.popupConfig = ExtendedPopoverConfig.builder(c)
                        .styleableParent(choice)
                        .build();
                    popupConfig.apply(popup);
                    popup.getRoot().requestLayout();
                })
                .executeNow(),
            onInvalidated(choice.selection())
                .then(_ -> updateViewCell())
                .executeNow(),
            popup.onState(null, (_, s) -> onPopupState(s))
        );
    }

    /// Updates the view cell with the current selection and issues a layout request.
    ///
    /// @see #buildViewCell()
    protected void updateViewCell() {
        MFXChoice<T> choice = getSkinnable();
        if (viewCell != null) viewCell.updateItem(choice.getSelectedItem());
        choice.requestLayout();
    }

    /// **What is the view cell?**
    ///
    /// This kind of component displays available choices as a list in a popup. However, they also need to communicate
    /// to the user what choice they made. There are various approaches to do this. This skin inspires to the JavaFX way.<br >
    /// By using the same cell factory specified by the [MFXChoice#cellFactoryProperty()], we build an additional cell
    /// which I call `view cell`. This cell is not part of a virtualized container, but it's directly part of the component's
    /// hierarchy. When the selection changes, we update this cell by calling [VFXCell#updateItem(Object)].<br >
    /// This way, the render logic is the same, and the rendered data is consistent between the popup and the component.
    ///
    /// If you need to perform additional operations, or you just want to change the render logic for this specific cell,
    /// there are several ways to detect if a cell is the `view cell`:
    /// - The [Node#getUserData()] is set to `VIEW_CELL`.
    /// - The 'view-cell' style class is added on its node, so you could query the [Node#getStyleClass()] list.
    /// - The context is and will remain `null` because it will never be part of a virtualized container, [VFXCell#onCreated(VFXContext)]
    /// - The index is set to [Integer#MIN_VALUE]. Index updates are always issued before item updates by `VirtualizedFX`.
    protected void buildViewCell() {
        if (viewCell != null) viewCell.dispose();
        MFXChoice<T> choice = getSkinnable();
        Function<T, VFXCell<T>> factory = choice.getCellFactory();
        if (factory != null && (viewCell = factory.apply(choice.getSelectedItem())) != null) {
            viewCell.updateIndex(Integer.MIN_VALUE);
            Node node = viewCell.toNode();
            node.getStyleClass().add("view-cell");
            node.setUserData("VIEW_CELL");
        }
    }

    /// This is responsible for building the popup used to show the available choices.
    ///
    /// The content is a [VFXList] which cell factory is bound to [MFXChoice#cellFactoryProperty()]. The list is wrapped
    /// in a [VFXScrollPane] to enable scrolling capabilities. The scroll pane's sizes are overridden:
    /// - the width will be at least the owner's width
    /// - the height will take into account the preferred number of visible items specified by [ExtendedPopoverConfig#itemsToShow()].
    // TODO implement placeholder both for split button and popup
    protected MFXPopover buildPopup() {
        MFXChoice<T> choice = getSkinnable();
        VFXList<T, VFXCell<T>> list = new VFXList<>(choice.getItems(), null);
        list.getCellFactory().bind(choice.cellFactoryProperty());
        list.context().setLocked(ISelectionModel.class, choice.getSelectionModel());
        list.setFocusTraversable(false);
        list.preloadSkin();

        VFXScrollPane vsp = new VFXScrollPane(list) {
            @Override
            protected double computeMinWidth(double height) {
                Node owner = popup.getOwner();
                double w = super.computeMinWidth(height);
                return owner != null ? Math.max(w, LayoutUtils.snappedBoundWidth(owner)) : w;
            }

            @Override
            protected double computePrefHeight(double width) {
                int itemsToShow = Optional.ofNullable(popupConfig)
                    .map(c -> NumberUtils.clamp(c.itemsToShow(), 0, list.size()))
                    .orElse(0);
                double cellSize = list.getHelper().getTotalCellSize();
                return Math.max(POPUP_PREF_HEIGHT, itemsToShow * cellSize);
            }
        };
        vsp.setHBarPolicy(ScrollBarPolicy.NEVER);
        vsp.preloadSkin();
        ScrollParams.cells(1.5).bind(vsp, Orientation.VERTICAL);
        return MFXPopups.popover().setContent(vsp).get();
    }

    /// This method is a hook into the popup's state property.<br >
    /// By default, it's responsible for updating the [MFXChoice#openProperty()].
    protected void onPopupState(PopupState state) {
        MFXChoice<T> choice = getSkinnable();
        ((BooleanProperty) choice.openProperty()).set(state == PopupState.SHOWING || state == PopupState.SHOWN);
    }
}
