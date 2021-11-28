package io.github.palexdev.materialfx.utils.others;

import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * A functional alternative to {@link javafx.util.StringConverter}.
 */
@FunctionalInterface
public interface FunctionalStringConverter<T> {
    T fromString(String s);

    default String toString(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a new {@link StringConverter} which uses the given function
     * to convert a String to an object of type T
     */
    static <T> StringConverter<T> converter(Function<String, T> fsFunction) {
        return new StringConverter<>() {
            @Override
            public String toString(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public T fromString(String string) {
                return fsFunction.apply(string);
            }
        };
    }

    /**
     * @return a new {@link StringConverter} which uses the given functions
     * to convert a String to an object of type T and vice versa.
     */
    static <T> StringConverter<T> converter(Function<String, T> fsFunction, Function<T, String> tsFunction) {
        return new StringConverter<>() {
            @Override
            public String toString(T t) {
                return t != null ? tsFunction.apply(t) : "";
            }

            @Override
            public T fromString(String string) {
                return fsFunction.apply(string);
            }
        };
    }
}
