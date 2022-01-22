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

package io.github.palexdev.materialfx.bindings;

import io.github.palexdev.materialfx.bindings.base.AbstractBindingHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.function.BiConsumer;

/**
 * Binding helper for bidirectional bindings.
 * <p></p>
 * Bidirectional bindings are syntactical sugar, because basically it's a listener attached to one or more observable values
 * which acts as 'sources', when one of them change the target is updated and all the other sources are updated.
 * <p></p>
 * There is one issue though: when a source changes and the value is updated, there's a 'bounce' effect
 * because all the other sources are updated causing the updateTarget to trigger every time. The same unwanted effect
 * occurs when the target changes, as the sources update will then trigger again the updateTarget.
 * To avoid this, and improve performance, two boolean flags are used to stop the listeners from
 * proceeding with useless updates.
 * <p></p>
 * There's also another issue. In JavaFX when you bind the target multiple times
 * (so you have multiple sources) the properties will have the value of the last used source.
 * This helper though, stores the sources in a Map, which as you know it's not ordered. I could have used
 * a LinkedHashMap, but I wanted to use a WeakHashMap to avoid memory leaks (I hope). Writing a
 * WeakLinkedHashMap would have been too much work, and also very complicated, so in the end I created the {@link BindingsMap},
 * which allows to retrieve the sources in order of insertion.
 * <p>
 * The Map associates the source observable with the {@link BiConsumer} responsible for updating it.
 *
 * @param <T> the properties' value type
 */
