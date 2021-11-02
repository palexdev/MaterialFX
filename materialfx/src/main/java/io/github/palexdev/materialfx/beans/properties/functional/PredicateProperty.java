package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Predicate;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Predicate}.
 *
 * @param <T> the predicate's input type
 */
public class PredicateProperty<T> extends SimpleObjectProperty<Predicate<T>> {}
