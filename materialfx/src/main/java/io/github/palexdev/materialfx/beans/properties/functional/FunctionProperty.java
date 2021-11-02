package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Function;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Function}.
 *
 * @param <T> the function's input type
 * @param <R> the function's return type
 */
public class FunctionProperty<T, R> extends SimpleObjectProperty<Function<T, R>> {}
