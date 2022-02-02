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
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.skins.MFXMagnifierPaneSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.List;

/**
 * MaterialFX implementation of a pane/control capable of "zooming" its content (any {@code Node}).
 * <p>
 * This control is quite complex and has a lot of features. A lens (part of the skin), controlled/positioned by the mouse,
 * is responsible for zooming a portion of the content by the specified {@link #zoomProperty()}.
 * <p>
 * The mouse wheel controls the {@link #zoomProperty()} by incrementing/decrementing it by the specified amount, {@link #zoomIncrementProperty()}.
 * <p>
 * The zoom level can be constrained between a min and max by setting {@link #minZoomProperty()} and {@link #maxZoomProperty()}.
 * <p>
 * You can also position the lens manually by setting the {@link #positionProperty()}.
 * <p>
 * The {@link #magnifierViewProperty()} specifies the portion of the content that is currently zoomed, it is a bound property,
 * managed by the skin, any attempt to set an Image will fail with an exception.
 * <p>
 * {@code MFXMagnifierPane} also includes a color picker tool. The lens has a custom cursor (that can also be hidden) that
 * tells the user which pixel is currently selected. By calling {@link #updatePickedColor()} the tool will read the selected pixel's color.
 * The color picker tool also shows a label for the picked color (can also be hidden), the color is converted to a String using a function specified
 * by the user, by default uses {@link ColorUtils#rgb(Color)}.
 * This mechanism allows you to customize the way the picker works. For example if you want the color picker to update the color
 * in real-time (thus also update the picked color label in real-time) you could do something like:
 * <pre>
 * {@code
 *      MFXMagnifierPane mp = new MFXMagnifierPane(content);
 *      mp.magnifierViewProperty().addListener(invalidated -> mp.updatePickedColor());
 * }
 * </pre>
 * Or, if you want to update it only when a mouse event occurs you could do something like:
 * <pre>
 * {@code
 *      MFXMagnifierPane mp = new MFXMagnifierPane(content);
 *      mp.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
 *          if (event.getButton() == MouseButton.PRIMARY) {
 *              mp.updatePickedColor();
 *          }
 *      });
 * }
 * </pre>
 */
