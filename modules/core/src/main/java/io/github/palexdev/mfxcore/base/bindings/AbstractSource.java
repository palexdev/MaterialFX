package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.ISource;
import javafx.beans.value.ObservableValue;

/**
 * Base class for all types of {@code Sources}, implements {@link ISource}.
 * <p></p>
 * This class allows to have common properties in one place (such as the source's observable and the target), but also
 * implements common methods and defines methods that need to be used only internally.
 *
 * @param <S> the type of the source's observable
 * @param <T> the type of the target's observable
 */
public abstract class AbstractSource<S, T> implements ISource<S, T> {
	//================================================================================
	// Properties
	//================================================================================
	protected ObservableValue<? extends S> observable;
	protected Target<T> target;

	//================================================================================
	// Constructors
	//================================================================================
	protected AbstractSource() {
	}

	public AbstractSource(ObservableValue<? extends S> observable) {
		this.observable = observable;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Activates the source by adding the needed listeners.
	 */
	protected abstract void listen();

	/**
	 * Activates the source by adding the needed listeners, unlike {@link #listen()} this
	 * is used by bidirectional biding, in fact a listener is also added to the given target
	 * to trigger the target update when this changes.
	 */
	protected abstract void listen(Target<T> target);

	//================================================================================
	// Methods
	//================================================================================
	@Override
	public ObservableValue<? extends S> getObservable() {
		return observable;
	}

	@Override
	public S getValue() {
		return observable.getValue();
	}
}
