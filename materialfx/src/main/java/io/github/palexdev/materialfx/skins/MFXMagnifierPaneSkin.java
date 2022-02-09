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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.MFXMagnifierPane;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.SnapshotResult;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.StringConverter;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This is the default skin implementation for {@link MFXMagnifierPane}.
 * <p>
 * There are three main components:
 * <p> 1) The lens container: the magnifier's lens is wrapped in a top container to allow clipping the lens (to make it a circle)
 * while still keeping the lens border
 * <p> 2) The lens: core component of the magnifier, responsible for showing a portion of the content zoomed by the current
 * zoom level, {@link MFXMagnifierPane#zoomProperty()}, more about this component here {@link MFXMagnifierLens}
 * <p> 3) The color picker: an extra functionality of the magnifier. To be precise the color picker is just an HBox
 * containing a square and a label. I decided to implement it because the actual "color picking" functionality is already
 * available as part of the magnifier, more here {@link ColorPicker}
 * <p></p>
 * The layout is quite complex and delicate as there is a lot to take in consideration, every component is controlled manually.
 * The lens position for example can't just be the current mouse position, ideally the mouse should be
 * at the center of the lens, for this reason the lens' positions must be shifted according to it's size and zoom level.
 * The lens' view is also a delicate "topic", since the lens is translated, the captured image must be adjusted too, always
 * taking into account the zoom level of course.
 */
