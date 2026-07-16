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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.popups.MFXPopover.PopoverConfig.Builder;
import io.github.palexdev.mfxcore.popups.MFXPopover.PopupPeer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import static io.github.palexdev.mfxcore.base.beans.Position.position;

/// Custom implementation of popovers based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to: '.root' and '.mfx-popup.'. This mimics JavaFX popups which also have the '.root' style class
/// applied. It's recommended to keep it so that if your theme defines some lookup color on the '.root' selector, it gets
/// propagated to the popovers too.
///
/// ### Definition, Usage and Features
///
/// Popovers are a type of popup appearing close to a specific UI element, usually triggered by user interaction with it,
/// such as hovering or clicking. They provide additional contextual information, options or actions related to the UI
/// element they are associated with.
///
/// Popovers are typically non-modal, and therefore they do not need to use backdrops either. They usually have
/// _light dismiss_ by design, meaning that they disappear if the user hovers out of the element that triggered it or clicks
/// out of the popover.
///
/// ### Implementation Details
///
/// `MFXPopover` uses a custom [PopupWindow] as the peer to which delegate the true actions (show, hide, position,
/// auto-fix, auto-hide, etc...). I didn't want to use that crap that is [PopupControl], so I suggest you reading [PopupPeer]'s
/// documentation to see what I came up with to compensate.
///
/// Popovers cannot be shown without an owner.
///
/// Features such as _auto-fix_ and _auto-hide_ are problematic:
/// 1) Auto fix changes the x and y coordinates of the window. Because it operates internally to the peer, the [#positionProperty()]
/// and the actual window position may end up being desynchronized. If you want to get the true window position,
/// use [#getPeerPosition()]
/// 2) Auto hide changes the visibility state internally to the peer. Because we have a custom way to represent the
/// popup's state ([#stateProperty()], and [PopupState]), it could cause a disastrous desynchronization. The good thing
/// is that such feature still hides the window by calling [Window#hide()]; therefore, we can override the logic in our
/// peer to redirect auto-hide requests to our method instead, as simple as adding a boolean flag.
public class MFXPopover extends MFXPopupBase<PopupPeer, Node> {

    //================================================================================
    // Properties
    //================================================================================

