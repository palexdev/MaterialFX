package io.github.palexdev.materialfx.validation.base;

import io.github.palexdev.materialfx.beans.binding.BooleanListBinding;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Base class for all validators.
 * @see IMFXValidator
 * @see BooleanListBinding
 */
public abstract class AbstractMFXValidator implements IMFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    protected ObservableList<BooleanProperty> conditions = FXCollections.observableArrayList();
    protected BooleanListBinding validation = new BooleanListBinding(conditions);
    private final StringProperty validatorMessage = new SimpleStringProperty("Validation failed!");

    //================================================================================
    // Methods
    //================================================================================
    public String getValidatorMessage() {
        return validatorMessage.get();
    }

    public StringProperty validatorMessageProperty() {
        return validatorMessage;
    }

    public void setValidatorMessage(String validatorMessage) {
        this.validatorMessage.set(validatorMessage);
    }

    public BooleanListBinding validationProperty() {
        return validation;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public boolean isValid() {
        return this.validation.get();
    }

    @Override
    public void addInvalidationListener(InvalidationListener listener) {
        this.validation.addListener(listener);
    }

    @Override
    public void addChangeListener(ChangeListener<? super Boolean> listener) {
        this.validation.addListener(listener);
    }

    @Override
    public void removeInvalidationListener(InvalidationListener listener) {
        this.validation.removeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener<? super Boolean> listener) {
        this.validation.removeListener(listener);
    }

    /**
     * Returns all booleans as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Values: ");
        for (BooleanProperty bp : conditions) {
            sb.append(bp.get()).append(", ");
        }
        return StringUtils.replaceLast(sb.toString(), ",", ".");
    }
}
