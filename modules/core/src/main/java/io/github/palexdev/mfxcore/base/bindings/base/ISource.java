package io.github.palexdev.mfxcore.base.bindings.base;

import io.github.palexdev.mfxcore.base.bindings.MappingSource;
import javafx.beans.value.ObservableValue;

/**
 * Public API every type of {@code Source} must implement.
 * <p>
 * The interface has two generics as it makes it easier to implement {@link MappingSource}s.
 *
 * @param <S> the type of the source's observable
 * @param <T> the type of the target's observable
 */
public interface ISource<S, T> {

	/**
	 * @return the source's {@link ObservableValue}
	 */
	ObservableValue<? extends S> getObservable();

	/**
	 * Uses the values of the source's observable to update the target.
	 */
	void updateTarget(S oldValue, S newValue);

	/**
	 * Uses the values of the target's observable to update the sources.
	 */
	void updateSource(T oldValue, T newValue);

	/**
	 * Disposes the source.
	 */
	void dispose();

	/**
	 * Shortcut for {@code getObservable().getValue()}.
	 */
	default S getValue() {
		return getObservable().getValue();
	}
}
