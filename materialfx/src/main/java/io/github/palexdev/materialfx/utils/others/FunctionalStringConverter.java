package io.github.palexdev.materialfx.utils.others;

/**
 * A functional alternative to {@link javafx.util.StringConverter}.
 */
@FunctionalInterface
public interface FunctionalStringConverter<T> {
    T fromString(String s);

    default String toString(T t) {
        throw new UnsupportedOperationException();
    }
}
