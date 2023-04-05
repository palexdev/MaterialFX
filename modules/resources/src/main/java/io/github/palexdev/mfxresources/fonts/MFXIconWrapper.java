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

package io.github.palexdev.mfxresources.fonts;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This component is intended for wrapping {@link MFXFontIcon}s, extends {@link StackPane} and offers
 * some common features that one may want to use with font icons like:
 * <p> - Generate ripple effects on click
 * <p> - Make the icon round
 * <p></p>
 * The new API makes these two features easier to use for developers. Many times I had to pass the wrapper instance to a
 * controller just to enable them. Now, they both can be activated in CSS and in FXML, you don't even need their instance
 * in the control as the icon can be specified by editing manually the FXML, the only limit being that you can't change
 * the icons provider.
 * <p></p>
 * Note that by default the size of the wrapper is always the same for both width and height, it can be specified via the
 * {@link #sizeProperty()} or you could let this figure it out automatically at layout time. In the latter case, the
 * size is computed as the maximum between the icon's width and height.
 */
// TODO add icon switch with animation
public class MFXIconWrapper extends StackPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-icon-wrapper";

	private final IconProperty icon = new IconProperty();
	private MFXRippleGenerator rg;
	private EventHandler<MouseEvent> rHandler;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXIconWrapper() {
		this(null, -1.0);
	}

	public MFXIconWrapper(MFXFontIcon icon) {
		this(icon, -1.0);
	}

	public MFXIconWrapper(MFXFontIcon icon, double size) {
		initialize();
		setIcon(icon);
		setSize(size);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		addEventHandler(MouseEvent.MOUSE_PRESSED, e -> requestFocus());
		icon.addListener((observable, oldValue, newValue) -> {
			super.getChildren().remove(oldValue);
			manageChildren();
		});
		size.addListener((observable, oldValue, newValue) -> setPrefSize(newValue.doubleValue(), oldValue.doubleValue()));
		rHandler = e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				rg.generate(e);
		};
	}

	/**
	 * Calls {@link #enableRippleGenerator(boolean, Function)}, the function to determine the ripple location
	 * uses the {@link MouseEvent#getX()} and {@link MouseEvent#getY()} coordinates.
	 */
	public MFXIconWrapper enableRippleGenerator(boolean enable) {
		return enableRippleGenerator(enable, e -> Position.of(e.getX(), e.getY()));
	}

	/**
	 * Enables or disables the ripple effect for this wrapper depending on the given boolean flag.
	 * <p></p>
	 * If the flag is false the ripple generator is removed from the container and set to null.
	 * <p></p>
	 * If the flag is true a new ripple generator is created, the given function determines where ripple effects will be
	 * generated. An {@link EventHandler} is also added to generate ripples on {@link MouseEvent#MOUSE_PRESSED} when
	 * the clicked button is the primary.
	 *
	 * @throws IllegalStateException if the boolean flag is true and the ripple generator is already present
	 */
	public MFXIconWrapper enableRippleGenerator(boolean enable, Function<MouseEvent, Position> positionFunction) {
		if (!enable) {
			if (rg != null) {
				removeEventHandler(MouseEvent.MOUSE_PRESSED, rHandler);
				super.getChildren().remove(rg);
				rg = null;
			}
			return this;
		}

		if (rg != null)
			throw new IllegalStateException("Ripple generator has already been enabled for this icon!");

		rg = new MFXRippleGenerator(this);
		rg.setPositionFunction(positionFunction);
		addEventHandler(MouseEvent.MOUSE_PRESSED, rHandler);
		manageChildren();
		setEnableRipple(true);
		return this;
	}

	/**
	 * Makes this container round by applying a {@link Circle} clip on it.
	 * <p></p>
	 * If the given boolean flag is false the clip is removed.
	 * <p>
	 * If the given boolean flag is true and the clip is already set, simply returns.
	 */
	public MFXIconWrapper makeRound(boolean state) {
		if (!state) {
			setClip(null);
			return this;
		}

		Node clip = getClip();
		if (clip != null) return this;

		Circle circle = new Circle();
		circle.radiusProperty().bind(widthProperty().divide(2.0));
		circle.centerXProperty().bind(widthProperty().divide(2.0));
		circle.centerYProperty().bind(heightProperty().divide(2.0));
		setClip(circle);
		setRound(true);
		return this;
	}

	/**
	 * Makes this container round by applying a {@link Circle} clip on it, uses the given radius for the circle.
	 * <p></p>
	 * If the given boolean flag is false the clip is removed.
	 * <p>
	 * If the given boolean flag is true and the clip is already set, simply returns.
	 */
	public MFXIconWrapper makeRound(boolean state, double radius) {
		if (!state) {
			setClip(null);
			return this;
		}

		Node clip = getClip();
		if (clip != null) return this;

		Circle circle = new Circle(radius);
		circle.centerXProperty().bind(widthProperty().divide(2.0));
		circle.centerYProperty().bind(heightProperty().divide(2.0));
		setClip(circle);
		setRound(true);
		return this;
	}

	/**
	 * Responsible for managing the children of this container.
	 * We want the ripple generator to always "appear" in the back of the icon.
	 */
	private void manageChildren() {
		ObservableList<Node> children = super.getChildren();
		children.clear();
		MFXFontIcon icon = getIcon();
		if (rg != null) children.add(rg);
		if (icon != null) children.add(icon);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to return {@link #getChildrenUnmodifiable()}
	 *
	 * @return
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to also compute the {@link #sizeProperty()} when its value is set to "-1", the size
	 * is computed as the maximum between the icon's width and height and takes into account the padding.
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		MFXFontIcon icon = getIcon();
		if (icon != null && getSize() == -1) {
			double iW = icon.prefWidth(-1);
			double iH = icon.prefHeight(-1);
			double size = Math.max(
					snappedLeftInset() + iW + snappedRightInset(),
					snappedTopInset() + iH + snappedBottomInset()
			);
			setSize(size);
		}
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
			StyleableProperties.SIZE,
			this,
			"size",
			-1.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty enableRipple = new SimpleStyleableBooleanProperty(
			StyleableProperties.ENABLE_RIPPLE,
			this,
			"enableRipple",
			false
	) {
		@Override
		protected void invalidated() {
			boolean state = get();
			enableRippleGenerator(state);
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty round = new SimpleStyleableBooleanProperty(
			StyleableProperties.ROUND,
			this,
			"round",
			false
	) {
		@Override
		protected void invalidated() {
			boolean state = get();
			makeRound(state);
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	public double getSize() {
		return size.get();
	}

	/**
	 * Specifies the size of this container, when set to -1 it will figure out the value automatically at layout time.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-size'.
	 */
	public StyleableDoubleProperty sizeProperty() {
		return size;
	}

	public void setSize(double size) {
		this.size.set(size);
	}

	public boolean isEnableRipple() {
		return enableRipple.get();
	}

	/**
	 * A useful property to enable ripple effect from CSS, the property will automatically call
	 * {@link #enableRippleGenerator(boolean)}.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-enable-ripple'.
	 */
	public StyleableBooleanProperty enableRippleProperty() {
		return enableRipple;
	}

	public void setEnableRipple(boolean enableRipple) {
		this.enableRipple.set(enableRipple);
	}

	public boolean isRound() {
		return round.get();
	}

	/**
	 * A useful property to make this container round from CSS, the property will automatically call
	 * {@link #makeRound(boolean)}.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-round'.
	 */
	public StyleableBooleanProperty roundProperty() {
		return round;
	}

	public void setRound(boolean round) {
		this.round.set(round);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXIconWrapper> FACTORY = new StyleablePropertyFactory<>(StackPane.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXIconWrapper, Number> SIZE =
				FACTORY.createSizeCssMetaData(
						"-mfx-size",
						MFXIconWrapper::sizeProperty,
						-1.0
				);

		private static final CssMetaData<MFXIconWrapper, Boolean> ENABLE_RIPPLE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-enable-ripple",
						MFXIconWrapper::enableRippleProperty,
						false
				);

		private static final CssMetaData<MFXIconWrapper, Boolean> ROUND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-round",
						MFXIconWrapper::roundProperty,
						false
				);

		static {
			List<CssMetaData<? extends Styleable, ?>> data = new ArrayList<>(StackPane.getClassCssMetaData());
			Collections.addAll(data, SIZE, ENABLE_RIPPLE, ROUND);
			cssMetaDataList = List.copyOf(data);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return MFXIconWrapper.getClassCssMetaData();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the {@link MFXRippleGenerator} instance for this wrapper, note that
	 * if the generator is not enabled this will return null
	 */
	public MFXRippleGenerator getRippleGenerator() {
		return rg;
	}

	public MFXFontIcon getIcon() {
		return icon.get();
	}

	/**
	 * Specifies the currently contained {@link MFXFontIcon}.
	 */
	public IconProperty iconProperty() {
		return icon;
	}

	public void setIcon(MFXFontIcon icon) {
		this.icon.set(icon);
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given an icon
	 * description/name.
	 * <p>
	 * Keep in mind that the default icons provider for new {@link MFXFontIcon} is {@link IconsProviders#defaultProvider()}
	 */
	public void setIcon(String desc) {
		setIcon(new MFXFontIcon(desc));
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given an {@link IconProvider} and
	 * an icon description/name.
	 */
	public void setIcon(IconProvider provider, String desc) {
		setIcon(new MFXFontIcon().setIconsProvider(provider).setDescription(desc));
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given a font icon pack,
	 * the function to convert descriptions/names to unicode characters, and the icon description/name.
	 *
	 * @see MFXFontIcon#setIconsProvider(Font, Function)
	 */
	public void setIcon(Font font, Function<String, Character> converter, String desc) {
		setIcon(new MFXFontIcon().setIconsProvider(font, converter).setDescription(desc));
	}
}
