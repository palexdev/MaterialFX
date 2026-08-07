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

package io.github.palexdev.mfxcomponents.popups;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.popups.MFXPopover;
import io.github.palexdev.mfxcore.popups.MFXPopover.PopoverConfig;
import io.github.palexdev.mfxcore.popups.MFXPopup;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu.MenuConfig;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Direction;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.geometry.Pos;
import javafx.scene.Node;

/// An extension of [PopoverConfig] that incorporates some of the settings from [MenuConfig].
///
/// Some controls may need menu capabilities but using generic popovers.
public record ExtendedPopoverConfig(
    Placement placement,
    Position offset,
    boolean autoFix,
    boolean autoHide,
    boolean hideOnEscape,
    boolean consumeAutoHideEvents,
    Node styleableParent,
    int itemsToShow
) implements MFXPopup.Config<MFXPopover> {
    public static final ExtendedPopoverConfig DEFAULT = builder().build();

    @Override
    public void apply(MFXPopover popup) {
        new PopoverConfig(
            offset,
            styleableParent,
            autoFix,
            autoHide,
            hideOnEscape,
            consumeAutoHideEvents
        ).apply(popup);
        // The rest of the settings are managed directly by the UI component using the popover with this config
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExtendedPopoverConfig config) {
        return new Builder()
            .placement(config.placement)
            .offset(config.offset)
            .autoFix(config.autoFix)
            .autoHide(config.autoHide)
            .hideOnEscape(config.hideOnEscape)
            .consumeAutoHideEvents(config.consumeAutoHideEvents)
            .styleableParent(config.styleableParent);
    }

    public static class Builder {
        private Placement placement = Placement.placement(Pos.BOTTOM_LEFT, Direction.AFTER, Direction.AFTER);
        private Position offset = Position.origin();
        private boolean autoFix = true;
        private boolean autoHide = true;
        private boolean hideOnEscape = true;
        private boolean consumeAutoHideEvents = false;
        private Node styleableParent;
        private int itemsToShow = 5;

        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder offset(Position offset) {
            this.offset = offset;
            return this;
        }

        public Builder autoFix(boolean autoFix) {
            this.autoFix = autoFix;
            return this;
        }

        public Builder autoHide(boolean autoHide) {
            this.autoHide = autoHide;
            return this;
        }

        public Builder hideOnEscape(boolean hideOnEscape) {
            this.hideOnEscape = hideOnEscape;
            return this;
        }

        public Builder consumeAutoHideEvents(boolean consumeAutoHideEvents) {
            this.consumeAutoHideEvents = consumeAutoHideEvents;
            return this;
        }

        public Builder styleableParent(Node styleableParent) {
            this.styleableParent = styleableParent;
            return this;
        }

        public Builder itemsToShow(int itemsToShow) {
            this.itemsToShow = itemsToShow;
            return this;
        }

        public ExtendedPopoverConfig build() {
            return new ExtendedPopoverConfig(
                placement,
                offset,
                autoFix,
                autoHide,
                hideOnEscape,
                consumeAutoHideEvents,
                styleableParent,
                itemsToShow
            );
        }
    }
}
