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
import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.controls.base.MFXLabeled;
import io.github.palexdev.materialfx.skins.MFXToggleButtonSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * This is the implementation of a toggle button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@link Labeled} and implements {@link Toggle} and {@link MFXLabeled}, its CSS selector is "-mfx-toggle-button",
 * includes a {@code RippleGenerator}(in the Skin) to generate ripple effect when selected/unselected.
 * <p></p>
 * It also introduces some new features like:
 * <p> - {@link #contentDispositionProperty()}: to control the toggle position
 * <p> - {@link #gapProperty()}: to control the gap between the toggle and the text
 * <p> - {@link #lengthProperty()}: to control the toggle's line width
 * <p> - {@link #radiusProperty()}: to control the toggle's circle radius
 * <p> - {@link #textExpandProperty()}: to control the text size and the checkbox layout (see documentation)
 */
public class MFXToggleButton extends Labeled implements Toggle, MFXLabeled {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXToggleButton> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-toggle-button";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXToggleButton.css");

	private final ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty<>();
	private final BooleanProperty selected = new SimpleBooleanProperty(false);
	protected static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	private final EventHandlerProperty<ActionEvent> onAction = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, get());
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public MFXToggleButton() {
		this("");
	}

	public MFXToggleButton(String text) {
		this(text, null);
	}

	public MFXToggleButton(String text, Node graphic) {
		super(text, graphic);
		initialize();
	}

	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setBehavior();
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void setBehavior() {
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> fire());

		toggleGroup.addListener((observable, oldTg, newTg) -> {
			if (newTg != null && newTg.getToggles().contains(this)) {
				if (oldTg != null) oldTg.getToggles().remove(this);
				newTg.getToggles().add(this);
			} else if (newTg == null) {
				oldTg.getToggles().remove(this);
			}
		});

		selected.addListener(invalidated -> {
			pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected());
			ToggleGroup tg = getToggleGroup();
			if (tg != null) {
				if (isSelected()) {
					tg.selectToggle(this);
				} else if (tg.getSelectedToggle() == this) {
					ToggleButtonsUtil.clearSelectedToggle(tg);
				}
			}
		});
	}

	/**
	 * Changes the state of the toggle button if not disabled.
	 */
	public void fire() {
		if (!isDisabled()) {
			setSelected(!isSelected());
			fireEvent(new ActionEvent());
		}
	}

	@Override
	public ToggleGroup getToggleGroup() {
		return toggleGroup.get();
	}

	@Override
	public ObjectProperty<ToggleGroup> toggleGroupProperty() {
		return toggleGroup;
	}

	@Override
	public void setToggleGroup(ToggleGroup toggleGroup) {
		this.toggleGroup.set(toggleGroup);
	}

	@Override
	public boolean isSelected() {
		return selected.get();
	}

	@Override
	public BooleanProperty selectedProperty() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public EventHandler<ActionEvent> getOnAction() {
		return onAction.get();
	}

	/**
	 * Specifies the action to perform when the toggle button is selected/unselected.
	 */
	public EventHandlerProperty<ActionEvent> onActionProperty() {
		return onAction;
	}

	public void setOnAction(EventHandler<ActionEvent> onAction) {
		this.onAction.set(onAction);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableObjectProperty<ContentDisplay> contentDisposition = new SimpleStyleableObjectProperty<>(
			StyleableProperties.CONTENT_DISPOSITION,
			this,
			"contentDisposition",
			ContentDisplay.LEFT
	);

	private final StyleableDoubleProperty gap = new SimpleStyleableDoubleProperty(
			StyleableProperties.GAP,
			this,
			"gap",
			8.0
	);

	private final StyleableDoubleProperty length = new SimpleStyleableDoubleProperty(
			StyleableProperties.LENGTH,
			this,
			"length",
			36.0
	);

	private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
			StyleableProperties.RADIUS,
			this,
			"radius",
			10.0
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

	public double getLength() {
		return length.get();
	}

	/**
	 * Specifies the length of the toggle button's line.
	 */
	public StyleableDoubleProperty lengthProperty() {
		return length;
	}

	public void setLength(double length) {
		this.length.set(length);
	}

	public double getRadius() {
		return radius.get();
	}

	/**
	 * Specifies the radius of the toggle button's circle.
	 */
	public StyleableDoubleProperty radiusProperty() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius.set(radius);
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

	/**
	 * Sets the colors of the toggle button when selected.
	 * <p>
	 * The color is set inline by using {@link Node#setStyle(String)}, the
	 * set CSS value is the "-mfx-main" property.
	 */
	public void setMainColor(Color color) {
		setStyle("-mfx-main: " + ColorUtils.toCss(color) + ";\n");
	}

	/**
	 * Sets the colors of the toggle button when not selected.
	 * <p>
	 * The color is set inline by using {@link Node#setStyle(String)}, the
	 * set CSS value is the "-mfx-secondary" property.
	 */
	public void setSecondaryColor(Color color) {
		setStyle("-mfx-secondary: " + ColorUtils.toCss(color) + ";\n");
	}

	/**
	 * Combines {@link #setMainColor(Color)} and {@link #setSecondaryColor(Color)}
	 * into one method.
	 * <p>
	 * If you want to set both colors then you <b>must</b> use this method
	 * since multiple calls to {@link Node#setStyle(String)} retain only the
	 * last specified style.
	 */
	public void setColors(Color main, Color secondary) {
		setStyle(
				"-mfx-main: " + ColorUtils.toCss(main) + ";\n" +
						"-mfx-secondary: " + ColorUtils.toCss(secondary) + ";\n"
		);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXToggleButton, ContentDisplay> CONTENT_DISPOSITION =
				FACTORY.createEnumCssMetaData(
						ContentDisplay.class,
						"-mfx-content-disposition",
						MFXToggleButton::contentDispositionProperty,
						ContentDisplay.LEFT
				);

		private static final CssMetaData<MFXToggleButton, Number> GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-gap",
						MFXToggleButton::gapProperty,
						8.0
				);

		private static final CssMetaData<MFXToggleButton, Number> LENGTH =
				FACTORY.createSizeCssMetaData(
						"-mfx-length",
						MFXToggleButton::lengthProperty,
						36.0
				);

		private static final CssMetaData<MFXToggleButton, Number> RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-radius",
						MFXToggleButton::radiusProperty,
						10.0
				);

		private static final CssMetaData<MFXToggleButton, Boolean> TEXT_EXPAND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-text-expand",
						MFXToggleButton::textExpandProperty,
						false
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Labeled.getClassCssMetaData(),
					CONTENT_DISPOSITION, GAP, LENGTH, RADIUS, TEXT_EXPAND
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
		return new MFXToggleButtonSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXToggleButton.getControlCssMetaDataList();
	}
}
