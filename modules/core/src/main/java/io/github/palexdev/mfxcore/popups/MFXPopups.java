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

package io.github.palexdev.mfxcore.popups;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.popups.menu.MenuBuilder;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Window;

/// A nice, convenient facade over the [MFXPopups][MFXPopup] API.
///
/// You can easily build, configure and show any kind of [MFXPopup] in a type-safe and declarative way.
public class MFXPopups {

    //================================================================================
    // Constructors
    //================================================================================
    private MFXPopups() {}

    //================================================================================
    // Static Methods
    //================================================================================

    public static Builder<Window, MFXDialog> dialog() {
        return new Builder<>(new MFXDialog(), null);
    }

    public static Builder<Window, MFXDialog> dialog(Consumer<MFXDialog.DialogConfig.Builder> config) {
        MFXDialog.DialogConfig.Builder builder = MFXDialog.DialogConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXDialog(), builder.build());
    }

    public static Builder<Node, MFXPopover> popover() {
        return new Builder<>(new MFXPopover(), null);
    }

    public static Builder<Node, MFXPopover> popover(Consumer<MFXPopover.PopoverConfig.Builder> config) {
        MFXPopover.PopoverConfig.Builder builder = MFXPopover.PopoverConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXPopover(), builder.build());
    }

    public static Builder<Node, MFXTooltip> tooltip() {
        return new Builder<>(new MFXTooltip(), null);
    }

    public static Builder<Node, MFXTooltip> tooltip(Consumer<MFXTooltip.TooltipConfig.Builder> config) {
        MFXTooltip.TooltipConfig.Builder builder = MFXTooltip.TooltipConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXTooltip(), builder.build());
    }

    public static Builder<Node, MFXMenu> menu() {
        return new Builder<>(new MFXMenu(), null);
    }

    public static Builder<Node, MFXMenu> menu(Consumer<MFXMenu.MenuConfig.Builder> config) {
        MFXMenu.MenuConfig.Builder builder = MFXMenu.MenuConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXMenu(), builder.build());
    }

    //================================================================================
    // Builder
    //================================================================================
    public static class Builder<O, P extends MFXPopup<O>> {
        private final P popup;

        private Builder(P popup, MFXPopup.Config<P> config) {
            this.popup = popup;
            if (config != null) config.apply(popup);
        }

        public Builder<O, P> setContent(Node content) {
            popup.setContent(content);
            return this;
        }

        public Builder<O, P> setContent(Function<P, Node> contentFn) {
            popup.setContent(contentFn.apply(popup));
            return this;
        }

        public Builder<O, P> setOffset(Position offset) {
            popup.setOffset(offset);
            return this;
        }

        public Builder<O, P> setStyleClass(String... styleClass) {
            popup.setStyleClass(styleClass);
            return this;
        }

        /// Node: works only for [MFXMenus][MFXMenu].
        public Builder<O, P> addMenuItems(MFXMenuItem... items) {
            if (popup instanceof MFXMenu m) {
                m.getItems().addAll(items);
            }
            return this;
        }

        public Builder<O, P> addMenuItems(MenuBuilder... builders) {
            return addMenuItems(Arrays.stream(builders)
                .map(MenuBuilder::build)
                .toArray(MFXMenuItem[]::new));
        }

        /// Node: works only for [MFXMenus][MFXMenu].
        public Builder<O, P> setMenuItems(List<MFXMenuItem> items) {
            if (popup instanceof MFXMenu m) {
                m.getItems().setAll(items);
            }
            return this;
        }

        /// Node: works only for [MFXMenus][MFXMenu].
        public Builder<O, P> setSubMenuFactory(Function<ObservableList<MFXMenuItem>, MFXMenu> factory) {
            if (popup instanceof MFXMenu m) {
                m.setSubMenuFactory(factory);
            }
            return this;
        }

        /// Note: for tooltips and menus this will call [MFXTooltip#install(Node)] and [MFXMenu#install(Node)]
        /// respectively!
        public P show(O owner, double x, double y) {
            if (popup instanceof MFXTooltip t) {
                t.install(((Node) owner));
                return popup;
            }
            if (popup instanceof MFXMenu m) {
                m.install(((Node) owner));
                return popup;
            }
            popup.show(owner, x, y);
            return popup;
        }

        /// Note: for tooltips and menus this will call [MFXTooltip#install(Node)] and [MFXMenu#install(Node)]
        /// respectively. The placement is overridden with the given one!
        public P show(O owner, Placement placement) {
            if (popup instanceof MFXTooltip t) {
                t.install(((Node) owner));
                MFXTooltip.TooltipConfig.builder(t.getConfig())
                    .placement(placement)
                    .build()
                    .apply(t);
                return popup;
            }
            if (popup instanceof MFXMenu m) {
                m.install(((Node) owner));
                MFXMenu.MenuConfig.builder(m.getConfig())
                    .placement(placement)
                    .build()
                    .apply(m);
                return popup;
            }
            popup.show(owner, placement);
            return popup;
        }

        public P get() {
            return popup;
        }
    }
}
