/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.validation.base;

import io.github.palexdev.materialfx.beans.binding.BooleanListBinding;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Base class for all validators.
 * <p></p>
 * Defines the common properties every validator should have, such as:
 * a message that reflects the validator's state, a list of conditions that must be met
 * in order for the state to be valid and a way to chain multiple validators considered
 * as "dependencies".
 * <p></p>
 * Those conditions are evaluated by a {@link BooleanListBinding} and doesn't always represent
 * the validator's state. In fact the validator's state is represented by another property, {@link #validProperty()}
 * which takes into account all the validator's dependencies too.
 * <p>
 * The valid property is bound to the {@link BooleanListBinding} but when a change occurs in the dependencies list
 * then its value is updated by the {@link #update()} method, which temporarily un-bounds the property and re-computes
 * its value.
 *
 * @see IMFXValidator
 * @see BooleanListBinding
 */
public abstract class AbstractMFXValidator implements IMFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    protected final StringProperty validatorMessage = new SimpleStringProperty("Validation failed!");
    protected final ObservableList<BooleanProperty> conditions;
    protected final ObservableList<AbstractMFXValidator> dependencies;
    protected final BooleanListBinding listBinding;
    protected final ReadOnlyBooleanWrapper valid;
    protected boolean initControlValidation = false;

    //================================================================================
    // Constructors
    //================================================================================
    public AbstractMFXValidator() {
        conditions = FXCollections.observableArrayList();
        dependencies = FXCollections.observableArrayList();
        listBinding = new BooleanListBinding(conditions);
        valid = new ReadOnlyBooleanWrapper();
        addListeners();
    }

    private void addListeners() {
        valid.bind(listBinding);
        dependencies.addListener((InvalidationListener) invalidated -> update());
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Updates the {@link #validProperty()} value as follows:
     * <p>
     * Unbinds the property, then defines a new {@link BooleanBinding} variable
     * instantiated as the {@link BooleanListBinding}, ({@code BooleanBinding binding = listBinding}),
     * then for each dependency applies the {@link BooleanBinding#and(ObservableBooleanValue)} method on
     * that variable with the dependency valid property as the argument.
     * <p>
     * At the end binds again the valid property to the new {@link BooleanBinding} variable.
     */
    public void update() {
        valid.unbind();
        BooleanBinding binding = listBinding;
        for (AbstractMFXValidator dependency : dependencies) {
            binding = binding.and(dependency.validProperty());
        }
        valid.bind(binding);
    }

    public String getValidatorMessage() {
        return validatorMessage.get();
    }

    /**
     * Specifies the validator's message.
     */
    public StringProperty validatorMessageProperty() {
        return validatorMessage;
    }

    public void setValidatorMessage(String validatorMessage) {
        this.validatorMessage.set(validatorMessage);
    }

    /**
     * @return the dependencies list
     */
    public ObservableList<AbstractMFXValidator> getDependencies() {
        return dependencies;
    }

    /**
     * Adds the specified dependencies to the list.
     */
    public void addDependencies(AbstractMFXValidator... dependencies) {
        this.dependencies.addAll(dependencies);
    }

    /**
     * Removes the specifies dependencies from the list.
     * <p>
     * Note: to remove all dependencies if you don't have their instance you can also
     * get the dependencies list with {@link #getDependencies()} and then call the clear() method.
     */
    public void removeDependencies(AbstractMFXValidator... dependencies) {
        this.dependencies.removeAll(dependencies);
    }

    public boolean isInitControlValidation() {
        return initControlValidation;
    }

    public void setInitControlValidation(boolean initControlValidation) {
        this.initControlValidation = initControlValidation;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public boolean isValid() {
        return this.valid.get();
    }

    /**
     * Specifies the validator's state.
     */
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    /**
     * Wrapper method to add an {@link InvalidationListener} to the valid property of the validator.
     */
    @Override
    public void addListener(InvalidationListener listener) {
        this.valid.addListener(listener);
    }

    /**
     * Wrapper method to add a {@link ChangeListener} to the valid property of the validator.
     */
    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {
        this.valid.addListener(listener);
    }

    /**
     * Wrapper method to remove an {@link InvalidationListener} from the valid property of the validator.
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        this.valid.removeListener(listener);
    }

    /**
     * Wrapper method to remove a {@link ChangeListener} from the valid property of the validator.
     */
    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
        this.valid.removeListener(listener);
    }

    /**
     * Returns all the boolean properties as a string.
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
