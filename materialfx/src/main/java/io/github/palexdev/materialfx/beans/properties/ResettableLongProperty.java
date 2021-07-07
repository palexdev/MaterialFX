package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleLongProperty;

public class ResettableLongProperty extends SimpleLongProperty implements ResettableProperty<Number> {
    private long defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableLongProperty() {
    }

    public ResettableLongProperty(long initialValue) {
        super(initialValue);
    }

    public ResettableLongProperty(long initialValue, long defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableLongProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableLongProperty(Object bean, String name, long initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableLongProperty(Object bean, String name, long initialValue, long defaultValue) {
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
    public void set(long newValue) {
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
    public Long getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Number defaultValue) {
        this.defaultValue = defaultValue.longValue();
    }
}