public class BiBindingHelper<T> extends AbstractBindingHelper<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected boolean fromSource;
	protected boolean fromTarget;
	private final BindingsMap<ObservableValue<? extends T>, BiConsumer<T, T>> sources = new BindingsMap<>();

	private final ChangeListener<? super T> targetListener = (observable, oldValue, newValue) -> updateSources(oldValue, newValue);

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the target to the specified one, and adds the targetListener to it.
	 */
	@Override
	public BiBindingHelper<T> bind(ObservableValue<? extends T> target) {
		this.target = target;
		target.addListener(targetListener);
		return this;
	}

	/**
	 * Sets the targetUpdater {@link BiConsumer}.
	 */
	@Override
	public BiBindingHelper<T> with(BiConsumer<T, T> targetUpdater) {
		this.targetUpdater = targetUpdater;
		return this;
	}

	/**
	 * Adds the given source and {@link BiConsumer}cto the sources map.
	 * <p></p>
	 * Also calls {@link #beforeBind()} and {@link #afterBind()}.
	 *
	 * @param source  the source observable
	 * @param updater the {@link BiConsumer} responsible for updating the source when the target changes
	 */
	public BiBindingHelper<T> addSource(ObservableValue<? extends T> source, BiConsumer<T, T> updater) {
		sources.put(source, updater);
		beforeBind();
		source.addListener(sourceListener);
		afterBind();
		return this;
	}

	/**
	 * Adds all the given entries (as a Map) to this helper's sources map.
	 * <p></p>
	 * To ensure that the insertion order is maintained, {@link BindingsMap#combine(BindingsMap)} is used.
	 * <p></p>
	 * Also calls {@link #beforeBind()} and {@link #afterBind()}.
	 */
	public BiBindingHelper<T> addSources(BindingsMap<ObservableValue<? extends T>, BiConsumer<T, T>> sources) {
		beforeBind();
		sources.keySet().forEach(observable -> observable.addListener(sourceListener));
		this.sources.combine(sources);
		afterBind();
		return this;
	}

	/**
	 * Invoked by the targetListener, it's responsible for updating the sources by calling {@link #updateSource(ObservableValue, BiConsumer, Object, Object)}.
	 * Also calls beforeUpdateSources() and afterUpdateSources()
	 * <p></p>
	 * If the method is triggered by {@link #updateTarget(ObservableValue, Object, Object)}, so
	 * the fromTarget flag is true, exits immediately.
	 * <p></p>
	 * Sets the fromSource flag to true then updates the sources.
	 * The whole process is wrapped in a try-finally block as it's super important that the flag
	 * is reset at the end.
	 */
	protected void updateSources(T oldValue, T newValue) {
		if (isFromTarget()) return;

		try {
			fromSource = true;
			beforeUpdateSources();
			sources.forEach((source, updater) -> updateSource(source, updater, oldValue, newValue));
			afterUpdateSources();
		} finally {
			fromSource = false;
		}
	}

	/**
	 * Updates the given source using the given {@link BiConsumer}
	 */
	protected void updateSource(ObservableValue<? extends T> source, BiConsumer<T, T> updater, T oldValue, T newValue) {
		updater.accept(oldValue, newValue);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * If the method is triggered by {@link #updateSources(Object, Object)}, so
	 * the fromSource flag is true, exits immediately.
	 * <p></p>
	 * Sets the fromTarget flag to true then updates the target.
	 * Sets the fromSource flag to true as now it's needed to also update
	 * all the other sources (except for the updatingSource, the one that triggered the target update).
	 * <p></p>
	 * Also calls beforeUpdateTarget() and afterUpdateTarget()
	 * <p></p>
	 * The whole process is wrapped in a try-finally block as it's super important to reset
	 * both the flags at the end.
	 *
	 * @param updatingSource the source that triggered the target update
	 * @param oldValue       the source's oldValue
	 * @param newValue       the source's newValue
	 */
	@Override
	protected void updateTarget(ObservableValue<? extends T> updatingSource, T oldValue, T newValue) {
		if (isFromSource()) return;

		try {
			fromTarget = true;
			beforeUpdateTarget();
			targetUpdater.accept(oldValue, newValue);

			fromSource = true;
			sources.forEach((source, updater) -> {
				if (source == updatingSource) return;
				updateSource(source, updater, oldValue, newValue);
			});

			afterUpdateTarget();
		} finally {
			fromTarget = false;
			fromSource = false;
		}
	}

	/**
	 * Causes the target to update with the last source's value.
	 * <p>
	 * The last source is retrieved using {@link BindingsMap#getLastKey()}.
	 * <p></p>
	 * This is necessary to 'simulate' the JavaFX's eager evaluation of bindings.
	 */
	@Override
	public void invalidate() {
		ObservableValue<? extends T> lastSource = sources.getLastKey();
		if (lastSource != null) {
			T value = lastSource.getValue();
			targetUpdater.accept(value, value);
		}
	}

	/**
	 * Removes the given source from the sources map and
	 * also removes the sourceListener from it.
	 * <p></p>
	 * Also calls {@link #beforeUnbind()}, {@link #afterUnbind()}.
	 */
	public void unbind(ObservableValue<? extends T> source) {
		if (sources.remove(source) != null) {
			beforeUnbind();
			source.removeListener(sourceListener);
			afterUnbind();
		}
	}

	/**
	 * Detaches the sourceListener from all the sources then clears the sources map.
	 * <p>
	 * This means that the helper won't be usable anymore until {@link #addSource(ObservableValue, BiConsumer)} or
	 * {@link #addSources(BindingsMap)} are called again.
	 */
	public void clear() {
		sources.forEach((observable, updater) -> observable.removeListener(sourceListener));
		sources.clear();
	}

	/**
	 * Calls {@link #clear()} and in addition to that
	 * also the target is set to null (and the targetListener removed too).
	 * <p>
	 * This means that the helper won't be usable anymore until {@link #bind(ObservableValue)} and
	 * {@link #addSource(ObservableValue, BiConsumer)} or {@link #addSources(BindingsMap)} are called again.
	 */
	@Override
	public void dispose() {
		clear();
		target.removeListener(targetListener);
		target = null;
	}

	/**
	 * Empty by default.
	 */
	protected void beforeUpdateSources() {}

	/**
	 * Empty by default.
	 */
	protected void afterUpdateSources() {}

	/**
	 * @return whether the updated is triggered by the updateTarget
	 */
	public boolean isFromTarget() {
		return fromTarget;
	}

	/**
	 * @return whether the update is triggered by the updateSources
	 */
	public boolean isFromSource() {
		return fromSource;
	}

	/**
	 * Checks if the helper has been disposed before.
	 */
	@Override
	public boolean isDispose() {
		return target == null;
	}

	/**
	 * @return the number of sources in the map.
	 */
	public int size() {
		return sources.size();
	}

	/**
	 * @return the sources map
	 */
	protected BindingsMap<ObservableValue<? extends T>, BiConsumer<T, T>> getSources() {
		return sources;
	}

	/**
	 * @return an unmodifiable view of the map's keys stored in a {@link LinkedList}
	 * (to keep track of insertion order), and wrapped in {@link WeakReference}s.
	 */
	public LinkedList<WeakReference<ObservableValue<? extends T>>> getUnmodifiableSources() {
		return sources.unmodifiableKeysList();
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Creates a new {@code BiBindingHelper} from the two given ones.
	 * <p></p>
	 * Note that at the end of the process both the helpers are disposed.
	 *
	 * @param first                 the first helper
	 * @param second                the second helper
	 * @param overrideTargetUpdater a flag to specify if the targetUpdater of the second helper must be used instead
	 */
	public static <T> BiBindingHelper<T> newFor(BiBindingHelper<T> first, BiBindingHelper<T> second, boolean overrideTargetUpdater) {
		BiBindingHelper<T> newHelper = new BiBindingHelper<>();
		newHelper.bind(second.target);
		if (overrideTargetUpdater) {
			newHelper.with(second.targetUpdater);
		} else {
			newHelper.with(first.targetUpdater);
		}

		first.sources.forEach((source, updater) -> source.removeListener(first.sourceListener));
		second.sources.forEach((source, updater) -> source.removeListener(second.sourceListener));

		newHelper.addSources(first.getSources());
		newHelper.addSources(second.getSources());

		first.sources.clear();
		second.sources.clear();

		first.target.removeListener(first.targetListener);
		first.target = null;

		second.target.removeListener(second.targetListener);
		second.target = null;

		return newHelper;
	}
}
