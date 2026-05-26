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
import io.github.palexdev.mfxcore.popups.MFXDialog.DialogConfig.Builder;
import io.github.palexdev.mfxcore.popups.MFXDialog.WindowPeer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Direction;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import io.github.palexdev.mfxcore.utils.fx.MFXBackdrop;
import io.github.palexdev.mfxcore.utils.fx.StageUtils;
import io.github.palexdev.mfxcore.utils.fx.WindowMover;
import io.github.palexdev.mfxcore.utils.fx.resize.StageResizer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;

/// My custom implementation of dialogs based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to '.mfx-dialog'.
///
/// ### Definition, Usage and Features
///
/// Dialogs are a type of popup used to present information and prompt users to input data or make decisions. They can
/// often interrupt the flow by being modal, but they can also be non-modal. Modal dialogs can show a backdrop effect
/// behind to lock the user in the context visually.
///
/// Dialogs usually have _explicit dismiss_ via a close button (e.g., x icon) or a 'cancel' button.
///
/// ### Implementation Details
///
/// `MFXDialog` uses a custom [Stage] as the peer to which delegate the true actions (show, hide, position, modality, etc...).
/// Which means that unfortunately, we inherit all the inconveniences of it like: not being able to create the dialog
/// outside the JavaFX Application Thread, not being able to change the modality or owner after it is shown, etc.
///
/// Unlike other kinds of popups, this is not necessarily tied to an owner. The [#computePosition(Window, Placement)] is
/// overridden to take this into account and use the [Screen] bounds instead. Which screen to use can be set via the
/// configuration [DialogConfig], otherwise it defaults to the primary.
///
/// Note that if no owner is specified, there won't be automatic dismissal. When an owner is set, this listens for
/// [WindowEvent#WINDOW_CLOSE_REQUEST] events on the owner and automatically closes itself!
///
/// @see AnchorHandlers
/// @see Placement
/// @see Direction
public class MFXDialog extends MFXPopupBase<WindowPeer, Window> {
    //================================================================================
    // Properties
    //================================================================================
    private boolean hasBeenShown = false;

    private Screen fallbackScreen;
    private MFXBackdrop backdrop;

    private boolean lockInPlace;
    private When<?> lockWhen;

    private WindowMover mover;
    private StageResizer resizer;

