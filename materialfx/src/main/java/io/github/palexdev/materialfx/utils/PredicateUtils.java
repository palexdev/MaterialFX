package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.enums.ChainMode;

import java.util.function.Predicate;

/**
 * Convenience methods for predicates.
 */
public class PredicateUtils {

	private PredicateUtils() {}

	/**
	 * @return a new predicate that is the combination of the original predicate
	 * with the given one according to the specified {@link ChainMode}.
	 */
	public static <T> Predicate<T> chain(Predicate<T> original, Predicate<T> other, ChainMode mode) {
		return mode == ChainMode.AND ? original.and(other) : original.or(other);
	}
}
