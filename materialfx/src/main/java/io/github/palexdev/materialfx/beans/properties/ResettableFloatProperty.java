package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleFloatProperty;

public class ResettableFloatProperty extends SimpleFloatProperty implements ResettableProperty<Number> {
    private float defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableFloatProperty() {
    }

    public ResettableFloatProperty(float initialValue) {
        super(initialValue);
    }

    public ResettableFloatProperty(float initialValue, float defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableFloatProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableFloatProperty(Object bean, String name, float initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableFloatProperty(Object bean, String name, float initialValue, Float defaultValue) {
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
    public void set(float newValue) {
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
    public Float getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Number defaultValue) {
        this.defaultValue = defaultValue.floatValue();
    }
}
