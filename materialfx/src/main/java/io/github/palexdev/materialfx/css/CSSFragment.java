/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.css;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.Base64;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Allows to build CSS stylesheets by code and apply them on any {@link Parent}.
 */
public class CSSFragment {
	//================================================================================
	// Properties
	//================================================================================
	private static final Base64.Encoder enc = Base64.getEncoder();
	private final String css;
	private String converted;

	public static final String DATA_URI_PREFIX = "data:base64,";

	//================================================================================
	// Constructors
	//================================================================================
	public CSSFragment(String css) {
		this.css = css;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Applies the given CSS to the given {@link Parent}
	 *
	 * @see #applyOn(Parent)
	 */
	public static void applyOn(String css, Parent parent) {
		CSSFragment f = new CSSFragment(css);
		f.applyOn(parent);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Converts a CSS string to a data uri that can be used by JavaFX nodes to parse styles.
	 * <p>
	 * Subsequent calls to this will be faster as the converted CSS is cached.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Data_URI_scheme">Data URI Scheme</a>
	 */
	public String toDataUri() {
		if (converted == null) {
			converted = DATA_URI_PREFIX + new String(enc.encode(css.getBytes(UTF_8)), UTF_8);
		}
		return converted;
	}

	/**
	 * If this CSS fragment has not been applied yet to the given {@link Parent}, applies it
	 * using {@link Parent#getStylesheets()}
	 *
	 * @see #isAppliedOn(Parent)
	 */
	public void applyOn(Parent parent) {
		if (!isAppliedOn(parent))
			parent.getStylesheets().add(toDataUri());
	}

	/**
	 * If this CSS fragment has not been applied yet to the given {@link Scene}, applies it
	 * using {@link Scene#getStylesheets()}.
	 *
	 * @see #isAppliedOn(Scene)
	 */
	public void applyOn(Scene scene) {
		if (!isAppliedOn(scene))
			scene.getStylesheets().add(toDataUri());
	}

	/**
	 * Checks whether this CSS Fragment has already been applied to the given {@link Parent}
	 * by checking if its stylesheets list contains this (converted to a Data URI).
	 */
	public boolean isAppliedOn(Parent parent) {
		return parent.getStylesheets().contains(toDataUri());
	}

	/**
	 * Checks whether this CSS Fragment has already been applied to the given {@link Scene}
	 * by checking if its stylesheets list contains this (converted to a Data URI).
	 */
	public boolean isAppliedOn(Scene scene) {
		return scene.getStylesheets().contains(toDataUri());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CSSFragment that = (CSSFragment) o;
		return css.equals(that.css);
	}

	@Override
	public int hashCode() {
		return Objects.hash(css);
	}

	@Override
	public String toString() {
		return css;
	}

	//================================================================================
	// Builder
	//================================================================================

	/**
	 * Allows building {@code CSSFragments} with fluent API.
	 * <p></p>
	 * Usage example:
	 * <pre>
	 * {@code
	 * Parent p = ...;
	 * CssFragment.Builder.build()
	 *     .addSelector("aSelector")
	 *     .addStyle("-fx-background-color: red")
	 *     .addStyle("-fx-background-radius: 10px")
	 *     .closeSelector()
	 *     .addSelector("aSelector:hover")
	 *     .addStyle("-fx-background-color: orange")
	 *     .addStyle("-fx-border-color: lightgray")
	 *     .closeSelector()
	 *     .applyOn(p);
	 * }
	 * </pre>
	 */
	public static class Builder {
		private final StringBuilder sb = new StringBuilder();

		public static Builder build() {
			return new Builder();
		}

		/**
		 * Use this to open a block for an element with its selector.
		 * It's not needed to add the ending '{\n' as it is automatically added.
		 * <p></p>
		 * Once you finish the styling block for this element you must call {@link #closeSelector()}
		 */
		public Builder addSelector(String selector) {
			sb.append(selector).append("{\n");
			return this;
		}

		/**
		 * Must always be called after {@link #addSelector(String)} to close the styling block for an element.
		 */
		public Builder closeSelector() {
			sb.append("}\n\n");
			return this;
		}

		/**
		 * Adds the given style to the fragment.
		 * It's not needed to add the ending ';\n' as it is automatically added.
		 */
		public Builder addStyle(String style) {
			sb.append(style).append(";\n");
			return this;
		}

		/**
		 * Converts the built string to a {@link CSSFragment}.
		 */
		public CSSFragment toCSS() {
			if (sb.length() == 0) throw new IllegalStateException("No styles set");
			return new CSSFragment(sb.toString());
		}

		/**
		 * Applies the created {@link CSSFragment} on the given {@link Parent}.
		 *
		 * @see #toCSS()
		 * @see CSSFragment#applyOn(Parent)
		 */
		public void applyOn(Parent parent) {
			toCSS().applyOn(parent);
		}

		/**
		 * Applies the created {@link CSSFragment} on the given {@link Scene}.
		 *
		 * @see #toCSS()
		 * @see CSSFragment#applyOn(Scene)
		 */
		public void applyOn(Scene scene) {
			toCSS().applyOn(scene);
		}
	}
}
