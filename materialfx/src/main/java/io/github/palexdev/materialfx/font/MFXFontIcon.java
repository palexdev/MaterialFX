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

package io.github.palexdev.materialfx.font;

import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.binding.Bindings;
import javafx.css.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Class used for MaterialFX font icon resources.
 */
public class MFXFontIcon extends Text {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXFontIcon> FACTORY = new StyleablePropertyFactory<>(Text.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-font-icon";

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFontIcon() {
		this(null);
	}

	public MFXFontIcon(String description) {
		this(description, 10);
	}

	public MFXFontIcon(String description, Color color) {
		this(description, 10, color);
	}

	public MFXFontIcon(String description, double size) {
		this(description, size, Color.web("#454545"));
	}

	public MFXFontIcon(String description, double size, Color color) {
		initialize();
		setDescription(description);
		setFont(Font.font(getFont().getFamily(), size));
		setColor(color);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setFont(FontHandler.getResources());
		setFontSmoothingType(FontSmoothingType.GRAY);

		textProperty().bind(Bindings.createStringBinding(
				() -> {
					String desc = getDescription();
					return desc != null && !desc.isBlank() ? descriptionToString(desc) : "";
				}, description
		));

		fillProperty().bind(colorProperty());
		sizeProperty().addListener((observable, oldValue, newValue) -> setFontSize(newValue.doubleValue()));
	}

	private String descriptionToString(String desc) {
		return String.valueOf(FontHandler.getCode(desc));
	}

	private void setFontSize(double size) {
		String fontFamily = getFont().getFamily();
		setFont(Font.font(fontFamily, size));
	}

	/**
	 * @return a new MFXFontIcon with a random icon, the specified size and color.
	 */
	public static MFXFontIcon getRandomIcon(double size, Color color) {
		FontResources[] resources = FontResources.values();
		int random = (int) (Math.random() * resources.length);
		String desc = resources[random].getDescription();
		return new MFXFontIcon(desc, size, color);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableObjectProperty<Color> color = new SimpleStyleableObjectProperty<>(
			StyleableProperties.COLOR,
			this,
			"color",
			Color.web("#454545")
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableStringProperty description = new SimpleStyleableStringProperty(
			StyleableProperties.DESCRIPTION,
			this,
			"description"
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
			StyleableProperties.SIZE,
			this,
			"size",
			10.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	public Color getColor() {
		return color.get();
	}

	/**
	 * Specifies the color of the icon.
	 */
	public StyleableObjectProperty<Color> colorProperty() {
		return color;
	}

	public void setColor(Color color) {
		this.color.set(color);
	}

	public String getDescription() {
		return description.get();
	}

	/**
	 * Specifies the icon's code.
	 */
	public StyleableStringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String code) {
		this.description.set(code);
	}

	public double getSize() {
		return size.get();
	}

	/**
	 * Specifies the size of the icon.
	 */
	public StyleableDoubleProperty sizeProperty() {
		return size;
	}

	public void setSize(double size) {
		this.size.set(size);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	public static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXFontIcon, Color> COLOR =
				FACTORY.createColorCssMetaData(
						"-mfx-color",
						MFXFontIcon::colorProperty,
						Color.web("#454545")
				);

		private static final CssMetaData<MFXFontIcon, String> DESCRIPTION =
				FACTORY.createStringCssMetaData(
						"-mfx-description",
						MFXFontIcon::descriptionProperty
				);

		private static final CssMetaData<MFXFontIcon, Number> SIZE =
				FACTORY.createSizeCssMetaData(
						"-mfx-size",
						MFXFontIcon::sizeProperty,
						10
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Text.getClassCssMetaData(),
					COLOR, DESCRIPTION, SIZE
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return MFXFontIcon.getClassCssMetaDataList();
	}
}
