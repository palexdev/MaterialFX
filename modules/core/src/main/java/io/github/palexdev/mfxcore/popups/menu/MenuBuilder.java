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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.input.KeyStroke;
import io.github.palexdev.mfxcore.popups.MFXPopups;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.FXCollectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/// Convenience class to build [MFXMenus][MFXMenu] in a DSL-like way. Should be used in conjunction with
/// [MFXPopups#menu()].
public class MenuBuilder {

    //================================================================================
    // Properties
    //================================================================================

    private Node graphic;
    private String text;
    private KeyStroke shortcut;
    private Runnable action;
    private ObservableList<MFXMenuItem> subItems = FXCollections.observableArrayList();
    private Consumer<MFXMenuItem> cfg;

    //================================================================================
    // Static Methods
    //================================================================================

    /// A special builder which constructs a special empty [MFXMenuItem], not focusable, without skin and behavior
    /// and with style class set to `.separator`.
    public static MenuBuilder separator() {
        return new MenuBuilder() {
            @Override
            public MFXMenuItem build() {
                return new MFXMenuItem(null) {
                    {
                        setFocusTraversable(false);
                    }

                    @Override
                    public List<String> defaultStyleClasses() {
                        return List.of("menu-separator");
                    }

                    @Override
                    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
                        return null;
                    }

                    @Override
                    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
                        return () -> new MFXSkinBase<MFXMenuItem>(this) {
                            @Override
                            protected void registerBehavior() {}
                        };
                    }
                };
            }
        };
    }

    //================================================================================
    // Methods
    //================================================================================

    protected MFXMenuItem create() {
        return new MFXMenuItem();
    }

    public MenuBuilder graphic(Node graphic) {
        this.graphic = graphic;
        return this;
    }

    public MenuBuilder text(String text) {
        this.text = text;
        return this;
    }

    public MenuBuilder shortcut(KeyStroke shortcut) {
        this.shortcut = shortcut;
        return this;
    }

    /// The string should follow the format indicated [here][KeyStroke#fromString(String)].
    public MenuBuilder shortcut(String shortcut) {
        this.shortcut = KeyStroke.fromString(shortcut);
        return this;
    }

    public MenuBuilder action(Runnable action) {
        this.action = action;
        return this;
    }

    public MenuBuilder subItems(MenuBuilder... builders) {
        this.subItems = Arrays.stream(builders)
            .map(MenuBuilder::build)
            .collect(FXCollectors.toList());
        return this;
    }

    public MenuBuilder subItems(MFXMenuItem... items) {
        this.subItems = FXCollections.observableArrayList(items);
        return this;
    }

    /// This method can be used to further customize the build [MFXMenuItem].
    ///
    /// For example, one may want to disable the item when a certain condition is not met:
    /// ```
    /// .config(it -> it.disableProperty().bind(myConditionProperty().not())
    /// ```
    public MenuBuilder config(Consumer<MFXMenuItem> cfg) {
        this.cfg = cfg;
        return this;
    }

    public MFXMenuItem build() {
        MFXMenuItem item = create();
        item.setText(text);
        item.setGraphic(graphic);
        item.setShortcut(shortcut);
        item.setAction(action);
        item.getSubItems().addAll(subItems);
        if (cfg != null) cfg.accept(item);
        return item;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    public static class CheckMenuBuilder extends MenuBuilder {
        private boolean selected = false;
        private SelectionGroup group;
        private Consumer<Boolean> onSelectionChanged;
        private boolean closeOnAction = false;

        public CheckMenuBuilder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public CheckMenuBuilder group(SelectionGroup group) {
            this.group = group;
            return this;
        }

        public CheckMenuBuilder onSelectionChanged(Consumer<Boolean> onSelectionChanged) {
            this.onSelectionChanged = onSelectionChanged;
            return this;
        }

        public CheckMenuBuilder onSelected(Runnable onSelected) {
            return onSelectionChanged(s -> {if (s) onSelected.run();});
        }

        public CheckMenuBuilder closeOnAction(boolean closeOnAction) {
            this.closeOnAction = closeOnAction;
            return this;
        }

        @Override
        protected MFXCheckMenuItem create() {
            return new MFXCheckMenuItem();
        }

        @Override
        public MFXCheckMenuItem build() {
            MFXCheckMenuItem item = (MFXCheckMenuItem) super.build();
            if (!item.selectedProperty().isBound()) item.setSelected(selected);
            item.setSelectionGroup(group);
            item.onSelectionChanged(onSelectionChanged);
            item.setCloseOnAction(closeOnAction);
            return item;
        }
    }
}
