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

    public static ShowBuilder<Window, MFXDialog> dialog() {
        return new ShowBuilder<>(new MFXDialog(), null);
    }

    public static ShowBuilder<Window, MFXDialog> dialog(Consumer<MFXDialog.DialogConfig.Builder> config) {
        MFXDialog.DialogConfig.Builder builder = MFXDialog.DialogConfig.builder();
        config.accept(builder);
        return new ShowBuilder<>(new MFXDialog(), builder.build());
    }

    public static ShowBuilder<Node, MFXPopover> popover() {
        return new ShowBuilder<>(new MFXPopover(), null);
    }

    public static ShowBuilder<Node, MFXPopover> popover(Consumer<MFXPopover.PopoverConfig.Builder> config) {
        MFXPopover.PopoverConfig.Builder builder = MFXPopover.PopoverConfig.builder();
        config.accept(builder);
        return new ShowBuilder<>(new MFXPopover(), builder.build());
    }

    public static TooltipBuilder tooltip() {
        return new TooltipBuilder(new MFXTooltip(), null);
    }

    public static TooltipBuilder tooltip(Consumer<MFXTooltip.TooltipConfig.Builder> config) {
        MFXTooltip.TooltipConfig.Builder builder = MFXTooltip.TooltipConfig.builder();
        config.accept(builder);
        return new TooltipBuilder(new MFXTooltip(), builder.build());
    }

    public static MenuPopupBuilder menu() {
        return new MenuPopupBuilder(new MFXMenu(), null);
    }

    public static MenuPopupBuilder menu(Consumer<MFXMenu.MenuConfig.Builder> config) {
        MFXMenu.MenuConfig.Builder builder = MFXMenu.MenuConfig.builder();
        config.accept(builder);
        return new MenuPopupBuilder(new MFXMenu(), builder.build());
    }

    //================================================================================
    // Builders
    //================================================================================

    /// Base class for all the popup builders, carries what every [MFXPopup] has in common.
    abstract static class Builder<O, P extends MFXPopup<O>, B extends Builder<O, P, B>> {
        protected final P popup;

        protected Builder(P popup, MFXPopup.Config<P> config) {
            this.popup = popup;
            if (config != null) config.apply(popup);
        }

        /// @return this builder as its concrete type `B`
        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B setContent(Node content) {
            popup.setContent(content);
            return self();
        }

        /// Variant of [#setContent(Node)] for contents that need the popup itself to be built.
        public B setContent(Function<P, Node> contentFn) {
            popup.setContent(contentFn.apply(popup));
            return self();
        }

        public B setOffset(Position offset) {
            popup.setOffset(offset);
            return self();
        }

        public B setStyleClass(String... styleClass) {
            popup.setStyleClass(styleClass);
            return self();
        }

        /// @return the built popup, without showing nor installing it
        public P get() {
            return popup;
        }
    }

    /// Builder for popups that are shown on demand: [MFXDialog] and [MFXPopover].
    public static class ShowBuilder<O, P extends MFXPopup<O>> extends Builder<O, P, ShowBuilder<O, P>> {

        private ShowBuilder(P popup, MFXPopup.Config<P> config) {
            super(popup, config);
        }

        /// Shows the popup, see [MFXPopup#show(Object, double, double)].
        ///
        /// @return the built popup
        public P show(O owner, double x, double y) {
            popup.show(owner, x, y);
            return popup;
        }

        /// Shows the popup at the given [Placement], see [MFXPopup#show(Object, Placement)].
        ///
        /// @return the built popup
        public P show(O owner, Placement placement) {
            popup.show(owner, placement);
            return popup;
        }
    }

    /// Builder for [MFXTooltips][MFXTooltip].
    public static class TooltipBuilder extends Builder<Node, MFXTooltip, TooltipBuilder> {

        private TooltipBuilder(MFXTooltip popup, MFXPopup.Config<MFXTooltip> config) {
            super(popup, config);
        }

        /// Installs the tooltip on the given owner, see [MFXTooltip#install(Node)].
        ///
        /// @return the built tooltip
        public MFXTooltip install(Node owner) {
            popup.install(owner);
            return popup;
        }
    }

    /// Builder for [MFXMenus][MFXMenu].
    public static class MenuPopupBuilder extends Builder<Node, MFXMenu, MenuPopupBuilder> {

        private MenuPopupBuilder(MFXMenu popup, MFXPopup.Config<MFXMenu> config) {
            super(popup, config);
        }

        public MenuPopupBuilder addItems(MFXMenuItem... items) {
            popup.getItems().addAll(items);
            return this;
        }

        /// Variant of [#addItems(MFXMenuItem...)] which builds the items from the given [MenuBuilders][MenuBuilder].
        public MenuPopupBuilder addItems(MenuBuilder... builders) {
            return addItems(Arrays.stream(builders)
                .map(MenuBuilder::build)
                .toArray(MFXMenuItem[]::new));
        }

        public MenuPopupBuilder setItems(List<MFXMenuItem> items) {
            popup.getItems().setAll(items);
            return this;
        }

        public MenuPopupBuilder setSubMenuFactory(Function<ObservableList<MFXMenuItem>, MFXMenu> factory) {
            popup.setSubMenuFactory(factory);
            return this;
        }

        /// Installs the menu on the given owner, see [MFXMenu#install(Node)].
        ///
        /// @return the built menu
        public MFXMenu install(Node owner) {
            popup.install(owner);
            return popup;
        }
    }
}
