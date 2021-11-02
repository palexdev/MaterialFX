package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Consumer;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Consumer}.
 *
 * @param <T> the consumer's input type
 */
public class ConsumerProperty<T> extends SimpleObjectProperty<Consumer<T>> {}
