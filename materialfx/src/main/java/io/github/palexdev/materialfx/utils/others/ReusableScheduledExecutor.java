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

package io.github.palexdev.materialfx.utils.others;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class wraps a {@link ScheduledExecutorService} to make it reusable by keeping
 * a reference to the {@link ScheduledFuture}.
 */
public class ReusableScheduledExecutor {
	//================================================================================
	// Properties
	//================================================================================
	private final ScheduledExecutorService service;
	private ScheduledFuture<?> task;

	//================================================================================
	// Constructors
	//================================================================================
	public ReusableScheduledExecutor(ScheduledExecutorService service) {
		this.service = service;
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Cancels the task by calling {@link ScheduledFuture#cancel(boolean)} with false as argument.
	 */
	public void cancel() {
		task.cancel(false);
	}

	/**
	 * Cancels the task by calling {@link ScheduledFuture#cancel(boolean)} with true as argument.
	 */
	public void cancelNow() {
		task.cancel(true);
	}

	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		task = service.schedule(command, delay, unit);
		return task;
	}

	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		task = service.schedule(callable, delay, unit);
		return (ScheduledFuture<V>) task;
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		task = service.scheduleAtFixedRate(command, initialDelay, period, unit);
		return task;
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		task = service.scheduleWithFixedDelay(command, initialDelay, delay, unit);
		return task;
	}

	//================================================================================
	// Getter/Setters
	//================================================================================
	public ScheduledExecutorService getService() {
		return service;
	}
}
