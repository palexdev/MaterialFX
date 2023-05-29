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

import io.github.palexdev.mfxcomponents.behaviors.MFXCheckBoxBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXCheckBoxSkin;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom implementation of a checkbox which extends {@link MFXSelectable}, has its own skin
 * {@link MFXCheckBoxSkin}.
 * <p>
 * {@code Checkboxes} are special type of 'selectables' because of their special {@code indeterminate} state.
 * Such state is not allowed by default, set {@link #allowIndeterminateProperty()} to change this. The state is specified by
 * the {@link #indeterminateProperty()}.
 * <p></p>
 * The default behavior type for all {@link MFXCheckBox} components is {@link MFXCheckBoxBehavior}.
 * <p>
 * The default style class of this component is: '.mfx-checkbox'.
 */
// TODO introduce validator
public class MFXCheckBox extends MFXSelectable<MFXCheckBoxBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private final BooleanProperty indeterminate = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.INDETERMINATE.setOn(MFXCheckBox.this, get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckBox() {
    }

    public MFXCheckBox(String text) {
        super(text);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXCheckBoxSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-checkbox");
    }

    @Override
    public Supplier<MFXCheckBoxBehavior> defaultBehaviorProvider() {
        return () -> new MFXCheckBoxBehavior(this);
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
        protected void invalidated() {
            boolean state = get();
            if (!state && isIndeterminate()) setIndeterminate(false);
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
        private static final StyleablePropertyFactory<MFXCheckBox> FACTORY = new StyleablePropertyFactory<>(MFXSelectable.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXCheckBox, Boolean> ALLOW_INDETERMINATE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-allow-indeterminate",
                MFXCheckBox::allowIndeterminateProperty,
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
    public boolean isIndeterminate() {
        return indeterminate.get();
    }

    /**
     * Specifies the {@code indeterminate} state of the checkbox.
     */
    public BooleanProperty indeterminateProperty() {
        return indeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate.set(indeterminate);
    }
}
