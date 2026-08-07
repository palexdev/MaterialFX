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

package io.github.palexdev.mfxcomponents.controls;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.base.MFXToggle;
import io.github.palexdev.mfxcomponents.skins.MFXCheckboxSkin;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.EnumUtils;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Custom implementation of a checkbox which extends [MFXToggle] and has its own skin [MFXCheckboxSkin].<br >
/// The default style class of this component is: '.mfx-checkbox'.
///
/// `Checkboxes` are a special type of toggles because they can also display the `indeterminate` state.<br >
/// Such state is not allowed by default and can be enabled through the [#allowIndeterminateProperty()].
///
/// My implementation uses the [TriState] enumerator to represent the three possible states. The selection state inherited
/// from [MFXToggle] and [Selectable] is bound to the [#selectedProperty()], and it's `true` only when the state is [TriState#SELECTED].<br >
/// As a consequence, the [#toggle()] behavior is overridden to alter the [#stateProperty()] instead.
///
/// [MFXToggle] offers a convenient way to run some action when the selection state changes through [#onSelectionChanged(Consumer)].
/// To offer the same convenience here, the checkbox offers a similar mechanism, but it runs on the [#stateProperty()] instead,
/// see [#onStateChanged(Consumer)].
///
/// **Important Note**<br >
/// Using the checkbox in a [SelectionGroup] will disable the [#allowIndeterminateProperty()] and you
/// won't be able to turn it back on until the checkbox is removed from the group. Selection groups are made to work with
/// selectables which are either selected/unselected, indeterminate states make no sense in a group.
// TODO introduce validator and properly handle error state (it's only visual for now)
public class MFXCheckbox extends MFXToggle {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<TriState> state = new SimpleObjectProperty<>(TriState.UNSELECTED) {
        @Override
        public void set(TriState newValue) {
            if (newValue == null) newValue = TriState.UNSELECTED;
            TriState validState = handleNewState(newValue);
            super.set(validState);
        }

        @Override
        protected void invalidated() {
            TriState state = get();
            PseudoClasses.INDETERMINATE.setOn(MFXCheckbox.this, state == TriState.INDETERMINATE);
            // ":selected" pseudo class is handled by the selection property
            onStateChanged.accept(state);
        }
    };
    private Consumer<TriState> onStateChanged = _ -> {};

    //================================================================================
    // Constructors
    //================================================================================

    public MFXCheckbox() {
        this("Checkbox");
    }

    public MFXCheckbox(String text) {
        super(text);
    }

    {
        selectedProperty().bind(state.isEqualTo(TriState.SELECTED));
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This method is important to ensure that the state is valid when the checkbox is inside a [SelectionGroup].
    protected TriState handleNewState(TriState requested) {
        SelectionGroup sg = getSelectionGroup();
        if (sg == null) return requested;
        boolean check = sg.check(this, requested == TriState.SELECTED);
        return check ? TriState.SELECTED : TriState.UNSELECTED;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// From the current state, sets the next constant in [TriState]. If [#allowIndeterminateProperty()] is `false`, then
    /// [TriState#INDETERMINATE] is ignored and the next valid constant is set instead.<br >
    /// Does nothing if the state is bound or the checkbox is disabled.
    @Override
    public void toggle() {
        if (isDisabled() || state.isBound()) return;
        TriState oldState = getState();
        TriState newState = EnumUtils.next(TriState.class, oldState);
        if (newState == TriState.INDETERMINATE && !isAllowIndeterminate())
            newState = EnumUtils.next(TriState.class, newState);
        setState(newState);

    }

    @Override
    protected void onSelectionGroupChanged(SelectionGroup group) {
        if (group != null) setAllowIndeterminate(false);
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXCheckboxSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-checkbox");
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty allowIndeterminate = new StyleableBooleanProperty(
        StyleableProperties.ALLOW_INDETERMINATE,
        this,
        "allowIndeterminate",
        false
    ) {
        @Override
        public void set(boolean v) {
            super.set(getSelectionGroup() == null && v);
        }

        @Override
        protected void invalidated() {
            if (!get() && isIndeterminate())
                setState(TriState.UNSELECTED);
        }
    };

    public final boolean isAllowIndeterminate() {
        return allowIndeterminate.get();
    }

    /// Specifies whether the checkbox can also transition to the `indeterminate` state.
    ///
    /// When turned off, the `indeterminate` state is also set to false.
    ///
    /// Can be set from CSS via the property: '-mfx-allow-indeterminate'.
    public final StyleableBooleanProperty allowIndeterminateProperty() {
        return allowIndeterminate;
    }

    public final void setAllowIndeterminate(boolean allowIndeterminate) {
        this.allowIndeterminate.set(allowIndeterminate);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXCheckbox> FACTORY = new StyleablePropertyFactory<>(MFXToggle.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXCheckbox, Boolean> ALLOW_INDETERMINATE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-allow-indeterminate",
                MFXCheckbox::allowIndeterminateProperty,
                false
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXToggle.getClassCssMetaData(),
                ALLOW_INDETERMINATE
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return whether the [#stateProperty()] is [TriState#INDETERMINATE]
    public boolean isIndeterminate() {
        return getState() == TriState.INDETERMINATE;
    }

    public TriState getState() {
        return state.get();
    }

    /// Specifies the selection state of the checkbox as an enum constant.
    ///
    /// @see TriState
    public ObjectProperty<TriState> stateProperty() {
        return state;
    }

    public void setState(TriState state) {
        this.state.set(state);
    }

    /// Overridden since checkboxes are a particular type of [Selectable].
    ///
    /// First the given boolean value is converted to a [TriState] by using [TriState#from(Boolean)], then
    /// the resulting value is fed to [#handleNewState(TriState)] to ensure the new state is valid if the checkbox
    /// is in a [SelectionGroup]. Finally, the result of the previous operations is given to [#setState(TriState)].
    @Override
    public void setSelected(boolean selected) {
        setState(handleNewState(TriState.from(selected)));
    }

    /// Allows specifying an action to run when the checkbox's state changes.<br >
    /// This can be considered a more specialized version of [#onSelectionChanged(Consumer)] since a checkbox can have
    /// tree possible states.
    public void onStateChanged(Consumer<TriState> onStateChanged) {
        this.onStateChanged = Optional.ofNullable(onStateChanged).orElse(_ -> {});
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Enumeration used to represent the three possible states of a `MFXCheckbox`.
    public enum TriState {
        UNSELECTED,
        SELECTED,
        INDETERMINATE,
        ;

        /// @return a `TriState` constant from the given [Boolean] object. When giving a `null` value,
        /// this will return the state [#INDETERMINATE]
        public static TriState from(Boolean b) {
            if (b == null) return INDETERMINATE;
            return b ? SELECTED : UNSELECTED;
        }
    }
}
