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

package io.github.palexdev.mfxcore.popups.menu;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.popups.menu.MenuBuilder.CheckMenuBuilder;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/// Specialization of [MFXMenuItem] to add selectable options in [MFXMenu].
/// Implements [Selectable], has an additional style class which is `.check` (so the complete selector is
/// `.mfx-menu-item.check`), and an additional default stylesheet to setup the checkmark svg icon (with style class `.mark`).
///
/// It's important to note that this type of item has a series of limitations and behavior changes:
/// - It does not support showing submenus, technically possible but a design choice
/// - The icon slot is shared between the [#graphicProperty()] and the checkmark: the graphic is shown while the item is
///   unselected and replaced by the checkmark once selected. Both nodes are kept in the slot, which is therefore sized on
///   the largest of the two, so that toggling the selection does not change the layout
/// - Triggering the item (via mouse click or keys) does change the selection state of the item (if not bound),
///   and runs the action specified by the [#actionProperty()].
///   If you want to run an action specifically when the selection changes, use the [#onSelectionChanged(Consumer)]
///   callback instead.<br >
///   Unlike the standard item, by default running the action does not close the menu.
///   You can change the behavior through [#setCloseOnAction(boolean)]
///
/// Such changes are defined in its default skin and behavior implementations: [MFXCheckMenuItemSkin] and [MFXCheckMenuItemBehavior]
/// respectively.
public class MFXCheckMenuItem extends MFXMenuItem implements Selectable {

    //================================================================================
    // Static Properties
    //================================================================================

    private static final String DEFAULT_CSS = new CSSFragment("""
        .mfx-menu-item.check .mark {
          -fx-background-color: rgba(0, 0, 0, 0.87);
          -fx-pref-width: 12px;
          -fx-pref-height: 8px;
          -fx-max-height: 8px;
          -fx-shape: "M7.507,17.885l-5.6-5.6c-0.91-0.91-0.91-2.386,0-3.296l0,0c0.91-0.91,2.386-0.91,3.296,0l4.375,4.374	c0.349,0.349,0.916,0.349,1.265,0l7.954-7.954c0.91-0.91,2.386-0.91,3.296,0l0,0c0.91,0.91,0.91,2.386,0,3.296l-9.18,9.18	C11.427,19.372,8.994,19.372,7.507,17.885z";
        }
        """).toDataUri();

    //================================================================================
    // Properties
    //================================================================================

    private final SelectionProperty selected = new SelectionProperty(this) {
        @Override
        protected void onInvalidated() {
            PseudoClasses.SELECTED.setOn(MFXCheckMenuItem.this, get());
            onSelectionChanged.accept(get());
        }
    };
    private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);
    private Consumer<Boolean> onSelectionChanged = _ -> {};
    private boolean closeOnAction = false;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXCheckMenuItem() {
        this("");
    }

    public MFXCheckMenuItem(String text) {
        this(text, null);
    }

    public MFXCheckMenuItem(String text, Node graphic) {
        super(text, graphic);
        getStylesheets().add(DEFAULT_CSS);
    }

    public static CheckMenuBuilder checkMenuItem(String text) {
        return (CheckMenuBuilder) new CheckMenuBuilder().text(text);
    }

    public static CheckMenuBuilder checkMenuItem(String text, Node graphic) {
        return (CheckMenuBuilder) new CheckMenuBuilder().text(text).graphic(graphic);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXCheckMenuItemSkin(this);
    }

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXCheckMenuItemBehavior(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.extend(super.defaultStyleClasses(), "check");
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    @Override
    public SelectionProperty selectedProperty() {
        return selected;
    }

    @Override
    public SelectionGroupProperty selectionGroupProperty() {
        return selectionGroup;
    }

    @Override
    public void onSelectionChanged(Consumer<Boolean> onSelectionChanged) {
        this.onSelectionChanged = Optional.ofNullable(onSelectionChanged).orElse(_ -> {});
    }

    public boolean isCloseOnAction() {
        return closeOnAction;
    }

    public void setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    public static class MFXCheckMenuItemSkin extends MFXMenuItemSkin {

        private final Region checkmark;

        public MFXCheckMenuItemSkin(MFXCheckMenuItem item) {
            checkmark = new Region();
            super(item);
            checkmark.getStyleClass().add("mark");
            checkmark.visibleProperty().bind(item.selectedProperty());
            iconContainer.getChildren().addFirst(checkmark);
        }

        @Override
        protected void updateIcon(Node oldIcon, Node newIcon) {
            if (oldIcon != null) {
                oldIcon.visibleProperty().unbind();
                iconContainer.getChildren().remove(oldIcon);
            }
            if (newIcon != null) {
                newIcon.visibleProperty().bind(checkmark.visibleProperty().not());
                iconContainer.getChildren().add(newIcon);
            }
        }

        @Override
        protected void handleSubMenu() {}

        @Override
        protected MFXCheckMenuItem getControl() {
            return (MFXCheckMenuItem) super.getControl();
        }
    }

    public static class MFXCheckMenuItemBehavior extends MFXMenuItemBehavior {

        public MFXCheckMenuItemBehavior(MFXCheckMenuItem item) {
            super(item);
        }

        @Override
        public void runAction() {
            MFXCheckMenuItem item = getNodeAs(MFXCheckMenuItem.class);
            if (item.isDisabled()) return;
            item.toggle();
            if (item.getAction() != null) {
                item.getAction().run();
            }

            // if the menu is not available, it means the action was probably run "manually", without the menu being visible.
            if (item.isCloseOnAction())
                Optional.ofNullable(item.getMenu())
                    .map(MFXMenu::getRootMenu)
                    .ifPresent(MFXMenu::hide);
        }
    }
}
