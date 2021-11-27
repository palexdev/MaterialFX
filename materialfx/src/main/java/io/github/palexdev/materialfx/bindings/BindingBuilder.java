package io.github.palexdev.materialfx.bindings;

import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

/**
 * Helper class for the {@link BindingManager}.
 * <p>
 * Makes the creation of unidirectional bindings easier with fluent methods.
 */
public class BindingBuilder<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObservableValue<? extends T> target;
	private ObservableValue<? extends T> source;
	private BiConsumer<T, T> updater;

	//================================================================================
	// Constructors
	//================================================================================
	public BindingBuilder(ObservableValue<? extends T> target) {
		this.target = target;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the binding's source.
	 */
	public BindingBuilder<T> to(ObservableValue<? extends T> source) {
		this.source = source;
		return this;
	}

	/**
	 * Sets the {@link BiConsumer} function responsible for updating the target
	 * when the source changes.
	 */
	public BindingBuilder<T> with(BiConsumer<T, T> updater) {
		this.updater = updater;
		return this;
	}

	/**
	 * @return the target observable
	 */
	public ObservableValue<? extends T> target() {
		return target;
	}

	/**
	 * @return the source observable
	 */
	public ObservableValue<? extends T> source() {
		return source;
	}

	/**
	 * @return the target updater
	 */
	public BiConsumer<T, T> targetUpdater() {
		return updater;
	}

	/**
	 * Confirms the creation of the binding by calling {@link BindingManager#apply(BindingBuilder, BindingHelper)}.
	 */
	public BindingManager create() {
		BindingHelper<T> bindingHelper = new BindingHelper<>();
		bindingHelper.with(updater);
		return BindingManager.instance().apply(this, bindingHelper);
	}
}
