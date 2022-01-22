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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXScrollPaneSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * This is the implementation of a scroll pane following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code ScrollPane} and redefines the style class to "mfx-scroll-pane" for usage in CSS.
 */
public class MFXScrollPane extends ScrollPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-scroll-pane";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXScrollPane.css");

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
		addListeners();
	}

	//================================================================================
	// Style Properties
	//================================================================================

	/**
	 * Specifies the color of the scrollbars' track.
	 */
	private final ObjectProperty<Paint> trackColor = new SimpleObjectProperty<>(Color.rgb(132, 132, 132));

	/**
	 * Specifies the color of the scrollbars' thumb.
	 */
	private final ObjectProperty<Paint> thumbColor = new SimpleObjectProperty<>(Color.rgb(137, 137, 137));

	/**
	 * Specifies the color of the scrollbars' thumb when mouse hover.
	 */
	private final ObjectProperty<Paint> thumbHoverColor = new SimpleObjectProperty<>(Color.rgb(89, 88, 91));

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
	 * Adds listeners for colors change and calls setColors().
	 */
	private void addListeners() {
		this.trackColor.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				setColors();
			}
		});

		this.thumbColor.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				setColors();
			}
		});

		this.thumbHoverColor.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				setColors();
			}
		});
	}

	/**
	 * Sets the CSS looked-up colors
	 */
	private void setColors() {
		StringBuilder sb = new StringBuilder();
		sb.append("-mfx-track-color: ").append(ColorUtils.toCss(trackColor.get()))
				.append(";\n-mfx-thumb-color: ").append(ColorUtils.toCss(thumbColor.get()))
				.append(";\n-mfx-thumb-hover-color: ").append(ColorUtils.toCss(thumbHoverColor.get()))
				.append(";");
		setStyle(sb.toString());
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXScrollPaneSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

}
