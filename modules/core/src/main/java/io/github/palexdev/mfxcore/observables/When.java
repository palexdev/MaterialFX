/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

/// Useful class to listen to changes for a given [ObservableValue] and perform any
/// specified action when it changes.
///
/// You can read this construct as "When condition changes, then do this"
///
/// This is just an abstract class that defines common properties and behavior, but it has two concrete
/// implementations, [OnChanged] and [OnInvalidated].
///
/// This construct also allows defining one-shot listeners, meaning that the above phrase changes to:
/// "When condition changes, then do this, then dispose(remove listener)"
///
/// There are also methods that allow executing the given action immediately, [#executeNow()] and [#executeNow(Supplier)].
///
/// Often one may also need to take into account for external conditions that may influence the outcome of the given action,
/// for this reason you can specify other [Observable]s that will automatically invalidate this, check [#invalidate()].
///
/// **Note:** Once the construct is not needed anymore it's highly recommended to dispose of it using the available disposal methods:
/// [#dispose()] or [#dispose(When)], to avoid memory leaks.
///
/// `When` constructs and ObservableValues are stored in a [WeakHashMap] for this purpose.
///
/// As per above, you may notice that to dispose of such constructs you need their reference, this is because it's possible to
/// have more than one construct per [ObservableValue], which automatically raises the issue: "Which construct do you want to dispose?"
public abstract class When<T> implements Disposable {
    //================================================================================
    // Properties
    //================================================================================
    protected static final WhensMap whens = new WhensMap();
    protected ObservableValue<T> observable;
    protected boolean oneShot = false;
    protected boolean execNowOneShot = false;
    protected boolean active = false;

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