    private boolean preloaded = false;
    private WhenEvent<WindowEvent> whenOwnerHiding;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXPopover() {
        setDefaultStyleClasses();
        PopoverConfig.DEFAULT.apply(this);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to change the configuration of this popover. The provided builder starts with the values from
    /// the current config.
    public MFXPopover configure(Consumer<Builder> cfg) {
        Builder builder = PopoverConfig.builder(getConfig());
        cfg.accept(builder);
        builder.build().apply(this);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected PopupPeer buildPeer() {
        return new PopupPeer();
    }

    @Override
    protected void doShow(Node owner, double x, double y) {
        // We need this as a workaround because for some reason when showing/hiding the popover at a very fast rate,
        // it causes the state property to no update, leaving the popover unable to show anymore
        When.onInvalidated(peer.showingProperty())
            .condition(s -> !s)
            .then(_ -> setState(PopupState.HIDDEN))
            .oneShot()
            .listen();

        this.owner = owner;
        Node content = getContent();
        content.setVisible(false);
        if (!preloaded) {
            peer.root.applyCss();
            peer.root.layout();
            preloaded = true;
        }

        peer.show(owner, x, y);
        positionProperty().setPosition(x, y);

        if (placement != null) {
            // Re-compute position
            Position pos = computePosition(owner, placement);
            setPosition(pos);
        }

        // When the owner's window starts hiding, JavaFX would otherwise reach us via the tree-showing
        // cascade only after the popup's native peer has already been torn down — producing the
        // 'window has already been closed' crash plus dangling pulse exceptions. Filtering WINDOW_HIDING
        // (which fires *before* the destructive cascade) lets us finalize the popup synchronously while
        // every native peer in the chain is still alive.
        Optional.ofNullable(peer.getOwnerWindow()).ifPresent(w ->
            whenOwnerHiding = WhenEvent.intercept(w, WindowEvent.WINDOW_HIDING)
                .handle(_ -> {
                    if (isShowing()) setState(PopupState.HIDING);
                    placement = null;
                    this.owner = null;
                    hidePeer();
                    setState(PopupState.HIDDEN);
                })
                .asFilter()
                .oneShot()
                .register()
        );

        content.setVisible(true);
        setState(PopupState.SHOWN);
    }

    @Override
    protected void hidePeer() {
        WhenEvent.dispose(whenOwnerHiding);
        whenOwnerHiding = null;
        peer.directHide = true;
        try {
            peer.hide();
        } catch (IllegalStateException ignored) {
            // Owner stage already disposed the native peer — there's nothing left to tear down.
        }
    }

    /// If the given placement is `null` returns a position of `<0, 0>`.
    ///
    /// Otherwise, the computation is delegated to the handlers in [AnchorHandlers].
    @Override
    protected Position computePosition(Node owner, Placement placement) {
        if (placement == null) return Position.origin();
        return AnchorHandlers.handler(placement.anchor())
            .compute(owner, getRoot(), placement.xDirection(), placement.yDirection(), getOffset());
    }

    @Override
    protected void onContentChanged() {
        Node content = getContent();
        peer.setContent(content);
    }

    @Override
    protected void preShowCheck(Node owner) {
        if (owner == null) throw new IllegalStateException("Owner cannot be null");
        if (getContent() == null) throw new IllegalStateException("Content cannot be null");
    }

    @Override
    public Position getPeerPosition() {
        return position(peer.getX(), peer.getY());
    }

    @Override
    public void setStyleClass(String... styleClass) {
        peer.setStyleClass(styleClass);
    }

    @Override
    public PopoverConfig getConfig() {
        return ((PopoverConfig) config);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("root", "mfx-popover");
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    // Peer

    /// Custom extension of [PopupWindow] which serves as the peer for [MFXPopover].
    ///
    /// Sets the root to a [PopupRoot].
    protected class PopupPeer extends PopupWindow implements Peer {
        private final PopupRoot root = new PopupRoot();
        boolean directHide = false;

        {
            getScene().setRoot(root);
            // this is a crucial configuration
            // it makes so that during layout/position computations it doesn't take into account effects/transforms
            // so that the positions computed by AnchorHandler are exact, and effects like drop-shadows
            // are visualized correctly (no cut/clip)
            setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
        }

        @Override
        public void hide() {
            if (directHide) {
                directHide = false;
                super.hide();
                return;
            }
            // JavaFX initiated this (auto-hide, hide-on-escape, tree-showing cascade).
            // Redirect to the popover's animated hide so our logic is honored end-to-end.
            MFXPopover.this.hide();
            setState(PopupState.AUTO_HIDE);
        }

        @Override
        public PopupRoot getRoot() {
            return root;
        }
    }

    // Config

    public record PopoverConfig(
        Position offset,
        Node styleableParent,
        boolean autoFix,
        boolean autoHide,
        boolean hideOnEscape,
        boolean consumeAutoHideEvents
    ) implements Config<MFXPopover> {
        public static final PopoverConfig DEFAULT = builder().build();

        @Override
        public void apply(MFXPopover popup) {
            popup.offset = offset;
            popup.setStyleableParent(styleableParent);
            popup.peer.setAutoFix(autoFix);
            popup.peer.setAutoHide(autoHide);
            popup.peer.setHideOnEscape(hideOnEscape);
            popup.peer.setConsumeAutoHidingEvents(consumeAutoHideEvents);
            popup.config = this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(PopoverConfig config) {
            return new Builder()
                .offset(config.offset)
                .styleableParent(config.styleableParent)
                .autoFix(config.autoFix)
                .autoHide(config.autoHide)
                .hideOnEscape(config.hideOnEscape)
                .consumeAutoHideEvents(config.consumeAutoHideEvents);
        }

        public static class Builder {
            private Position offset = Position.origin();
            private Node styleableParent;
            private boolean autoFix = true;
            private boolean autoHide = true;
            private boolean hideOnEscape = true;
            private boolean consumeAutoHideEvents = false;

            public Builder offset(Position offset) {
                this.offset = offset;
                return this;
            }

            public Builder styleableParent(Node styleableParent) {
                this.styleableParent = styleableParent;
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

            public PopoverConfig build() {
                return new PopoverConfig(
                    offset,
                    styleableParent,
                    autoFix,
                    autoHide,
                    hideOnEscape,
                    consumeAutoHideEvents
                );
            }
        }
    }
}
