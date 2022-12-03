package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Utility class mainly used to track both {@link Binding}s and {@link BidirectionalBinding}s.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MFXBindings {
	//================================================================================
	// Static Members
	//================================================================================
	private static final MFXBindings instance = new MFXBindings();

	public static MFXBindings instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final WeakHashMap<ObservableValue, Binding> bindings = new WeakHashMap<>();
	private final WeakHashMap<ObservableValue, BidirectionalBinding> biBindings = new WeakHashMap<>();

	//================================================================================
	// Constructors
	//================================================================================
	private MFXBindings() {
	}

	//================================================================================
	// Unidirectional Bindings
	//================================================================================

	/**
	 * Delegate for {@link Binding#create()}.
	 */
	public <T> Binding<T> bind() {
		return Binding.create();
	}

	/**
	 * Shortcut for {@code Binding.create().target(target)}.
	 *
	 * @see Binding
	 */
	public <T> Binding<T> bind(ObservableValue<? extends T> target) {
		return Binding.<T>create().target(target);
	}

	/**
	 * Checks if there's already a unidirectional binding for the given observable.
	 */
	public <T> boolean isBound(ObservableValue<? extends T> observable) {
		return bindings.containsKey(observable);
	}

	/**
	 * Shortcut for {@code isBound(target.getObservable())}.
	 *
	 * @see #isBound(ObservableValue)
	 */
	public <T> boolean isBound(Target<T> target) {
		return isBound(target.getObservable());
	}

	/**
	 * If a unidirectional binding for the given observable exists, calls {@link Binding#unbind()}.
	 */
	public <T> MFXBindings unbind(ObservableValue<? extends T> observable) {
		Optional.ofNullable(bindings.get(observable))
				.ifPresent(IBinding::unbind);
		return this;
	}

	/**
	 * Disposes and clears all the registered unidirectional bindings.
	 */
	public MFXBindings dispose() {
		List<IBinding> bs = new ArrayList<>(bindings.values());
		bs.forEach(IBinding::dispose);
		return this;
	}

	/**
	 * If a unidirectional binding exists for the given observable, calls {@link Binding#dispose()}.
	 */
	public <T> MFXBindings dispose(ObservableValue<? extends T> observable) {
		Optional.ofNullable(bindings.get(observable))
				.ifPresent(IBinding::dispose);
		return this;
	}

	/**
	 * @return a {@link Binding} instance for the given observable, or null
	 * if it doesn't exist
	 */
	public <T> Binding<T> getBinding(ObservableValue<? extends T> observable) {
		return bindings.get(observable);
	}

	/**
	 * Shortcut for {@code getBinding(target.getObservable())}.
	 *
	 * @see #getBinding(ObservableValue)
	 */
	public <T> Binding<T> getBinding(Target<T> target) {
		return getBinding(target.getObservable());
	}

	/**
	 * Registers the given unidirectional binding in this utility.
	 */
	protected <T> void addBinding(Binding binding) {
		bindings.put(binding.getTarget().getObservable(), binding);
	}

	/**
	 * Unregisters the given unidirectional binding from this utility.
	 */
	protected <T> void removeBinding(Binding<T> binding) {
		bindings.remove(binding.getTarget().getObservable());
	}

	/**
	 * See {@link Target#isFromSource()}.
	 */
	public <T> boolean hasSourceChanged(ObservableValue<? extends T> observable) {
		return Optional.ofNullable(bindings.get(observable))
				.map(b -> b.getTarget().isFromSource())
				.orElse(false);
	}

	/**
	 * See {@link Target#isIgnoreBinding()}.
	 */
	public <T> boolean isIgnoreBinding(ObservableValue<? extends T> observable) {
		return Optional.ofNullable(bindings.get(observable))
				.map(b -> b.getTarget().isIgnoreBinding())
				.orElse(false);
	}

	/**
	 * If a {@link Binding} exists for the given observable, calls {@link Binding#invalidate()} on it.
	 * <p>
	 * This may be useful when creating bindings with invalidating sources with fluent API as there is no way
	 * to refer to the binding unless it is a local variable, this helps with that.
	 */
	public <T> MFXBindings invTarget(ObservableValue<? extends T> observable) {
		Optional.ofNullable(bindings.get(observable))
				.ifPresent(Binding::invalidate);
		return this;
	}

	//================================================================================
	// Bidirectional Bindings
	//================================================================================

	/**
	 * Delegate for {@link BidirectionalBinding#create()}.
	 */
	public <T> BidirectionalBinding<T> bindBidirectional() {
		return BidirectionalBinding.create();
	}

	/**
	 * Shortcut for {@code BidirectionalBinding.create().target(target)}
	 *
	 * @see BidirectionalBinding
	 */
	public <T> BidirectionalBinding<T> bindBidirectional(ObservableValue<? extends T> target) {
		return BidirectionalBinding.<T>create().target(target);
	}

	/**
	 * Checks if a bidirectional binding already exists for the given observable.
	 */
	public <T> boolean isBoundBidirectional(ObservableValue<? extends T> observable) {
		return biBindings.containsKey(observable);
	}

	/**
	 * Shortcut for {@code isBoundBidirectional(target.getObservable())}.
	 *
	 * @see #isBoundBidirectional(ObservableValue)
	 */
	public <T> boolean isBoundBidirectional(Target<T> target) {
		return isBoundBidirectional(target.getObservable());
	}

	/**
	 * If a bidirectional binding exists for the given observable, calls {@link BidirectionalBinding#unbind()}.
	 */
	public <T> MFXBindings unbindBidirectional(ObservableValue<? extends T> target) {
		Optional.ofNullable(biBindings.get(target))
				.ifPresent(IBinding::unbind);
		return this;
	}

	/**
	 * If a bidirectional binding exists for the given observable, calls {@link BidirectionalBinding#unbind(ObservableValue)}
	 */
	public <T, S> MFXBindings unbindBidirectional(ObservableValue<? extends T> target, ObservableValue<? extends S> source) {
		Optional.ofNullable(biBindings.get(target))
				.ifPresent(b -> b.unbind(source));
		return this;
	}

	/**
	 * Disposes and clears all the registered bidirectional bindings.
	 */
	public MFXBindings disposeBidirectionals() {
		List<IBinding> bs = new ArrayList<>(biBindings.values());
		bs.forEach(IBinding::dispose);
		return this;
	}

	/**
	 * If a bidirectional binding exists for the given observable, calls {@link BidirectionalBinding#dispose()}.
	 */
	public <T> MFXBindings disposeBidirectional(ObservableValue<? extends T> observable) {
		Optional.ofNullable(biBindings.get(observable))
				.ifPresent(IBinding::dispose);
		return this;
	}

	/**
	 * @return a {@link BidirectionalBinding} instance for the given observable, or null
	 * if it doesn't exist
	 */
	public <T> BidirectionalBinding<T> getBiBinding(ObservableValue<? extends T> observable) {
		return biBindings.get(observable);
	}


	/**
	 * Shortcut for {@code getBiBinding(target.getObservable())}.
	 *
	 * @see #getBiBinding(Target)
	 */
	public <T> BidirectionalBinding<T> getBiBinding(Target<T> target) {
		return getBiBinding(target.getObservable());
	}

	/**
	 * Registers the given bidirectional binding in this utility.
	 */
	protected <T> void addBinding(BidirectionalBinding<T> binding) {
		biBindings.put(binding.getTarget().getObservable(), binding);
	}

	/**
	 * Unregisters the given bidirectional binding from this utility.
	 */
	protected <T> void removeBinding(BidirectionalBinding<T> binding) {
		biBindings.remove(binding.getTarget().getObservable());
	}

	/**
	 * If a {@link BidirectionalBinding} exists for the given observable, calls {@link BidirectionalBinding#invalidate()} on it.
	 * <p>
	 * This may be useful when creating bindings with invalidating sources with fluent API as there is no way
	 * to refer to the binding unless it is a local variable, this helps with that.
	 */
	public <T> MFXBindings biInvalidate(ObservableValue<? extends T> observable) {
		Optional.ofNullable(biBindings.get(observable))
				.ifPresent(BidirectionalBinding::invalidate);
		return this;
	}

	/**
	 * If a {@link BidirectionalBinding} exists for the given observable, calls {@link BidirectionalBinding#invalidateSource()} on it.
	 * <p>
	 * This may be useful when creating bindings with invalidating sources with fluent API as there is no way
	 * to refer to the binding unless it is a local variable, this helps with that.
	 */
	public <T> MFXBindings biInvalidateSources(ObservableValue<? extends T> observable) {
		Optional.ofNullable(biBindings.get(observable))
				.ifPresent(BidirectionalBinding::invalidateSource);
		return this;
	}

	//================================================================================
	// Misc
	//================================================================================

	/**
	 * @return the number of registered unidirectional bindings
	 */
	public int size() {
		return bindings.size();
	}

	/**
	 * @return the number of registered bidirectional bindings
	 */
	public int biSize() {
		return biBindings.size();
	}

	/**
	 * @return whether {@link #size()} is 0
	 */
	public boolean isEmpty() {
		return bindings.isEmpty();
	}

	/**
	 * @return whether {@link #biSize()} is 0
	 */
	public boolean biIsEmpty() {
		return biBindings.isEmpty();
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyBooleanWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyBooleanWrapper target, boolean bidirectional) {
		ReadOnlyBooleanProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyStringWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyStringWrapper target, boolean bidirectional) {
		ReadOnlyStringProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyIntegerWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyIntegerWrapper target, boolean bidirectional) {
		ReadOnlyIntegerProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyLongWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyLongWrapper target, boolean bidirectional) {
		ReadOnlyLongProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyFloatWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyFloatWrapper target, boolean bidirectional) {
		ReadOnlyFloatProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyDoubleWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public MFXBindings unbindReadOnly(ReadOnlyDoubleWrapper target, boolean bidirectional) {
		ReadOnlyDoubleProperty obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}

	/**
	 * Retrieves the read-only property with {@link ReadOnlyObjectWrapper#getReadOnlyProperty()} and unbinds it.
	 * <p></p>
	 * The "bidirectional" parameter is fundamental as it determines whether {@link #unbind(ObservableValue)}
	 * is called or {@link #unbindBidirectional(ObservableValue)}
	 */
	public <T> MFXBindings unbindReadOnly(ReadOnlyObjectWrapper<T> target, boolean bidirectional) {
		ReadOnlyObjectProperty<T> obs = target.getReadOnlyProperty();
		return bidirectional ? unbindBidirectional(obs) : unbind(obs);
	}
}
