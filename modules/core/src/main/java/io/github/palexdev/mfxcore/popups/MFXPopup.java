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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Direction;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.PopupControl;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

/// Public API for all kinds of MaterialFX popups.
///
/// ### History and Rant
///
/// It's not the first time I make my own API for dialogs and popups on top of JavaFX. The goal was always the same:
/// making a system that is easier and more pleasant to use compared to the mess offered by JavaFX. Every attempt so far
/// was interesting in its own way, but in my opinion, not good enough by my standards. I partially blame myself for lack
/// of experience, but also the framework: for its sometimes mysterious design, for its internal, private APIs and tricks,
/// for its documentation being all over the place, for its monolithic giant classes that make no sense. Just to give you
/// some examples of what I'm talking about:
/// 1) Too fucking many classes: Window, Stage, PopupWindow, Popup, PopupControl,...
/// 2) [Stage] extends [Window] and its documentation affirms it cannot be instantiated outside the JavaFX Thread.
/// But you can create a window from another thread, even if the constructor is protected. And in fact, you can create
/// [PopupWindow] in another thread. Consistency, ever heard of it?
/// 3) Once you set the owner and modality for a [Stage] you cannot override them anymore. Even if the stage is hidden
/// and you do it before showing it again... Why the fuck? Why? Don't tell me you can't because that's bullshit.
/// But you know what's the worst part in this? Creating stages is a very expensive operation that may make the app stutter
/// for a split second. So, it would be sensible to create it once and re-configure it as needed. Well, fuck that I guess,
/// you'll need a new one (:
/// 4) [PopupControl], oh, oh my, what absolute garbage it is. It uses some internal tricks to make it styleable from the
/// owner, even if technically it's a separate window with its own scenegraph. But the worst part is that, for some fucking
/// stupid reason, you need to have a skin on it... Why? I've read the internals. All it does is recurse the owner hierarchy
/// to fetch the stylesheets and apply on a "bRiDGe" node... so why forcing a Skin? To make layout computations harder?
/// I see I see, nice idea guys, as always.
/// 5) [PopupWindow] extends [Window], which exposes setters for its position as `setX(...)` and `setY(...)`. Then...
/// why the fuck [PopupWindow] exposes properties such as `anchorX` and `anchorY`, what's the difference...
/// None! They both position the window
/// 6) Have you seen the [Dialog]'s documentation? "Wrapper for a _DialogPane_...,_ButtonType..., callback...,closing rules...,"
/// Jeeeez, who the hell made this design, like how bad can you be at programming fuck sake. Keep it simple, keep it stupid.
/// Do you think I have the time to read and understand all that bullshit? Do I look like a student?
/// Since all popups show something, why not just make a `setContent`(spoiler) or a similar approach...
///
/// I can go on and on... But I don't want my blood to boil away, so... Let's focus on my API. Sorry for the rant.
///
/// ### Intentions and Design
///
/// Let's start with some basic reasoning and concepts:
/// 1) What is a popup? What is a dialog? Why so many terms in the first place? Well, there are reasons.
/// I came across [this](https://medium.com/design-bootcamp/popups-dialogs-tooltips-and-popovers-ux-patterns-2-939da7a1ddcd)
/// amazing article that explains everything. To sum it up, here's a table:
/// ```markdown
/// |          | Dialog            | Popover   | Tooltip |
/// |----------|-------------------|-----------|---------|
/// | Focus    | Modal & non-modal | Non-modal |         |
/// | Backdrop | For modal dialogs | No        | No      |
/// | Dismiss  | Explicit          | Light     | Light   |
/// ```
/// This is **great**! Know why? Because knowing these differences limits the scope. Take, for example [PopupWindow].
/// By the above definitions, a popover is typically shown close to a specific UI element for additional information,
/// options, actions, etc.
///
/// So, why [PopupWindow] can be shown next to a window: [PopupWindow#show(Window)]
/// 2) By making those distinctions, we can say that in JavaFX: a dialog would be a [Stage] that may or may not have an owner,
/// and that owner is another [Window]; a popover or tooltip is a [PopupWindow] that must have an owner [Node].
/// This is the major difference between the three kinds, therefore, a `MFXPopup` needs to handle a certain generic owner
/// `<O>`. The other differences are properties, configurations specific to each kind, like the backdrop and modality for
/// dialogs, and the auto hide, hide on escape, etc. for popovers and tooltips. And for this reason we can't expose them
/// as part of this interface.
/// 3) A popup is an external window that shows something to the user. No need to overcomplicate things...
/// Let's just say that every popup has some **content**. There are other common properties too: every window has its position,
/// and every window has a state, represented in `MFXPopup` as an enumeration: [PopupState].
///
/// So, the intention with this is to have an API that exposes the bare minimum to show a popup. The user is free to not
/// understand the internals and each kind. From the user perspective, I just want to show a window for
/// <insert goal here>. And `MFXPopup` does exactly this.
///
/// **_Peers_**
///
/// The world "peer" in this context means:
/// - That the peer is not the main component itself
/// - But it represents or acts on behalf of it
/// - And that it handles platform/system-specific concerns
///
/// In other words, it's a _delegate_. The implementations still delegate to JavaFX APIs. And that's actually a good thing.
/// Let me explain: unfortunately, as always, JavaFX has hidden APIs, tricks under the hood that are hard to replicate
/// and thus forcing you to use their classes. For example: [Stage] for the modality and owner; [PopupWindow] for the
/// auto-hide, auto-fix, etc.
///
/// It's not impossible to re-create such behaviors with public APIs and tricks, but it's hard and error-prone.
/// The goal is not to replace them, but to make their usage more pleasant and easy.
public interface MFXPopup<O> {

