package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ResettableObjectProperty<T> extends SimpleObjectProperty<T> implements ResettableProperty<T> {
    private T defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableObjectProperty() {
    }

    public ResettableObjectProperty(T initialValue) {
        super(initialValue);
    }

    public ResettableObjectProperty(T initialValue, T defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableObjectProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableObjectProperty(Object bean, String name, T initialValue, T defaultValue) {
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
    public void set(T newValue) {
        hasBeenReset = newValue == defaultValue;
        super.set(newValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        if (getValue().equals(defaultValue) && !fireChangeOnReset) {
            return;
        }

        super.fireValueChangedEvent();
    }

    @Override
    public boolean hasBeenReset() {
        return hasBeenReset;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }
}
