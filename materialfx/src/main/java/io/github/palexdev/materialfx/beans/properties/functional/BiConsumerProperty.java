package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.BiConsumer;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link BiConsumer}.
 *
 * @param <T> the consumer's first argument
 * @param <U> the consumer's second argument
 */
public class BiConsumerProperty<T, U> extends SimpleObjectProperty<BiConsumer<T, U>> {}
