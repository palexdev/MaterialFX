package io.github.palexdev.materialfx.beans;

import java.util.function.BiPredicate;

/**
 * A simple bean that wraps a {@link BiPredicate} and s String that represents
 * the name for the predicate.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument the predicate
 */
public class BiPredicateBean<T, U> {
    //================================================================================
    // Properties
    //================================================================================
    private final String name;
    private final BiPredicate<T, U> predicate;

    //================================================================================
    // Constructors
    //================================================================================
    public BiPredicateBean(String name, BiPredicate<T, U> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    //================================================================================
    // Getters
    //================================================================================
    public String name() {
        return name;
    }

    public BiPredicate<T, U> predicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return name;
    }
}
