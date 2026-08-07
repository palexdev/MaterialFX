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

import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.events.Event;
import io.github.palexdev.mfxcore.events.bus.EventBus;
import io.github.palexdev.mfxcore.events.bus.EventsNetwork;
import javafx.stage.Window;

/// Special extension of [MFXDialog] that pauses code execution to return a certain result to the user before proceeding.
/// It's very similar to a file chooser.
///
/// Because it reuses the [MFXDialog]'s API, and heavily differs from JavaFX dialogs, there are a bunch of peculiarities:
/// 1) The `await` config in [DialogConfig] that makes the dialog pause code execution is ignored. In fact, a _result dialog_
/// is always supposed and will wait for user input.
/// 2) The result is produced by a supplier that can be set via [#setResultSupplier(Supplier)]. It can't and will never be
/// `null`, but can produce `null` results.
/// 3) Typical usage is to have confirm and cancel buttons. The API mimics such use case by defining two methods:
///     - [#confirm()] will tell the dialog that the result can be computed by the set supplier
///     - [#cancel()] will make the dialog ignore the supplier and return `null`
///
///     Both will automatically hide the dialog.
///
/// Keep in mind that as long as [#confirm()] is not invoked, the result will always be `null`! In fact, you could even
/// not call [#cancel()] and directly hide the dialog. That method exists for API clarity.
///
/// ### Usage
///
/// Because of such design choices, the usage is the same as [MFXDialog] except for very few differences:
/// ```java
/// MFXResultDialog<String> dialog = new MFXResultDialog<>();
/// // 1) Set the result supplier
/// dialog.setResultSupplier(() -> "A result!");
/// // 2) Show the dialog
/// dialog.show(...);
/// // 3) Get the result
/// String res = dialog.result(); // or resultOpt()
///
/// // Now you just need hooks to the confirm and cancel methods.
/// // Typically that happens within the dialog's content.
/// // For example, you may have a content with a bar at the bottom with the two buttons
/// Button confirm = new Button("Confirm");
/// confirm.setOnAction(_ -> dialog.confirm());
/// Button cancel = new Button("Cancel");
/// cancel.setOnAction(_ -> dialog.cancel());
/// ```
public class MFXResultDialog<R> extends MFXDialog {
    //================================================================================
    // Properties
    //================================================================================
    private Supplier<R> resultSupplier = () -> null;
    protected boolean confirmed = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXResultDialog() {}

    //================================================================================
    // Methods
    //================================================================================

    /// Sets the `confirmed` flag to `true` and hides the dialog, causing [#result()] to compute the value from the set supplier.
    ///
    /// @see #getResultSupplier()
    public void confirm() {
        confirmed = true;
        super.hide();
    }

    /// Sets the `confirmed` flag to `false` and hides the dialog, causing [#result()] to return `null`.
    public void cancel() {
        confirmed = false;
        super.hide();
    }

    /// Pauses code execution and waits for user input, resuming and returning a value only once the dialog is closed.
    ///
    /// @see #confirm()
    /// @see #cancel()
    public R result() {
        doAwait();
        return confirmed ? resultSupplier.get() : null;
    }

    /// Wraps the return value of [#result()] in an [Optional] container.
    public Optional<R> resultOpt() {
        return Optional.ofNullable(result());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void doShow(Window owner, double x, double y) {
        await = false;
        super.doShow(owner, x, y);
    }

    @Override
    protected void doAwait() {
        confirmed = false;
        super.doAwait();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the supplier responsible for computing the value returned by [#result()] if the dialog is closed by calling
    /// [#confirm()].
    public Supplier<R> getResultSupplier() {
        return resultSupplier;
    }

    /// Sets the supplier responsible for computing the value returned by [#result()] if the dialog is closed by calling
    /// [#confirm()].
    ///
    /// If the given supplier is `null`, it's replaced by one that returns a `null` result.
    public MFXResultDialog<R> setResultSupplier(Supplier<R> resultSupplier) {
        this.resultSupplier = Optional.ofNullable(resultSupplier).orElse(() -> null);
        return this;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Extension of [Event] to be used with the [EventBus] or [EventsNetwork] systems.<br >
    /// Can be used to manage the dialog even when the instance is out of reach.
    ///
    /// **Usage:**<br >
    /// ```java
    /// // Assuming you have an event bus here or use the network as `bus`
    /// MFXResultDialog<String> dialog = new MFXResultDialog<>();
    /// bus.subscribe(ResultDialogEven.class, e -> {
    ///     if (e.isConfirmed()){
    ///         d.confirm();
    /// } else {
    ///         d.cancel();
    /// }
    /// });
    ///
    /// // From somewhere else in your program, on the same `bus` or network
    /// bus.publish(new ResultDialogEvent(<true | false>));
    /// ```
    public static class ResultDialogEvent extends Event {
        public ResultDialogEvent(boolean confirmed) {
            super(confirmed);
        }

        public boolean isConfirmed() {
            return ((boolean) data());
        }
    }
}
