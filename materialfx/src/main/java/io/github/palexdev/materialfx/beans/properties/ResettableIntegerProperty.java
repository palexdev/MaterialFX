package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ResettableIntegerProperty extends SimpleIntegerProperty implements ResettableProperty<Number> {
    private int defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableIntegerProperty() {
    }

    public ResettableIntegerProperty(int initialValue) {
        super(initialValue);
    }

    public ResettableIntegerProperty(int initialValue, int defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableIntegerProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableIntegerProperty(Object bean, String name, int initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableIntegerProperty(Object bean, String name, int initialValue, int defaultValue) {
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
    public void set(int newValue) {
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
    public Integer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Number defaultValue) {
        this.defaultValue = defaultValue.intValue();
    }
}
