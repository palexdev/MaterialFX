/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;

/**
 * Base class for a material notification content pane.
 * <p>
 * Extends {@code VBox} and redefines the style class to "mfx-notification" for usage in CSS.
 */
public abstract class AbstractMFXNotificationPane extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    protected final String STYLE_CLASS = "mfx-notification";
    protected final String STYLESHEET = MFXResourcesLoader.load("css/mfx-notification.css");

    protected final StringProperty headerProperty = new SimpleStringProperty("");
    protected final StringProperty titleProperty = new SimpleStringProperty("");
    protected final StringProperty contentProperty = new SimpleStringProperty("");

    //================================================================================
    // Methods
    //================================================================================
    public String getHeaderProperty() {
        return headerProperty.get();
    }

    public StringProperty headerPropertyProperty() {
        return headerProperty;
    }

    public void setHeaderProperty(String headerProperty) {
        this.headerProperty.set(headerProperty);
    }

    public String getTitleProperty() {
        return titleProperty.get();
    }

    public StringProperty titlePropertyProperty() {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty) {
        this.titleProperty.set(titleProperty);
    }

    public String getContentProperty() {
        return contentProperty.get();
    }

    public StringProperty contentPropertyProperty() {
        return contentProperty;
    }

    public void setContentProperty(String contentProperty) {
        this.contentProperty.set(contentProperty);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
