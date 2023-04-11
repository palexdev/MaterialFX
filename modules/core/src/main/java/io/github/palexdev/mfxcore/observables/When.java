/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.observables;

import io.github.palexdev.mfxcore.collections.WeakHashSet;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Useful class to listen to changes for a given {@link ObservableValue} and perform any
 * specified action when it changes.
 * <p>
 * You can read this construct as "When condition changes, then do this"
 * <p>
 * This is just an abstract class that defines common properties and behavior, but it has two concrete
 * implementations, {@link OnChanged} and {@link OnInvalidated}.
 * <p>
 * This construct also allows to define one-shot listeners, meaning that the
 * above phrase changes like this: "When condition changes, then do this, then dispose(remove listener)"
 * <p>
 * There are also methods that allow to execute the given action immediately, {@link #executeNow()} and {@link #executeNow(Supplier)}.
 * <p>
 * Often one may also need to take into account for external conditions that may influence the outcome of the given action,
 * for this reason you can specify other {@link Observable}s that will automatically invalidate this, check {@link #invalidate()}.
 * <p></p>
 * <b>Note: </b>Once the construct is not needed anymore it's highly recommended to dispose it
 * using the available disposal methods such as: {@link #dispose()} or {@link #dispose(When)}, to avoid memory leaks.
 * When constructs and ObservableValues are stored in a {@link WeakHashMap} for this purpose.
 * <p></p>
 * As per above, you may notice that to dispose such constructs you need their reference, this is because it's allowed to
 * have more than one construct per {@link ObservableValue}, which automatically raises the issue:
 * "Which construct you want to dispose?"
 */
public abstract class When<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected static final WhensMap whens = new WhensMap();
    protected ObservableValue<T> observable;
    protected boolean oneShot = false;
    protected boolean execNowOneShot = false;

    protected Set<Observable> invalidating;
    protected InvalidationListener invalidatingListener;

    //================================================================================
    // Constructors
    //================================================================================
    public When(ObservableValue<T> observable) {
        this.observable = observable;
        invalidating = new HashSet<>();
        invalidatingListener = i -> invalidate();
    }

    public static <T> OnInvalidated<T> onInvalidated(ObservableValue<T> observable) {
        return new OnInvalidated<>(observable);
    }

    public static <T> OnChanged<T> onChanged(ObservableValue<T> observable) {
        return new OnChanged<>(observable);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Implementations of this should provide the logic that adds the listener on the given {@link ObservableValue},
     * as well as handling cases such {@link #oneShot()} and {@link #invalidating(Observable)} as well as making sure that
     * the construct is registered at the end, {@link #register()}.
     */
    public abstract When<T> listen();

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This is responsible for registering the {@code When} construct in a map that keeps references to all the built
     * constructs. This is to avoid garbage collection and to handle {@code When}s disposal easily.
     * <p>
     * It's also responsible for adding a listener on every {@link Observable} added through {@link #invalidating(Observable)},
     * which will trigger {@link #invalidate()}.
     * <p></p>
     * This should be called by implementations of {@link #listen()}.
     */
    protected void register() {
        invalidating.forEach(o -> o.addListener(invalidatingListener));
        WeakHashSet<When<?>> set = whens.computeIfAbsent(observable, o -> new WeakHashSet<>());
        set.add(this);
    }

    /**
     * Adds an {@link Observable} to watch for changes that will trigger {@link #invalidate()}.
     */
    public When<T> invalidating(Observable o) {
        invalidating.add(o);
        return this;
    }

    /**
     * Does nothing by default, implementations are responsible to define this behavior.
     */
    protected When<T> invalidate() {
        return this;
    }

    /**
     * Does nothing by default, implementations of this should allow the execution of the given action immediately, before
     * the listener is attached to the observable. Additionally, these should take into account the flag set by {@link #oneShot(boolean)}.
     */
    public When<T> executeNow() {
        return this;
    }

    /**
     * Calls {@link #executeNow()} if the given condition is true.
     */
    public When<T> executeNow(Supplier<Boolean> condition) {
        if (condition.get()) executeNow();
        return this;
    }

    /**
     * @return whether the construct is "one-shot"
     * @see #oneShot(boolean)
     */
    public boolean isOneShot() {
        return oneShot;
    }

    /**
     * Sets the construct as 'one-shot', meaning that once the value changes the first time and the action is executed,
     * the construct will automatically dispose itself.
     * <p>
     * An additional parameter flag, allows you to further customize this behavior by specifying what happens when
     * {@link #executeNow()} is called. Sometimes, a user may want the construct to be 'one-shot' even for that method.
     *
     * @param affectsExecuteNow specifies whether the 'one-shot' construct should be disposed even if the action is
     *                          executed by the {@link #executeNow()} method
     */
    public When<T> oneShot(boolean affectsExecuteNow) {
        this.execNowOneShot = affectsExecuteNow;
        this.oneShot = true;
        return this;
    }

    /**
     * Calls {@link #oneShot(boolean)} with 'false' as parameter.
     * <p>
     * This shortcut is probably the one that will be used the most.
     */
    public When<T> oneShot() {
        return oneShot(false);
    }

    /**
     * Removes all the invalidating sources added through {@link #invalidating(Observable)} and removes the listener
     * from them.
     * <p>
     * Subclasses should expand this behavior by also disposing: the observable, actions, and any other listener.
     */
    public void dispose() {
        invalidating.forEach(o -> o.removeListener(invalidatingListener));
        invalidating.clear();
        invalidating = null;
        invalidatingListener = null;
    }

    /**
     * Calls {@link #dispose()} on the given {@code When} construct.
     */
    public static void dispose(When<?> w) {
        if (w != null) w.dispose();
    }

    /**
     * Calls {@link #dispose(When)} on each of the given {@code When} construct.
     */
    public static void dispose(When<?>... whens) {
        for (When<?> w : whens) dispose(w);
    }

    /**
     * @return whether this construct has been disposed before. By default, checks if the given {@link ObservableValue}
     * is null, there are no invalidating sources and the invalidation listener is null. A construct is considered to be
     * properly disposed only when all these conditions are verified
     */
    public boolean isDisposed() {
        return observable == null &&
            invalidating == null &&
            invalidatingListener == null;
    }

    /**
     * @return the total number of existing When constructs for a given {@link ObservableValue}
     */
    public static int size(ObservableValue<?> observable) {
        return Optional.ofNullable(whens.get(observable))
            .map(WeakHashSet::size)
            .orElse(0);
    }

    /**
     * @return the total number of existing When constructs for any registered {@link ObservableValue}
     */
    public static int totalSize() {
        return whens.keySet().stream()
            .mapToInt(When::size)
            .sum();
    }

    /**
     * @return this construct wrapped in a {@link WeakReference}
     */
    protected final WeakReference<When<T>> asWeak() {
        return new WeakReference<>(this);
    }

    /**
     * This should be called by implementations when handling the construct's disposal.
     * The aforementioned Map used to store the built {@code Whens}, uses this mapping:
     * <pre>
     * {@code
     * [key -> value] = [ObservableValue -> WeakHashSet<When<?>>]
     * }
     * </pre>
     * This is because {@code When} allows to register multiple constructs on a single {@link ObservableValue},
     * for this reason, there are several things to consider on disposal:
     * <p> 1) There is a non-null Set mapped to the current Observable
     * <p> 2) The construct can be removed from the Set without any null check, but after the removal
     * it's good to check whether the Set is now empty
     * <p> 3) In such case, we can also remove the mapping from the Map.
     */
    protected final void handleMapDisposal() {
        WeakHashSet<When<?>> set = whens.get(observable);
        if (set == null) return;
        set.remove(this);
        if (set.isEmpty()) whens.remove(observable);
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * Abbreviation for {@code WeakHashMap<ObservableValue<?>, WeakHashSet<When<?>>>}.
     *
     * @see WeakHashMap
     * @see WeakHashSet
     */
    public static class WhensMap extends WeakHashMap<ObservableValue<?>, WeakHashSet<When<?>>> {
        public WhensMap() {
        }
    }
}
