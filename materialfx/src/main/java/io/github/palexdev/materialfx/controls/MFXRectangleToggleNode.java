/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXToggleNode;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.skins.MFXRectangleToggleNodeSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;

/**
 * This is the implementation of a {@link ToggleButton} with a completely different skin, {@link MFXRectangleToggleNodeSkin}.
 * <p></p>
 * Extends {@link ToggleButton} and redefines the style class to "mfx-toggle-node" for usage in CSS.
 * <p>
 * Allows to specify up to two icons for toggle's label.
 */
public class MFXRectangleToggleNode extends AbstractMFXToggleNode {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXRectangleToggleNode.css");
    private final ObjectProperty<RippleClipTypeFactory> rippleClipTypeFactory = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRectangleToggleNode() {
        this("");
    }

    public MFXRectangleToggleNode(String text) {
        this(text, null);
    }

    public MFXRectangleToggleNode(String text, Node leadingIcon) {
        this(text, leadingIcon, null);
    }

    public MFXRectangleToggleNode(String text, Node leadingIcon, Node trailingIcon) {
        super(text, null);
        setLabelLeadingIcon(leadingIcon);
        setLabelTrailingIcon(trailingIcon);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefSize(145, 45);
        setRippleClipTypeFactory(
                new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE)
                .setArcs(15)
        );
    }

    public RippleClipTypeFactory getRippleClipTypeFactory() {
        return rippleClipTypeFactory.get();
    }

    /**
     * Specifies the ripple generator's clip factory.
     * <p></p>
     * If you change the borders' radius this property will most likely need to be changed.
     */
    public ObjectProperty<RippleClipTypeFactory> rippleClipTypeFactoryProperty() {
        return rippleClipTypeFactory;
    }

    public void setRippleClipTypeFactory(RippleClipTypeFactory rippleClipTypeFactory) {
        this.rippleClipTypeFactory.set(rippleClipTypeFactory);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXRectangleToggleNodeSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
