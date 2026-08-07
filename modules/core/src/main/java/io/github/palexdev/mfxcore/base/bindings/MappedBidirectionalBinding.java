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

package io.github.palexdev.mfxcore.base.bindings;

import java.util.*;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

/// This utility provides bidirectional binding between two [ObservableValues][ObservableValue] of different types,
/// filling a gap in JavaFX's binding system by implementing type conversion through functions for this kind of bindings.
///
/// **Usage Example:**
/// ```java
/// StringProperty first = new SimpleStringProperty();
/// IntegerProperty second = new SimpleIntegerProperty();
/// MappedBidirectionalBinding.bind(first, second)\
///     .setFirstToSecondMapper(Integer::parseInt)
///     .setSecondToFirstMapper(String::valueOf)
///     .bind();
/// ```
///
/// @param <A> the type of the first observable value
/// @param <B> the type of the second observable value
public class MappedBidirectionalBinding<A, B> implements Disposable {
    //================================================================================
    // Properties
    //================================================================================
    private ObservableValue<A> first;
    private Mapper<A, B> firstToSecondMapper = Mapper.of(_ -> null);
    private ObservableValue<B> second;
    private Mapper<B, A> secondToFirstMapper = Mapper.of(_ -> null);

    private When<?> firstWhen;
    private When<?> secondWhen;
    private final Map<Target, Set<Observable>> dependencies = new HashMap<>();
    private boolean locked = false;

    //================================================================================
    // Constructors
    //================================================================================

