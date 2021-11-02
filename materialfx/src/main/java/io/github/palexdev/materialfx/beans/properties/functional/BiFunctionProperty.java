package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.BiFunction;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link BiFunction}.
 *
 * @param <T> the function's first argument
 * @param <U> the function's second argument
 * @param <R> the function's return type
 */
public class BiFunctionProperty<T, U, R> extends SimpleObjectProperty<BiFunction<T, U, R>> {}
