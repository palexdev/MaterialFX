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

import io.github.palexdev.mfxcomponents.behaviors.MFXFabBehavior;
import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.List;
import java.util.function.Supplier;

/**
 * Base class for MaterialFX controls. The idea is to have a separate hierarchy of components from the JavaFX one,
 * that perfectly integrates with the new Behavior and Theming APIs.
 * <p>
 * Extends {@link Control} and implements both {@link WithBehavior} and {@link MFXStyleable}.
 * Enforces the use of {@link MFXSkinBase} instances as Skin implementations and makes the {@link #createDefaultSkin()}
 * final thus denying users to override it. To set custom skins you should override the new provided method {@link #buildSkin()}.
 * <p>
 * I wanted to avoid adding a listener of the skin property for memory and performance reasons. Every time a skin is created
 * it's needed to pass the current built behavior to the skin for initialization. A good hook place for this call was the
 * {@link #createDefaultSkin()} method, but this would make it harder for users to override it because then you would also
 * have to take into account the behavior initialization. Having a new method maintains the usual simplicity of setting
 * custom skins while avoiding listeners for better performance.
 * <p></p>
 * The integration with the new Behavior API is achieved by having a specific property, {@link #behaviorProviderProperty()},
 * which allows to change at any time the component's behavior. The property automatically handles initialization and disposal
 * of behaviors. A reference to th current built behavior object is kept to be retrieved via {@link #getBehavior()}.
 * <p>
 * In MaterialFX, the Behavior API is not a closed API, it's not meant to be private. A user can always take it and invoke
 * its methods directly, extend it, suppress it, do whatever you like. Also, some components' behavior may specify methods
 * that are meant to be called by the user when needed, see {@link MFXFabBehavior} as an example.
 * <p></p>
 * Components that primarily deal with text should extend {@link MFXLabeled} instead.
 * <p></p>
 * Design guidelines (like MD3), may specify in the components' specs the initial/minimum sizes for each component.
 * For this specific purpose, there are two properties that can be set in CSS: {@link #initHeightProperty()}, {@link #initWidthProperty()}.
 *
 * @param <B> the behavior type used by the control
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class MFXControl<B extends BehaviorBase<? extends Node>> extends Control implements WithBehavior<B>, MFXStyleable {
	//================================================================================
	// Properties
	//================================================================================
	private B behavior;
	private final SupplierProperty<B> behaviorProvider = new SupplierProperty<>() {
		@Override
		protected void invalidated() {
			if (behavior != null) {
				behavior.dispose();
			}
			behavior = get().get();
			MFXSkinBase skin = (MFXSkinBase) getSkin();
			if (skin != null && behavior != null) skin.initBehavior(behavior);
		}
	};

	//================================================================================
	// Abstract Methods
	//================================================================================
	protected abstract MFXSkinBase<?, ?> buildSkin();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Applies the sizes specified by {@link #initHeightProperty()} and {@link #initWidthProperty()},
	 * both set in a CSS stylesheet.
	 * <p>
	 * By default, this sets the component's pref height and width, can be overridden to change the behavior.
	 * <p></p>
	 * By default, the values are set only if the pref height and width are greater than 0, because
	 * with 'init' sizes we suppose that the components sizes upon creation are still 0
	 * The 'force' boolean parameter will skip the check and set them anyway.
	 */
	protected void applyInitSizes(boolean force) {
		double ih = getInitHeight();
		double iw = getInitWidth();
		if (force || getPrefHeight() <= 0.0) setPrefHeight(ih);
		if (force || getPrefWidth() <= 0.0) setPrefWidth(iw);
	}

	/**
	 * Subclasses can change the actions to perform if the component is being used in SceneBuilder
	 * by overriding this method. Typically called automatically on components' initialization.
	 */
	protected void sceneBuilderIntegration() {
	}

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

	@Override
	protected final MFXSkinBase<?, ?> createDefaultSkin() {
		MFXSkinBase skin = buildSkin();
		if (behavior != null) skin.initBehavior(behavior);
		return skin;
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
		public void set(double v) {
			super.set(v);
			applyInitSizes(false);
		}
	};

	private final StyleableDoubleProperty initWidth = new StyleableDoubleProperty(
			StyleableProperties.INIT_WIDTH,
			this,
			"initWidth",
			USE_COMPUTED_SIZE
	) {
		@Override
		public void set(double v) {
			super.set(v);
			applyInitSizes(false);
		}
	};

	public final double getInitHeight() {
		return initHeight.get();
	}

	/**
	 * Specifies the component's initial height when created.
	 * <p></p>
	 * This can be useful when using components that define certain sizes by specs, in
	 * SceneBuilder and other similar cases. One could also use the '-fx-pref-height' CSS
	 * property JavaFX offers, but the issue is that once it is set by CSS it won't be possible to
	 * overwrite the value in some cases. To overcome this, the size can be set via code, this property
	 * just offers a way to specify the height in CSS and still apply it via code.
	 * <p>
	 * The way initial sizes are applied is managed by {@link #applyInitSizes(boolean)}.
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
	 * The way initial sizes are applied is managed by {@link #applyInitSizes(boolean)}.
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
	public B getBehavior() {
		return behavior;
	}

	@Override
	public Supplier<B> getBehaviorProvider() {
		return behaviorProvider.get();
	}

	@Override
	public SupplierProperty<B> behaviorProviderProperty() {
		return behaviorProvider;
	}

	@Override
	public void setBehaviorProvider(Supplier<B> behaviorProvider) {
		this.behaviorProvider.set(behaviorProvider);
	}
}