    public MappedBidirectionalBinding(ObservableValue<A> first, ObservableValue<B> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public static <A, B> MappedBidirectionalBinding<A, B> bind(ObservableValue<A> first, ObservableValue<B> second) {
        return new MappedBidirectionalBinding<>(first, second);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Delegates to [#bind(boolean)] with `false` as the parameter.
    public MappedBidirectionalBinding<A, B> bind() {
        return bind(false);
    }

    /// Activates the bidirectional binding.
    ///
    /// This method sets up the listeners on both observable values and thus establishing the bidirectional relationship.<br >
    /// Once active, changes to either observable will trigger the appropriate mapper and update the other observable.
    ///
    /// @param lazyInit if `false`, immediately updates the second observable with the mapped value from the first;
    /// if `true`, waits for the first change to trigger initialization
    /// @throws IllegalStateException if the binding is already active
    /// @see Target
    public MappedBidirectionalBinding<A, B> bind(boolean lazyInit) {
        if (isActive()) throw new IllegalStateException("The binding is already active");

        firstWhen = When.onInvalidated(first)
            .condition(_ -> !locked)
            .then(_ -> Target.SECOND.update(this))
            .listen();
        secondWhen = When.onInvalidated(second)
            .condition(_ -> !locked)
            .then(_ -> Target.FIRST.update(this))
            .listen();
        if (!lazyInit) Target.SECOND.update(this);

        // Register dependencies now that listeners have been created
        dependencies.forEach((t, deps) -> t.registerDependencies(this, deps));

        return this;
    }

    /// Adds additional dependencies that allow the binding to react to changes in other observables beyond
    /// the two primary bound values.<br >
    /// For example, if your mapper function depends on external configuration properties,
    /// you can register those as dependencies.
    ///
    /// Dependencies can be added before or after calling [#bind()].
    ///
    /// @param target determines which of the two observables depend on the given dependencies
    /// @see Target
    public MappedBidirectionalBinding<A, B> addDependenciesFor(Target target, Observable... dependencies) {
        Set<Observable> set = this.dependencies.computeIfAbsent(target, _ -> new LinkedHashSet<>());
        Collections.addAll(set, dependencies);
        if (isActive()) target.registerDependencies(this, Arrays.asList(dependencies));
        return this;
    }

    /// Checks whether this binding is currently active.
    ///
    /// A binding is considered active if it has been bound via [#bind()] and has not been subsequently unbound.
    public boolean isActive() {
        return firstWhen != null || secondWhen != null;
    }

    /// Deactivates the binding.
    ///
    /// This removes the change listeners and stops the automatic synchronization between the observable values.<br >
    /// The binding can be reactivated by calling [#bind()] again.
    ///
    /// @param clearDependencies if `true`, clears all registered dependencies;
    /// if `false`, preserves dependencies for potential rebinding
    public void unbind(boolean clearDependencies) {
        if (firstWhen != null) firstWhen.dispose();
        if (secondWhen != null) secondWhen.dispose();
        if (clearDependencies) dependencies.clear();
        firstWhen = null;
        secondWhen = null;
    }

    /// Completely disposes of this binding. After calling this method, the binding cannot be reused.<br >
    /// This should be called when the binding is no longer needed to prevent memory leaks.
    ///
    /// Partly delegates to [#unbind(boolean)] with `true` as the parameter.
    public void dispose() {
        unbind(true);
        first = null;
        second = null;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the mapper function for converting values from the first observable to the second.
    public Mapper<A, B> getFirstToSecondMapper() {
        return firstToSecondMapper;
    }

    /// Sets the mapper function for converting values from the first observable to the second.
    ///
    /// This mapper is used when the first observable changes, and its value needs to be
    /// converted and applied to the second observable.
    public MappedBidirectionalBinding<A, B> setFirstToSecondMapper(Function<A, B> firstToSecondMapper) {
        this.firstToSecondMapper = Mapper.of(firstToSecondMapper);
        return this;
    }

    /// Same as [#setFirstToSecondMapper(Function)] but more flexible because a [Mapper] also allows to specify an 'orElse' value.
    public MappedBidirectionalBinding<A, B> setFirstToSecondMapper(Mapper<A, B> firstToSecondMapper) {
        this.firstToSecondMapper = Objects.requireNonNull(firstToSecondMapper);
        return this;
    }

    /// @return the mapper function for converting values from the second observable to the first.
    public Mapper<B, A> getSecondToFirstMapper() {
        return secondToFirstMapper;
    }

    /// Sets the mapper function for converting values from the second observable to the first.
    ///
    /// This mapper is used when the second observable changes, and its value needs to be
    /// converted and applied to the first observable.
    public MappedBidirectionalBinding<A, B> setSecondToFirstMapper(Function<B, A> secondToFirstMapper) {
        this.secondToFirstMapper = Mapper.of(secondToFirstMapper);
        return this;
    }

    /// Same as [#setSecondToFirstMapper(Function)] but more flexible because a [Mapper] also allows to specify an 'orElse' value.
    public MappedBidirectionalBinding<A, B> setSecondToFirstMapper(Mapper<B, A> secondToFirstMapper) {
        this.secondToFirstMapper = Objects.requireNonNull(secondToFirstMapper);
        return this;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Enum representing the two directions of the bidirectional binding.<br >
    /// This is used internally to manage updates and dependencies while avoiding code duplication.
    ///
    /// While dependencies are added by [MappedBidirectionalBinding#addDependenciesFor(Target, Observable...)], they
    /// are effectively registered by the target constant, [#registerDependencies(MappedBidirectionalBinding, Collection)].
    public enum Target {
        /// Represents the direction from the second observable to the first.
        ///
        /// When the second observable changes, the `FIRST` target is updated using the second-to-first mapper.
        FIRST {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            <A, B> void update(MappedBidirectionalBinding<A, B> binding) {
                try {
                    binding.locked = true;
                    A newVal = binding.secondToFirstMapper.apply(binding.second.getValue());
                    if (binding.first instanceof WritableValue wv) wv.setValue(newVal);
                } finally {
                    binding.locked = false;
                }
            }

            @Override
            <A, B> void registerDependencies(MappedBidirectionalBinding<A, B> binding, Collection<Observable> dependencies) {
                for (Observable dependency : dependencies) {
                    binding.secondWhen.invalidating(dependency);
                }
            }
        },

        /// Represents the direction from the first observable to the second.
        ///
        /// When the first observable changes, the `SECOND` target is updated using the first-to-second mapper.
        SECOND {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            <A, B> void update(MappedBidirectionalBinding<A, B> binding) {
                try {
                    binding.locked = true;
                    B newVal = binding.firstToSecondMapper.apply(binding.first.getValue());
                    if (binding.second instanceof WritableValue wv) wv.setValue(newVal);
                } finally {
                    binding.locked = false;
                }
            }

            @Override
            <A, B> void registerDependencies(MappedBidirectionalBinding<A, B> binding, Collection<Observable> dependencies) {
                for (Observable dependency : dependencies) {
                    binding.firstWhen.invalidating(dependency);
                }
            }
        };

        abstract <A, B> void update(MappedBidirectionalBinding<A, B> binding);

        abstract <A, B> void registerDependencies(MappedBidirectionalBinding<A, B> binding, Collection<Observable> dependencies);
    }
}