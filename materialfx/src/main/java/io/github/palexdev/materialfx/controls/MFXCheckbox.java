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
import io.github.palexdev.materialfx.skins.MFXCheckboxSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.css.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * This is the implementation of a checkbox following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code CheckBox}, redefines the style class to "mfx-checkbox" for usage in CSS and
 * includes a {@code RippleGenerator}(in the Skin) to generate ripple effect on click.
 * <p></p>
 * It also introduces some new features like:
 * <p> - {@link #contentDispositionProperty()}: to control the checkbox position
 * <p> - {@link #gapProperty()}: to control the gap between the checkbox and the text
 * <p> - {@link #textExpandProperty()}: to control the text size and the checkbox layout (see documentation)
 */
public class MFXCheckbox extends CheckBox implements MFXLabeled {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXCheckbox> FACTORY = new StyleablePropertyFactory<>(CheckBox.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-checkbox";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXCheckBox.css");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckbox() {
		this("");
	}

	public MFXCheckbox(String text) {
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
	// Stylesheet properties
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

		private static final CssMetaData<MFXCheckbox, ContentDisplay> CONTENT_DISPOSITION =
				FACTORY.createEnumCssMetaData(
						ContentDisplay.class,
						"-mfx-content-disposition",
						MFXCheckbox::contentDispositionProperty,
						ContentDisplay.LEFT
				);

		private static final CssMetaData<MFXCheckbox, Number> GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-gap",
						MFXCheckbox::gapProperty,
						8.0
				);

		private static final CssMetaData<MFXCheckbox, Boolean> TEXT_EXPAND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-text-expand",
						MFXCheckbox::textExpandProperty,
						false
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					CheckBox.getClassCssMetaData(),
					CONTENT_DISPOSITION, GAP, TEXT_EXPAND
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
		return new MFXCheckboxSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXCheckbox.getControlCssMetaDataList();
	}
}
