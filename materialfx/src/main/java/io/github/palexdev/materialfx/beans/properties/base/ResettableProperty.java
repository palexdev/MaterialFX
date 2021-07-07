package io.github.palexdev.materialfx.beans.properties.base;

import javafx.beans.property.Property;

public interface ResettableProperty<T> extends Property<T> {
    default void reset() {
        setValue(getDefaultValue());
    }



    boolean isFireChangeOnReset();
    void setFireChangeOnReset(boolean fireChangeOnReset);
    boolean hasBeenReset();
    T getDefaultValue();
    void setDefaultValue(T defaultValue);
}
