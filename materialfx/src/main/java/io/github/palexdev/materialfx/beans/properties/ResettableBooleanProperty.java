package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ResettableBooleanProperty extends SimpleBooleanProperty implements ResettableProperty<Boolean> {
    private boolean defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableBooleanProperty() {
    }

    public ResettableBooleanProperty(boolean initialValue) {
        super(initialValue);
    }

    public ResettableBooleanProperty(boolean initialValue, boolean defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableBooleanProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableBooleanProperty(Object bean, String name, boolean initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableBooleanProperty(Object bean, String name, boolean initialValue, boolean defaultValue) {
        super(bean, name, initialValue);
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isFireChangeOnReset() {
        return fireChangeOnReset;
    }

    @Override
    public void setFireChangeOnReset(boolean fireChangeOnReset) {
        this.fireChangeOnReset = fireChangeOnReset;
    }

    @Override
    public void set(boolean newValue) {
        hasBeenReset = newValue == defaultValue;
        super.set(newValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        if (getValue() == defaultValue && !fireChangeOnReset) {
            return;
        }

        super.fireValueChangedEvent();
    }

    @Override
    public boolean hasBeenReset() {
        return hasBeenReset;
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
}
