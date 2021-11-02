package io.github.palexdev.materialfx.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience class that offers some methods useful on combination with Java {@link Stream}
 * to collect to JavaFX's collections.
 */
public class FXCollectors {

    private FXCollectors() {}

    /**
     * @return a collector that returns an {@link ObservableSet}
     */
    public static <T> Collector<T, ?, ObservableSet<T>> toSet() {
        return Collectors.collectingAndThen(Collectors.toSet(), FXCollections::observableSet);
    }

    /**
     * @return a collector that returns an {@link ObservableList}
     */
    public static <T> Collector<T, ?, ObservableList<T>> toList() {
        return Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList);
    }
}
