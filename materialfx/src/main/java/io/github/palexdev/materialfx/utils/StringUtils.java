/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.i18n.I18N;

import java.util.Arrays;
import java.util.List;

/**
 * Utils class for {@code Strings}.
 */
public class StringUtils {
	public static final String EMPTY = "";
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * Finds the difference between two {@code Strings}.
	 *
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
	 * Finds the index at which two {@code CharSequences} differ.
	 *
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

	/**
	 * Replaces the last occurrence of the given string with a new string.
	 *
	 * @param string      The string to modify
	 * @param substring   The last occurrence to find
	 * @param replacement The replacement
	 * @return The modified string
	 */
	public static String replaceLast(String string, String substring, String replacement) {
		int index = string.lastIndexOf(substring);
		if (index == -1)
			return string;
		return string.substring(0, index) + replacement
				+ string.substring(index + substring.length());
	}

	public static String replaceIndex(String string, int startIndex, int endIndex, String replacement) {
		StringBuilder sb = new StringBuilder(string);
		sb.replace(startIndex, endIndex, replacement);
		return sb.toString();
	}

	public static String titleCaseWord(String str) {
		if (str.length() > 0) {
			int firstChar = str.codePointAt(0);
			if (!Character.isTitleCase(firstChar)) {
				str = new String(new int[]{Character.toTitleCase(firstChar)}, 0, 1) +
						str.substring(Character.offsetByCodePoints(str, 0, 1));
			}
		}
		return str;
	}

	/**
	 * <p>Checks if a CharSequence contains a search CharSequence irrespective of case,
	 * handling {@code null}. Case-insensitivity is defined as by
	 * {@link String#equalsIgnoreCase(String)}.
	 *
	 * <p>A {@code null} CharSequence will return {@code false}.</p>
	 */
	public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		final int len = searchStr.length();
		final int max = str.length() - len;
		for (int i = 0; i <= max; i++) {
			if (regionMatches(str, i, searchStr, len)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if thee given string starts with the specifies prefix, ignores case.
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		return str.regionMatches(true, 0, prefix, 0, prefix.length());
	}

	/**
	 * Checks if the given string ends with the given prefix, ignores case.
	 */
	public static boolean endsWithIgnoreCase(String str, String suffix) {
		int suffixLength = suffix.length();
		return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
	}

	/**
	 * Checks if the given string contains at least one of the given words.
	 *
	 * @param split this is the character that will split the input string, see {@link String#split(String)}
	 */
	public static boolean containsAny(String str, String split, String... words) {
		List<String> inputStringList = Arrays.asList(str.split(split));
		List<String> wordsList = Arrays.asList(words);

		return wordsList.stream().anyMatch(inputStringList::contains);
	}

	/**
	 * Checks if the given string contains all the specifies words.
	 *
	 * @param split this is the character that will split the input string, see {@link String#split(String)}
	 */
	public static boolean containsAll(String str, String split, String... words) {
		List<String> inputStringList = Arrays.asList(str.split(split));
		List<String> wordsList = Arrays.asList(words);

		return inputStringList.containsAll(wordsList);
	}

	/**
	 * A useful method to convert a given elapsed time in seconds to a
	 * String.
	 * <p></p>
	 * <p> - "Just now" if elapsed is less than 60 seconds
	 * <p> - minutes + " minutes ago" if the elapsed seconds is greater than 60 seconds
	 * <p> - hours + " minutes ago" if the elapsed minutes are greater than 60 minutes
	 * <p> - days + " days ago" if the elapsed hours are greater than 24
	 */
	public static String timeToHumanReadable(long elapsedSeconds) {
		if (elapsedSeconds < 60) {
			return I18N.getOrDefault("stringUtil.now");
		} else {
			long minutes = elapsedSeconds / 60;
			if (minutes < 60) {
				return I18N.getOrDefault("stringUtil.minutes", minutes);
			} else {
				long hours = minutes / 60;
				return hours < 24 ? I18N.getOrDefault("stringUtils.hours", hours) : I18N.getOrDefault("stringUtils.days", hours / 24);
			}
		}
	}

	/**
	 * Generates a random alphabetic string of given length
	 */
	public static String randAlphabetic(int length) {
		return RandomUtils.random.ints(97, 123)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	/**
	 * Generates a random alphanumeric string of given length
	 */
	public static String randAlphanumeric(int length) {
		return RandomUtils.random.ints(48, 123)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	private static boolean regionMatches(final CharSequence cs, final int thisStart,
	                                     final CharSequence substring, final int length) {
		if (cs instanceof String && substring instanceof String) {
			return ((String) cs).regionMatches(true, thisStart, (String) substring, 0, length);
		}
		int index1 = thisStart;
		int index2 = 0;
		int tmpLen = length;

		// Extract these first so we detect NPEs the same as the java.lang.String version
		final int srcLen = cs.length() - thisStart;
		final int otherLen = substring.length();

		// Check for invalid parameters
		if (thisStart < 0 || length < 0) {
			return false;
		}

		// Check that the regions are long enough
		if (srcLen < length || otherLen < length) {
			return false;
		}

		while (tmpLen-- > 0) {
			final char c1 = cs.charAt(index1++);
			final char c2 = substring.charAt(index2++);

			if (c1 == c2) {
				continue;
			}

			// The real same check as in String.regionMatches():
			final char u1 = Character.toUpperCase(c1);
			final char u2 = Character.toUpperCase(c2);
			if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
				return false;
			}
		}

		return true;
	}
}