    /// Shows this popup for the given owner at the given coordinates.
    ///
    /// The owner handling may differ depending on the implementation.
    void show(O owner, double x, double y);

    /// Shows this popup for the given owner, at the specified [placement][Placement], all relative to the owner.<br >
    /// The coordinates' computation may differ depending on the implementation.
    ///
    /// @see AnchorHandlers
    /// @see Placement
    /// @see Direction
    void show(O owner, Placement placement);

    /// Hides the popup.
    void hide();

    /// Repositions the popup's window if possible.
    ///
    /// See [MFXPopupBase#reposition()].
    void reposition();

    /// @return the owner's for which the popup is shown. Depending on the implementations, this may return `null` if the
    /// popup is hidden.
    O getOwner();

    /// @return the parent node which contains the popup's content, or in other words, the popup scene's root
    Parent getRoot();

    default Node getContent() {
        return contentProperty().get();
    }

    /// Specifies the popup's content.
    ///
    /// This is not necessarily the popup's root node, it depends on the implementation.
    NodeProperty contentProperty();

    default void setContent(Node content) {
        contentProperty().set(content);
    }

    /// Convenience method to set the popup's content from a supplier, delegates to [#setContent(Node)].
    default void supplyContent(Supplier<Node> contentSupplier) {
        setContent(contentSupplier.get());
    }

    /// @return the popup's true position, see [#positionProperty()]
    Position getPeerPosition();

    default Position getPosition() {
        return positionProperty().get();
    }

    /// Specifies the popup's position on the screen.
    ///
    /// This may not return the true position of the window depending on the implementation. For example, for popovers
    /// that have the auto-fix feature active, this property is not updated for simplicity. You can query the true position
    /// by using [#getPeerPosition()]
    PositionProperty positionProperty();

    default void setPosition(Position position) {
        positionProperty().set(position);
    }

    /// @return the [Position] object that will be used to offset the popup's window according to its anchor position
    Position getOffset();

    /// Sets the [Position] object that will be used to offset the popup's window according to its anchor position
    void setOffset(Position offset);

    /// Convenience method to check if the [#stateProperty()] is either [PopupState#SHOWING] or [PopupState#SHOWN].
    default boolean isShowing() {
        PopupState state = getState();
        return state == PopupState.SHOWING || state == PopupState.SHOWN;
    }

    default PopupState getState() {
        return stateProperty().get();
    }

    /// Specifies the popup's visibility state.
    ReadOnlyObjectProperty<PopupState> stateProperty();

    /// Adds and returns a listener on the [#stateProperty()] which executes the given action when the given state is `null`
    /// or is the same as the popup's.
    ///
    /// @see When
    default When<?> onState(PopupState state, BiConsumer<MFXPopup<O>, PopupState> action) {
        return When.onInvalidated(stateProperty())
            .condition(s -> state == null || s == state)
            .then(s -> action.accept(this, s))
            .listen();
    }

    /// As explained by [#contentProperty()], the popup's root may not necessarily be the content. This method can be used
    /// to set the style-classes of the root node.
    void setStyleClass(String... styleClass);

    /// @return the currently applied confing on this popup
    Config<? extends MFXPopup<O>> getConfig();

    //================================================================================
    // Inner Classes
    //================================================================================

    /// This interface defines the bare minimum API any popup's peer should expose.
    ///
    /// @see PopupRoot
    interface Peer {

        /// @return the popup window's root node. By design, this is always a [PopupRoot] and the actual
        /// popup's content is wrapped in it. This vastly simplifies the implementation.
        PopupRoot getRoot();

        default void setContent(Node content) {
            getRoot().setContent(content);
        }

        default void setStyleClass(String... styleClass) {
            getRoot().getStyleClass().setAll(styleClass);
        }
    }

    /// Interface for popups-specific configurations.
    @FunctionalInterface
    interface Config<P extends MFXPopup<?>> {
        void apply(P popup);
    }
}
