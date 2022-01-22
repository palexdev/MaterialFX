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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Base class for all list views based on VirtualizedFX, defines common properties and behavior.
 *
 * @param <T> the type of data within the ListView
 */
public abstract class AbstractMFXListView<T, C extends Cell<T>> extends Control implements IListView<T, C> {
	//================================================================================
	// Properties
	//================================================================================
	protected final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>(FXCollections.observableArrayList());
	protected final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
	protected final IMultipleSelectionModel<T> selectionModel = new MultipleSelectionModel<>(items);

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXListView() {}

	public AbstractMFXListView(ObservableList<T> items) {
		setItems(items);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Abstract method called automatically to set a default factory for the cells.
	 */
	protected abstract void setDefaultCellFactory();

	//================================================================================
	// Methods
	//================================================================================
	protected void initialize() {
		setDefaultCellFactory();
		addBarsListeners();
	}

	protected void addBarsListeners() {
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
	protected void setColors() {
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
	private final ObjectProperty<Paint> trackColor = new SimpleObjectProperty<>(Color.rgb(230, 230, 230));
	private final ObjectProperty<Paint> thumbColor = new SimpleObjectProperty<>(Color.rgb(137, 137, 137));
	private final ObjectProperty<Paint> thumbHoverColor = new SimpleObjectProperty<>(Color.rgb(89, 88, 91));
	private final ObjectProperty<Duration> hideAfter = new SimpleObjectProperty<>(Duration.seconds(1));

	public Paint getTrackColor() {
		return trackColor.get();
	}

	/**
	 * Specifies the color of the scrollbars' track.
	 */
	public ObjectProperty<Paint> trackColorProperty() {
		return trackColor;
	}

	public void setTrackColor(Paint trackColor) {
		this.trackColor.set(trackColor);
	}

	public Paint getThumbColor() {
		return thumbColor.get();
	}

	/**
	 * Specifies the color of the scrollbars' thumb.
	 */
	public ObjectProperty<Paint> thumbColorProperty() {
		return thumbColor;
	}

	public void setThumbColor(Paint thumbColor) {
		this.thumbColor.set(thumbColor);
	}

	public Paint getThumbHoverColor() {
		return thumbHoverColor.get();
	}

	/**
	 * Specifies the color of the scrollbars' thumb when mouse hover.
	 */
	public ObjectProperty<Paint> thumbHoverColorProperty() {
		return thumbHoverColor;
	}

	public void setThumbHoverColor(Paint thumbHoverColor) {
		this.thumbHoverColor.set(thumbHoverColor);
	}

	public Duration getHideAfter() {
		return hideAfter.get();
	}

	/**
	 * Specifies the time after which the scrollbars are hidden.
	 */
	public ObjectProperty<Duration> hideAfterProperty() {
		return hideAfter;
	}

	public void setHideAfter(Duration hideAfter) {
		this.hideAfter.set(hideAfter);
	}


	//================================================================================
	// Getters/Setters
	//================================================================================
	@Override
	public ObservableList<T> getItems() {
		return items.get();
	}

	@Override
	public ObjectProperty<ObservableList<T>> itemsProperty() {
		return items;
	}

	@Override
	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}

	@Override
	public StringConverter<T> getConverter() {
		return converter.get();
	}

	@Override
	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	@Override
	public void setConverter(StringConverter<T> converter) {
		this.converter.set(converter);
	}

	@Override
	public IMultipleSelectionModel<T> getSelectionModel() {
		return selectionModel;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty hideScrollBars = new SimpleStyleableBooleanProperty(
			StyleableProperties.HIDE_SCROLLBARS,
			this,
			"hideScrollBars",
			false
	);

	private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
			StyleableProperties.DEPTH_LEVEL,
			this,
			"depthLevel",
			DepthLevel.LEVEL2
	);

	public boolean isHideScrollBars() {
		return hideScrollBars.get();
	}

	/**
	 * Specifies if the scrollbars should be hidden when the mouse is not on the list.
	 */
	public StyleableBooleanProperty hideScrollBarsProperty() {
		return hideScrollBars;
	}

	public void setHideScrollBars(boolean hideScrollBars) {
		this.hideScrollBars.set(hideScrollBars);
	}

	public DepthLevel getDepthLevel() {
		return depthLevel.get();
	}

	/**
	 * Specifies the shadow strength around the control.
	 */
	public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
		return depthLevel;
	}

	public void setDepthLevel(DepthLevel depthLevel) {
		this.depthLevel.set(depthLevel);
	}

	private static class StyleableProperties {
		private static final StyleablePropertyFactory<AbstractMFXListView<?, ?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<AbstractMFXListView<?, ?>, Boolean> HIDE_SCROLLBARS =
				FACTORY.createBooleanCssMetaData(
						"-mfx-hide-scrollbars",
						AbstractMFXListView::hideScrollBarsProperty,
						false
				);

		private static final CssMetaData<AbstractMFXListView<?, ?>, DepthLevel> DEPTH_LEVEL =
				FACTORY.createEnumCssMetaData(
						DepthLevel.class,
						"-mfx-depth-level",
						AbstractMFXListView::depthLevelProperty,
						DepthLevel.LEVEL2
				);


		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Control.getClassCssMetaData(),
					HIDE_SCROLLBARS, DEPTH_LEVEL
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return AbstractMFXListView.getClassCssMetaData();
	}
}
