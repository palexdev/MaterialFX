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

package io.github.palexdev.materialfx.theming;

import io.github.palexdev.mfxcore.controls.Text;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

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
	 * If this CSS fragment has not been applied yet as the {@link Application}'s global user agent stylesheet, calls
	 * {@link Application#setUserAgentStylesheet(String)}.
	 */
	public void setGlobal() {
		if (!isGlobal())
			Application.setUserAgentStylesheet(toDataUri());
	}

	/**
	 * Checks whether this CSS fragment has already been applied to the given {@link Parent}
	 * by checking if its stylesheets list contains this (converted to a Data URI).
	 */
	public boolean isAppliedOn(Parent parent) {
		return parent.getStylesheets().contains(toDataUri());
	}

	/**
	 * Checks whether this CSS fragment has already been applied to the given {@link Scene}
	 * by checking if its stylesheets list contains this (converted to a Data URI).
	 */
	public boolean isAppliedOn(Scene scene) {
		return scene.getStylesheets().contains(toDataUri());
	}

	/**
	 * Checks whether this CSS fragment has already been applied as the {@link Application}'s global user agent stylesheet,
	 * by checking if {@link Application#getUserAgentStylesheet()} is equal to this (converted to a Data URI).
	 */
	public boolean isGlobal() {
		return Objects.equals(Application.getUserAgentStylesheet(), toDataUri());
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
	 * Builder.build()
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
		private boolean isSelectorOpen = false;
		private boolean isBracketOpen = false;

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
			if (isSelectorOpen) sb.append(",\n");
			isSelectorOpen = true;
			sb.append(selector.trim());
			return this;
		}

		/**
		 * Must always be called after {@link #addSelector(String)} to close the styling block for an element.
		 */
		public Builder closeSelector() {
			if (!isBracketOpen) {
				sb.append(" {");
			}
			sb.append("\n}\n\n");
			isSelectorOpen = false;
			isBracketOpen = false;
			return this;
		}

		/**
		 * Adds the given style to the fragment.
		 * It's not needed to add the ending ';\n' as it is automatically added.
		 */
		public Builder addStyle(String style) {
			if (!isSelectorOpen) throw new IllegalStateException("No selector was opened!");
			if (!isBracketOpen) {
				sb.append(" {");
				isBracketOpen = true;
			}
			sb.append("\n  ").append(style).append(";");
			return this;
		}

		/**
		 * Overridden to return the built CSS string.
		 */
		@Override
		public String toString() {
			return sb.toString().trim();
		}

		/**
		 * Converts the built string to a {@link CSSFragment}.
		 */
		public CSSFragment toCSS() {
			if (sb.length() == 0) throw new IllegalStateException("No styles set");
			if (isSelectorOpen) throw new IllegalStateException("Selector was not closed!");
			return new CSSFragment(sb.toString().trim());
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

		//================================================================================
		// Convenient Methods
		//================================================================================
		// NODE
		public Builder blendMode(BlendMode val) {
			addStyle("-prop: " + val);
			return this;
		}

		public Builder cursor(Cursor val) {
			addStyle("-fx-cursor: " + val);
			return this;
		}

		public Builder focusTraversable(boolean val) {
			addStyle("-fx-focus-traversable: " + val);
			return this;
		}

		public Builder viewOrder(int val) {
			addStyle("-fx-view-order: " + val);
			return this;
		}

		public Builder opacity(double val) {
			addStyle("-fx-opacity: " + val);
			return this;
		}

		public Builder rotate(double val) {
			addStyle("-fx-rotate: " + val);
			return this;
		}

		public Builder scaleX(double val) {
			addStyle("-fx-scale-x: " + val);
			return this;
		}

		public Builder scaleY(double val) {
			addStyle("-fx-scale-y: " + val);
			return this;
		}

		public Builder scaleZ(double val) {
			addStyle("-fx-scale-z: " + val);
			return this;
		}

		public Builder translateX(double val) {
			addStyle("-fx-translate-x: " + val);
			return this;
		}

		public Builder translateY(double val) {
			addStyle("-fx-translate-y: " + val);
			return this;
		}

		public Builder translateZ(double val) {
			addStyle("-fx-translate-z: " + val);
			return this;
		}

		public Builder visibility(String val) {
			addStyle("visibility: " + val);
			return this;
		}

		public Builder managed(boolean val) {
			addStyle("-fx-managed: " + val);
			return this;
		}

		// REGION
		public Builder background(String val) {
			addStyle("-fx-background-color: " + val);
			return this;
		}

		public Builder backgroundInsets(String val) {
			addStyle("-fx-background-insets: " + val);
			return this;
		}

		public Builder backgroundRadius(String val) {
			addStyle("-fx-background-radius: " + val);
			return this;
		}

		public Builder border(String val) {
			addStyle("-fx-border-color: " + val);
			return this;
		}

		public Builder borderInsets(String val) {
			addStyle("-fx-border-insets: " + val);
			return this;
		}

		public Builder borderRadius(String val) {
			addStyle("-fx-border-radius: " + val);
			return this;
		}

		public Builder padding(String val) {
			addStyle("-fx-padding: " + val);
			return this;
		}

		public Builder positionShape(boolean val) {
			addStyle("-fx-position-shape: " + val);
			return this;
		}

		public Builder scaleShape(boolean val) {
			addStyle("-fx-scale-shape: " + val);
			return this;
		}

		public Builder shape(String val) {
			addStyle("-fx-shape: " + val);
			return this;
		}

		public Builder snapToPixel(boolean val) {
			addStyle("-fx-snap-to-pixel: " + val);
			return this;
		}

		public Builder minHeight(double val) {
			addStyle("-fx-min-height: " + val);
			return this;
		}

		public Builder prefHeight(double val) {
			addStyle("-fx-pref-height: " + val);
			return this;
		}

		public Builder maxHeight(double val) {
			addStyle("-fx-max-height: " + val);
			return this;
		}

		public Builder minWidth(double val) {
			addStyle("-fx-min-width: " + val);
			return this;
		}

		public Builder prefWidth(double val) {
			addStyle("-fx-pref-width: " + val);
			return this;
		}

		public Builder maxWidth(double val) {
			addStyle("-fx-max-width: " + val);
			return this;
		}

		// PANES
		public Builder hGap(double val) {
			addStyle("-fx-hgap: " + val);
			return this;
		}

		public Builder vGap(double val) {
			addStyle("-fx-vgap: " + val);
			return this;
		}

		public Builder alignment(Pos val) {
			addStyle("-fx-alignment: " + val);
			return this;
		}

		public Builder orientation(Orientation val) {
			addStyle("-fx-orientation: " + val);
			return this;
		}

		public Builder columnHAlignment(HPos val) {
			addStyle("-fx-column-halignment: " + val);
			return this;
		}

		public Builder rowVAlignment(VPos val) {
			addStyle("-fx-row-valignment: " + val);
			return this;
		}

		public Builder gridLineVisible(boolean val) {
			addStyle("-fx-grid-lines-visible: " + val);
			return this;
		}

		public Builder spacing(double val) {
			addStyle("-fx-spacing: " + val);
			return this;
		}

		public Builder fillHeight(boolean val) {
			addStyle("-fx-fill-height: " + val);
			return this;
		}

		public Builder fillWidth(boolean val) {
			addStyle("-fx-fill-width: " + val);
			return this;
		}

		public Builder prefRows(int val) {
			addStyle("-fx-pref-rows: " + val);
			return this;
		}

		public Builder prefColumns(int val) {
			addStyle("-fx-pref-columns: " + val);
			return this;
		}

		public Builder prefTileWidth(double val) {
			addStyle("-fx-pref-tile-width: " + val);
			return this;
		}

		public Builder prefTileHeight(double val) {
			addStyle("-fx-pref-tile-height: " + val);
			return this;
		}

		public Builder tileAlignment(Pos val) {
			addStyle("-fx-tile-alignment: " + val);
			return this;
		}

		// SHAPE
		public Builder fill(String val) {
			addStyle("-fx-fill: " + val);
			return this;
		}

		public Builder smooth(boolean val) {
			addStyle("-fx-smooth: " + val);
			return this;
		}

		public Builder stroke(String val) {
			addStyle("-fx-stroke: " + val);
			return this;
		}

		public Builder strokeType(StrokeType val) {
			addStyle("-fx-stroke-type: " + val);
			return this;
		}

		public Builder strokeDashArray(String val) {
			addStyle("-fx-stroke-dash-array: " + val);
			return this;
		}

		public Builder strokeDashOffset(double val) {
			addStyle("-fx-stroke-dash-offset: " + val);
			return this;
		}

		public Builder strokeLineCap(StrokeLineCap val) {
			addStyle("-fx-stroke-line-cap: " + val);
			return this;
		}

		public Builder strokeLineJoin(StrokeLineJoin val) {
			addStyle("-fx-stroke-line-join: " + val);
			return this;
		}

		public Builder strokeMiterLimit(double val) {
			addStyle("-fx-stroke-miter-limit: " + val);
			return this;
		}

		public Builder strokeWidth(double val) {
			addStyle("-fx-stroke-width: " + val);
			return this;
		}

		public Builder arcHeight(double val) {
			addStyle("-fx-arc-height: " + val);
			return this;
		}

		public Builder arcWidth(String val) {
			addStyle("-fx-arc-width: " + val);
			return this;
		}

		// TEXT
		public Builder fontFamily(String val) {
			addStyle("-fx-font-family: " + val);
			return this;
		}

		public Builder fontSize(String val) {
			addStyle("-fx-font-size: " + val);
			return this;
		}

		public Builder fontWeight(FontWeight val) {
			addStyle("-fx-font-weight: " + val);
			return this;
		}

		public Builder fontStyle(FontPosture val) {
			addStyle("-prop: " + val);
			return this;
		}

		public Builder fontSmoothingType(FontSmoothingType val) {
			addStyle("-fx-font-smoothing-type: " + val);
			return this;
		}

		public Builder tabSize(int val) {
			addStyle("-fx-tab-size: " + val);
			return this;
		}

		public Builder textAlignment(TextAlignment val) {
			addStyle("-fx-text-alignment: " + val);
			return this;
		}

		public Builder textOrigin(VPos val) {
			addStyle("-fx-text-origin: " + val);
			return this;
		}

		public Builder underline(boolean val) {
			addStyle("-fx-underline: " + val);
			return this;
		}

		/**
		 * This will work only for {@link Text}.
		 */
		public Builder wrappingWidth(double val) {
			addStyle("-fx-wrapping-width: " + val);
			return this;
		}

		// LABEL
		public Builder textOverrun(OverrunStyle val) {
			addStyle("-fx-text-overrun: " + val);
			return this;
		}

		public Builder wrapText(boolean val) {
			addStyle("-fx-wrap-text: " + val);
			return this;
		}

		public Builder contentDisplay(ContentDisplay val) {
			addStyle("-fx-content-display: " + val);
			return this;
		}

		public Builder graphicTextGap(double val) {
			addStyle("-fx-graphic-text-gap: " + val);
			return this;
		}

		public Builder labelPadding(double val) {
			addStyle("-fx-label-padding: " + val);
			return this;
		}

		public Builder textFill(String val) {
			addStyle("-fx-text-fill: " + val);
			return this;
		}

		public Builder ellipsisString(String val) {
			addStyle("-fx-ellipsis-string: " + val);
			return this;
		}

		// TEXT INPUT CONTROL
		public Builder promptTextFill(String val) {
			addStyle("-fx-prompt-text-fill: " + val);
			return this;
		}

		public Builder highlightFill(String val) {
			addStyle("-fx-highlight-fill: " + val);
			return this;
		}

		public Builder highlightTextFill(String val) {
			addStyle("-fx-highlight-text-fill: " + val);
			return this;
		}

		public Builder displayCaret(boolean val) {
			addStyle("-fx-display-caret: " + val);
			return this;
		}

		// TEXT AREA
		public Builder prefColumnCount(int val) {
			addStyle("-fx-pref-column-count: " + val);
			return this;
		}

		public Builder prefRowCount(int val) {
			addStyle("-fx-pref-row-count: " + val);
			return this;
		}

		// WEB VIEW
		public Builder contextMenuEnabled(boolean val) {
			addStyle("-fx-context-menu-enabled: " + val);
			return this;
		}

		public Builder pageFill(String val) {
			addStyle("-fx-page-fill: " + val);
			return this;
		}

		public Builder fontScale(double val) {
			addStyle("-fx-font-scale: " + val);
			return this;
		}
	}
}
