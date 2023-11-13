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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.base.Themable;
import io.github.palexdev.materialfx.skins.MFXScrollPaneSkin;
import io.github.palexdev.materialfx.theming.CSSFragment;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.base.Theme;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Objects;

/**
 * This is the implementation of a scroll pane following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code ScrollPane} and redefines the style class to "mfx-scroll-pane" for usage in CSS.
 */
public class MFXScrollPane extends ScrollPane implements Themable {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-scroll-pane";
	private String colorsStylesheet;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXScrollPane() {
		initialize();
	}

	public MFXScrollPane(Node content) {
		super(content);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		sceneBuilderIntegration();
	}

	//================================================================================
	// Style Properties
	//================================================================================

	/**
	 * Specifies the color of the scrollbars' track.
	 */
	private final ObjectProperty<Paint> trackColor = new SimpleObjectProperty<>(Color.rgb(132, 132, 132)) {
		@Override
		public void set(Paint newValue) {
			Paint old = get();
			if (!Objects.equals(old, newValue)) setColors();
			super.set(newValue);
		}
	};

	/**
	 * Specifies the color of the scrollbars' thumb.
	 */
	private final ObjectProperty<Paint> thumbColor = new SimpleObjectProperty<>(Color.rgb(137, 137, 137)) {
		@Override
		public void set(Paint newValue) {
			Paint old = get();
			if (!Objects.equals(old, newValue)) setColors();
			super.set(newValue);
		}
	};

	/**
	 * Specifies the color of the scrollbars' thumb when mouse hover.
	 */
	private final ObjectProperty<Paint> thumbHoverColor = new SimpleObjectProperty<>(Color.rgb(89, 88, 91)) {
		@Override
		public void set(Paint newValue) {
			Paint old = get();
			if (!Objects.equals(old, newValue)) setColors();
			super.set(newValue);
		}
	};

	public Paint getTrackColor() {
		return trackColor.get();
	}

	public ObjectProperty<Paint> trackColorProperty() {
		return trackColor;
	}

	public void setTrackColor(Paint trackColor) {
		this.trackColor.set(trackColor);
	}

	public Paint getThumbColor() {
		return thumbColor.get();
	}

	public ObjectProperty<Paint> thumbColorProperty() {
		return thumbColor;
	}

	public void setThumbColor(Paint thumbColor) {
		this.thumbColor.set(thumbColor);
	}

	public Paint getThumbHoverColor() {
		return thumbHoverColor.get();
	}

	public ObjectProperty<Paint> thumbHoverColorProperty() {
		return thumbHoverColor;
	}

	public void setThumbHoverColor(Paint thumbHoverColor) {
		this.thumbHoverColor.set(thumbHoverColor);
	}

	/**
	 * Sets the CSS looked-up colors
	 */
	private void setColors() {
		if (colorsStylesheet != null) {
			getStylesheets().remove(colorsStylesheet);
		}
		colorsStylesheet = CSSFragment.Builder.build()
			.addSelector(".mfx-scroll-pane")
			.addStyle("-track-color: " + ColorUtils.toCss(trackColor.get()))
			.addStyle("-thumb-color: " + ColorUtils.toCss(thumbColor.get()))
			.addStyle("-thumb-hover-color: " + ColorUtils.toCss(thumbHoverColor.get()))
			.closeSelector()
			.toCSS()
			.toDataUri();
		getStylesheets().add(colorsStylesheet);
	}

	//================================================================================
	// Override Methods
	//================================================================================

	@Override
	public Parent toParent() {
		return this;
	}

	@Override
	public Theme getTheme() {
		return MaterialFXStylesheets.SCROLL_PANE;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXScrollPaneSkin(this);
	}
}
