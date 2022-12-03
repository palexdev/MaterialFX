package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.enums.BindingType;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.value.ObservableValue;

/**
 * Concrete implementation of {@link AbstractSource}. This type of {@code Source} is capable of
 * mapping the source's type S to the target's type and vice-versa to perform both {@link #updateTarget(Object, Object)}
 * and {@link #updateSource(Object, Object)}.
 *
 * @param <S> the source's observable type
 * @param <T> the target's observable type
 */
public class MappingSource<S, T> extends AbstractSource<S, T> {
	//================================================================================
	// Properties
	//================================================================================
	protected MappedUpdater<S, T> targetUpdater; // From source to target
	protected MappedUpdater<T, S> sourceUpdater; // From target to source

	//================================================================================
	// Constructors
	//================================================================================
	protected MappingSource() {
	}

	public MappingSource(ObservableValue<? extends S> observable) {
		super(observable);
	}

	public static <S, T> MappingSource<S, T> of(ObservableValue<? extends S> observable) {
		return new MappingSource<>(observable);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operates differently depending on which binding activated this.
	 * <p>
	 * For unidirectional bindings the target instance is null so the {@link #getTargetUpdater()} is called
	 * then exits.
	 * <p>
	 * For bidirectional bindings the target instance is not null. Before calling the {@link #getTargetUpdater()}
	 * we first must check that the update is not has not been invoked because of a "bounce" effect, {@link Target#isFromSource()}.
	 */
	@Override
	public void updateTarget(S oldValue, S newValue) {
		if (target.bindingType() == BindingType.UNIDIRECTIONAL) {
			try {
				target.ignoreBinding = true;
				targetUpdater.update(oldValue, newValue);
			} finally {
				target.ignoreBinding = false;
			}
			return;
		}

		if (target.isFromSource()) return;
		targetUpdater.update(oldValue, newValue);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operates differently depending on which binding activated this.
	 * <p>
	 * For unidirectional bindings the target instance is null so the {@link #getTargetUpdater()} is called
	 * then exits.
	 * <p>
	 * For bidirectional bindings the target instance is not null. The call to {@link #getSourceUpdater()}
	 * is surrounded by a try-finally block in which we also set {@link Target#isFromSource()} to true, if anything goes wrong
	 * the finally block ensures to reset {@link Target#isFromSource()} back to false.
	 */
	@Override
	public void updateSource(T oldValue, T newValue) {
		if (target == null) {
			sourceUpdater.update(oldValue, newValue);
			return;
		}

		try {
			target.fromSource = true;
			sourceUpdater.update(oldValue, newValue);
		} finally {
			target.fromSource = false;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For unidirectional bindings. The listener to this source's observable is added by using
	 * {@link When#onChanged(ObservableValue)} and is responsible for triggering {@link #updateTarget(Object, Object)}.
	 */
	@Override
	protected void listen() {
		When.onChanged(observable)
				.then(this::updateTarget)
				.listen();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For bidirectional bindings. The source's target is set to the given one.
	 * Then {@link #listen()} is called. Then a listener to the given target is added by using
	 * {@link When#onChanged(ObservableValue)} and is responsible for triggering {@link #updateSource(Object, Object)}.
	 */
	@Override
	protected void listen(Target<T> target) {
		listen();

		this.target = target;
		When.onChanged(target.getObservable())
				.then(this::updateSource)
				.listen();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Uses {@link When#disposeFor(ObservableValue)} to remove the listeners added to this source's observable
	 * and the target's observable. Then sets all its properties to null.
	 */
	@Override
	public void dispose() {
		When.disposeFor(observable);
		if (target != null) When.disposeFor(target.getObservable());
		observable = null;
		target = null;
		targetUpdater = null;
		sourceUpdater = null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the {@link MappedUpdater} responsible for mapping the source's values to a compatible type and then
	 * update the target
	 */
	public MappedUpdater<S, T> getTargetUpdater() {
		return targetUpdater;
	}

	/**
	 * Sets the {@link MappedUpdater} for the target.
	 */
	public MappingSource<S, T> setTargetUpdater(MappedUpdater<S, T> targetUpdater) {
		this.targetUpdater = targetUpdater;
		return this;
	}

	/**
	 * Sets the {@link MappedUpdater} for the target.
	 */
	public MappingSource<S, T> setTargetUpdater(Mapper<S, T> mapper, Updater<T> updater) {
		this.targetUpdater = new MappedUpdater<>(mapper, updater);
		return this;
	}

	/**
	 * @return the {@link MappedUpdater} responsible for mapping the target's values to a compatible type and then
	 * update the source
	 */
	public MappedUpdater<T, S> getSourceUpdater() {
		return sourceUpdater;
	}

	/**
	 * Sets the {@link MappedUpdater} for the source.
	 */
	public MappingSource<S, T> setSourceUpdater(MappedUpdater<T, S> sourceUpdater) {
		this.sourceUpdater = sourceUpdater;
		return this;
	}

	/**
	 * Sets the {@link MappedUpdater} for the source.
	 */
	public MappingSource<S, T> setSourceUpdater(Mapper<T, S> mapper, Updater<S> updater) {
		this.sourceUpdater = new MappedUpdater<>(mapper, updater);
		return this;
	}

	//================================================================================
	// Builder
	//================================================================================
	public static class Builder<S, T> {
		private final MappingSource<S, T> source = new MappingSource<>();

		public Builder<S, T> observable(ObservableValue<? extends S> observable) {
			source.observable = observable;
			return this;
		}

		public Builder<S, T> targetUpdater(MappedUpdater<S, T> targetUpdater) {
			source.targetUpdater = targetUpdater;
			return this;
		}

		public Builder<S, T> sourceUpdater(MappedUpdater<T, S> sourceUpdater) {
			source.sourceUpdater = sourceUpdater;
			return this;
		}

		public MappingSource<S, T> get() {
			if (source.observable == null) throw new NullPointerException("Source is invalid as observable is null");
			return source;
		}
	}
}

