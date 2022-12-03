/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.enums;

import io.github.palexdev.mfxlocalization.I18N;

/**
 * Enumeration to specify how two predicates should be chained.
 * Also specify how a ChainMode enumeration should be represented in UI.
 */
@SuppressWarnings("NonFinalFieldInEnum")
public enum ChainMode {
	AND("&"),
	OR(I18N.getOrDefault("chainMode.or"));

	public static boolean useAlternativeAnd = false;
	private final String text;

	ChainMode(String text) {
		this.text = text;
	}

	public String text() {
		return this == AND && useAlternativeAnd ? I18N.getOrDefault("chainMode.alternativeAnd") : this.text;
	}

	/**
	 * Chains the given two boolean values according to the given {@link ChainMode}.
	 */
	public static boolean chain(ChainMode mode, boolean first, boolean second) {
		return (mode == AND) ? first && second : first || second;
	}
}
