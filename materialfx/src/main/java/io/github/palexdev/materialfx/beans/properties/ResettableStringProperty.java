package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResettableStringProperty extends SimpleStringProperty implements ResettableProperty<String> {
    private String defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableStringProperty() {
    }

    public ResettableStringProperty(String initialValue) {
        super(initialValue);
    }

    public ResettableStringProperty(String initialValue, String defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableStringProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableStringProperty(Object bean, String name, String initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableStringProperty(Object bean, String name, String initialValue, String defaultValue) {
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
    public void set(String newValue) {
        hasBeenReset = newValue.equals(defaultValue);
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
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