    protected boolean await = false;
    protected boolean inNestedLoop = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDialog() {
        setDefaultStyleClasses();
        DialogConfig.DEFAULT.apply(this);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to change the configuration of this dialog. The provided builder starts with the values from
    /// the current config.
    public MFXDialog configure(Consumer<Builder> cfg) {
        Builder builder = DialogConfig.builder(getConfig());
        cfg.accept(builder);
        builder.build().apply(this);
        return this;
    }

    /// Stops code execution until the dialog is closed.
    ///
    /// @throws IllegalStateException if the dialog is not showing or already waiting
    protected void doAwait() {
        if (!isShowing())
            throw new IllegalStateException("Dialog must be showing to await!");
        if (inNestedLoop)
            throw new IllegalStateException("Already awaiting!");

        try {
            inNestedLoop = true;
            Platform.enterNestedEventLoop(peer);
        } catch (Exception ex) {
            ex.printStackTrace();
            inNestedLoop = false;
        }
    }

    public void setMoveable(Node anchor) {
        if (anchor == null) {
            if (mover != null) mover.uninstall();
            return;
        }

        if (lockInPlace)
            throw new IllegalStateException("Dialog is configured to be lock in place");
        if (mover != null) mover.uninstall();
        mover = StageUtils.makeDraggable(peer, anchor);
    }

    public void setResizable(boolean resizable) {
        if (!resizable) {
            if (resizer != null) resizer.dispose();
            return;
        }

        if (resizer != null) resizer.dispose();
        resizer = StageUtils.makeResizable(peer, peer.getRoot());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected WindowPeer buildPeer() {
        return new WindowPeer();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void doShow(Window owner, double x, double y) {
        if (animation != null) animation.stop();

        this.owner = owner;
        if (owner != null) {
            WhenEvent.intercept(owner, WindowEvent.WINDOW_CLOSE_REQUEST)
                .handle(_ -> hide())
                .asFilter()
                .oneShot()
                .register();

            if (lockInPlace) {
                lockWhen = When.observe(
                    this::reposition,
                    owner.xProperty(), owner.yProperty(),
                    owner.widthProperty(), owner.heightProperty(),
                    getRoot().layoutBoundsProperty()
                ).listen();
            }

            if (!hasBeenShown)
                peer.initOwner(owner);
        }

        Node content = getContent();
        content.setVisible(false);

        positionProperty().setPosition(x, y);
        hasBeenShown = true;
        peer.show();

        if (placement != null) {
            // Re-compute position because the content's bounds are reliable now
            Position pos = computePosition(owner, placement);
            setPosition(pos);
        }
        if (backdrop != null)
            backdrop.show(owner.getScene().getRoot());

        if (animation != null) animation.playIn();
        content.setVisible(true);
        setState(PopupState.SHOWN);
        if (await) doAwait();
    }

    @Override
    public void show(Window owner, double x, double y) {
        if (isShowing()) {
            peer.toFront();
            return;
        }
        super.show(owner, x, y);
    }

    @Override
    public void show(Window owner, Placement placement) {
        if (isShowing()) {
            peer.toFront();
            return;
        }
        super.show(owner, placement);
    }

    @Override
    public void hide() {
        if (inNestedLoop) {
            Platform.exitNestedEventLoop(peer, null);
            inNestedLoop = false;
        }

        if (lockWhen != null) {
            lockWhen.dispose();
            lockWhen = null;
        }
        super.hide();

        if (backdrop != null)
            backdrop.hide();
    }

    @Override
    protected void hidePeer() {
        peer.directHide = true;
        peer.hide();
    }

    /// If the given placement is `null` returns a position of `<0, 0>`.
    ///
    /// If the owner is `null`, the position is computed relative to the screen. The [Screen] used is the primary by
    /// default or the fallback one specified through the [DialogConfig].
    ///
    /// In any case, the computation is delegated to the handlers in [AnchorHandlers].
    @Override
    protected Position computePosition(Window owner, Placement placement) {
        if (placement == null) return Position.origin();
        AnchorHandlers.AnchorHandler handler = AnchorHandlers.handler(placement.anchor());
        if (owner != null)
            return handler.compute(owner, getRoot(), placement.xDirection(), placement.yDirection(), getOffset());

        Screen screen = Optional.ofNullable(fallbackScreen).orElse(Screen.getPrimary());
        Rectangle2D vb = screen.getVisualBounds();
        Bounds screenBounds = new BoundingBox(
            vb.getMinX(), vb.getMinY(),
            vb.getWidth(), vb.getHeight()
        );
        return handler.compute(screenBounds, getRoot(), placement.xDirection(), placement.yDirection(), getOffset());
    }

    @Override
    protected void onContentChanged() {
        Node content = getContent();
        peer.setContent(content);
    }

    @Override
    protected void preShowCheck(Window owner) {
        if (owner != null && !owner.isShowing()) throw new IllegalStateException("Owner window must be showing");
        if (getContent() == null) throw new IllegalStateException("Content cannot be null");
    }

    @Override
    public Position getPeerPosition() {
        return Position.of(peer.getX(), peer.getY());
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("root", "mfx-dialog");
    }

    @Override
    public void setStyleClass(String... styleClass) {
        peer.setStyleClass(styleClass);
    }

    @Override
    public DialogConfig getConfig() {
        return ((DialogConfig) config);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================

    public String getTitle() {return peer.getTitle();}

    public StringProperty titleProperty() {return peer.titleProperty();}

    public void setTitle(String value) {peer.setTitle(value);}

    public boolean isAlwaysOnTop() {return peer.isAlwaysOnTop();}

    public ReadOnlyBooleanProperty alwaysOnTopProperty() {return peer.alwaysOnTopProperty();}

    public void setAlwaysOnTop(boolean value) {peer.setAlwaysOnTop(value);}

    public void toFront() {peer.toFront();}

    public void toBack() {peer.toBack();}

    public void centerOnScreen() {peer.centerOnScreen();}

    public boolean isFocused() {return peer.isFocused();}

    public ReadOnlyBooleanProperty focusedProperty() {return peer.focusedProperty();}

    public void requestFocus() {peer.requestFocus();}

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Custom extension of [Stage] which serves as the peer for [MFXDialog].
    ///
    /// Sets the root node to a [PopupRoot], the style to [StageStyle#TRANSPARENT] and the scene's fill to transparent.
    protected class WindowPeer extends Stage implements Peer {
        private final PopupRoot root = new PopupRoot();
        boolean directHide = false;

        {
            initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            setScene(scene);
        }

        @Override
        public PopupRoot getRoot() {
            return root;
        }

        @Override
        public void hide() {
            if (directHide) {
                directHide = false;
                super.hide();
                return;
            }
            // JavaFX initiated this (auto-hide, hide-on-escape). Redirect to dialog's animated hide.
            MFXDialog.this.hide();
            setState(PopupState.AUTO_HIDE);
        }
    }

    // Config

    public record DialogConfig(
        Position offset,
        Screen fallbackScreen,
        Modality modality,
        boolean useBackdrop,
        String[] backdropStyleClass,
        boolean alwaysOnTop,
        boolean lockInPlace,
        boolean await,
        Node styleableParent
    ) implements Config<MFXDialog> {
        public static final DialogConfig DEFAULT = builder().build();

        @Override
        public void apply(MFXDialog popup) {
            popup.offset = offset;
            popup.fallbackScreen = fallbackScreen;
            if (!popup.hasBeenShown) {
                popup.peer.initModality(modality);
            }
            if (useBackdrop) {
                popup.backdrop = new MFXBackdrop();
                popup.backdrop.getStyleClass().addAll(backdropStyleClass);
            } else {
                popup.backdrop = null;
            }
            popup.peer.setAlwaysOnTop(alwaysOnTop);
            popup.lockInPlace = lockInPlace;
            popup.await = await;
            popup.setStyleableParent(styleableParent);
            popup.config = this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(DialogConfig config) {
            return new Builder()
                .offset(config.offset)
                .fallbackScreen(config.fallbackScreen)
                .modality(config.modality)
                .useBackdrop(config.useBackdrop)
                .backdropStyleClass(config.backdropStyleClass)
                .alwaysOnTop(config.alwaysOnTop)
                .lockInPlace(config.lockInPlace)
                .await(config.await);
        }

        public static final class Builder {
            private Position offset = Position.origin();
            private Screen fallbackScreen;
            private Modality modality = Modality.NONE;
            private boolean useBackdrop = false;
            private String[] backdropStyleClass = new String[]{};
            private boolean alwaysOnTop = false;
            private boolean lockInPlace = false;
            private boolean await = false;
            private Node styleableParent;

            public Builder offset(Position offset) {
                this.offset = offset;
                return this;
            }

            public Builder fallbackScreen(Screen fallbackScreen) {
                this.fallbackScreen = fallbackScreen;
                return this;
            }

            public Builder modality(Modality modality) {
                this.modality = modality;
                return this;
            }

            public Builder useBackdrop(boolean useBackdrop) {
                this.useBackdrop = useBackdrop;
                return this;
            }

            public Builder backdropStyleClass(String... backdropStyleClass) {
                this.backdropStyleClass = backdropStyleClass;
                return this;
            }

            public Builder alwaysOnTop(boolean alwaysOnTop) {
                this.alwaysOnTop = alwaysOnTop;
                return this;
            }

            public Builder lockInPlace(boolean lockInPlace) {
                this.lockInPlace = lockInPlace;
                return this;
            }

            public Builder await(boolean await) {
                this.await = await;
                return this;
            }

            public Builder styleableParent(Node styleableParent) {
                this.styleableParent = styleableParent;
                return this;
            }

            public DialogConfig build() {
                return new DialogConfig(
                    offset,
                    fallbackScreen,
                    modality,
                    useBackdrop,
                    backdropStyleClass,
                    alwaysOnTop,
                    lockInPlace,
                    await,
                    styleableParent
                );
            }
        }
    }
}
