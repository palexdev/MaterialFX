package io.github.palexdev.materialfx.validation;

import javafx.beans.binding.BooleanExpression;

/**
 * Bean used by {@link MFXValidator} to define a condition to be met in order
 * for the validator' state to be valid.
 * <p></p>
 * This bean allows to specify the {@link Severity} of the condition, the message
 * to show when invalid, and of course the {@link BooleanExpression} that is the condition itself
 * <p></p>
 * To build a constraint you can use the offered static methods, the {@link Builder}, or the parameterized constructors.
 */
public class Constraint {
	//================================================================================
	// Properties
	//================================================================================
	private Severity severity;
	private String message;
	private BooleanExpression condition;

	//================================================================================
	// Constructors
	//================================================================================
	protected Constraint() {
	}

	/**
	 * Calls {@link #Constraint(Severity, String, BooleanExpression)} with {@link Severity#ERROR}.
	 */
	public Constraint(String message, BooleanExpression condition) {
		this(Severity.ERROR, message, condition);
	}

	public Constraint(Severity severity, String message, BooleanExpression condition) {
		if (condition == null) {
			throw new NullPointerException("The condition cannot be null!");
		}
		this.severity = severity;
		this.message = message;
		this.condition = condition;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * @return a new {@code Constraint} with ERROR severity and the given message and condition
	 */
	public static Constraint of(String message, BooleanExpression condition) {
		return new Constraint(message, condition);
	}

	/**
	 * @return a new {@code Constraint} with the given severity, message and condition
	 */
	public static Constraint of(Severity severity, String message, BooleanExpression condition) {
		return new Constraint(severity, message, condition);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return whether the specified condition is valid
	 */
	public boolean isValid() {
		return condition.getValue();
	}

	/**
	 * @return the severity of the condition
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Sets the severity of the condition.
	 */
	protected void setSeverity(Severity severity) {
		this.severity = severity;
	}

	/**
	 * @return the message to show in case the condition is not valid
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to show in case the condition is not valid.
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the {@link BooleanExpression} used to define the condition
	 */
	public BooleanExpression getCondition() {
		return condition;
	}

	/**
	 * Sets the {@link BooleanExpression} used to define the condition.
	 */
	protected void setCondition(BooleanExpression condition) {
		this.condition = condition;
	}

	//================================================================================
	// Builder Class
	//================================================================================
	public static class Builder {
		private final Constraint constraint = new Constraint();

		public static Builder build() {
			return new Builder();
		}

		public Builder setSeverity(Severity severity) {
			constraint.setSeverity(severity);
			return this;
		}

		public Builder setMessage(String message) {
			constraint.setMessage(message);
			return this;
		}

		public Builder setCondition(BooleanExpression condition) {
			constraint.setCondition(condition);
			return this;
		}

		public Constraint get() {
			checkConstraint();
			return constraint;
		}

		private void checkConstraint() {
			Severity severity = constraint.getSeverity();
			BooleanExpression condition = constraint.getCondition();

			if (severity == null) throw new IllegalArgumentException("Severity not set!");
			if (condition == null) throw new IllegalArgumentException("Condition not set!");
		}
	}
}
