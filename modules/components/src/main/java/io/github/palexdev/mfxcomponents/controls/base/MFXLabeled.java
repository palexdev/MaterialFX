/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls.base;

import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Labeled;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

import java.util.List;

/**
 * Extension of {@link Labeled} and base class for text-based MaterialFX controls. The idea is to have a separate
 * hierarchy of components from the JavaFX one that perfectly integrates with the new Behavior, Skin and Theming APIs.
 * In addition to the integrations brought by {@link Labeled}, this also implements {@link MFXStyleable} and {@link MFXResizable}.
 * <p>
 * <b>Note</b>: the correct way to change the skin is to call {@link #changeSkin(SkinBase)}.
 * <p></p>
 * MaterialFX components, descendants of this, support the usage of custom tooltips through the property {@link #mfxTooltipProperty()}.
 *
 * @see MFXSkinBase
 * @see MFXResizable
 * @see MFXTooltip
 */
public abstract class MFXLabeled<B extends BehaviorBase<? extends Node>> extends Labeled<B> implements MFXStyleable, MFXResizable {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<MFXTooltip> mfxTooltip = new SimpleObjectProperty<>() {
		@Override
		public void set(MFXTooltip newValue) {
			MFXTooltip oldValue = get();
			if (oldValue != null) oldValue.dispose();
			if (oldValue == newValue) return;
			newValue.install();
			super.set(newValue);
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLabeled() {}

	public MFXLabeled(String text) {
		super(text);
	}

	public MFXLabeled(String text, Node graphic) {
		super(text, graphic);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Create a new instance of the default skin for this component.
	 */
	protected abstract MFXSkinBase<?, ?> buildSkin();

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public double computeMinWidth(double height) {
		return super.computeMinWidth(height);
	}

	@Override
	public double computeMinHeight(double width) {
		return super.computeMinHeight(width);
	}

	@Override
	public double computePrefWidth(double height) {
		return super.computePrefWidth(height);
	}

	@Override
	public double computePrefHeight(double width) {
		return super.computePrefHeight(width);
	}

	@Override
	public double computeMaxWidth(double height) {
		return super.computeMaxWidth(height);
	}

	@Override
	public double computeMaxHeight(double width) {
		return super.computeMaxHeight(width);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty textOpacity = new StyleableDoubleProperty(
		StyleableProperties.TEXT_OPACITY,
		this,
		"textOpacity",
		1.0
	);

	public double getTextOpacity() {
		return textOpacity.get();
	}

	/**
	 * It is now possible through this property to specify the opacity of the text node of components
	 * extending this.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-text-opacity'.
	 */
	public StyleableDoubleProperty textOpacityProperty() {
		return textOpacity;
	}

	public void setTextOpacity(double textOpacity) {
		this.textOpacity.set(textOpacity);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXLabeled<?>> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXLabeled<?>, Number> TEXT_OPACITY =
			FACTORY.createSizeCssMetaData(
				"-mfx-text-opacity",
				MFXLabeled::textOpacityProperty,
				1.0
			);

		static {
			cssMetaDataList = StyleUtils.cssMetaDataList(
				Labeled.getClassCssMetaData(),
				TEXT_OPACITY
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

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXTooltip getMFXTooltip() {
		return mfxTooltip.get();
	}

	/**
	 * Specifies the {@link MFXTooltip} to use on this labeled.
	 */
	public ObjectProperty<MFXTooltip> mfxTooltipProperty() {
		return mfxTooltip;
	}

	public void setMFXTooltip(MFXTooltip mfxTooltip) {
		this.mfxTooltip.set(mfxTooltip);
	}
}
