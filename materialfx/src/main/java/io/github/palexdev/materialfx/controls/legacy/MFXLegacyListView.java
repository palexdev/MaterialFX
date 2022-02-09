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

package io.github.palexdev.materialfx.controls.legacy;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.skins.legacy.MFXLegacyListViewSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the implementation of a ListView restyled to comply with modern standards.
 * <p>
 * Extends {@code ListView}, redefines the style class to "mfx-legacy-list-view for usage in CSS,
 * for cells it uses {@link MFXLegacyListCell} by default.
 */
public class MFXLegacyListView<T> extends ListView<T> {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXLegacyListView<?>> FACTORY = new StyleablePropertyFactory<>(ListView.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-legacy-list-view";
	private final String STYLESHEET = MFXResourcesLoader.load("css/legacy/MFXLegacyListView.css");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLegacyListView() {
		initialize();
	}

	public MFXLegacyListView(ObservableList<T> observableList) {
		super(observableList);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setCellFactory(cell -> new MFXLegacyListCell<>());
		addListeners();
	}

	/**
	 * Adds listeners for colors change to the scrollbars and calls setColors().
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
	// ScrollBars Properties
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

	/**
	 * Specifies the time after which the scrollbars are hidden.
	 */
	private final ObjectProperty<Duration> hideAfter = new SimpleObjectProperty<>(Duration.seconds(1));

	//================================================================================
	// Styleable Properties
	//================================================================================

	/**
	 * Specifies if the scrollbars should be hidden when the mouse is not on the list.
	 */
	private final StyleableBooleanProperty hideScrollBars = new SimpleStyleableBooleanProperty(
			StyleableProperties.HIDE_SCROLLBARS,
			this,
			"hideScrollBars",
			false
	);

	/**
	 * Specifies the shadow strength around the control.
	 */
	private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
			StyleableProperties.DEPTH_LEVEL,
			this,
			"depthLevel",
			DepthLevel.LEVEL2
	);

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

	public Duration getHideAfter() {
		return hideAfter.get();
	}

	public ObjectProperty<Duration> hideAfterProperty() {
		return hideAfter;
	}

	public void setHideAfter(Duration hideAfter) {
		this.hideAfter.set(hideAfter);
	}

	public boolean isHideScrollBars() {
		return hideScrollBars.get();
	}

	public StyleableBooleanProperty hideScrollBarsProperty() {
		return hideScrollBars;
	}

	public void setHideScrollBars(boolean hideScrollBars) {
		this.hideScrollBars.set(hideScrollBars);
	}

	public DepthLevel getDepthLevel() {
		return depthLevel.get();
	}

	public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
		return depthLevel;
	}

	public void setDepthLevel(DepthLevel depthLevel) {
		this.depthLevel.set(depthLevel);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXLegacyListView<?>, Boolean> HIDE_SCROLLBARS =
				FACTORY.createBooleanCssMetaData(
						"-mfx-hide-scrollbars",
						MFXLegacyListView::hideScrollBarsProperty,
						false
				);

		private static final CssMetaData<MFXLegacyListView<?>, DepthLevel> DEPTH_LEVEL =
				FACTORY.createEnumCssMetaData(
						DepthLevel.class,
						"-mfx-depth-level",
						MFXLegacyListView::depthLevelProperty,
						DepthLevel.LEVEL2
				);

		static {
			List<CssMetaData<? extends Styleable, ?>> lsvCssMetaData = new ArrayList<>(ListView.getClassCssMetaData());
			Collections.addAll(lsvCssMetaData, HIDE_SCROLLBARS, DEPTH_LEVEL);
			cssMetaDataList = Collections.unmodifiableList(lsvCssMetaData);
		}

	}

	public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXLegacyListViewSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXLegacyListView.getControlCssMetaDataList();
	}
}
