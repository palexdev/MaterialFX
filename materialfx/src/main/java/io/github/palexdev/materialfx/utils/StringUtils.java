package io.github.palexdev.materialfx.utils;

/**
 * Utils class for {@code String}s.
 */
public class StringUtils {
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Finds the difference between two {@code String}s.
     * @param str1 The first String
     * @param str2 The second String
     * @return the difference between the two given strings
     */
    public static String difference(final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference(str1, str2);
        if (at == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * Finds the index at which two {@code CharSequence}s differ.
     * @param cs1 The first sequence
     * @param cs2 The second sequence
     */
    public static int indexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }
}
