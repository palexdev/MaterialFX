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
