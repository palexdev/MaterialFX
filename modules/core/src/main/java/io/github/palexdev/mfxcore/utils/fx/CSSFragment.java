/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.Base64;
import java.util.Objects;

import io.github.palexdev.mfxcore.controls.Text;
import io.github.palexdev.mfxcore.utils.fx.InsetsUtils.InsetsBuilder;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;

/// Allows building CSS stylesheets by code and apply them on any [Parent].
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

    /// Applies the given CSS to the given [Parent]
    ///
    /// @see #applyOn(Parent)
    public static void applyOn(String css, Parent parent) {
        CSSFragment f = new CSSFragment(css);
        f.applyOn(parent);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Converts a CSS string to a data uri that can be used by JavaFX nodes to parse styles.
    ///
    /// Subsequent calls to this will be faster as the converted CSS is cached.
    ///
    /// @see [Data URI Scheme](https://en.wikipedia.org/wiki/Data_URI_scheme)
    public String toDataUri() {
        if (converted == null) {
            converted = DATA_URI_PREFIX + new String(enc.encode(css.getBytes(UTF_8)), UTF_8);
        }
        return converted;
    }

    /// If this CSS fragment has not been applied yet to the given [Parent], applies it
    /// using [Parent#getStylesheets()]
    ///
    /// @see #isAppliedOn(Parent)
    public void applyOn(Parent parent) {
        if (!isAppliedOn(parent))
            parent.getStylesheets().add(toDataUri());
    }

    /// If this CSS fragment has not been applied yet to the given [Scene], applies it
    /// using [Parent#getStylesheets()].
    ///
    /// @see #isAppliedOn(Scene)
    public void applyOn(Scene scene) {
        if (!isAppliedOn(scene))
            scene.getStylesheets().add(toDataUri());
    }

    /// If this CSS fragment has not been applied yet as the [Application]'s global user agent stylesheet, calls
    /// [Application#setUserAgentStylesheet(String)].
    public void setGlobal() {
        if (!isGlobal())
            Application.setUserAgentStylesheet(toDataUri());
    }

    /// Checks whether this CSS fragment has already been applied to the given [Parent]
    /// by checking if its stylesheets list contains this (converted to a Data URI).
    public boolean isAppliedOn(Parent parent) {
        return parent.getStylesheets().contains(toDataUri());
    }

    /// Checks whether this CSS fragment has already been applied to the given [Scene]
    /// by checking if its stylesheets list contains this (converted to a Data URI).
    public boolean isAppliedOn(Scene scene) {
        return scene.getStylesheets().contains(toDataUri());
    }

    /// Checks whether this CSS fragment has already been applied as the [Application]'s global user agent stylesheet,
    /// by checking if [Application#getUserAgentStylesheet()] is equal to this (converted to a Data URI).
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

    /// Allows building `CSSFragments` with fluent API.
    ///
    ///
    /// Usage example:
    /// <pre>
    ///
    /// `Parent p = ...;CssFragment.Builder.build().select(".aSelector").background(Color.RED).backgroundRadius(InsetsBuilder.uniform(10)).select(".aSelector:hover").background(Color.ORANGE).border(Color.LIGHTGRAY).style("-my-custom-property: custom-value").applyOn(p);`
    /// </pre>
    public static class Builder {
        private final StringBuilder sb = new StringBuilder();
        private boolean isSelectorOpen = false;
        private boolean isBracketOpen = false;

        public static Builder build() {
            return new Builder();
        }

        /// Use this to open a block for an element with its selector.
        /// It's unnecessary to add the ending '{\n' as it is automatically inserted when adding styles.
        public Builder select(String selector) {
            if (isSelectorOpen) {
                sb.append("\n}\n");
                isSelectorOpen = false;
                isBracketOpen = false;
            }
            sb.append(selector.trim());
            isSelectorOpen = true;
            return this;
        }

        /// Opens a selector with the style classes of the given [Styleable] object. If the list is empty, then uses
        /// the simple class name.
        ///
        /// Delegates to [#select(String)] and **beware** that the resulting selector is the result of chaining
        /// all the style classes.
        ///
        /// See: [css-tricks.com](https://css-tricks.com/multiple-class-id-selectors)
        public Builder select(Styleable styleable) {
            ObservableList<String> classes = styleable.getStyleClass();
            if (classes.isEmpty()) return select(styleable.getClass().getSimpleName());
            StringBuilder chain = new StringBuilder();
            classes.forEach(c -> chain.append(".").append(c));
            return select(chain.toString());
        }

        public Builder select(Styleable styleable, String... descendants) {
            ObservableList<String> classes = styleable.getStyleClass();
            if (classes.isEmpty()) return this;
            StringBuilder chain = new StringBuilder();
            classes.forEach(c -> chain.append(".").append(c));

            for (String descendant : descendants) {
                chain.append(" ").append(descendant);
            }
            return select(chain.toString());
        }

        /// Appends the given selector without closing the currently opne one.
        ///
        /// If one is not open, then delegates to [#select(String)].
        public Builder appendSelector(String selector) {
            if (!isSelectorOpen) return select(selector);
            sb.append(" ").append(selector);
            return this;
        }

        /// Appends the given pseudo-states to the currently open selector.
        ///
        /// @throws IllegalStateException if no selector is open
        public Builder states(String... states) {
            if (states.length == 0) return this;
            if (!isSelectorOpen) throw new IllegalStateException("No selector was opened!");
            for (String state : states) {
                sb.append(":").append(state.trim());
            }
            return this;
        }

        /// This method can be used to group multiple selectors since [#select(String)] automatically closes the
        /// previous one. An example of grouped selectors:
        /// <pre>
        ///
        /// `.my-selector-one,.my-selector-two{-common-style: common-value;}`
        /// </pre>
        ///
        /// If no selector was open before, fallbacks to [#select(String)].
        public Builder and(String selector) {
            if (isSelectorOpen) {
                sb.append(",\n");
                sb.append(selector.trim());
                return this;
            }
            return select(selector);
        }

        /// Adds the given style to the fragment.
        /// It's unnecessary to add the ending ';\n' as it is automatically added.
        public Builder style(String style) {
            if (!isSelectorOpen) throw new IllegalStateException("No selector was opened!");
            if (!isBracketOpen) {
                sb.append(" {");
                isBracketOpen = true;
            }
            sb.append("\n  ").append(style).append(";");
            return this;
        }

        /// Overridden to return the built CSS string.
        public String toCSS() {
            if (isSelectorOpen && !isBracketOpen) {
                sb.append(" {}\n");
            } else if (isBracketOpen) {
                sb.append("\n}");
                isBracketOpen = false;
            }
            return sb.toString().trim();
        }

        /// Converts the built string to a [CSSFragment].
        public CSSFragment toFragment() {
            if (sb.length() == 0) throw new IllegalStateException("No styles set");
            return new CSSFragment(toCSS().trim());
        }

        /// Applies the created [CSSFragment] on the given [Parent].
        ///
        /// @see #toFragment()
        /// @see CSSFragment#applyOn(Parent)
        public void applyOn(Parent parent) {
            toFragment().applyOn(parent);
        }

        /// Applies the created [CSSFragment] on the given [Scene].
        ///
        /// @see #toFragment()
        /// @see CSSFragment#applyOn(Scene)
        public void applyOn(Scene scene) {
            toFragment().applyOn(scene);
        }

        //================================================================================
        // Convenient Methods
        //================================================================================
        // NODE
        public Builder blendMode(BlendMode val) {
            style("-fx-blend-mode: " + val);
            return this;
        }

        public Builder cursor(Cursor val) {
            style("-fx-cursor: " + val);
            return this;
        }

        public Builder focusTraversable(boolean val) {
            style("-fx-focus-traversable: " + val);
            return this;
        }

        public Builder viewOrder(int val) {
            style("-fx-view-order: " + val);
            return this;
        }

        public Builder opacity(double val) {
            style("-fx-opacity: " + val);
            return this;
        }

        public Builder rotate(double val) {
            style("-fx-rotate: " + val);
            return this;
        }

        public Builder scaleX(double val) {
            style("-fx-scale-x: " + val);
            return this;
        }

        public Builder scaleY(double val) {
            style("-fx-scale-y: " + val);
            return this;
        }

        public Builder scaleZ(double val) {
            style("-fx-scale-z: " + val);
            return this;
        }

        public Builder translateX(double val) {
            style("-fx-translate-x: " + val);
            return this;
        }

        public Builder translateY(double val) {
            style("-fx-translate-y: " + val);
            return this;
        }

        public Builder translateZ(double val) {
            style("-fx-translate-z: " + val);
            return this;
        }

        public Builder visibility(Visibility val) {
            style("visibility: " + val.toString());
            return this;
        }

        public Builder managed(boolean val) {
            style("-fx-managed: " + val);
            return this;
        }

        // REGION
        public Builder background(String val) {
            style("-fx-background-color: " + val);
            return this;
        }

        public Builder background(Color val) {
            style("-fx-background-color: " + ColorUtils.toWebAlpha(val));
            return this;
        }

        public Builder backgroundInsets(String val) {
            style("-fx-background-insets: " + val);
            return this;
        }

        public Builder backgroundInsets(Insets val) {
            style("-fx-background-insets: " + InsetsUtils.stringify(val));
            return this;
        }

        public Builder backgroundInsets(InsetsBuilder val) {
            return backgroundInsets(val.get());
        }

        public Builder backgroundRadius(String val) {
            style("-fx-background-radius: " + val);
            return this;
        }

        // Insets cover the vast majority of use cases
        public Builder backgroundRadius(Insets val) {
            style("-fx-background-radius: " + InsetsUtils.stringify(val));
            return this;
        }

        // Insets cover the vast majority of use cases
        public Builder backgroundRadius(InsetsBuilder val) {
            return backgroundRadius(val.get());
        }

        public Builder border(String val) {
            style("-fx-border-color: " + val);
            return this;
        }

        public Builder border(Color val) {
            style("-fx-border-color: " + ColorUtils.toWebAlpha(val));
            return this;
        }

        public Builder borderInsets(String val) {
            style("-fx-border-insets: " + val);
            return this;
        }

        public Builder borderInsets(Insets val) {
            style("-fx-border-insets: " + InsetsUtils.stringify(val));
            return this;
        }

        public Builder borderInsets(InsetsBuilder val) {
            return borderInsets(val.get());
        }

        public Builder borderRadius(String val) {
            style("-fx-border-radius: " + val);
            return this;
        }

        // Insets cover the vast majority of use cases
        public Builder borderRadius(Insets val) {
            style("-fx-border-radius: " + InsetsUtils.stringify(val));
            return this;
        }

        // Insets cover the vast majority of use cases
        public Builder borderRadius(InsetsBuilder val) {
            return borderRadius(val.get());
        }

        public Builder borderWidth(double val) {
            style("-fx-border-width: " + val);
            return this;
        }

        public Builder padding(String val) {
            style("-fx-padding: " + val);
            return this;
        }

        public Builder padding(Insets val) {
            style("-fx-padding: " + InsetsUtils.stringify(val));
            return this;
        }

        public Builder padding(InsetsBuilder val) {
            return padding(val.get());
        }

        public Builder positionShape(boolean val) {
            style("-fx-position-shape: " + val);
            return this;
        }

        public Builder scaleShape(boolean val) {
            style("-fx-scale-shape: " + val);
            return this;
        }

        public Builder shape(String val) {
            style("-fx-shape: " + val);
            return this;
        }

        public Builder snapToPixel(boolean val) {
            style("-fx-snap-to-pixel: " + val);
            return this;
        }

        public Builder minHeight(double val) {
            style("-fx-min-height: " + val);
            return this;
        }

        public Builder prefHeight(double val) {
            style("-fx-pref-height: " + val);
            return this;
        }

        public Builder maxHeight(double val) {
            style("-fx-max-height: " + val);
            return this;
        }

        public Builder minWidth(double val) {
            style("-fx-min-width: " + val);
            return this;
        }

        public Builder prefWidth(double val) {
            style("-fx-pref-width: " + val);
            return this;
        }

        public Builder maxWidth(double val) {
            style("-fx-max-width: " + val);
            return this;
        }

        // PANES
        public Builder hGap(double val) {
            style("-fx-hgap: " + val);
            return this;
        }

        public Builder vGap(double val) {
            style("-fx-vgap: " + val);
            return this;
        }

        public Builder alignment(Pos val) {
            style("-fx-alignment: " + val);
            return this;
        }

        public Builder orientation(Orientation val) {
            style("-fx-orientation: " + val);
            return this;
        }

        public Builder columnHAlignment(HPos val) {
            style("-fx-column-halignment: " + val);
            return this;
        }

        public Builder rowVAlignment(VPos val) {
            style("-fx-row-valignment: " + val);
            return this;
        }

        public Builder gridLineVisible(boolean val) {
            style("-fx-grid-lines-visible: " + val);
            return this;
        }

        public Builder spacing(double val) {
            style("-fx-spacing: " + val);
            return this;
        }

        public Builder fillHeight(boolean val) {
            style("-fx-fill-height: " + val);
            return this;
        }

        public Builder fillWidth(boolean val) {
            style("-fx-fill-width: " + val);
            return this;
        }

        public Builder prefRows(int val) {
            style("-fx-pref-rows: " + val);
            return this;
        }

        public Builder prefColumns(int val) {
            style("-fx-pref-columns: " + val);
            return this;
        }

        public Builder prefTileWidth(double val) {
            style("-fx-pref-tile-width: " + val);
            return this;
        }

        public Builder prefTileHeight(double val) {
            style("-fx-pref-tile-height: " + val);
            return this;
        }

        public Builder tileAlignment(Pos val) {
            style("-fx-tile-alignment: " + val);
            return this;
        }

        // SHAPE
        public Builder fill(String val) {
            style("-fx-fill: " + val);
            return this;
        }

        public Builder fill(Color val) {
            style("-fx-fill: " + ColorUtils.toWebAlpha(val));
            return this;
        }

        public Builder smooth(boolean val) {
            style("-fx-smooth: " + val);
            return this;
        }

        public Builder stroke(String val) {
            style("-fx-stroke: " + val);
            return this;
        }

        public Builder stroke(Color val) {
            style("-fx-stroke: " + ColorUtils.toWebAlpha(val));
            return this;
        }

        public Builder strokeType(StrokeType val) {
            style("-fx-stroke-type: " + val);
            return this;
        }

        public Builder strokeDashArray(String val) {
            style("-fx-stroke-dash-array: " + val);
            return this;
        }

        public Builder strokeDashArray(double... vals) {
            StringBuilder sb = new StringBuilder();
            for (double val : vals) {
                sb.append(val).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            style("-fx-stroke-dash-array: " + sb);
            return this;
        }

        public Builder strokeDashOffset(double val) {
            style("-fx-stroke-dash-offset: " + val);
            return this;
        }

        public Builder strokeLineCap(StrokeLineCap val) {
            style("-fx-stroke-line-cap: " + val);
            return this;
        }

        public Builder strokeLineJoin(StrokeLineJoin val) {
            style("-fx-stroke-line-join: " + val);
            return this;
        }

        public Builder strokeMiterLimit(double val) {
            style("-fx-stroke-miter-limit: " + val);
            return this;
        }

        public Builder strokeWidth(double val) {
            style("-fx-stroke-width: " + val);
            return this;
        }

        public Builder arcHeight(double val) {
            style("-fx-arc-height: " + val);
            return this;
        }

        public Builder arcWidth(double val) {
            style("-fx-arc-width: " + val);
            return this;
        }

        // TEXT
        public Builder fontFamily(String val) {
            style("-fx-font-family: " + val);
            return this;
        }

        public Builder fontSize(double val) {
            style("-fx-font-size: " + val);
            return this;
        }

        public Builder fontWeight(FontWeight val) {
            style("-fx-font-weight: " + val);
            return this;
        }

        public Builder fontStyle(FontPosture val) {
            style("-fx-font-style: " + val);
            return this;
        }

        public Builder fontSmoothingType(FontSmoothingType val) {
            style("-fx-font-smoothing-type: " + val);
            return this;
        }

        public Builder tabSize(int val) {
            style("-fx-tab-size: " + val);
            return this;
        }

        public Builder textAlignment(TextAlignment val) {
            style("-fx-text-alignment: " + val);
            return this;
        }

        public Builder textOrigin(VPos val) {
            style("-fx-text-origin: " + val);
            return this;
        }

        public Builder underline(boolean val) {
            style("-fx-underline: " + val);
            return this;
        }

        /// This will work only for [Text].
        public Builder wrappingWidth(double val) {
            style("-fx-wrapping-width: " + val);
            return this;
        }

        // LABEL
        public Builder textOverrun(OverrunStyle val) {
            style("-fx-text-overrun: " + val);
            return this;
        }

        public Builder wrapText(boolean val) {
            style("-fx-wrap-text: " + val);
            return this;
        }

        public Builder contentDisplay(ContentDisplay val) {
            style("-fx-content-display: " + val);
            return this;
        }

        public Builder graphicTextGap(double val) {
            style("-fx-graphic-text-gap: " + val);
            return this;
        }

        public Builder labelPadding(double val) {
            style("-fx-label-padding: " + val);
            return this;
        }

        public Builder textFill(String val) {
            style("-fx-text-fill: " + val);
            return this;
        }

        public Builder textFill(Color val) {
            style("-fx-text-fill: " + ColorUtils.toWeb(val));
            return this;
        }

        public Builder ellipsisString(String val) {
            style("-fx-ellipsis-string: " + val);
            return this;
        }

        // TEXT INPUT CONTROL
        public Builder promptTextFill(String val) {
            style("-fx-prompt-text-fill: " + val);
            return this;
        }

        public Builder promptTextFill(Color val) {
            style("-fx-prompt-text-fill: " + ColorUtils.toWeb(val));
            return this;
        }

        public Builder highlightFill(String val) {
            style("-fx-highlight-fill: " + val);
            return this;
        }

        public Builder highlightFill(Color val) {
            style("-fx-highlight-fill: " + ColorUtils.toWeb(val));
            return this;
        }

        public Builder highlightTextFill(String val) {
            style("-fx-highlight-text-fill: " + val);
            return this;
        }

        public Builder highlightTextFill(Color val) {
            style("-fx-highlight-text-fill: " + ColorUtils.toWeb(val));
            return this;
        }

        public Builder displayCaret(boolean val) {
            style("-fx-display-caret: " + val);
            return this;
        }

        // TEXT AREA
        public Builder prefColumnCount(int val) {
            style("-fx-pref-column-count: " + val);
            return this;
        }

        public Builder prefRowCount(int val) {
            style("-fx-pref-row-count: " + val);
            return this;
        }

        // WEB VIEW
        public Builder contextMenuEnabled(boolean val) {
            style("-fx-context-menu-enabled: " + val);
            return this;
        }

        public Builder pageFill(String val) {
            style("-fx-page-fill: " + val);
            return this;
        }

        public Builder pageFill(Color val) {
            style("-fx-page-fill: " + ColorUtils.toWeb(val));
            return this;
        }

        public Builder fontScale(double val) {
            style("-fx-font-scale: " + val);
            return this;
        }

        // TRANSITIONS
        public Builder transition(String transition) {
            style("transition: " + transition);
            return this;
        }

        public Builder transitionProperty(String property) {
            style("transition-property: " + property);
            return this;
        }

        public Builder transitionDuration(double millis) {
            style("transition-duration: " + millis + "ms");
            return this;
        }

        public Builder transitionDuration(Duration duration) {
            style("transition-duration: " + duration.toString().replace(" ", ""));
            return this;
        }

        public Builder transitionCurve(String curve) {
            style("transition-timing-function: " + curve);
            return this;
        }

        public Builder transitionDelay(Duration duration) {
            style("transition-delay: " + duration);
            return this;
        }
    }

    public enum Visibility {
        VISIBLE,
        HIDDEN,
        COLLAPSE,
        INHERIT,
        ;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