public class MFXMagnifierPaneSkin extends SkinBase<MFXMagnifierPane> {
	//================================================================================
	// Properties
	//================================================================================
	private final StackPane lensContainer;
	private final MFXMagnifierLens lens;
	private final ColorPicker picker;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXMagnifierPaneSkin(MFXMagnifierPane magnifier) {
		super(magnifier);

		lens = new MFXMagnifierLens();
		magnifier.setSnapToPixel(false);

		lensContainer = new StackPane(lens);
		lensContainer.setManaged(false);
		lensContainer.setSnapToPixel(false);
		lensContainer.getStyleClass().add("lens-container");

		Circle circle = new Circle();
		circle.radiusProperty().bind(magnifier.lensSizeProperty().divide(2.0).multiply(magnifier.zoomProperty()));
		circle.centerXProperty().bind(lensContainer.widthProperty().divide(2.0));
		circle.centerYProperty().bind(lensContainer.heightProperty().divide(2.0));
		lens.setClip(circle);

		picker = new ColorPicker();
		Node content = magnifier.getContent();
		if (content != null) {
			getChildren().addAll(content, lensContainer, picker);
		} else {
			getChildren().addAll(lensContainer, picker);
		}

		setBehavior();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the behavior of the magnifier.
	 * <p>
	 * Responsible for adding the following handlers/listeners:
	 * <p> - MOUSE_MOVED event: to update the {@link MFXMagnifierPane#positionProperty()}, see {@link #updatePosition(MouseEvent)}
	 * <p> - SCROLL event: to update the {@link MFXMagnifierPane#zoomProperty()}, see {@link #updateZoom(ScrollEvent)}
	 * <p> - A listener on the {@link MFXMagnifierPane#contentProperty()} to update the content
	 * <p> - A listener on the {@link MFXMagnifierPane#lensSizeProperty()} to update the lens' size and view
	 * <p> - A listener on the {@link MFXMagnifierPane#zoomProperty()} to update the lens position and zoom
	 * <p> - Two bindings to control the position of the color picker tool according to the lens position
	 */
	private void setBehavior() {
		MFXMagnifierPane magnifier = getSkinnable();

		magnifier.addEventFilter(MouseEvent.MOUSE_MOVED, this::updatePosition);
		magnifier.addEventFilter(ScrollEvent.SCROLL, this::updateZoom);
		magnifier.contentProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<Node> children = getChildren();
			if (newValue == null && children.size() == 3) {
				children.remove(0);
			} else if (newValue != null) {
				if (children.size() == 3) {
					children.set(0, newValue);
				} else {
					children.add(0, newValue);
				}
			}
		});

		magnifier.lensSizeProperty().addListener(invalidated -> magnifier.requestLayout());
		magnifier.zoomProperty().addListener(invalidated -> magnifier.requestLayout());

		picker.translateXProperty().bind(Bindings.createDoubleBinding(
				() -> {
					double halfPickerW = picker.getWidth() / 2;
					double halfLensW = lensContainer.getWidth() / 2;
					return lensContainer.getTranslateX() - (halfPickerW - halfLensW);
				},
				magnifier.pickedColorProperty(), picker.widthProperty(),
				lensContainer.widthProperty(), lensContainer.translateXProperty()
		));
		picker.translateYProperty().bind(Bindings.createDoubleBinding(
				() -> {
					VPos pos = magnifier.getPickerPos();
					double value;
					if (pos == VPos.TOP) {
						double pickerH = picker.getHeight();
						value = lensContainer.getTranslateY() - pickerH - magnifier.getPickerSpacing();
					} else {
						double lensHeight = lensContainer.getHeight();
						value = lensContainer.getTranslateY() + lensHeight + magnifier.getPickerSpacing();
					}
					return value;
				},
				magnifier.pickedColorProperty(), magnifier.pickerPosProperty(), magnifier.pickerSpacingProperty(),
				picker.heightProperty(), lensContainer.heightProperty(), lensContainer.translateYProperty()
		));
	}

	/**
	 * Responsible for updating the {@link MFXMagnifierPane#positionProperty()} and
	 * calling {@link #updateMagnifier()}.
	 * <p></p>
	 * Also responsible for hiding the lens if the mouse is outside the magnifier.
	 */
	private void updatePosition(MouseEvent me) {
		MFXMagnifierPane magnifier = getSkinnable();

		double meX = me.getX();
		double meY = me.getY();
		double magnWidth = magnifier.getWidth();
		double magnHeight = magnifier.getHeight();
		if (meX < 0 || meY < 0 || meX > magnWidth || meY > magnHeight) {
			lensContainer.setVisible(false);
			return;
		}

		magnifier.setPosition(new PositionBean(meX, meY));
		lensContainer.setVisible(true);

		updateMagnifier();
	}

	/**
	 * Core method responsible for updating the lens position and its view.
	 * <p>
	 * After setting the lens translate X/Y properties, gets the content node.
	 * It it's null exits, otherwise takes a screenshot of the content at the current
	 * position and for the current lens size, the parameters for the snapshot are built by
	 * {@link #snapshotParamsFor(PositionBean, double, double)}.
	 * <p>
	 * Once the snapshot is ready {@link MFXMagnifierLens#update(SnapshotResult)} is called.
	 */
	private void updateMagnifier() {
		MFXMagnifierPane magnifier = getSkinnable();
		PositionBean position = magnifier.getPosition();
		lensContainer.setTranslateX(position.getX() - lensContainer.getWidth() / 2);
		lensContainer.setTranslateY(position.getY() - lensContainer.getHeight() / 2);

		Node content = magnifier.getContent();
		if (content == null) return;

		double size = magnifier.getLensSize();
		SnapshotParameters params = snapshotParamsFor(position, size, size);
		content.snapshot(lens::update, params, null);
	}

	/**
	 * Responsible for updating the {@link MFXMagnifierPane#zoomProperty()}
	 * according to the given {@link ScrollEvent} direction and the specified {@link MFXMagnifierPane#zoomIncrementProperty()}.
	 * <p>
	 * Calls {@link MFXMagnifierLens#zoomIn()} or {@link MFXMagnifierLens#zoomOut()}, and then {@link #updateMagnifier()}.
	 */
	private void updateZoom(ScrollEvent se) {
		ScrollUtils.ScrollDirection scrollDirection = ScrollUtils.determineScrollDirection(se);
		switch (scrollDirection) {
			case UP: {
				lens.zoomIn();
				break;
			}
			case DOWN: {
				lens.zoomOut();
				break;
			}
		}
		updateMagnifier();
	}

	/**
	 * Builds the {@link SnapshotParameters} to take a snapshot of the magnifier's content which will be the
	 * lens' view.
	 * <p>
	 * This method is crucial as a little error here can distort the lens' view.
	 * It needed to properly take into account the lens position, the lens size and the zoom level;
	 * to properly compute the viewport position and size.
	 */
	private SnapshotParameters snapshotParamsFor(PositionBean positionBean, double width, double height) {
		MFXMagnifierPane magnifier = getSkinnable();
		SnapshotParameters sp = new SnapshotParameters();

		double offset = (magnifier.getLensSize() / (magnifier.getZoom() - (magnifier.getZoom() - magnifier.getMinZoom())));
		double xOffset = positionBean.getX() - offset;
		double yOffset = positionBean.getY() - offset;
		sp.setViewport(new Rectangle2D(xOffset, yOffset, width, height));
		return sp;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

		picker.autosize();

		MFXMagnifierPane magnifier = getSkinnable();
		double lensSize = magnifier.getLensSize();
		double zoom = magnifier.getZoom();
		double size = lensSize * zoom;
		lensContainer.resize(size, size);
		if (magnifier.getPosition() != null) updateMagnifier();
	}

	//================================================================================
	// Magnifier Lens
	//================================================================================

	/**
	 * This is the default lens for {@link MFXMagnifierPane}.
	 * <p>
	 * There are three components:
	 * <p> 1) An {@link ImageView} responsible for showing the zoomed portion of the
	 * magnifier's content. The image is specified by the {@link #imageProperty()}
	 * <p> 2) A custom cursor which easily allows the user to understand which is the current selected pixel
	 * <p> 3) A label to show the current zoom level. The label is shown only when the zoom changes, and it is hidden
	 * after a certain amount of time. See {@link MFXMagnifierPane#showZoomLabelProperty()} and {@link MFXMagnifierPane#hideZoomLabelAfterProperty()}.
	 */
	class MFXMagnifierLens extends StackPane {
		//================================================================================
		// Properties
		//================================================================================
		private final String STYLE_CLASS = "lens";

		private final ObjectProperty<Image> image = new SimpleObjectProperty<>() {
			@Override
			public void set(Image newValue) {
				super.set(newValue != null ? postProcess(newValue) : null);
			}
		};
		private final ImageView view;
		private final Rectangle cursor;
		private final Label zoomLabel;

		private final Animation zlShowAnimation;
		private final PauseTransition zlHideAnimation;

		//================================================================================
		// Constructors
		//================================================================================
		MFXMagnifierLens() {
			MFXMagnifierPane magnifier = getSkinnable();
			magnifier.magnifierViewProperty().bind(image);

			view = new ImageView();
			view.imageProperty().bind(image);

			cursor = new Rectangle(1, 1, Color.TRANSPARENT);
			cursor.setManaged(false);
			cursor.getStyleClass().add("cursor");
			cursor.visibleProperty().bind(magnifier.hideCursorProperty().not());
			updateZoom(0);

			zoomLabel = new Label();
			zoomLabel.textProperty().bind(magnifier.zoomProperty().asString());
			zoomLabel.visibleProperty().bind(magnifier.showZoomLabelProperty());
			zoomLabel.setManaged(false);
			zoomLabel.setOpacity(0.0);

			zlHideAnimation = PauseBuilder.build()
					.setDuration(magnifier.getHideZoomLabelAfter())
					.setOnFinished(event ->
							TimelineBuilder.build()
									.add(KeyFrames.of(400, zoomLabel.opacityProperty(), 0.0, Interpolator.EASE_BOTH))
									.getAnimation()
									.play()
					).getAnimation();
			zlShowAnimation = TimelineBuilder.build()
					.add(KeyFrames.of(400, zoomLabel.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
					.getAnimation();
			magnifier.zoomProperty().addListener(invalidated -> {
				zlHideAnimation.stop();
				zlShowAnimation.play();
				zlHideAnimation.play();
			});

			zlHideAnimation.durationProperty().bind(Bindings.createObjectBinding(
					() -> Duration.millis(magnifier.getHideZoomLabelAfter()),
					magnifier.hideZoomLabelAfterProperty()
			));

			getStyleClass().add(STYLE_CLASS);
			getChildren().addAll(view, cursor, zoomLabel);
		}

		//================================================================================
		// Methods
		//================================================================================

		/**
		 * Updates the {@link #imageProperty()} of the lens with {@link SnapshotResult#getImage()}.
		 * <p>
		 * The image property will then automatically call {@link #postProcess(Image)}.
		 */
		private Void update(SnapshotResult snapshot) {
			setImage(snapshot.getImage());
			return null;
		}

		/**
		 * This is actually a core method of the lens.
		 * <p>
		 * In JavaFX snapshots are anti-aliased by default and as far as I know there's no way to
		 * prevent that.
		 * <p>
		 * For a magnifier/color picker tool this is very bad as when you zoom the image you won't be able to see
		 * the single pixels, colors will appear mixed, the image blurry, it's a disaster for a tool that needs pixel precision.
		 * <p></p>
		 * This method fixes this by:
		 * <p> 1) Converts the input image to a {@link java.awt.image.BufferedImage}
		 * <p> 2) Writes the buffered image into a {@link ByteArrayOutputStream}
		 * <p> 3) Creates a new JavaFX {@link Image} from the stream, with width and height computed
		 * taking into account the current zoom level, and setting "preserveRatio" and "smooth" to false
		 * <p></p>
		 * This is a heavy performance operation, but it's the easiest and actually the fastest.
		 * From some basic testing the operation can take from 2ms to 25ms(rarely) to complete, so reaching
		 * 60fps is not an issue at all.
		 */
		@SuppressWarnings("ConstantConditions")
		private Image postProcess(Image input) {
			if (input == null) {
				throw new NullPointerException("Cannot post process null input!");
			}

			MFXMagnifierPane magnifier = getSkinnable();
			Image output = input;
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				double w = magnifier.getZoom() * input.getWidth();
				double h = magnifier.getZoom() * input.getHeight();
				ImageIO.write(SwingFXUtils.fromFXImage(input, null), "png", baos);
				output = new Image(new ByteArrayInputStream(baos.toByteArray()), w, h, false, false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return output;
		}

		/**
		 * Increments the zoom by {@link MFXMagnifierPane#zoomIncrementProperty()},
		 * calls {@link #updateZoom(double)}.
		 */
		private void zoomIn() {
			MFXMagnifierPane magnifier = getSkinnable();
			if (magnifier.getZoom() == magnifier.getMaxZoom()) return;
			updateZoom(magnifier.getZoomIncrement());
		}

		/**
		 * Decrements the zoom by {@link MFXMagnifierPane#zoomIncrementProperty()},
		 * calls {@link #updateZoom(double)}.
		 */
		private void zoomOut() {
			MFXMagnifierPane magnifier = getSkinnable();
			if (magnifier.getZoom() == magnifier.getMinZoom()) return;
			updateZoom(-magnifier.getZoomIncrement());
		}

		/**
		 * After updating the {@link MFXMagnifierPane#zoomProperty()}, sets
		 * the custom cursor' scale to the current zoom level.
		 */
		private void updateZoom(double increment) {
			MFXMagnifierPane magnifier = getSkinnable();
			magnifier.setZoom(magnifier.getZoom() + increment);
			cursor.setScaleX(magnifier.getZoom());
			cursor.setScaleY(magnifier.getZoom());
		}

		//================================================================================
		// Overridden Methods
		//================================================================================
		@Override
		protected double computeMinWidth(double height) {
			MFXMagnifierPane magnifier = getSkinnable();
			return magnifier.getLensSize();
		}

		@Override
		protected double computeMinHeight(double width) {
			MFXMagnifierPane magnifier = getSkinnable();
			return magnifier.getLensSize();
		}

		@Override
		protected void layoutChildren() {
			super.layoutChildren();

			MFXMagnifierPane magnifier = getSkinnable();
			double zoom = magnifier.getZoom() / 2;
			layoutInArea(
					cursor,
					zoom,
					zoom,
					getWidth(),
					getHeight(),
					0,
					getPadding(),
					false,
					false,
					HPos.CENTER,
					VPos.CENTER,
					false
			);

			layoutInArea(
					zoomLabel,
					-10,
					0,
					getWidth(),
					getHeight(),
					0,
					getPadding(),
					false,
					false,
					HPos.RIGHT,
					VPos.CENTER,
					true
			);
		}

		//================================================================================
		// Getters/Setters
		//================================================================================
		public Image getImage() {
			return image.get();
		}

		/**
		 * Specifies the lens' view
		 */
		public ObjectProperty<Image> imageProperty() {
			return image;
		}

		public void setImage(Image image) {
			this.image.set(image);
		}
	}

	//================================================================================
	// Color Picker
	//================================================================================

	/**
	 * This is the color picker tool for {@link MFXMagnifierPane}.
	 * <p>
	 * There are two components:
	 * <p> 1) A {@link Rectangle} sized to be a square, which has the same color as {@link MFXMagnifierPane#pickedColorProperty()}
	 * <p> 2) A label to show the {@link MFXMagnifierPane#pickerPosProperty()} as a String using the {@link MFXMagnifierPane#colorConverterProperty()}
	 * <p></p>
	 * So, to be precise this is not really a color picker, it just shows the picked color, the functionality was already available
	 * as part of the magnifier, this is just a control to show it.
	 */
	class ColorPicker extends HBox {
		//================================================================================
		// Properties
		//================================================================================
		private final String STYLE_CLASS = "color-picker";
		private final Rectangle colorSquare;
		private final Label colorLabel;

		//================================================================================
		// Constructors
		//================================================================================
		ColorPicker() {
			MFXMagnifierPane magnifier = getSkinnable();

			setSpacing(10);
			setAlignment(Pos.CENTER);
			visibleProperty().bind(magnifier.pickedColorProperty().isNotNull().and(lensContainer.visibleProperty()));

			colorSquare = new Rectangle(20, 20);
			colorSquare.getStyleClass().add("color-square");
			colorSquare.fillProperty().bind(magnifier.pickedColorProperty());

			colorLabel = new Label();
			colorLabel.textProperty().bind(Bindings.createStringBinding(
					() -> {
						StringConverter<Color> converter = magnifier.getColorConverter();
						Color color = magnifier.getPickedColor();
						return converter != null ? converter.toString(color) : color.toString();
					},
					magnifier.contentProperty(), magnifier.pickedColorProperty()
			));
			colorLabel.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(colorLabel, Priority.ALWAYS);

			getStyleClass().add(STYLE_CLASS);
			getChildren().addAll(colorSquare, colorLabel);
		}
	}
}
