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

package io.github.palexdev.materialfx.controls.buttons;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;

import java.util.List;

/**
 * Extension and variant of {@link MFXButton}, redefines the default style class to: '.mfx-button.elevated'
 * and implements the mechanism needed to 'elevate' the button, see {@link #elevationProperty()}.
 *
 * @see <a href="https://www.w3schools.com/cssref/css_selectors.php">CSS Selectors</a>
 */
public class MFXElevatedButton extends MFXButton {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXElevatedButton() {
		this("");
	}

	public MFXElevatedButton(String text) {
		this(text, null);
	}

	public MFXElevatedButton(String text, Node icon) {
		super(text, icon);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		setPickOnBounds(false);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button", "elevated");
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableObjectProperty<ElevationLevel> elevation = new StyleableObjectProperty<>(
			StyleableProperties.ELEVATION,
			this,
			"elevation"
	) {
		@Override
		public void set(ElevationLevel newValue) {
			if (newValue == ElevationLevel.LEVEL0) {
				setEffect(null);
				super.set(newValue);
				return;
			}

			Effect effect = getEffect();
			if (effect == null) {
				setEffect(newValue.toShadow());
				super.set(newValue);
				return;
			}
			if (!(effect instanceof DropShadow)) {
				return;
			}

			ElevationLevel oldValue = get();
			if (oldValue != newValue)
				oldValue.animateTo((DropShadow) effect, newValue);
			super.set(newValue);
		}
	};

	public ElevationLevel getElevation() {
		return elevation.get();
	}

	/**
	 * Specifies the emphasis of the button' shadow. In other words, this property will apply a {@link DropShadow}
	 * to the button when the specified level is greater than 0.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-elevation'
	 */
	public StyleableObjectProperty<ElevationLevel> elevationProperty() {
		return elevation;
	}

	public void setElevation(ElevationLevel elevation) {
		this.elevation.set(elevation);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<io.github.palexdev.materialfx.controls.buttons.MFXElevatedButton> FACTORY = new StyleablePropertyFactory<>(MFXButton.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<io.github.palexdev.materialfx.controls.buttons.MFXElevatedButton, ElevationLevel> ELEVATION =
				FACTORY.createEnumCssMetaData(
						ElevationLevel.class,
						"-mfx-elevation",
						io.github.palexdev.materialfx.controls.buttons.MFXElevatedButton::elevationProperty
				);

		static {
			cssMetaDataList = StyleUtils.cssMetaDataList(
					MFXButton.getClassCssMetaData(),
					ELEVATION
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return getClassCssMetaData();
	}
}