    /// Builds a special construct that is not tied to a specific [ObservableValue] but rather to a group of generic
    /// [Observables][Observable], executing the given action when any of them changes.
    ///
    /// This is not added to the disposal map and therefore will not result in [#totalSize()]. To dispose of such
    /// special constructs, you need the instance.
    public static When<?> observe(Runnable action, Observable... observables) {
        return new When<Void>(null) {

            {
                for (Observable o : observables) invalidating(o);
            }

            @Override
            protected When<Void> invalidate() {
                action.run();
                if (oneShot) dispose();
                return this;
            }

            @Override
            public When<Void> executeNow() {
                action.run();
                if (oneShot && execNowOneShot) dispose();
                return this;
            }

            @Override
            public When<Void> listen() {
                if (isDisposed() || isActive()) return this;
                register();
                return this;
            }

            @Override
            protected void register() {
                invalidating.forEach(o -> o.addListener(invalidatingListener));
                active = true;
            }
        };
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /// Implementations of this should provide the logic that adds the listener on the given [ObservableValue],
    /// as well as handling cases such as [#oneShot()] and [#invalidating(Observable)] as well as making sure that
    /// the construct is registered at the end, [#register()].
    public abstract When<T> listen();

    //================================================================================
    // Methods
    //================================================================================

    /// This is responsible for registering the `When` construct in a map that keeps references to all the built
    /// constructs. This is to avoid garbage collection and to handle `Whens` disposal easily.
    ///
    /// It's also responsible for adding a listener on every [Observable] added through [#invalidating(Observable)],
    /// which will trigger [#invalidate()].
    ///
    /// This should be called by implementations of [#listen()].
    protected void register() {
        invalidating.forEach(o -> o.addListener(invalidatingListener));
        WeakHashSet<When<?>> set = whens.computeIfAbsent(observable, o -> new WeakHashSet<>());
        set.add(this);
        active = true;
    }

    /// Adds an [Observable] to watch for changes that will trigger [#invalidate()].
    public When<T> invalidating(Observable o) {
        boolean added = invalidating.add(o);
        if (active && added) o.addListener(invalidatingListener);
        return this;
    }

    /// Does nothing by default, implementations are responsible to define this behavior.
    protected When<T> invalidate() {
        return this;
    }

    /// Does nothing by default; Implementations of this should allow the execution of the given action immediately, before
    /// the listener is attached to the observable. Additionally, these should take into account the flag set by [#oneShot(boolean)].
    public When<T> executeNow() {
        return this;
    }

    /// Calls [#executeNow()] if the given condition is true.
    public When<T> executeNow(Supplier<Boolean> condition) {
        if (condition.get()) executeNow();
        return this;
    }

    /// @return whether the construct is "one-shot"
    /// @see #oneShot(boolean)
    public boolean isOneShot() {
        return oneShot;
    }

    /// Sets the construct as 'one-shot', meaning that once the value changes the first time and the action is executed,
    /// the construct will automatically dispose itself.
    ///
    /// An additional parameter flag allows you to further customize this behavior by specifying what happens when
    /// [#executeNow()] is called. Sometimes, a user may want the construct to be 'one-shot' even for that method.
    ///
    /// @param affectsExecuteNow specifies whether the 'one-shot' construct should be disposed even if the action is
    ///                          executed by the [#executeNow()] method
    public When<T> oneShot(boolean affectsExecuteNow) {
        this.execNowOneShot = affectsExecuteNow;
        this.oneShot = true;
        return this;
    }

    /// Calls [#oneShot(boolean)] with 'false' as a parameter.
    ///
    /// This shortcut is probably the one that will be used the most.
    public When<T> oneShot() {
        return oneShot(false);
    }

    /// Removes all the invalidating sources added through [#invalidating(Observable)] and removes the listener from them.
    ///
    /// Subclasses should expand this behavior by also disposing: the observable, actions, and any other listener.
    @Override
    public void dispose() {
        if (isDisposed()) return;
        invalidating.forEach(o -> o.removeListener(invalidatingListener));
        invalidating.clear();
        invalidating = null;
        invalidatingListener = null;
        active = false;
    }

    /// Calls [#dispose()] on the given `When` construct.
    public static void dispose(When<?> w) {
        if (w != null) w.dispose();
    }

    /// Calls [#dispose(When)] on each of the given `When` constructs.
    public static void dispose(When<?>... whens) {
        for (When<?> w : whens) dispose(w);
    }

    /// @return whether the construct is active and not disposed, the flag is set if [#register()] run successfully
    public boolean isActive() {
        return active;
    }

    /// @return whether this construct has been disposed before. By default, checks if the given [ObservableValue]
    /// is null, there are no invalidating sources, and the invalidation listener is null. A construct is considered to be
    /// properly disposed only when all these conditions are verified
    public boolean isDisposed() {
        return observable == null &&
               invalidating == null &&
               invalidatingListener == null;
    }

    /// @return the total number of existing `When` constructs for a given [ObservableValue]
    public static int size(ObservableValue<?> observable) {
        return Optional.ofNullable(whens.get(observable))
            .map(WeakHashSet::size)
            .orElse(0);
    }

    /// @return the total number of existing `When` constructs for any registered [ObservableValue]
    public static int totalSize() {
        return whens.keySet().stream()
            .mapToInt(When::size)
            .sum();
    }

    /// @return this construct wrapped in a [WeakReference]
    protected final WeakReference<When<T>> asWeak() {
        return new WeakReference<>(this);
    }

    /// This should be called by implementations when handling the construct's disposal.
    /// The aforementioned Map used to store the built `Whens`, uses this mapping:
    /// `[key -> value] = [ObservableValue -> WeakHashSet<When<?>>]`
    ///
    /// This is because `When` allows registering multiple constructs on a single [ObservableValue],
    /// for this reason, there are several things to consider on disposal:
    ///  1) There is a non-null Set mapped to the current Observable
    ///  2) The construct can be removed from the Set without any null check, but after the removal
    ///  it's good to check whether the Set is now empty
    ///  3) In such case, we can also remove the mapping from the Map.
    protected final void handleMapDisposal() {
        WeakHashSet<When<?>> set = whens.get(observable);
        if (set == null) return;
        set.remove(this);
        if (set.isEmpty()) whens.remove(observable);
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /// Abbreviation for `WeakHashMap<ObservableValue<?>, WeakHashSet<When<?>>>`.
    ///
    /// @see WeakHashMap
    /// @see WeakHashSet
    public static class WhensMap extends WeakHashMap<ObservableValue<?>, WeakHashSet<When<?>>> {}
}
