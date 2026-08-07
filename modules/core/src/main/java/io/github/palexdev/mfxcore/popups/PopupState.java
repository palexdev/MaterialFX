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

import javafx.stage.PopupWindow;

/// Enumeration to represent the various show/hide states of a [MFXPopup].
public enum PopupState {
    /// Indicates that the popup is about to be closed
    HIDING,

    /// Indicates that the popup has been hidden
    HIDDEN,

    /// Special state indicating the popup hide request comes from some internal JavaFX bullshit.<br >
    /// For example, [PopupWindow] can be [set to be auto-hidden][PopupWindow#autoHideProperty()] when it loses focus.
    /// Now, ad the docs also explain, if the window is shown with an owner, then focusing the node **should not** close
    /// the popup. And yet, this shitty framework finds a way to screw up and you can end up having situations like this:
    /// ```java
    /// MFXPopover popover = ...;
    /// MFXButton btn = ...;
    /// btn.setOnAction(_ -> {
    /// // Doesn't work for whatever fucking reason and the popup keeps getting shown.
    ///   if (popover.isShowing()){
    ///     popover.hide();
    /// } else {
    ///     popover.show(...);
    /// }
    /// });
    /// ```
    /// Now, thanks to **my** implementation that hooks directly into auto-hide bullshit, you can query for this special
    /// state and workaround the issue.
    /// ```java
    /// MFXPopover popover = ...;
    /// MFXButton btn = ...;
    /// btn.setOnAction(_ -> {
    ///   if (popover.getState() == PopupState.AUTO_HIDE){
    ///     return;
    /// } else {
    ///     popover.show(...);
    /// }
    /// });
    /// ```
    AUTO_HIDE,

    /// Indicates that the popup is about to get shown
    SHOWING,

    /// Indicates that the popup is showing
    SHOWN
}
