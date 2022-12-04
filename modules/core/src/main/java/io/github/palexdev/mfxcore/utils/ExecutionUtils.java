/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utils class to help with concurrency and callables.
 */
public class ExecutionUtils {

	private ExecutionUtils() {
	}

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
}
