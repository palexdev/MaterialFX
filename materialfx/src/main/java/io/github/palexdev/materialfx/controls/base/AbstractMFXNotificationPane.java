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
    protected final String STYLESHEET = MFXResourcesLoader.load("css/mfx-notification.css").toString();

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