public class MFXMagnifierPane extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-magnifier";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXMagnifier.css");

	private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
	private final ObjectProperty<PositionBean> position = new SimpleObjectProperty<>();

	private final ObjectProperty<Image> magnifierView = new SimpleObjectProperty<>() {
		@Override
		public void unbind() {
		}
	};

	private final ReadOnlyObjectWrapper<Color> pickedColor = new ReadOnlyObjectWrapper<>();
	private final ObjectProperty<StringConverter<Color>> colorConverter = new SimpleObjectProperty<>(FunctionalStringConverter.to(ColorUtils::rgb));

	//================================================================================
	// Constructors
	//================================================================================
	public MFXMagnifierPane(Node content) {
		setContent(content);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setCursor(Cursor.NONE);
		setSnapToPixel(false);
	}

	/**
	 * Updates the {@link #pickedColorProperty()}.
	 * <p>
	 * Attempts to read the selected pixel's color by getting the current
	 * zoomed portion of the content (if null exits), then uses the image's
	 * {@link PixelReader} to read the color.
	 */
	public void updatePickedColor() {
		Image currentView = getMagnifierView();
		if (currentView != null) {
			PixelReader pixelReader = currentView.getPixelReader();
			Color color = pixelReader.getColor(
					(int) (currentView.getWidth() / 2),
					(int) (currentView.getHeight() / 2)
			);
			setPickedColor(color);
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXMagnifierPaneSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty lensSize = new StyleableDoubleProperty(
			StyleableProperties.LENS_SIZE,
			this,
			"lensSize",
			100.0
	);

	private final StyleableDoubleProperty zoom = new StyleableDoubleProperty(
			StyleableProperties.ZOOM,
			this,
			"zoom",
			2.0
	);

	private final StyleableDoubleProperty zoomIncrement = new StyleableDoubleProperty(
			StyleableProperties.ZOOM_INCREMENT,
			this,
			"zoomIncrement",
			0.25
	);

	private final StyleableDoubleProperty minZoom = new StyleableDoubleProperty(
			StyleableProperties.MIN_ZOOM,
			this,
			"minZoom",
			2.0
	);

	private final StyleableDoubleProperty maxZoom = new StyleableDoubleProperty(
			StyleableProperties.MAX_ZOOM,
			this,
			"maxZoom",
			8.0
	);

	private final StyleableObjectProperty<VPos> pickerPos = new StyleableObjectProperty<>(
			StyleableProperties.PICKER_POS,
			this,
			"pickerPos",
			VPos.BOTTOM
	);

	private final StyleableDoubleProperty pickerSpacing = new StyleableDoubleProperty(
			StyleableProperties.PICKER_SPACING,
			this,
			"pickerSpacing",
			10.0
	);

	private final StyleableBooleanProperty hideCursor = new StyleableBooleanProperty(
			StyleableProperties.HIDE_CURSOR,
			this,
			"hideCursor",
			false
	);

	private final StyleableBooleanProperty showZoomLabel = new StyleableBooleanProperty(
			StyleableProperties.SHOW_ZOOM_LABEL,
			this,
			"showZoomLabel",
			true
	);

	private final StyleableDoubleProperty hideZoomLabelAfter = new StyleableDoubleProperty(
			StyleableProperties.HIDE_ZOOM_LABEL_AFTER,
			this,
			"hideZoomLabelAfter",
			2000.0
	);

	public double getLensSize() {
		return lensSize.get();
	}

	/**
	 * Specifies the size of the lens.
	 * <p>
	 * The default lens by default is a square, but then it is clipped to be
	 * a circle. You can think of this property as the diameter of the circle.
	 */
	public StyleableDoubleProperty lensSizeProperty() {
		return lensSize;
	}

	public void setLensSize(double lensSize) {
		this.lensSize.set(lensSize);
	}

	public double getZoom() {
		return zoom.get();
	}

	/**
	 * Specifies the current zoom level of the lens.
	 */
	public StyleableDoubleProperty zoomProperty() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom.set(zoom);
	}

	public double getZoomIncrement() {
		return zoomIncrement.get();
	}

	/**
	 * Specifies the zoom increment/decrement when using the mouse wheel.
	 */
	public StyleableDoubleProperty zoomIncrementProperty() {
		return zoomIncrement;
	}

	public void setZoomIncrement(double zoomIncrement) {
		this.zoomIncrement.set(zoomIncrement);
	}

	public double getMinZoom() {
		return minZoom.get();
	}

	/**
	 * Specifies the minimum zoom level allowed.
	 */
	public StyleableDoubleProperty minZoomProperty() {
		return minZoom;
	}

	public void setMinZoom(double minZoom) {
		this.minZoom.set(minZoom);
	}

	public double getMaxZoom() {
		return maxZoom.get();
	}

	/**
	 * Specifies the maximum zoom level allowed.
	 */
	public StyleableDoubleProperty maxZoomProperty() {
		return maxZoom;
	}

	public void setMaxZoom(double maxZoom) {
		this.maxZoom.set(maxZoom);
	}

	public VPos getPickerPos() {
		return pickerPos.get();
	}

	/**
	 * Specifies the position of the color picker tool.
	 * <p>
	 * Only two positions are allowed, above the lens (TOP) or below the lens (BOTTOM).
	 */
	public StyleableObjectProperty<VPos> pickerPosProperty() {
		return pickerPos;
	}

	public void setPickerPos(VPos pickerPos) {
		this.pickerPos.set(pickerPos);
	}

	public double getPickerSpacing() {
		return pickerSpacing.get();
	}

	/**
	 * Specifies the gap between the lens and the color picker tool.
	 */
	public StyleableDoubleProperty pickerSpacingProperty() {
		return pickerSpacing;
	}

	public void setPickerSpacing(double pickerSpacing) {
		this.pickerSpacing.set(pickerSpacing);
	}

	public boolean isHideCursor() {
		return hideCursor.get();
	}

	/**
	 * Specifies whether to show or hide the custom cursor.
	 * <p>
	 * Node that to use the custom cursor by default the magnifier pane's cursor is set
	 * to {@link Cursor#NONE}.
	 */
	public StyleableBooleanProperty hideCursorProperty() {
		return hideCursor;
	}

	public void setHideCursor(boolean hideCursor) {
		this.hideCursor.set(hideCursor);
	}

	public boolean isShowZoomLabel() {
		return showZoomLabel.get();
	}

	/**
	 * Specifies whether to show a label that indicates the current zoom level.
	 * <p>
	 * The label is shown only when the {@link #zoomProperty()} changes, and is hidden
	 * after {@link #hideZoomLabelAfterProperty()}.
	 */
	public StyleableBooleanProperty showZoomLabelProperty() {
		return showZoomLabel;
	}

	public void setShowZoomLabel(boolean showZoomLabel) {
		this.showZoomLabel.set(showZoomLabel);
	}

	public double getHideZoomLabelAfter() {
		return hideZoomLabelAfter.get();
	}

	/*
	 * Specifies the amount of time (in milliseconds) after which the zoom label will be hidden.
	 */
	public StyleableDoubleProperty hideZoomLabelAfterProperty() {
		return hideZoomLabelAfter;
	}

	public void setHideZoomLabelAfter(double hideZoomLabelAfter) {
		this.hideZoomLabelAfter.set(hideZoomLabelAfter);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXMagnifierPane> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXMagnifierPane, Number> LENS_SIZE =
				FACTORY.createSizeCssMetaData(
						"-mfx-lens-size",
						MFXMagnifierPane::lensSizeProperty,
						100.0
				);

		private static final CssMetaData<MFXMagnifierPane, Number> ZOOM =
				FACTORY.createSizeCssMetaData(
						"-mfx-zoom",
						MFXMagnifierPane::zoomProperty,
						2.0
				);

		private static final CssMetaData<MFXMagnifierPane, Number> ZOOM_INCREMENT =
				FACTORY.createSizeCssMetaData(
						"-mfx-zoom-increment",
						MFXMagnifierPane::zoomIncrementProperty,
						0.25
				);

		private static final CssMetaData<MFXMagnifierPane, Number> MIN_ZOOM =
				FACTORY.createSizeCssMetaData(
						"-mfx-min-zoom",
						MFXMagnifierPane::minZoomProperty,
						2.0
				);

		private static final CssMetaData<MFXMagnifierPane, Number> MAX_ZOOM =
				FACTORY.createSizeCssMetaData(
						"-mfx-max-zoom",
						MFXMagnifierPane::maxZoomProperty,
						8.0
				);

		private static final CssMetaData<MFXMagnifierPane, VPos> PICKER_POS =
				FACTORY.createEnumCssMetaData(
						VPos.class,
						"-mfx-picker-pos",
						MFXMagnifierPane::pickerPosProperty,
						VPos.BOTTOM
				);

		private static final CssMetaData<MFXMagnifierPane, Number> PICKER_SPACING =
				FACTORY.createSizeCssMetaData(
						"-mfx-picker-spacing",
						MFXMagnifierPane::pickerSpacingProperty,
						10.0
				);

		private static final CssMetaData<MFXMagnifierPane, Boolean> HIDE_CURSOR =
				FACTORY.createBooleanCssMetaData(
						"-mfx-hide-cursor",
						MFXMagnifierPane::hideCursorProperty,
						false
				);

		private static final CssMetaData<MFXMagnifierPane, Boolean> SHOW_ZOOM_LABEL =
				FACTORY.createBooleanCssMetaData(
						"-mfx-show-zoom-label",
						MFXMagnifierPane::showZoomLabelProperty,
						true
				);

		private static final CssMetaData<MFXMagnifierPane, Number> HIDE_ZOOM_LABEL_AFTER =
				FACTORY.createSizeCssMetaData(
						"-mfx-hide-zoom-label-after",
						MFXMagnifierPane::hideZoomLabelAfterProperty,
						2000.0
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Control.getClassCssMetaData(),
					LENS_SIZE, ZOOM, ZOOM_INCREMENT, MIN_ZOOM, MAX_ZOOM,
					PICKER_POS, PICKER_SPACING, HIDE_CURSOR,
					SHOW_ZOOM_LABEL, HIDE_ZOOM_LABEL_AFTER
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXMagnifierPane.getClassCssMetaData();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Node getContent() {
		return content.get();
	}

	/**
	 * Specifies the magnifier's content.
	 */
	public ObjectProperty<Node> contentProperty() {
		return content;
	}

	public void setContent(Node content) {
		this.content.set(content);
	}

	public PositionBean getPosition() {
		return position.get();
	}

	/**
	 * Specifies the position of the lens.
	 */
	public ObjectProperty<PositionBean> positionProperty() {
		return position;
	}

	public void setPosition(PositionBean position) {
		this.position.set(position);
	}

	public Image getMagnifierView() {
		return magnifierView.get();
	}

	/**
	 * Specifies the current zoomed portion of the content.
	 */
	public ObjectProperty<Image> magnifierViewProperty() {
		return magnifierView;
	}

	private void setMagnifierView(Image magnifierView) {
		this.magnifierView.set(magnifierView);
	}

	public Color getPickedColor() {
		return pickedColor.get();
	}

	/**
	 * Specifies the picked color.
	 * <p>
	 * Not updated automatically, you must call {@link #updatePickedColor()},
	 * see class documentation for examples.
	 */
	public ReadOnlyObjectProperty<Color> pickedColorProperty() {
		return pickedColor.getReadOnlyProperty();
	}

	private void setPickedColor(Color pickedColor) {
		this.pickedColor.set(pickedColor);
	}

	public StringConverter<Color> getColorConverter() {
		return colorConverter.get();
	}

	/**
	 * Specifies the {@link StringConverter} used to convert a {@link Color} to a String.
	 */
	public ObjectProperty<StringConverter<Color>> colorConverterProperty() {
		return colorConverter;
	}

	public void setColorConverter(StringConverter<Color> colorConverter) {
		this.colorConverter.set(colorConverter);
	}
}
