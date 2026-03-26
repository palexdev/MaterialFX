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
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.popups.menu.MenuBuilder.CheckMenuBuilder;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/// Specialization of [MFXMenuItemSkin] to add selectable options in [MFXMenu].
/// Implements [Selectable], has an additional style class which is `.check` (so the complete selector is
/// `.mfx-menu-item.check`), and an additional default stylesheet to setup the checkmark svg icon (with style class `.mark`).
///
/// It's important to note that this type of item has a series of limitations and behavior changes:
/// - It does not support showing submenus, technically possible but a design choice
/// - You can't specify an icon through the [#graphicProperty()]. The value will be ignored as the slot is reserved for
/// the checkmark icon
/// - Triggering the item (via mouse click or keys) does change the selection state of the item, which in turn calls the
/// set [#actionProperty()] (so the action runs only if the selection changes!!). Unlike the standard icon, running the
/// action does not close the menu
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
          -fx-pref-height: 12px;
          -fx-max-height: 12px;
          -fx-shape: "M530.8 134.1C545.1 144.5 548.3 164.5 537.9 178.8L281.9 530.8C276.4 538.4 267.9 543.1 258.5 543.9C249.1 544.7 240 541.2 233.4 534.6L105.4 406.6C92.9 394.1 92.9 373.8 105.4 361.3C117.9 348.8 138.2 348.8 150.7 361.3L252.2 462.8L486.2 141.1C496.6 126.8 516.6 123.6 530.9 134z";
        }
        """).toDataUri();

    //================================================================================
    // Properties
    //================================================================================

    private final SelectionProperty selected = new SelectionProperty(this) {
        @Override
        protected void onInvalidated() {
            Runnable action = getAction();
            if (action != null) action.run();
        }
    };
    private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);

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

    //================================================================================
    // Inner Classes
    //================================================================================

    public static class MFXCheckMenuItemSkin extends MFXMenuItemSkin {

        public MFXCheckMenuItemSkin(MFXCheckMenuItem item) {
            super(item);
            Region checkmark = new Region();
            checkmark.getStyleClass().add("mark");
            checkmark.visibleProperty().bind(item.selectedProperty());
            iconContainer.getChildren().setAll(checkmark);
        }

        @Override
        protected void updateIcon() {}

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
        protected void runAction() {
            MFXCheckMenuItem item = getNodeAs(MFXCheckMenuItem.class);
            item.setSelected(!item.isSelected());
        }
    }
}
