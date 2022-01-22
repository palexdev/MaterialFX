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

package io.github.palexdev.materialfx.utils;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Utils class to help with concurrency and callables.
 */
public class ExecutionUtils {

	private static class ThrowableWrapper {
		Throwable t;
	}

	/**
	 * Invokes a Runnable on the JavaFX Application Thread and waits for it to finish.
	 *
	 * @param run The Runnable that has to be called on JFX thread.
	 * @throws InterruptedException If the execution is interrupted.
	 * @throws ExecutionException   If an exception is occurred in the run method of the Runnable
	 */
	public static void runAndWaitEx(final Runnable run)
			throws InterruptedException, ExecutionException {
		if (Platform.isFxApplicationThread()) {
			try {
				run.run();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		} else {
			final Lock lock = new ReentrantLock();
			final Condition condition = lock.newCondition();
			final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
			lock.lock();
			try {
				Platform.runLater(() -> {
					lock.lock();
					try {
						run.run();
					} catch (Throwable e) {
						throwableWrapper.t = e;
					} finally {
						try {
							condition.signal();
						} finally {
							lock.unlock();
						}
					}
				});
				condition.await();
				if (throwableWrapper.t != null) {
					throw new ExecutionException(throwableWrapper.t);
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * Calls {@link #runAndWaitEx(Runnable)} but consumes/ignores any thrown exception.
	 */
	public static void runAndWait(final Runnable runnable) {
		try {
			runAndWaitEx(runnable);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Tries to execute the given callable and prints the stacktrace in case of exception.
	 *
	 * @return the callable result or null in case of exception
	 */
	public static <V> V tryCallableAndPrint(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Tries to execute the given callable but ignores the exception in case of fail.
	 *
	 * @return the callable result or null in case of exception
	 */
	public static <V> V tryCallableAndIgnore(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception ignored) {
			return null;
		}
	}

	/**
	 * Executes the given {@link BiConsumer} action when the given {@link ObservableValue} changes.
	 * <p>
	 * The consumer inputs are the oldValue and the newValue of the observable.
	 * <p>
	 * If executeNow is true the consumer action is immediately executed with null as the oldValue
	 * and the current value as newValue, the listener is added anyway.
	 * <p>
	 * The executeCondition {@link BiFunction} is used to specify on what conditions the action can be executed,
	 * the inputs of the function are the oldValue and newValue of the observable.
	 * <p>
	 * The isOneShot flag is to specify if the added listener should be removed after the first time the observable changes.
	 *
	 * @param property           the observable to listen to
	 * @param consumer           the action to perform on change
	 * @param executeNow         to specify if the given action should be immediately executed
	 * @param executionCondition to specify on what conditions the action should be executed
	 * @param isOneShot          to specify if the added listener should be removed after the first time the observable changes
	 * @param <T>                the value type of the property
	 */
	public static <T> void executeWhen(ObservableValue<? extends T> property, BiConsumer<T, T> consumer, boolean executeNow, BiFunction<T, T, Boolean> executionCondition, boolean isOneShot) {
		if (executeNow) {
			consumer.accept(null, property.getValue());
		}

		property.addListener(new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
				if (executionCondition.apply(oldValue, newValue)) {
					consumer.accept(oldValue, newValue);
					if (isOneShot) {
						property.removeListener(this);
					}
				}
			}
		});
	}

	/**
	 * Executes the given action when the given {@link Observable} changes.
	 * <p>
	 * If executeNow is true the action is immediately executed.
	 * <p>
	 * Adds a listener to the observable and executes the given action every time the observable changes and the
	 * execution condition is met or just once if the isOneShot parameter is true.
	 *
	 * @param observable         the observable to listen to
	 * @param action             the action to execute when the observable changes
	 * @param executeNow         to specify if the given action should be immediately executed
	 * @param executionCondition to specify on what conditions the action should be executed
	 * @param isOneShot          to specify if the added listener should be removed after the first time the observable changes
	 */
	public static void executeWhen(Observable observable, Runnable action, boolean executeNow, Supplier<Boolean> executionCondition, boolean isOneShot) {
		if (executeNow) {
			action.run();
		}

		observable.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (executionCondition.get()) {
					action.run();
					if (isOneShot) {
						observable.removeListener(this);
					}
				}
			}
		});
	}

	/**
	 * Executes the given action if the given expression and the executeNow parameter are true.
	 * <p>
	 * If the given expression is false or the addListenerIfTrue parameter is true, adds a listener to
	 * the expression and executes the given action every time the property becomes true or just once if
	 * the isOneShot parameter is true.
	 *
	 * @param booleanExpression the expression to evaluate
	 * @param action            the action to execute when the expression is true
	 * @param executeNow        to specify if the given action should be immediately executed if the expression is already true
	 * @param addListenerIfTrue to specify if the listener should be added anyway to the expression even if it is already true
	 * @param isOneShot         to specify if the added listener should be removed after the first time the expression becomes true
	 */
	public static void executeWhen(BooleanExpression booleanExpression, Runnable action, boolean executeNow, boolean addListenerIfTrue, boolean isOneShot) {
		if (booleanExpression.get() && executeNow) {
			action.run();
		}

		if (!booleanExpression.get() || addListenerIfTrue) {
			booleanExpression.addListener(new ChangeListener<>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						action.run();
						if (isOneShot) {
							booleanExpression.removeListener(this);
						}
					}
				}
			});
		}
	}

	/**
	 * Executes the given truAction if the given expression and the executeTrueNow parameter are true.
	 * <p>
	 * Executes the given falseAction if the given expression is false and the executeFalseNow parameter is true.
	 * <p>
	 * If the given expression is false or the addListenerIfTrue parameter is true, adds a listener to
	 * the expression and executes the given trueAction every time the property becomes true, and the given
	 * falseAction every time the property becomes false, or just once if the isOneShot parameter is true.
	 *
	 * @param booleanExpression the expression to evaluate
	 * @param trueAction        the action to execute when the expression is true
	 * @param falseAction       the action to execute when the expression is false
	 * @param executeTrueNow    to specify if the given trueAction should be immediately executed if the expression is already true
	 * @param executeFalseNow   to specify if the given falseAction should be immediately executed if the expression is already false
	 * @param addListenerIfTrue to specify if the listener should be added anyway to the expression even if it is already true
	 * @param isOneShot         to specify if the added listener should be removed after the first time the expression becomes true
	 */
	public static void executeWhen(BooleanExpression booleanExpression, Runnable trueAction, Runnable falseAction,
	                               boolean executeTrueNow, boolean executeFalseNow, boolean addListenerIfTrue, boolean isOneShot) {
		if (booleanExpression.get() && executeTrueNow) {
			trueAction.run();
		} else if (!booleanExpression.get() && executeFalseNow) {
			falseAction.run();
		}

		if (!booleanExpression.get() || addListenerIfTrue) {
			booleanExpression.addListener(new ChangeListener<>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						trueAction.run();
					} else {
						falseAction.run();
					}
					if (isOneShot) {
						booleanExpression.removeListener(this);
					}
				}
			});
		}
	}
}
