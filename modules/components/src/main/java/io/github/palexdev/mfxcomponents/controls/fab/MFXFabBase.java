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
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Pos;
import javafx.scene.transform.Scale;

import java.util.List;
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
 * <b>Super important note!</b>
 * Material Design guidelines show that animations are played when the FAB changes its icon (not extended), or both
 * the icon and the text (when extended). This brings a nasty issue. Let's consider a simple messaging app with a bottom
 * navigation bar. The view is wrapped in a scroll pane of course. The pane then has an extended FAB at the bottom right,
 * just above the navigation bar, that allows the user to perform some important/main action.
 * <p>
 * As also seen in the M3 guidelines example, when you switch from one view to another, the FAB also changes.
 * For example at the 'Chat' view you may have a FAB with a 'message' icon and 'Write' as text.
 * When switching to another view you may want to change them to something related to the new view.
 * Both the changes need to be displayed in a single animation of course, and this is not easy to accomplish in JavaFX.
 * <p>
 * Since here, the controls are modular. The animation is triggered by a listener in the skin.
 * And since we are changing two properties we would need to add two listeners, but this would trigger the animation
 * two times in a row, unless we use some flag to check whether both were changed.
 * Still, that would not be easy to manage, what happens if we are changing only one of them for example.
 * In other words, if we want a solution for the above example we need the two changes to be "atomic". Both at the same time.
 * Of course, it's not possible. So, the solution I came up with it's a pretty basic and recurrent trick: the wrapping technique.
 * Now, both the FAB's icon and text are wrapped in a single property, {@link #attributesProperty()}.
 * This solves the above issue, but comes with some caveats:
 * <p> 1) The FAB's text is now bound to this new property
 * <p> 2) For the above reason you cannot set the text using the traditional way, {@link #setText(String)} or through
 * {@link #textProperty()}
 * <p> 3) Unbinding it would cause malfunctions of course, so you must use {@link #setFabText(String)} instead.
 * For convenience the wrapping class is immutable, which means you won't be able to change the text or icon without
 * actually creating a new instance of it.
 * <p></p>
 * Also, note that the animation will play even if just only one of the two attributes changed. While this is not
 * explicitly shown on the M3 Guidelines I believe it's a valid behavior.
 *
 * @see MFXFabSkin
 */
public class MFXFabBase extends MFXButtonBase<MFXButtonBehaviorBase<MFXFabBase>> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<PropsWrapper> attributes = new SimpleObjectProperty<>(PropsWrapper.DEFAULT) {
		@Override
		public void set(PropsWrapper newValue) {
			if (newValue == null) newValue = PropsWrapper.DEFAULT;
			super.set(newValue);
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabBase() {
		initialize();
	}

	public MFXFabBase(String text) {
		initialize();
		setFabText(text);
	}

	public MFXFabBase(MFXFontIcon icon) {
		initialize();
		setIcon(icon);
	}

	public MFXFabBase(String text, MFXFontIcon icon) {
		initialize();
		setAttributes(text, icon);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		textProperty().bind(attributes.map(p -> p != null ? p.getText() : null));
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void onInitSizesChanged() {
		// Reset the prefWidth if not extended and init sizes changed
		if (!isExtended()) setPrefWidth(USE_COMPUTED_SIZE);
	}

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
	 * Specifies whether to play or not an animation when:ù
	 * <p> - The FAB transitions from collapsed to extended and vice-versa
	 * <p> - The icon changes (collapsed)
	 * <p> - Both the text and icon change (extended)
	 * <p>
	 * The animations are managed by the default skin implementation, see {@link MFXFabSkin}.
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
	public void setAttributes(PropsWrapper attributes) {
		this.attributes.set(attributes);
	}

	public void setAttributes(String text, MFXFontIcon icon) {
		this.attributes.set(new PropsWrapper(text, icon));
	}

	public void setAttributes(String text, Supplier<MFXFontIcon> iconSupplier) {
		this.attributes.set(new PropsWrapper(text, iconSupplier.get()));
	}

	/**
	 * Specifies the wrapper object containing both the current icon and text of the FAB.
	 * <p></p>
	 * For convenience, and to avoid too many checks, you won't be able to set {@code null} as a value, it will
	 * always be corrected to a default value with empty text and {@code null} icon.
	 */
	public ObjectProperty<PropsWrapper> attributesProperty() {
		return attributes;
	}

	public PropsWrapper getAttributes() {
		return attributes.get();
	}

	/**
	 * Use this method to set the FAB's text since the default property {@link #textProperty()} is bound to
	 * {@link #attributesProperty()}.
	 */
	public void setFabText(String text) {
		PropsWrapper attributes = getAttributes();
		this.attributes.set(new PropsWrapper(text, attributes.getIcon()));
	}

	public String getFabText() {
		return getAttributes().getText();
	}

	public void setIcon(MFXFontIcon icon) {
		PropsWrapper attributes = getAttributes();
		this.attributes.set(new PropsWrapper(attributes.getText(), icon));
	}

	public MFXFontIcon getIcon() {
		return getAttributes().getIcon();
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	public static class PropsWrapper {
		public static final PropsWrapper DEFAULT = new PropsWrapper("", null);
		private final String text;
		private final MFXFontIcon icon;

		public PropsWrapper(String text, MFXFontIcon icon) {
			this.text = text;
			this.icon = icon;
		}

		public String getText() {
			return text;
		}

		public MFXFontIcon getIcon() {
			return icon;
		}
	}
}
