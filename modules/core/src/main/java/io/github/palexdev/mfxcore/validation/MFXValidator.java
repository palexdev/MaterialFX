/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import io.github.palexdev.mfxcore.enums.ChainMode;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/// A basic implementation of a validator in JavaFX.
///
/// This validator allows specifying the conditions to be met as [Constraints][Constraint], and also allows adding other [MFXValidator]s
/// as dependencies, meaning that the validator will be valid only when all its constraints **and** dependencies are valid.
///
/// You can track every single constraint change by defining the [#setOnUpdated(BiConsumer)] action performed when the
/// [#update()] method is triggered.
///
/// You have two ways of querying the validator's state:
///  1) Query the [#validProperty()]
///  2) Call [#validate()]
public class MFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    private final ObservableList<Constraint> constraints = FXCollections.observableArrayList();
    private final ObservableList<MFXValidator> dependencies = FXCollections.observableArrayList();
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(true);
    private BiConsumer<Boolean, List<Constraint>> onUpdated;
    private boolean sortBySeverity = true;
    private boolean failFast = false;

    protected When<Boolean> when;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXValidator() {
        constraints.addListener((InvalidationListener) _ -> update());
        dependencies.addListener((InvalidationListener) _ -> update());
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the given [Constraint] to the validator's constraint list.
    ///
    /// Also adds an [InvalidationListener] to the constraint's condition to trigger the [#update()] method when it changes.
    /// This is needed to automatically update the [#validProperty()].
    ///
    /// The listener is built using the new [When] construct.
    public MFXValidator constraint(Constraint constraint) {
        constraint.when = When.onInvalidated(constraint.getCondition()).then(_ -> update()).listen();
        constraints.add(constraint);
        return this;
    }

    /// Delegates to [#constraint(Constraint)].
    public MFXValidator constraints(Constraint... constraints) {
        for (Constraint constraint : constraints) {
            constraint(constraint);
        }
        return this;
    }

    /// Removes the given [Constraint] from the validator.
    ///
    /// Also invokes [#dispose()] to properly dispose the listener added by [#constraint(Constraint)].
    public MFXValidator removeConstraint(Constraint constraint) {
        if (constraints.remove(constraint)) constraint.dispose();
        return this;
    }

    /// Delegates to [#removeConstraint(Constraint)].
    public MFXValidator removeConstraints(Constraint... constraints) {
        for (Constraint constraint : constraints) {
            removeConstraint(constraint);
        }
        return this;
    }

    /// Adds the given [MFXValidator] dependency to this validator.
    ///
    /// Also adds an [InvalidationListener] to the dependency [#validProperty()] to trigger the [#update()] method when it changes.
    /// This is needed to automatically update the [#validProperty()].
    public MFXValidator dependsOn(MFXValidator validator) {
        validator.when = When.onInvalidated(validator.validProperty()).then(value -> update()).listen();
        dependencies.add(validator);
        return this;
    }

    /// Removes the given validator dependency from this validator.
    ///
    /// Also calls [#dispose()] on the dependency to properly dispose the listener added by [#dependsOn(MFXValidator)].
    public MFXValidator removeDependency(MFXValidator validator) {
        if (dependencies.remove(validator)) validator.dispose();
        return this;
    }

    /// This method queries all the validator's dependencies and constraints to build a list containing all the unmet constraints.
    ///
    /// If the list is not empty, then the validator's state is invalid.
    ///
    /// The list can also be sorted by constraint severity by setting [#setSortBySeverity(boolean)] to true.
    ///
    /// The method can be also set to "fail fast" meaning that we do not care about all the invalid conditions,
    /// but it's also enough to get the first one. This applies to both dependencies and constraints.
    /// In this case the sorting is ignored, of course, since the list will always contain at most one constraint.
    public List<Constraint> validate() {
        List<Constraint> invalidConstraints = new ArrayList<>();
        for (MFXValidator dependency : dependencies) {
            if (!dependency.isValid()) {
                if (failFast) return List.of(dependency.validate().getFirst());
                invalidConstraints.addAll(dependency.validate());
            }
        }
        for (Constraint constraint : constraints) {
            if (!constraint.isValid()) {
                invalidConstraints.add(constraint);
                if (failFast) return invalidConstraints;
            }
        }
        if (sortBySeverity) invalidConstraints.sort(Comparator.comparing(Constraint::getSeverity));
        return invalidConstraints;
    }

    /// This is the method responsible for updating the validator's state.
    ///
    /// Despite being public, it should not be necessary to call it automatically as the constraints and the dependencies
    /// automatically trigger this method.
    ///
    /// Note that constraints are evaluated in order of insertion and according to their [Constraint#getChainMode()],
    ///  so be careful with `OR` modes.
    ///
    ///
    /// At the end invokes [#onUpdated()].
    public void update() {
        boolean valid = true;
        for (MFXValidator dependency : dependencies) {
            valid = valid && dependency.isValid();
        }
        for (Constraint constraint : constraints) {
            valid = ChainMode.chain(constraint.getChainMode(), valid, constraint.isValid());
        }
        setValid(valid);
        onUpdated();
    }

    /// Calls [#validate()] then chains all the invalid constraints' messages into a String.
    public String validateToString() {
        List<Constraint> invalidConstraints = validate();
        if (invalidConstraints.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        invalidConstraints.forEach(constraint -> sb.append(constraint.getMessage()).append("\n"));
        return sb.toString();
    }

    /// This is called when the [#update()] method is triggered, and it's responsible for running the action specified
    /// by the user, [#setOnUpdated(BiConsumer)].
    protected void onUpdated() {
        if (onUpdated != null) {
            onUpdated.accept(isValid(), validate());
        }
    }

    /// Used when another validator is being removed from the dependencies.
    ///
    /// When a dependency is added, a listener is added to update the main validator with the [When] construct.
    /// Since we need the instance to properly dispose of it afterward, we store the reference here.
    /// The disposal is easily and automatically handled by [#removeDependency(MFXValidator)].
    protected void dispose() {
        if (when != null) when.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return whether the validator' state is valid
    public boolean isValid() {
        return valid.get();
    }

    /// Specifies the validator' state. This is given by chaining all the validator's dependencies and constraints.
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    protected void setValid(boolean valid) {
        this.valid.set(valid);
    }

    /// @return the action to perform after an [#update()]
    /// @see #setOnUpdated(BiConsumer)
    public BiConsumer<Boolean, List<Constraint>> getOnUpdated() {
        return onUpdated;
    }

    /// Allows specifying the action to perform every time the [#update()] method is triggered.
    /// The action is a [BiConsumer] carrying the validator's state and the list of invalid constraints (empty if valid, of course).
    public MFXValidator setOnUpdated(BiConsumer<Boolean, List<Constraint>> onUpdated) {
        this.onUpdated = onUpdated;
        return this;
    }

    /// @return whether the invalid constraints list is sorted by severity
    public boolean isSortBySeverity() {
        return sortBySeverity;
    }

    /// Allows to specify whether to sort the invalid constraints list by severity when
    /// calling [#validate()].
    public MFXValidator setSortBySeverity(boolean sortBySeverity) {
        this.sortBySeverity = sortBySeverity;
        return this;
    }

    /// @return whether the [#validate()] method should fail fast
    public boolean isFailFast() {
        return failFast;
    }

    /// Sets whether the [#validate()] method should fail fast.
    public MFXValidator setFailFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }
}
