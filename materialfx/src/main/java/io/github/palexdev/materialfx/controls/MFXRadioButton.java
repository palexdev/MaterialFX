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
import io.github.palexdev.materialfx.controls.base.MFXLabeled;
import io.github.palexdev.materialfx.skins.MFXRadioButtonSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.css.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * This is the implementation of a radio button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code RadioButton}, redefines the style class to "mfx-radio-button" for usage in CSS and
 * includes a {@code RippleGenerator} to generate ripple effects on click.
 * <p></p>
 * It also introduces some new features like:
 * <p> - {@link #contentDispositionProperty()}: to control the radio position
 * <p> - {@link #gapProperty()}: to control the gap between the radio button and the text
 * <p> - {@link #radioGapProperty()}: to control the gap between the outer and inner circle
 * <p> - {@link #radiusProperty()}: to control the circles' radius
 * <p> - {@link #textExpandProperty()}: to control the text size and the checkbox layout (see documentation)
 */
public class MFXRadioButton extends RadioButton implements MFXLabeled {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXRadioButton> FACTORY = new StyleablePropertyFactory<>(RadioButton.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-radio-button";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXRadioButton.css");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXRadioButton() {
		this("");
	}

	public MFXRadioButton(String text) {
		super(text);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableObjectProperty<ContentDisplay> contentDisposition = new SimpleStyleableObjectProperty<>(
			StyleableProperties.CONTENT_DISPOSITION,
			this,
			"contentDisposition",
			ContentDisplay.LEFT
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty gap = new SimpleStyleableDoubleProperty(
			StyleableProperties.GAP,
			this,
			"gap",
			8.0
	);

	private final StyleableDoubleProperty radioGap = new SimpleStyleableDoubleProperty(
			StyleableProperties.RADIO_GAP,
			this,
			"radioGap",
			3.5
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
			StyleableProperties.RADIUS,
			this,
			"radius",
			8.0
	);

	private final StyleableBooleanProperty textExpand = new SimpleStyleableBooleanProperty(
			StyleableProperties.TEXT_EXPAND,
			this,
			"textExpand",
			false
	);

	public ContentDisplay getContentDisposition() {
		return contentDisposition.get();
	}

	public StyleableObjectProperty<ContentDisplay> contentDispositionProperty() {
		return contentDisposition;
	}

	public void setContentDisposition(ContentDisplay contentDisposition) {
		this.contentDisposition.set(contentDisposition);
	}

	public double getGap() {
		return gap.get();
	}

	public StyleableDoubleProperty gapProperty() {
		return gap;
	}

	public void setGap(double gap) {
		this.gap.set(gap);
	}

	public double getRadioGap() {
		return radioGap.get();
	}

	/**
	 * Specifies the gap between the outer and the inner circles of the radio button.
	 */
	public StyleableDoubleProperty radioGapProperty() {
		return radioGap;
	}

	public void setRadioGap(double radioGap) {
		this.radioGap.set(radioGap);
	}

	public double getRadius() {
		return radius.get();
	}

	/**
	 * Specifies the radius of the radio button.
	 */
	public StyleableDoubleProperty radiusProperty() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius.set(radius);
	}

	public boolean isTextExpand() {
		return textExpand.get();
	}

	public StyleableBooleanProperty textExpandProperty() {
		return textExpand;
	}

	public void setTextExpand(boolean textExpand) {
		this.textExpand.set(textExpand);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXRadioButton, ContentDisplay> CONTENT_DISPOSITION =
				FACTORY.createEnumCssMetaData(
						ContentDisplay.class,
						"-mfx-content-disposition",
						MFXRadioButton::contentDispositionProperty,
						ContentDisplay.LEFT
				);

		private static final CssMetaData<MFXRadioButton, Number> GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-gap",
						MFXRadioButton::gapProperty,
						8.0
				);

		private static final CssMetaData<MFXRadioButton, Number> RADIO_GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-radio-gap",
						MFXRadioButton::radioGapProperty,
						3.5
				);

		private static final CssMetaData<MFXRadioButton, Number> RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-radius",
						MFXRadioButton::radiusProperty,
						8.0
				);

		private static final CssMetaData<MFXRadioButton, Boolean> TEXT_EXPAND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-text-expand",
						MFXRadioButton::textExpandProperty,
						false
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					RadioButton.getClassCssMetaData(),
					CONTENT_DISPOSITION, GAP, RADIO_GAP,
					RADIUS, TEXT_EXPAND
			);
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
		return new MFXRadioButtonSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXRadioButton.getControlCssMetaDataList();
	}
}
