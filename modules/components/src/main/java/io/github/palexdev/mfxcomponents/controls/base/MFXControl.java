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

import io.github.palexdev.mfxcomponents.layout.LayoutStrategy;
import io.github.palexdev.mfxcomponents.layout.MFXResizable;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Control;
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
 * Extension of {@link Control} and base class for MaterialFX components. The idea is to have a separate hierarchy of
 * components from the JavaFX one that perfectly integrates with the new Behavior and Theming APIs. In addition to the
 * integrations brought by {@link Control}, this also implements {@link MFXStyleable} and {@link MFXResizable}.
 * <p></p>
 * Note: as already mentioned the correct way to change the skin is not to call {@link #changeSkin(SkinBase)}. The method
 * accepts instances of type {@link SkinBase}, however, keep in mind that in order for the {@link LayoutStrategy} to work,
 * it's needed a skin of type {@link MFXSkinBase}.
 * <p></p>
 * Design guidelines (like MD3), may specify in the components' specs the initial/minimum sizes for each component.
 * For this specific purpose, there are two properties that can be set in CSS: {@link #initHeightProperty()}
 * and {@link #initWidthProperty()}.
 * <p></p>
 * Last but not least, MaterialFX components, descendants of this, support the usage of custom tooltips through the property
 * {@link #mfxTooltipProperty()}.
 *
 * @see MFXSkinBase
 * @see MFXResizable
 * @see MFXTooltip
 */
public abstract class MFXControl<B extends BehaviorBase<? extends Node>> extends Control<B> implements MFXStyleable, MFXResizable {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<LayoutStrategy> layoutStrategy = new SimpleObjectProperty<>(defaultLayoutStrategy()) {
		@Override
		protected void invalidated() {
			onLayoutStrategyChanged();
		}
	};
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
	// Abstract Methods
	//================================================================================

	/**
	 * Create a new instance of the default skin for this component.
	 */
	protected abstract MFXSkinBase<?, ?> buildSkin();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This is automatically invoked when either {@link #initHeightProperty()} or {@link #initWidthProperty()} change.
	 * By default, this method triggers a layout request.
	 * <p></p>
	 * The consequence is that the current set {@link LayoutStrategy} will be used to re-compute the component's sizes, and
	 * if it takes into account those init sizes, the component will resize accordingly.
	 */
	protected void onInitSizesChanged() {
		requestLayout();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void onLayoutStrategyChanged() {
		requestLayout();
	}

	@Override
	public LayoutStrategy defaultLayoutStrategy() {
		return LayoutStrategy.defaultStrategy()
			.setPrefWidthFunction(LayoutStrategy.Defaults.DEF_PREF_WIDTH_FUNCTION.andThen(r -> Math.max(r, getInitWidth())))
			.setPrefHeightFunction(LayoutStrategy.Defaults.DEF_PREF_HEIGHT_FUNCTION.andThen(r -> Math.max(r, getInitHeight())));
	}

	@Override
	public double computeMinWidth(double height) {
		return getLayoutStrategy().computeMinWidth(this);
	}

	@Override
	public double computeMinHeight(double width) {
		return getLayoutStrategy().computeMinHeight(this);
	}

	@Override
	public double computePrefWidth(double height) {
		return getLayoutStrategy().computePrefWidth(this);
	}

	@Override
	public double computePrefHeight(double width) {
		return getLayoutStrategy().computePrefHeight(this);
	}

	@Override
	public double computeMaxWidth(double height) {
		return getLayoutStrategy().computeMaxWidth(this);
	}

	@Override
	public double computeMaxHeight(double width) {
		return getLayoutStrategy().computeMaxHeight(this);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty initHeight = new StyleableDoubleProperty(
		StyleableProperties.INIT_HEIGHT,
		this,
		"initHeight",
		USE_COMPUTED_SIZE
	) {
		@Override
		public void invalidated() {
			onInitSizesChanged();
		}
	};

	private final StyleableDoubleProperty initWidth = new StyleableDoubleProperty(
		StyleableProperties.INIT_WIDTH,
		this,
		"initWidth",
		USE_COMPUTED_SIZE
	) {
		@Override
		public void invalidated() {
			onInitSizesChanged();
		}
	};

	public final double getInitHeight() {
		return initHeight.get();
	}

	/**
	 * Specifies the component's initial height upon creation.
	 * <p></p>
	 * This can be useful when using components that define certain sizes by specs, in
	 * SceneBuilder and other similar cases. One could also use the '-fx-pref-height' CSS
	 * property JavaFX offers, but the issue is that once it is set by CSS it won't be possible to
	 * overwrite the value in some cases. To overcome this, the size can be set via code, this property
	 * just offers a way to specify the height in CSS and still apply it via code.
	 * <p>
	 * The way initial sizes are applied depends on the set {@link LayoutStrategy}, when this changes the layout request
	 * is automatically triggered by {@link #onInitSizesChanged()}.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-init-height'.
	 */
	protected final StyleableDoubleProperty initHeightProperty() {
		return initHeight;
	}

	protected final void setInitHeight(double initHeight) {
		this.initHeight.set(initHeight);
	}

	public final double getInitWidth() {
		return initWidth.get();
	}

	/**
	 * Specifies the component's initial width when created.
	 * <p></p>
	 * This can be useful when using components that define certain sizes by specs, in
	 * SceneBuilder and other similar cases. One could also use the '-fx-pref-width' CSS
	 * property JavaFX offers, but the issue is that once it is set by CSS it won't be possible to
	 * overwrite the value in some cases. To overcome this, the size can be set via code, this property
	 * just offers a way to specify the width in CSS and still apply it via code.
	 * <p>
	 * The way initial sizes are applied depends on the set {@link LayoutStrategy}, when this changes the layout request
	 * is automatically triggered by {@link #onInitSizesChanged()}.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-init-width'.
	 */
	protected final StyleableDoubleProperty initWidthProperty() {
		return initWidth;
	}

	protected final void setInitWidth(double initWidth) {
		this.initWidth.set(initWidth);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXControl<?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		protected static final CssMetaData<MFXControl<?>, Number> INIT_HEIGHT =
			FACTORY.createSizeCssMetaData(
				"-mfx-init-height",
				MFXControl::initHeightProperty,
				USE_COMPUTED_SIZE
			);

		private static final CssMetaData<MFXControl<?>, Number> INIT_WIDTH =
			FACTORY.createSizeCssMetaData(
				"-mfx-init-width",
				MFXControl::initWidthProperty,
				USE_COMPUTED_SIZE
			);

		static {
			cssMetaDataList = StyleUtils.cssMetaDataList(
				Control.getClassCssMetaData(),
				INIT_HEIGHT, INIT_WIDTH
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
	@Override
	public LayoutStrategy getLayoutStrategy() {
		return layoutStrategy.get();
	}

	@Override
	public ObjectProperty<LayoutStrategy> layoutStrategyProperty() {
		return layoutStrategy;
	}

	@Override
	public void setLayoutStrategy(LayoutStrategy layoutStrategy) {
		this.layoutStrategy.set(layoutStrategy);
	}

	public MFXTooltip getMFXTooltip() {
		return mfxTooltip.get();
	}

	/**
	 * Specifies the {@link MFXTooltip} to use on this control.
	 */
	public ObjectProperty<MFXTooltip> mfxTooltipProperty() {
		return mfxTooltip;
	}

	public void setMFXTooltip(MFXTooltip mfxTooltip) {
		this.mfxTooltip.set(mfxTooltip);
	}
}
