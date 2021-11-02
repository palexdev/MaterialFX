package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.NumberFilter;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link NumberFilter} for double fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for doubles equality
 * <p> - "is not": checks for doubles inequality
 * <p> - "greater than": checks if a double is greater than another double
 * <p> - "greater or equal to": checks if a double is greater or equal to another double
 * <p> - "lesser than": checks if a double is lesser than another double
 * <p> - "lesser or equal to": checks if a double is lesser or equal to another double
 */
public class DoubleFilter<T> extends NumberFilter<T, Double> {

    //================================================================================
    // Constructors
    //================================================================================
    public DoubleFilter(String name, Function<T, Double> extractor) {
        this(name, extractor, new DoubleStringConverter());
    }

    public DoubleFilter(String name, Function<T, Double> extractor, StringConverter<Double> converter) {
        super(name, extractor, converter);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected ObservableList<BiPredicateBean<Double, Double>> defaultPredicates() {
        return Stream.<BiPredicateBean<Double, Double>>of(
                new BiPredicateBean<>("is", Double::equals),
                new BiPredicateBean<>("is not", (aDouble, aDouble2) -> !aDouble.equals(aDouble2)),
                new BiPredicateBean<>("greater than", (aDouble, aDouble2) -> aDouble > aDouble2),
                new BiPredicateBean<>("greater or equal to", (aDouble, aDouble2) -> aDouble >= aDouble2),
                new BiPredicateBean<>("lesser than", (aDouble, aDouble2) -> aDouble < aDouble2),
                new BiPredicateBean<>("lesser or equal to", (aDouble, aDouble2) -> aDouble <= aDouble2)
        ).collect(FXCollectors.toList());
    }

    @SafeVarargs
    @Override
    protected final DoubleFilter<T> extend(BiPredicateBean<Double, Double>... predicateBeans) {
        Collections.addAll(super.predicates, predicateBeans);
        return this;
    }
}
