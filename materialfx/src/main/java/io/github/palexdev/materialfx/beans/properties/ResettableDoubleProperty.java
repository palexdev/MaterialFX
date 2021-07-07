package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ResettableDoubleProperty extends SimpleDoubleProperty implements ResettableProperty<Number> {
    private double defaultValue;
    private boolean fireChangeOnReset = false;
    private boolean hasBeenReset = false;

    public ResettableDoubleProperty() {
    }

    public ResettableDoubleProperty(double initialValue) {
        super(initialValue);
    }

    public ResettableDoubleProperty(double initialValue, double defaultValue) {
        super(initialValue);
        this.defaultValue = defaultValue;
    }

    public ResettableDoubleProperty(Object bean, String name) {
        super(bean, name);
    }

    public ResettableDoubleProperty(Object bean, String name, double initialValue) {
        super(bean, name, initialValue);
    }

    public ResettableDoubleProperty(Object bean, String name, double initialValue, Double defaultValue) {
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
    public void set(double newValue) {
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
    public Double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Number defaultValue) {
        this.defaultValue = defaultValue.doubleValue();
    }
}
