/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.validation;

import io.github.palexdev.materialfx.enums.ChainMode;
import io.github.palexdev.materialfx.utils.others.observables.When;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A basic implementation of a validator in JavaFX.
 * <p>
 * This validator allows to specify the conditions to be met as {@link Constraint}s,
 * and also allows to add other {@link MFXValidator}s as dependencies, meaning that
 * the validator will be valid only when all its constraints are valid and all its dependencies
 * are also valid.
 * <p></p>
 * You can track every single constraint change by defining the {@link #setOnUpdated(BiConsumer)}
 * action performed when the {@link #update()} method is triggered.
 * <p></p>
 * You have two ways of querying the validator's state:
 * <p> One is to simply query the {@link #validProperty()}
 * <p> The other is to call {@link #validate()}
 */
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

	//================================================================================
	// Constructors
	//================================================================================
	public MFXValidator() {
		constraints.addListener((InvalidationListener) invalidated -> update());
		dependencies.addListener((InvalidationListener) invalidated -> update());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the given {@link Constraint} to the validator's constraint list.
	 * <p>
	 * Also adds an {@link InvalidationListener} to the constraint's condition
	 * to trigger the {@link #update()} method when it changes. This is needed
	 * to automatically update the {@link #validProperty()}.
	 * <p></p>
	 * The listener is build using the new {@link When} construct.
	 */
	public MFXValidator constraint(Constraint constraint) {
		When.onInvalidated(constraint.getCondition()).then(value -> update()).listen();
		constraints.add(constraint);
		return this;
	}

	/**
	 * Creates a {@link Constraint} with the given parameters, then calls {@link #constraint(Constraint)}.
	 */
	public MFXValidator constraint(Severity severity, String message, BooleanExpression condition) {
		return constraint(Constraint.of(severity, message, condition));
	}

	/**
	 * Creates a {@link Constraint} with ERROR severity and the given message and condition, then calls {@link #constraint(Constraint)}.
	 */
	public MFXValidator constraint(String message, BooleanExpression condition) {
		return constraint(Severity.ERROR, message, condition);
	}

	/**
	 * Removes the given {@link Constraint} from the validator.
	 * <p>
	 * Also invokes {@link When#disposeFor(ObservableValue)} on the
	 * constraint's condition (see {@link #constraint(Constraint)} and {@link When}).
	 */
	public MFXValidator removeConstraint(Constraint constraint) {
		if (constraints.remove(constraint)) {
			When.disposeFor(constraint.getCondition());
		}
		return this;
	}

	/**
	 * Adds the given {@link MFXValidator} dependency to this validator.
	 * <p>
	 * Also adds an {@link InvalidationListener} to the dependency {@link #validProperty()}
	 * to trigger the {@link #update()} method when it changes. This is needed to automatically update
	 * the {@link #validProperty()}
	 */
	public MFXValidator dependsOn(MFXValidator validator) {
		When.onInvalidated(validator.validProperty()).then(value -> update()).listen();
		dependencies.add(validator);
		return this;
	}

	/**
	 * Removes the given validator dependency from this validator.
	 * <p>
	 * Also calls {@link When#disposeFor(ObservableValue)} on the dependency's
	 * valid property (see {@link #dependsOn(MFXValidator)} and {@link When}).
	 */
	public MFXValidator removeDependency(MFXValidator validator) {
		if (dependencies.remove(validator)) {
			When.disposeFor(validator.validProperty());
		}
		return this;
	}

	/**
	 * This method queries all the validator's dependencies and constraints
	 * to build a list containing all the unmet constraints.
	 * <p>
	 * If the list is not empty then the validator' state is invalid.
	 * <p></p>
	 * The list can also be sorted by constraint severity by setting
	 * {@link #setSortBySeverity(boolean)} to true.
	 * <p></p>
	 * The method can be also set to "fail fast" meaning that we do not
	 * care about all the invalid conditions but it's also enough to get
	 * the first one. This applies to both dependencies and constraints.
	 * In this case the sorting is ignored of course since the list
	 * will always contain at most one constraint.
	 */
	public List<Constraint> validate() {
		List<Constraint> invalidConstraints = new ArrayList<>();
		for (MFXValidator dependency : dependencies) {
			if (!dependency.isValid()) {
				if (failFast) return List.of(dependency.validate().get(0));
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

	/**
	 * This is the method responsible for updating the validator' state.
	 * Despite being public it should not be necessary to call it automatically as the
	 * constraints and the dependencies automatically trigger this method.
	 * <p>
	 * Note that constraints are evaluated in order of insertion and according to their
	 * {@link Constraint#getChainMode()}, so be careful with OR modes.
	 * <p></p>
	 * At the end invokes {@link #onUpdated()}.
	 */
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

	/**
	 * Calls {@link #validate()} then chains all the invalid constraints' messages
	 * into a String.
	 */
	public String validateToString() {
		List<Constraint> invalidConstraints = validate();
		if (invalidConstraints.isEmpty()) return "";

		StringBuilder sb = new StringBuilder();
		invalidConstraints.forEach(constraint -> sb.append(constraint.getMessage()).append("\n"));
		return sb.toString();
	}

	/**
	 * This is called when the {@link #update()} method is triggered and
	 * it's responsible for running the action specified by the user, {@link #setOnUpdated(BiConsumer)}.
	 */
	protected void onUpdated() {
		if (onUpdated != null) {
			onUpdated.accept(isValid(), validate());
		}
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return whether the validator' state is valid
	 */
	public boolean isValid() {
		return valid.get();
	}

	/**
	 * Specifies the validator' state. This is given by chaining all the
	 * validator's dependencies and constraints.
	 */
	public ReadOnlyBooleanProperty validProperty() {
		return valid.getReadOnlyProperty();
	}

	protected void setValid(boolean valid) {
		this.valid.set(valid);
	}

	/**
	 * @return the action to perform after an {@link #update()}
	 * @see #setOnUpdated(BiConsumer)
	 */
	public BiConsumer<Boolean, List<Constraint>> getOnUpdated() {
		return onUpdated;
	}

	/**
	 * Allows to specify the action to perform every time the {@link #update()} method
	 * is triggered. The action is a {@link BiConsumer} carrying the validator' state
	 * and the list of invalid constraints (empty if valid of course).
	 */
	public MFXValidator setOnUpdated(BiConsumer<Boolean, List<Constraint>> onUpdated) {
		this.onUpdated = onUpdated;
		return this;
	}

	/**
	 * @return whether the invalid constraints list is sorted by severity
	 */
	public boolean isSortBySeverity() {
		return sortBySeverity;
	}

	/**
	 * Allows to specify whether to sort the invalid constraints list by severity when
	 * calling {@link #validate()}.
	 */
	public MFXValidator setSortBySeverity(boolean sortBySeverity) {
		this.sortBySeverity = sortBySeverity;
		return this;
	}

	/**
	 * @return whether the {@link #validate()} method should fail fast
	 */
	public boolean isFailFast() {
		return failFast;
	}

	/**
	 * Sets whether the {@link #validate()} method should fail fast.
	 */
	public MFXValidator setFailFast(boolean failFast) {
		this.failFast = failFast;
		return this;
	}
}
