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
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the implementation of a ListCell restyled to comply with modern standards.
 * <p>
 * Extends {@code ListCell}, redefines the style class to "mfx-legacy-list-cell" for usage in CSS,
 * each cell has a {@code RippleGenerator} to generate ripple effects on click.
 */
public class MFXLegacyListCell<T> extends ListCell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXLegacyListCell<?>> FACTORY = new StyleablePropertyFactory<>(ListCell.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-legacy-list-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/legacy/MFXLegacyListCell.css");
	private final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLegacyListCell() {
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setPadding(InsetsFactory.of(8, 12, 8, 12));
		addListeners();
	}

	/**
	 * Adds a listener to {@code listViewProperty} to bind the ripple radius to the
	 * listView width.
	 * <p>
	 * Adds listeners to {@code selectedProperty} and {@code hoverProperty} to set the background color
	 * according to {@link #selectedColor} and {@link #hoverColor}. When not selected the default color
	 * is white.
	 * <p>
	 * Adds a listener to the {@link #selectedColor} property in case of changes and the cell is selected.
	 */
	private void addListeners() {
		listViewProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				rippleGenerator.rippleRadiusProperty().bind(newValue.widthProperty().divide(2.0));
			}
		});

		selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				NodeUtils.updateBackground(MFXLegacyListCell.this, getSelectedColor(), new CornerRadii(getCornerRadius()), InsetsFactory.all(getBackgroundInsets()));
			} else {
				NodeUtils.updateBackground(MFXLegacyListCell.this, Color.WHITE, new CornerRadii(getCornerRadius()), InsetsFactory.all(getBackgroundInsets()));
			}
		});

		backgroundProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && isSelected() && !containsFill(newValue.getFills(), getSelectedColor())) {
				NodeUtils.updateBackground(this, getSelectedColor(), new CornerRadii(getCornerRadius()), InsetsFactory.all(getBackgroundInsets()));
			}
		});

		hoverProperty().addListener((observable, oldValue, newValue) -> {
			if (isSelected() || isEmpty()) {
				return;
			}

			if (newValue) {
				if (getIndex() == 0) {
					setBackground(new Background(new BackgroundFill(getHoverColor(), CornerRadii.EMPTY, Insets.EMPTY)));
				} else {
					NodeUtils.updateBackground(MFXLegacyListCell.this, getHoverColor(), new CornerRadii(getCornerRadius()), InsetsFactory.all(getBackgroundInsets()));
				}
			} else {
				NodeUtils.updateBackground(MFXLegacyListCell.this, Color.WHITE, new CornerRadii(getCornerRadius()), InsetsFactory.all(getBackgroundInsets()));
			}
		});

		selectedColor.addListener((observableValue, oldValue, newValue) -> {
			if (!newValue.equals(oldValue) && isSelected()) {
				NodeUtils.updateBackground(MFXLegacyListCell.this, newValue);
			}
		});

		setupRippleGenerator();
	}

	protected void setupRippleGenerator() {
		rippleGenerator.setRippleColor(Color.rgb(50, 150, 255));
		rippleGenerator.setRipplePositionFunction(event -> PositionBean.of(event.getX(), event.getY()));
		addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
	}

	private boolean containsFill(List<BackgroundFill> backgroundFills, Paint fill) {
		List<Paint> paints = backgroundFills.stream()
				.map(BackgroundFill::getFill)
				.collect(Collectors.toList());

		return paints.contains(fill);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================

	/**
	 * Specifies the background color of the cell when it is selected.
	 */
	private final StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.SELECTED_COLOR,
			this,
			"selectedColor",
			Color.rgb(180, 180, 255)
	);

	/**
	 * Specifies the background color of the cell when the mouse is hover.
	 */
	private final StyleableObjectProperty<Paint> hoverColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.HOVER_COLOR,
			this,
			"hoverColor",
			Color.rgb(50, 150, 255, 0.2)
	);

	private final StyleableDoubleProperty cornerRadius = new SimpleStyleableDoubleProperty(
			StyleableProperties.CORNER_RADIUS,
			this,
			"cornerRadius",
			0.0
	);

	private final StyleableDoubleProperty backgroundInsets = new SimpleStyleableDoubleProperty(
			StyleableProperties.BACKGROUND_INSETS,
			this,
			"backgroundInsets",
			0.0
	);

	public Paint getSelectedColor() {
		return selectedColor.get();
	}

	public StyleableObjectProperty<Paint> selectedColorProperty() {
		return selectedColor;
	}

	public void setSelectedColor(Paint selectedColor) {
		this.selectedColor.set(selectedColor);
	}

	public Paint getHoverColor() {
		return hoverColor.get();
	}

	public StyleableObjectProperty<Paint> hoverColorProperty() {
		return hoverColor;
	}

	public void setHoverColor(Paint hoverColor) {
		this.hoverColor.set(hoverColor);
	}

	public double getCornerRadius() {
		return cornerRadius.get();
	}

	public StyleableDoubleProperty cornerRadiusProperty() {
		return cornerRadius;
	}

	public void setCornerRadius(double cornerRadius) {
		this.cornerRadius.set(cornerRadius);
	}

	public double getBackgroundInsets() {
		return backgroundInsets.get();
	}

	public StyleableDoubleProperty backgroundInsetsProperty() {
		return backgroundInsets;
	}

	public void setBackgroundInsets(double backgroundInsets) {
		this.backgroundInsets.set(backgroundInsets);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXLegacyListCell<?>, Paint> SELECTED_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-selected-color",
						MFXLegacyListCell::selectedColorProperty,
						Color.rgb(180, 180, 255)
				);

		private static final CssMetaData<MFXLegacyListCell<?>, Paint> HOVER_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-hover-color",
						MFXLegacyListCell::hoverColorProperty,
						Color.rgb(50, 150, 255, 0.2)
				);

		private static final CssMetaData<MFXLegacyListCell<?>, Number> CORNER_RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-corner-radius",
						MFXLegacyListCell::cornerRadiusProperty,
						0
				);

		private static final CssMetaData<MFXLegacyListCell<?>, Number> BACKGROUND_INSETS =
				FACTORY.createSizeCssMetaData(
						"-mfx-background-insets",
						MFXLegacyListCell::backgroundInsetsProperty,
						0
				);

		static {
			List<CssMetaData<? extends Styleable, ?>> licCssMetaData = new ArrayList<>(ListCell.getClassCssMetaData());
			Collections.addAll(licCssMetaData, SELECTED_COLOR, HOVER_COLOR, CORNER_RADIUS, BACKGROUND_INSETS);
			cssMetaDataList = Collections.unmodifiableList(licCssMetaData);
		}

	}

	public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXLegacyListCell.getControlCssMetaDataList();
	}

	/**
	 * Overridden method to add the {@code RippleGenerator} and
	 * allow {@code Nodes}.
	 */
	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setGraphic(null);
			setText(null);
		} else {
			if (item instanceof Node) {
				Node nodeItem = (Node) item;
				setGraphic(nodeItem);
			} else {
				setText(item.toString());
			}

			if (!getChildren().contains(rippleGenerator)) {
				getChildren().add(0, rippleGenerator);
			}
		}
	}
}
