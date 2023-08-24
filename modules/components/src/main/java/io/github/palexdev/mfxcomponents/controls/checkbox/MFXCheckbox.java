/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls.checkbox;

import io.github.palexdev.mfxcomponents.behaviors.MFXCheckboxBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXCheckboxSkin;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom implementation of a checkbox which extends {@link MFXSelectable}, has its own skin
 * {@link MFXCheckboxSkin}.
 * <p>
 * {@code Checkboxes} are special type of 'selectables' because of their special {@code indeterminate} state.
 * Such state is not allowed by default, set {@link #allowIndeterminateProperty()} to change this.
 * <p></p>
 * The default behavior type for all {@link MFXCheckbox} components is {@link MFXCheckboxBehavior}.
 * <p>
 * The default style class of this component is: '.mfx-checkbox'.
 * <p></p>
 * <b>About this component</b>
 * <p></p>
 * Not so long ago, I was developing a little tool for myself to help me perform boring actions in a bunch of clicks, with
 * a beautiful UI, so of course I used all of my libraries. While developing the app, I was struggling with checkboxes
 * because for necessity I needed in some cases their state to be bound to something else. Turns out, JavaFX checkboxes
 * are not very easy to use in such occasions because they split the {@code state} in two different properties (selected, indeterminate).
 * <p>
 * I understand why it was done like this, it's a common pattern for tri-states, but if it can be avoided then it should
 * always be. Booleans are not meant to represent more than two states, boolean algebra consists of true and false values.
 * <p>
 * So, what's the other option? In one word: {@code Enumerators}.
 * <p>
 * {@code MFXCheckbox} uses the {@link TriState} enumerator to represent its states. Because of this, the checkbox's code
 * is much simpler, using listeners and bindings is also more straightforward.
 * <p></p>
 * <b>Important Notes</b>
 * <p>
 * {@code MFXCheckbox} is a particular type of selectable component. Since it extends {@link MFXSelectable} (thus implementing
 * {@link Selectable} too), all the API used for common selectable components is inherited, <b>but</b>:
 * <p> 1) Using the checkbox in a {@link SelectionGroup} will disable the {@link #allowIndeterminateProperty()} and you
 * won't be able to turn it back on until the checkbox is removed from the group. Selection groups are made to work with
 * selectables which are either selected/unselected, indeterminate states make no sense for in a group.
 * <p> 2) The {@link #selectedProperty()} is bound to the {@link #stateProperty()}. First of all, for this reason
 * you won't be able to set it directly, {@link #setSelected(boolean)} is also overridden to change the {@link #stateProperty()}
 * instead. Second, <b>don't try to unbind it</b>, it's just not meant to work like that.
 */
// TODO introduce validator
public class MFXCheckbox extends MFXSelectable<MFXCheckboxBehavior> {
    //================================================================================
    // Enums
    //================================================================================
    public enum TriState {
        UNSELECTED,
        SELECTED,
        INDETERMINATE,
        ;

        /**
         * @return a {@code TriState} constant from the given {@link Boolean} object. When giving a {@code null} value,
         * this will return the state {@link #INDETERMINATE}
         */
        public static TriState from(Boolean b) {
            if (b == null) return INDETERMINATE;
            return b ? SELECTED : UNSELECTED;
        }
    }

    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<TriState> state = new SimpleObjectProperty<>(TriState.UNSELECTED) {
        @Override
        public void set(TriState newValue) {
            if (newValue == null) newValue = TriState.UNSELECTED;
            TriState state = handleNewState(newValue);
            super.set(state);
        }

        @Override
        protected void invalidated() {
            TriState state = get();
            PseudoClasses.INDETERMINATE.setOn(MFXCheckbox.this, state == TriState.INDETERMINATE);
            // ":selected" pseudo class is handled by the onSelectionChanged(boolean) method
            fire();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckbox() {
        initialize();
    }

    public MFXCheckbox(String text) {
        super(text);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        selectionGroupProperty().addListener(i -> {
            if (getSelectionGroup() != null) setAllowIndeterminate(false);
        });
        selectedProperty().bind(stateProperty().isEqualTo(TriState.SELECTED));
    }

    /**
     * Invoked when a state change is requested. This is responsible for returning a valid state when the checkbox is in
     * a {@link SelectionGroup}. If it is not, returns the given parameter untouched.
     *
     * @param requested the new requested state for the checkbox
     */
    protected TriState handleNewState(TriState requested) {
        SelectionGroup sg = getSelectionGroup();
        if (sg == null) return requested;
        boolean check = sg.check(this, requested == TriState.SELECTED);
        return check ? TriState.SELECTED : TriState.UNSELECTED;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXCheckboxSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-checkbox");
    }

    @Override
    public Supplier<MFXCheckboxBehavior> defaultBehaviorProvider() {
        return () -> new MFXCheckboxBehavior(this);
    }

    @Override
    protected void sceneBuilderIntegration() {
        super.sceneBuilderIntegration();
        SceneBuilderIntegration.ifInSceneBuilder(() -> setText("Checkbox"));
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
        public void set(boolean newValue) {
            SelectionGroup sg = getSelectionGroup();
            super.set(sg == null && newValue);
        }

        @Override
        protected void invalidated() {
            boolean state = get();
            if (!state && isIndeterminate()) setState(TriState.UNSELECTED);
        }
    };

    public boolean isAllowIndeterminate() {
        return allowIndeterminate.get();
    }

    /**
     * Specifies whether the checkbox can also transition to the {@code indeterminate} state.
     * <p>
     * When turned off, the {@code indeterminate} state is also set to false.
     * <p></p>
     * Can be set in CSS via the property: '-mfx-allow-indeterminate'.
     */
    public StyleableBooleanProperty allowIndeterminateProperty() {
        return allowIndeterminate;
    }

    public void setAllowIndeterminate(boolean allowIndeterminate) {
        this.allowIndeterminate.set(allowIndeterminate);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXCheckbox> FACTORY = new StyleablePropertyFactory<>(MFXSelectable.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXCheckbox, Boolean> ALLOW_INDETERMINATE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-allow-indeterminate",
                    MFXCheckbox::allowIndeterminateProperty,
                false
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXSelectable.getClassCssMetaData(),
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

    /**
     * @return whether the {@link #stateProperty()} is {@link TriState#INDETERMINATE}
     */
    public boolean isIndeterminate() {
        return getState() == TriState.INDETERMINATE;
    }

    public TriState getState() {
        return state.get();
    }

    /**
     * Specifies the selection state of the checkbox as an enum constant.
     *
     * @see TriState
     */
    public ObjectProperty<TriState> stateProperty() {
        return state;
    }

    public void setState(TriState state) {
        this.state.set(state);
    }

    /**
     * Overridden since checkboxes are a particular type of {@link Selectable}.
     * <p>
     * First the given boolean value is converted to a {@link TriState} by using {@link TriState#from(Boolean)}, then
     * the resulting value is fed to {@link #handleNewState(TriState)} to ensure the new state is valid if the checkbox
     * is in a {@link SelectionGroup}. Finally, the result of the previous operations is given to {@link #setState(TriState)}.
     */
    @Override
    public void setSelected(boolean selected) {
        setState(handleNewState(TriState.from(selected)));
    }
}
