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

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.popups.MFXPopup.Peer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Window;

/// Abstract implementation of [MFXPopup] to contain properties and behaviors that are common to all kinds of popups.
///
/// Implements [MFXStyleable] and delegates all [Styleable] methods to the [#getRoot()] node.
///
/// @param <O> the popup's owner type
/// @param <P> the popup's peer type, which is some kind of [Window]
/// @see PopupRoot
public abstract class MFXPopupBase<P extends Window & Peer, O> implements MFXPopup<O>, MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    protected P peer;
    protected O owner;
    protected Placement placement;
    protected Position offset = Position.origin();
    protected PopupAnimation animation = new PopupAnimation(PopupAnimationFunction.FADE);

    private final NodeProperty content = new NodeProperty() {
        @Override
        protected void invalidated() {
            Node content = get();
            if (content != null && animation != null) animation.init(MFXPopupBase.this, content);
            onContentChanged();
        }
    };
    private final PositionProperty position = new PositionProperty(Position.origin()) {
        @Override
        protected void invalidated() {
            peer.setX(getX());
            peer.setY(getY());
        }
    };
    private final ReadOnlyObjectWrapper<PopupState> state = new ReadOnlyObjectWrapper<>(PopupState.HIDDEN);

    protected Config<? extends MFXPopup<O>> config;

    //================================================================================
    // Constructors
    //================================================================================
    protected MFXPopupBase() {
        this.peer = buildPeer();
        if (animation != null) animation.init(this, peer.getScene().getRoot());
        setDefaultStyleClasses();
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /// This is responsible for building the popup's peer, to which delegate the effective show/hide logic
    /// (as well as some other features specific to each kind).
    protected abstract P buildPeer();

    /// This is the method responsible for effectively delegating to the peer and showing the popup's window.
    /// The public show methods invoke this after all checks are passed and essential setup is done.
    ///
    /// @see #preShowCheck(Object)
    protected abstract void doShow(O owner, double x, double y);

    /// This is responsible for computing the popup's window position relative to the given owner and according to the
    /// given [placement][Placement]
    ///
    /// Implementations should take into account the offset returned by [#getOffset()].
    protected abstract Position computePosition(O owner, Placement placement);

    /// Why this exists: every popup kind eventually needs to delegate to the underlying JavaFX [Window#hide()],
    /// but most implementations route incoming hide requests through their own animated flow first (so that
    /// auto-hide, hide-on-escape, and tree-showing cascades can be intercepted and decorated with animations).
    /// That redirect creates a chicken-and-egg problem when the framework itself wants to finalize the hide:
    /// calling the peer's `hide()` would just bounce back into our own logic again.
    ///
    /// `hidePeer()` is the single, well-defined escape hatch out of that loop. Concentrating the "actually take
    /// the popup down" responsibility in one overridable method also lets each implementation handle peer-specific
    /// teardown concerns (its own bypass-redirect mechanism, native-peer-already-dead races, listener cleanup)
    /// without leaking those concerns into [#hide()] or into the public [MFXPopup.Peer] contract.
    protected abstract void hidePeer();

    //================================================================================
    // Methods
    //================================================================================

    /// This method should be used by public show methods to check that everything is ok before actually showing the popup.
    ///
    /// Each implementation may perform different, specific checks.
    protected void preShowCheck(O owner) {}

    /// Hook to the [#contentProperty()] triggered when the content is changed. This can be used by implementations to
    /// update the peer's content, as well as other actions if needed.
    protected void onContentChanged() {}

    /// Delegate to [peer.getRoot().updateStylesheets()][PopupRoot#updateStylesheets()].
    public void updateStylesheets() {
        peer.getRoot().updateStylesheets();
    }

    /// Delegate to [peer.getRoot().setStyleableParent(Node)][PopupRoot#setStyleableParent(Node)].
    public void setStyleableParent(Node node) {
        peer.getRoot().setStyleableParent(node);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void show(O owner, double x, double y) {
        if (isShowing()) return;
        preShowCheck(owner);
        setState(PopupState.SHOWING);
        Position offset = getOffset();
        doShow(owner, x + offset.x(), y + offset.y());
    }

    @Override
    public void show(O owner, Placement placement) {
        if (isShowing()) return;
        preShowCheck(owner);
        setState(PopupState.SHOWING);
        this.placement = placement;
        Position p = computePosition(owner, placement);
        doShow(owner, p.x(), p.y());
    }

    @Override
    public void hide() {
        if (!isShowing()) return;
        if (animation != null) {
            animation.stop();
        }
        setState(PopupState.HIDING);
        placement = null;
        owner = null;

        if (animation != null) {
            animation.playOut(_ -> {
                    hidePeer();
                    setState(PopupState.HIDDEN);
                }
            );
            return;
        }

        hidePeer();
        setState(PopupState.HIDDEN);
    }

    /// Simply calls [#computePosition(Object, Placement)] to re-compute the position and then sets the [#positionProperty()].
    ///
    /// Note that this will work only if the popup was shown with an anchor using [#show(Object, Placement)].
    /// Technically, it depends on the implementation of [#computePosition(Object, Placement)], so make sure to read both
    /// [MFXDialog#computePosition(Window, Placement)] and [MFXPopover#computePosition(Node, Placement)].
    @Override
    public void reposition() {
        Position pos = computePosition(owner, placement);
        setPosition(pos);
    }

    @Override
    public O getOwner() {
        return owner;
    }

    /// {@inheritDoc}
    ///
    /// Delegates to [Peer#getRoot()].
    public Parent getRoot() {
        return peer.getRoot();
    }

    @Override
    public NodeProperty contentProperty() {
        return content;
    }

    @Override
    public PositionProperty positionProperty() {
        return position;
    }

    @Override
    public Position getOffset() {
        return Optional.ofNullable(offset).orElse(Position.origin());
    }

    @Override
    public void setOffset(Position offset) {
        this.offset = offset;
    }

    @Override
    public ReadOnlyObjectWrapper<PopupState> stateProperty() {
        return state;
    }

    protected void setState(PopupState state) {
        this.state.set(state);
    }

    @Override
    public PopupAnimation getAnimation() {
        return animation;
    }

    @Override
    public void setAnimation(PopupAnimation animation) {
        if (this.animation != null) {
            this.animation.stop();
            this.animation.reset(peer.getScene().getRoot());
        }
        this.animation = animation;
        if (this.animation != null) {
            this.animation.init(this, peer.getScene().getRoot());
        }
    }

    @Override
    public String getTypeSelector() {
        return getRoot().getTypeSelector();
    }

    @Override
    public String getId() {
        return getRoot().getId();
    }

    @Override
    public ObservableList<String> getStyleClass() {
        return getRoot().getStyleClass();
    }

    @Override
    public String getStyle() {
        return getRoot().getStyle();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getRoot().getCssMetaData();
    }

    @Override
    public Styleable getStyleableParent() {
        return getRoot().getStyleableParent();
    }

    @Override
    public ObservableSet<PseudoClass> getPseudoClassStates() {
        return getRoot().getPseudoClassStates();
    }
}
