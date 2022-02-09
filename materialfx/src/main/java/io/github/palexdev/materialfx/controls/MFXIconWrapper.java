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

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.base.IRippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Function;

/**
 * Convenience class for creating icons wrapped in a StackPane.
 * <p>
 * The size is equal and fixed both for height and width, can be changed via CSS.
 */
public class MFXIconWrapper extends StackPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-icon-wrapper";

	private final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
	private final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXIconWrapper() {
		initialize();
	}

	public MFXIconWrapper(Node icon, double size) {
		initialize();

		setIcon(icon);
		setSize(size);
	}

	public MFXIconWrapper(String description, double iconSize, double wrapperSize) {
		initialize();

		setIcon(new MFXFontIcon(description, iconSize));
		setSize(wrapperSize);
	}

	public MFXIconWrapper(String description, double iconSize, Color iconColor, double wrapperSize) {
		initialize();

		setIcon(new MFXFontIcon(description, iconSize, iconColor));
		setSize(wrapperSize);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds a ripple generator to the icon. It is an optional.
	 */
	public MFXIconWrapper addRippleGenerator() {
		if (!getChildren().contains(rippleGenerator)) {
			super.getChildren().add(0, rippleGenerator);
		}

		return this;
	}

	/**
	 * Adds the ripple generator to the icon by calling {@link #addRippleGenerator()}, sets its position function
	 * to use the mouse event x and y coordinates, and adds the event filter to the icon to generate the ripples.
	 *
	 * @see IRippleGenerator
	 * @see MFXCircleRippleGenerator
	 */
	public MFXIconWrapper defaultRippleGeneratorBehavior() {
		addRippleGenerator();
		rippleGenerator.setRipplePositionFunction(event -> PositionBean.of(event.getX(), event.getY()));
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				rippleGenerator.generateRipple(event);
			}
		});
		return this;
	}

	/**
	 * Adds the ripple generator to the icon by calling {@link #addRippleGenerator()}, sets its position function
	 * to the given function, and adds the event filter to the icon to generate the ripples.
	 *
	 * @see IRippleGenerator
	 * @see MFXCircleRippleGenerator
	 */
	public MFXIconWrapper rippleGeneratorBehavior(Function<MouseEvent, PositionBean> positionFunction) {
		addRippleGenerator();
		rippleGenerator.setRipplePositionFunction(positionFunction);
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				rippleGenerator.generateRipple(event);
			}
		});
		return this;
	}

	private void initialize() {
		getStyleClass().add(STYLE_CLASS);

		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> requestFocus());
		icon.addListener((observable, oldValue, newValue) -> {
			super.getChildren().remove(oldValue);
			manageIcon(newValue);
		});
		size.addListener((observable, oldValue, newValue) -> setPrefSize(newValue.doubleValue(), newValue.doubleValue()));
	}

	/**
	 * This method handles the positioning of the icon in the children list.
	 */
	private void manageIcon(Node icon) {
		if (icon == null) {
			return;
		}

		ObservableList<Node> children = super.getChildren();

		if (children.isEmpty()) {
			children.add(icon);
			return;
		}

		if (children.contains(rippleGenerator)) {
			if (children.size() == 1) {
				children.add(icon);
			} else {
				children.set(1, icon);
			}
		}
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * @return an unmodifiable list of the StackPane children
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return FXCollections.unmodifiableObservableList(super.getChildren());
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		Node icon = getIcon();
		if (icon != null && getSize() == -1) {
			double iW = icon.prefWidth(-1);
			double iH = icon.prefHeight(-1);
			Insets padding = getPadding();
			double size = Math.max(padding.getLeft() + iW + padding.getRight(), padding.getTop() + iH + padding.getBottom());
			setSize(size);
		}
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty size = new StyleableDoubleProperty(
			StyleableProperties.SIZE,
			this,
			"size",
			-1.0
	);

	public double getSize() {
		return size.get();
	}

	/**
	 * Specifies the size of the container.
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
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXIconWrapper> FACTORY = new StyleablePropertyFactory<>(StackPane.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXIconWrapper, Number> SIZE =
				FACTORY.createSizeCssMetaData(
						"-mfx-size",
						MFXIconWrapper::sizeProperty,
						-1.0
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					StackPane.getClassCssMetaData(),
					SIZE
			);
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
	 * @return the RippleGenerator instance.
	 */
	public MFXCircleRippleGenerator getRippleGenerator() {
		return rippleGenerator;
	}

	public Node getIcon() {
		return icon.get();
	}

	/**
	 * Contains the reference to the icon.
	 */
	public ObjectProperty<Node> iconProperty() {
		return icon;
	}

	public void setIcon(Node icon) {
		this.icon.set(icon);
	}

	/**
	 * Removes the icon node.
	 */
	public void removeIcon() {
		setIcon(null);
	}
}
