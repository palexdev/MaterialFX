package io.github.palexdev.materialfx.beans;

public class NumberRange<T extends Number> {
    private final T min;
    private final T max;

    public NumberRange(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public static <T extends Number> NumberRange<T> of(T min, T max) {
        return new NumberRange<>(min, max);
    }
}
