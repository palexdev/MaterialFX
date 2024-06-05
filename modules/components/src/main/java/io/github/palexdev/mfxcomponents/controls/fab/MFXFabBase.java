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

package io.github.palexdev.mfxcomponents.controls.fab;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXFabSkin;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.IconProvider;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of {@link MFXButtonBase} and base class to implement the Floating Action Buttons shown in the MD3 guidelines.
 * This is meant to be used by users that want an untouched base FAB, this component just like {@link MFXButtonBase} is
 * not styled by the themes by default. This base class has one implementation that is styled which is: {@link MFXFab}.
 * <p></p>
 * M3 guidelines also show the Extended variant. Since they also show that a standard FAB can transition to an Extended
 * one through an animation, and vice-versa, I decided to merge the Extended variant into the standard one, and add specific
 * properties for the animations.
 * <p>
 * It's selector in CSS is: '.mfx-button.fab-base'.
 * <p></p>
 * Since FABs are meant to be used with icons, these enforce the usage of {@link MFXFontIcon}s rather than generic nodes.
 * Also, since they are simply buttons with a different appearance and purpose, the behavior used by this is just
 * {@link MFXButtonBehaviorBase}.
 * <p></p>
 * As shown by the Material Design 3 guidelines, FABs can communicate changes (text/icon changes) through animations.
 * These are handled by the default skin {@link MFXFabSkin} and can be enabled/disabled via the {@link #animatedProperty()}.
 * A little suggestion, when changing the attributes of an extended FAB, you should first change the text and then the icon.
 * This is because the default skin will play the animation as soon as the text changes, so you have a smoother animation
 * if you change the icon afterward (this applies only to extended FABs!).
 *
 * @see MFXFabSkin
 */
public class MFXFabBase extends MFXButtonBase<MFXButtonBehaviorBase<MFXFabBase>> {
	//================================================================================
	// Properties
	//================================================================================
	private final IconProperty icon = new IconProperty();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabBase() {
		this("");
	}

	public MFXFabBase(String text) {
		this(text, null);
	}

	public MFXFabBase(MFXFontIcon icon) {
		this("", icon);
	}

	public MFXFabBase(String text, MFXFontIcon icon) {
		super(text);
		setIcon(icon);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		graphicProperty().bind(icon);
		setPickOnBounds(false);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Supplier<MFXButtonBehaviorBase<MFXFabBase>> defaultBehaviorProvider() {
		return () -> new MFXButtonBehaviorBase<>(this);
	}

	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button", "fab-base");
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return getClassCssMetaData();
	}

	@Override
	protected MFXSkinBase<?, ?> buildSkin() {
		return new MFXFabSkin(this);
	}

	@Override
	protected void sceneBuilderIntegration() {
		super.sceneBuilderIntegration();
		SceneBuilderIntegration.ifInSceneBuilder(() -> setText("Floating Action Button"));
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
		StyleableProperties.ANIMATED,
		this,
		"animated",
		true
	);

	private final StyleableBooleanProperty extended = new StyleableBooleanProperty(
		StyleableProperties.EXTENDED,
		this,
		"extended",
		false
	) {
		@Override
		protected void invalidated() {
			PseudoClasses.EXTENDED.setOn(MFXFabBase.this, get());
		}
	};

	private final StyleableObjectProperty<Pos> scalePivot = new StyleableObjectProperty<>(
		StyleableProperties.SCALE_PIVOT,
		this,
		"scalePivot",
		Pos.BOTTOM_RIGHT
	);

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies whether to animate the component when its attributes (text/icon) change.
	 * The animations are implemented and managed by the default skin {@link MFXFabSkin}.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-animated'.
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated.set(animated);
	}

	public boolean isExtended() {
		return extended.get();
	}

	/**
	 * Specifies whether the FAB also shows its text or not.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-extended'.
	 *
	 * @see #animatedProperty()
	 */
	public StyleableBooleanProperty extendedProperty() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended.set(extended);
	}

	public Pos getScalePivot() {
		return scalePivot.get();
	}

	/**
	 * Specifies the pivot/anchor used by the {@link Scale} transform used for the collapsed FAB when its icon changes.
	 * <p></p>
	 * Can be set in CSS via the property: '-mfx-scale-pivot'.
	 */
	public StyleableObjectProperty<Pos> scalePivotProperty() {
		return scalePivot;
	}

	public void setScalePivot(Pos scalePivot) {
		this.scalePivot.set(scalePivot);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXFabBase> FACTORY = new StyleablePropertyFactory<>(MFXButtonBase.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXFabBase, Boolean> ANIMATED =
			FACTORY.createBooleanCssMetaData(
				"-mfx-animated",
				MFXFabBase::animatedProperty,
				true
			);

		private static final CssMetaData<MFXFabBase, Boolean> EXTENDED =
			FACTORY.createBooleanCssMetaData(
				"-mfx-extended",
				MFXFabBase::extendedProperty,
				false
			);

		private static final CssMetaData<MFXFabBase, Pos> SCALE_PIVOT =
			FACTORY.createEnumCssMetaData(
				Pos.class,
				"-mfx-scale-pivot",
				MFXFabBase::scalePivotProperty,
				Pos.BOTTOM_RIGHT
			);

		static {
			cssMetaDataList = StyleUtils.cssMetaDataList(
				MFXButtonBase.getClassCssMetaData(),
				ANIMATED, EXTENDED, SCALE_PIVOT
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXFontIcon getIcon() {
		return iconProperty().get();
	}

	/**
	 * Specifies the FAB's icon.
	 */
	public IconProperty iconProperty() {
		return icon;
	}

	public void setIcon(MFXFontIcon icon) {
		iconProperty().set(icon);
	}

	/**
	 * Delegate of {@link IconProperty#setDescription(String)}.
	 */
	public IconProperty setIconDescription(String description) {
		return icon.setDescription(description);
	}

	/**
	 * Delegate of {@link IconProperty#setProvider(IconProvider)}.
	 *
	 * @see MFXFontIcon#setIconsProvider(IconProvider)
	 */
	public IconProperty setIconProvider(IconProvider provider) {
		return icon.setProvider(provider);
	}

	/**
	 * Delegate of {@link IconProperty#setProvider(Font, Function)}.
	 *
	 * @see MFXFontIcon#setIconsProvider(Font, Function)
	 */
	public IconProperty setIconProvider(Font font, Function<String, Character> converter) {
		return icon.setProvider(font, converter);
	}

	/**
	 * Delegate of {@link IconProperty#setProvider(IconProvider, String)}.
	 */
	public IconProperty setIconProvider(IconProvider provider, String description) {
		return icon.setProvider(provider, description);
	}
}
