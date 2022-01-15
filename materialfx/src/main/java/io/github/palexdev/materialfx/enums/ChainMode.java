package io.github.palexdev.materialfx.enums;

/**
 * Enumeration to specify how two predicates should be chained.
 * Also specify how a ChainMode enumeration should be represented in UI.
 */
public enum ChainMode {
    AND("&"),
    OR("or");

    public static boolean useAlternativeAnd = false;
    private final String text;

    ChainMode(String text) {
        this.text = text;
    }

    public String text() {
        return this == AND && useAlternativeAnd ? "and" : this.text;
    }

    /**
     * Chains the given two boolean values according to the given {@link ChainMode}.
     */
    public static boolean chain(ChainMode mode, boolean first, boolean second) {
        return (mode == AND) ? first && second : first || second;
    }
}
