package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;
import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.enums.BindingState;
import io.github.palexdev.mfxcore.enums.BindingType;
import javafx.beans.value.ObservableValue;

/**
 * Concrete implementation of {@link AbstractBinding} to define unidirectional bindings.
 * <p></p>
 * Unidirectional bindings have a single source and can have multiple other sources, {@link ExternalSource},
 * which should be used to invalidate the binding when needed, in special occasions.
 * <p>
 * {@link #invalidateSource()} is unsupported as unidirectional bindings do not update their source.
 * <p></p>
 * Note that you can use whatever source you want as long as the source produces, when updating, values compatible
 * with this binding's target type.
 *
 * @param <T> the binding's target type
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Binding<T> extends AbstractBinding<T> {
	//================================================================================
	// Properties
	//================================================================================
	private AbstractSource source;

	//================================================================================
	// Constructors
	//================================================================================
	public Binding() {
	}

	public Binding(ObservableValue<? extends T> target, AbstractSource<?, ?> source) {
		super.target = new Target<>(target);
		this.source = source;
	}

	public static <T> Binding<T> create() {
		return new Binding<>();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets this binding's target.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public Binding<T> target(ObservableValue<? extends T> observable) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set target as this binding is currently active");
		super.target = new Target<>(observable);
		return this;
	}

	/**
	 * Sets this binding's source, as no updater is given attempts to use {@link Updater#implicit(Target)}.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public Binding<T> source(ObservableValue<? extends T> source) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set source as this binding is currently active");
		this.source = new Source<>(source).setTargetUpdater(Updater.implicit(target));
		return this;
	}

	/**
	 * Sets this binding' source.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public <S> Binding<T> source(AbstractSource<S, T> source) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set source as this binding is currently active");
		this.source = source;
		return this;
	}

	/**
	 * Sets the binding' source from the given {@link Source.Builder}.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public <S> Binding<T> source(Source.Builder<S> sBuilder) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set source as this binding is currently active");
		this.source = sBuilder.get();
		return this;
	}

	/**
	 * Sets the binding' source from the given {@link MappingSource.Builder}.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public <S> Binding<T> source(MappingSource.Builder<S, T> sBuilder) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set source as this binding is currently active");
		this.source = sBuilder.get();
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Before activating the binding checks if there are already unidirectional or bidirectional bindings registered
	 * for the given target and eventually disposes them.
	 * <p>
	 * Then activates the source with {@link AbstractSource#listen()}, registers the binding in {@link MFXBindings}
	 * and sets the state to {@link BindingState#BOUND}.
	 *
	 * @throws IllegalStateException if the binding has been disposed before OR the target is null
	 *                               OR the source is null
	 */
	@Override
	public Binding<T> get() {
		if (isDisposed())
			throw new IllegalStateException("This binding has been previously disposed and cannot be used anymore");
		if (target == null) throw new IllegalStateException("Cannot bind as target is null");
		if (source == null) throw new IllegalStateException("Cannot bind as source is null");
		MFXBindings bindings = MFXBindings.instance();

		if (bindings.isBoundBidirectional(target)) {
			bindings.getBiBinding(target).dispose();
		}

		if (mayBeBound() || bindings.isBound(target)) {
			bindings.getBinding(target).dispose();
		}

		target.bindingType = BindingType.UNIDIRECTIONAL;
		source.target = target;
		source.listen();
		bindings.addBinding(this);
		state = BindingState.BOUND;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Also runs {@link #getBeforeTargetInvalidation()} and {@link #getAfterTargetInvalidation()}.
	 */
	@Override
	public Binding<T> invalidate() {
		beforeTargetInvalidation.run();
		source.updateTarget(source.getValue(), source.getValue());
		afterTargetInvalidation.run();
		return this;
	}

	/**
	 * Unsupported.
	 *
	 * @throws UnsupportedOperationException unidirectional bindings cannot update their source
	 */
	@Override
	public IBinding<T> invalidateSource() {
		throw new UnsupportedOperationException("Unidirectional bindings cannot update their source");
	}

	@Override
	public <S> Binding<T> addInvalidatingSource(ExternalSource<S> source) {
		source.listen();
		invalidatingSources.put(source.getObservable(), source);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Disposes the source and sets it to null, calls {@link #clearInvalidatingSources()},
	 * sets the state to {@link BindingState#UNBOUND} then un-registers the binding from {@link MFXBindings}.
	 */
	@Override
	public Binding<T> unbind() {
		source.dispose();
		source = null;
		clearInvalidatingSources();
		state = BindingState.UNBOUND;
		MFXBindings.instance().removeBinding(this);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Calls {@link #unbind()}.
	 * <p>
	 * Then sets the remaining properties to null and the state to {@link BindingState#DISPOSED}.
	 */
	@Override
	public void dispose() {
		unbind();
		target.dispose();
		target = null;
		state = BindingState.DISPOSED;
	}
}
