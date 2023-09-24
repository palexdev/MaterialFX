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
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableStringProperty;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
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
 * selectables which are either selected/unselected, indeterminate states make no sense in a group.
 * <p> 2) The {@link #selectedProperty()} is bound to the {@link #stateProperty()}. First of all, for this reason
 * you won't be able to set it directly, {@link #setSelected(boolean)} is also overridden to change the {@link #stateProperty()}
 * instead. Second, <b>don't try to unbind it</b>, it's just not meant to work like that.
 * <p> 3) Previously the icon was changed according to the state by the theme. Now, since the checkbox is animated, the
 * icon's description/identifier has been moved from the themes to code for one reason. Every time the state changes, a
 * new icon is created and switched from the old one, this is because for the animation this uses the new API introduced
 * by {@link MFXIconWrapper}. That being said, I didn't want to hard code it, so I added to properties to specify the icons
 * in CSS: {@link #selectedIconProperty()} and {@link #indeterminateIconProperty()}. Also, keep in mind that you can even
 * disable the animations via {@link #animatedProperty()} or change the animation in CSS, again see {@link MFXIconWrapper}.
 */
// TODO introduce validator
public class MFXCheckbox extends MFXSelectable<MFXCheckboxBehavior> {
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
	private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
		StyleableProperties.ANIMATED,
		this,
		"animated",
		true
	);

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

	private final StyleableStringProperty selectedIcon = new StyleableStringProperty(
		StyleableProperties.SELECTED_ICON,
		this,
		"selectedIcon",
		"fas-check"
	);

	private final StyleableStringProperty indeterminateIcon = new StyleableStringProperty(
		StyleableProperties.INDETERMINATE_ICON,
		this,
		"indeterminateBean",
		"fas-minus"
	);

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies whether to play animations when the checkbox' state changes.
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated.set(animated);
	}

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

	public String getSelectedIcon() {
		return selectedIcon.get();
	}

	/**
	 * Specifies the {@link IconDescriptor} as a String, used to build a new {@link MFXFontIcon} when the checkbox is
	 * selected.
	 * <p>
	 * As of now, only {@link FontAwesomeSolid} are supported.
	 */
	public StyleableStringProperty selectedIconProperty() {
		return selectedIcon;
	}

	public void setSelectedIcon(String selectedIcon) {
		this.selectedIcon.set(selectedIcon);
	}

	public String getIndeterminateIcon() {
		return indeterminateIcon.get();
	}

	/**
	 * Specifies the {@link IconDescriptor} as a String, used to build a new {@link MFXFontIcon} when the checkbox is
	 * indeterminate.
	 * <p>
	 * As of now, only {@link FontAwesomeSolid} are supported.
	 */
	public StyleableStringProperty indeterminateIconProperty() {
		return indeterminateIcon;
	}

	public void setIndeterminateIcon(String indeterminateIcon) {
		this.indeterminateIcon.set(indeterminateIcon);
	}

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXCheckbox> FACTORY = new StyleablePropertyFactory<>(MFXSelectable.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXCheckbox, Boolean> ANIMATED =
			FACTORY.createBooleanCssMetaData(
				"-mfx-animated",
				MFXCheckbox::animatedProperty,
				true
			);

        private static final CssMetaData<MFXCheckbox, Boolean> ALLOW_INDETERMINATE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-allow-indeterminate",
                    MFXCheckbox::allowIndeterminateProperty,
                false
            );

		private static final CssMetaData<MFXCheckbox, String> SELECTED_ICON =
			FACTORY.createStringCssMetaData(
				"-mfx-selected-icon",
				MFXCheckbox::selectedIconProperty,
				"fas-check"
			);

		private static final CssMetaData<MFXCheckbox, String> INDETERMINATE_ICON =
			FACTORY.createStringCssMetaData(
				"-mfx-indeterminate-icon",
				MFXCheckbox::indeterminateIconProperty,
				"fas-minus"
			);

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXSelectable.getClassCssMetaData(),
				ANIMATED, ALLOW_INDETERMINATE, SELECTED_ICON, INDETERMINATE_ICON
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
