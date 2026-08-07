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

package io.github.palexdev.mfxcomponents.controls.base;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import javafx.scene.Node;

/// Base class for all buttons that are also selectable, therefore extends [MFXButtonBase] and implements [Selectable].
///
/// The [#trigger()] method is overridden to also flip the selection state, see [#toggle()].<br >
/// So, toggle buttons have both the behaviors of a standard button and a selection control:
/// - You can specify an action to run when the component is triggered, [#onActionProperty()]
/// - Or you can specify an action to run when the selection changes, [#onSelectionChanged(Consumer)] (this is invoked first)
public abstract class MFXToggle extends MFXButtonBase implements Selectable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionGroupProperty group = new SelectionGroupProperty(this) {
        @Override
        protected void invalidated() {
            onSelectionGroupChanged(get());
        }
    };
    private final SelectionProperty selected = new SelectionProperty(this) {
        @Override
        protected void onInvalidated() {
            boolean val = get();
            PseudoClasses.SELECTED.setOn(MFXToggle.this, val);
            onSelectionChanged.accept(val);
        }
    };
    private Consumer<Boolean> onSelectionChanged = _ -> {};

    //================================================================================
    // Constructors
    //================================================================================

    protected MFXToggle() {}

    protected MFXToggle(String text) {
        super(text);
    }

    protected MFXToggle(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// A hook into the [SelectionGroupProperty] of this selectable, to perform some actions when the group changes.<br >
    /// By default, does nothing.
    protected void onSelectionGroupChanged(SelectionGroup group) {}

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to also switch the selection state.
    @Override
    public void trigger() {
        toggle();
        super.trigger();
    }

    /// Flips the selection state.<br >
    /// Does nothing if the [#selectedProperty()] is bound or the component is disabled.
    @Override
    public void toggle() {
        if (isDisabled() || selected.isBound()) return;
        selected.set(!selected.get());
    }

    @Override
    public SelectionGroupProperty selectionGroupProperty() {
        return group;
    }

    @Override
    public SelectionProperty selectedProperty() {
        return selected;
    }

    @Override
    public void onSelectionChanged(Consumer<Boolean> onSelectionChanged) {
        this.onSelectionChanged = Optional.ofNullable(onSelectionChanged).orElse(_ -> {});
    }

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXButtonBehavior<>(this);
    }
}
