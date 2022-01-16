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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Little utils class to convert a throwable stack trace to a String.
 */
public class ExceptionUtils {

	private ExceptionUtils() {
	}

	/**
	 * Converts the given exception stack trace to a String
	 * by using a {@link StringWriter} and a {@link PrintWriter}.
	 */
	public static String getStackTraceString(Throwable ex) {
		StringWriter sw = new StringWriter();
		sw.flush();
		ex.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Returns a formatted string in the java style exception format.
	 */
	public static String formatException(Throwable ex) {
		StringBuilder sb = new StringBuilder();
		sb.append(ex.getMessage());
		sb.append("\n");
		StackTraceElement[] trace = ex.getStackTrace();
		for (StackTraceElement stackTraceElement : trace) {
			sb.append("\t");
			sb.append(stackTraceElement);
			sb.append("\n");
		}
		return sb.toString();
	}
}
