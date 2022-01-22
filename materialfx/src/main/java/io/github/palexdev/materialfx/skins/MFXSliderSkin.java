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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.enums.SliderEnums.SliderMode;
import io.github.palexdev.materialfx.enums.SliderEnums.SliderPopupSide;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis.TickMark;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This is the {@code Skin} used by default by every {@link MFXSlider}l.
 * <p></p>
 * To be honest, I always thought that making a slider would be a much easier task, but this skin
 * but this skin proves just the opposite. Don't get me wrong, if you calmly read the code it is intuitive
 * but still, one of the most complicated skins I've ever made so far.
 * <p></p>
 * At the core a slider is simply a progress bar that the user can adjust with an icon (the thumb).
 * So, conceptually it is easy, but computing the value based on the mouse drag or press, computing the
 * layout based on the value, managing the popup position and visibility, positioning the ticks correctly,
 * the possibility to change the thumb and the popup, the bidirectional feature, all of that is quite tricky, but,
 * I must say that I'm happy with the final result.
 * <p></p>
 * A little note on how the popup visibility is managed.
 * <p>
 * The popup is shown when an arrow key is pressed or the mouse is pressed on the thumb.
 * <p>
 * To hide the popup a {@link PauseTransition} of 800 milliseconds is played and at the end
 * the popup is hidden.
 * <p></p>
 * This transition, the release timer, should ensure that the popup is not closed when the value is
 * adjusted rapidly.
 */
public class MFXSliderSkin extends SkinBase<MFXSlider> {
	//================================================================================
	// Properties
	//================================================================================
	private final Rectangle track;
	private final Rectangle bar;
	private final Group group;
	private final Group ticksGroup;
	private final NumberAxis ticksAxis;
	private Node thumb;
	private Region popup;

	private final LayoutData layoutData = new LayoutData();
	private final PopupManager popupManager = new PopupManager();

	private double preDragThumbPos;
	private Point2D dragStart;
	private EventHandler<MouseEvent> thumbPressHandler;
	private EventHandler<MouseEvent> thumbDragHandler;
	private EventHandler<MouseEvent> trackPressedHandler;

	private boolean mousePressed = false;
	private boolean trackPressed = false;
	private boolean keyPressed = false;
	private boolean keyWasPressed = false;
	private PauseTransition releaseTimer = new PauseTransition();

	private boolean isSnapping = false;
	private boolean wasSnapping = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXSliderSkin(MFXSlider slider) {
		super(slider);

		track = buildRectangle("track");
		track.heightProperty().bind(slider.heightProperty());
		track.widthProperty().bind(slider.widthProperty());
		track.setFill(Color.rgb(82, 0, 237, 0.3));
		track.setStroke(Color.GOLD);

		bar = buildRectangle("bar");
		bar.heightProperty().bind(slider.heightProperty());
		bar.setFill(Color.GREEN);
		bar.setMouseTransparent(true);

		thumb = slider.getThumbSupplier().get();
		popup = slider.getPopupSupplier().get();
		popup.setVisible(false);
		popup.setOpacity(0.0);

		ticksAxis = new NumberAxis(slider.getMin(), slider.getMax(), slider.getTickUnit());
		ticksAxis.setMinorTickCount(slider.getMinorTicksCount());
		ticksAxis.setManaged(false);
		ticksAxis.setMouseTransparent(true);
		ticksAxis.setTickMarkVisible(false);
		ticksAxis.setTickLabelsVisible(false);

		Rectangle clip = new Rectangle();
		clip.heightProperty().bind(slider.heightProperty());
		clip.widthProperty().bind(slider.widthProperty());
		clip.arcHeightProperty().bind(track.arcHeightProperty());
		clip.arcWidthProperty().bind(track.arcWidthProperty());

		ticksGroup = new Group(ticksAxis);
		ticksGroup.setClip(clip);
		ticksGroup.setManaged(false);
		ticksGroup.setMouseTransparent(true);

		group = new Group(track, ticksGroup, bar, thumb, popup);
		group.setManaged(false);
		group.getStylesheets().add(slider.getUserAgentStylesheet());
		getChildren().setAll(group);

		releaseTimer.setDuration(Duration.millis(800));
		releaseTimer.setOnFinished(event -> hidePopup());

		thumbPressHandler = event -> {
			dragStart = thumb.localToParent(event.getX(), event.getY());
			preDragThumbPos = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
		};
		thumbDragHandler = this::handleDrag;
		trackPressedHandler = this::trackPressed;

		if (slider.getOrientation() == Orientation.VERTICAL) {
			slider.setRotate(-90);
		} else {
			slider.setRotate(0);
		}

		setBehavior();
	}

	//================================================================================
	// Behavior
	//================================================================================

	/**
	 * Calls {@link #sliderHandlers()}, {@link #sliderListeners()}, {@link #skinBehavior()}.
	 */
	protected void setBehavior() {
		sliderHandlers();
		sliderListeners();
		skinBehavior();
	}

	/**
	 * Defines the slider's behavior as follows:
	 * <p></p>
	 * <p> - Adds a MOUSE_CLICKED event handler to request the focus
	 * <p> - Adds a MOUSE_PRESSED event filter to show the popup if the thumb is pressed, {@link #showPopup()},
	 * also sets a flag "mousePressed" to true, more on this here {@link #updateLayout()}.
	 * <p> - Adds a MOUSE_RELEASED event filter to reset the "mousePressed" flag and starts the releaseTimer
	 * (more about this timer in the skin documentation)
	 * <p> - Adds a KEY_PRESSED event filter to increase/decrease the slider's value accordingly to the
	 * pressed arrow key. Also sets the "keyPressed" and "keyWasPressed" flags to true
	 */
	private void sliderHandlers() {
		MFXSlider slider = getSkinnable();

		/* FOCUS */
		slider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> slider.requestFocus());

		/* POPUP HANDLING */
		slider.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			mousePressed = true;
			Node intersectedNode = event.getPickResult().getIntersectedNode();
			if (intersectedNode == track || NodeUtils.inHierarchy(intersectedNode, thumb)) {
				showPopup();
			}
		});
		slider.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			mousePressed = false;
			releaseTimer.playFromStart();
		});

		/* KEYBOARD HANDLING */
		slider.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (!slider.isEnableKeyboard()) {
				return;
			}

			double val = (event.isShiftDown() || event.isControlDown()) ? slider.getAlternativeUnitIncrement() : slider.getUnitIncrement();
			if (isIncreaseKey(event)) {
				keyPressed = true;
				keyWasPressed = true;
				slider.setValue(
						NumberUtils.clamp(slider.getValue() + val, slider.getMin(), slider.getMax())
				);
			} else if (isDecreaseKey(event)) {
				keyPressed = true;
				keyWasPressed = true;
				slider.setValue(
						NumberUtils.clamp(slider.getValue() - val, slider.getMin(), slider.getMax())
				);
			}
		});
	}

	/**
	 * Adds listeners to the following slider's properties:
	 * <p></p>
	 * <p> - {@link MFXSlider#valueProperty()}, to update the update the layout
	 * <p> - {@link MFXSlider#minProperty()}, if this changes the slider's value is reset to prevent inconsistencies,
	 * then updates the layout
	 * <p> - {@link MFXSlider#maxProperty()}, if this changes the slider's value is reset to prevent inconsistencies,
	 * then updates the layout
	 * <p> - {@link MFXSlider#minorTicksCountProperty()}, {@link MFXSlider#tickUnitProperty()}, {@link MFXSlider#showMinorTicksProperty()},
	 * to update the ticks layout according to the changes
	 * <p> - {@link MFXSlider#popupSupplierProperty()}, {@link MFXSlider#thumbSupplierProperty()}, to replace
	 * the popup and the thumb according to the new suppliers, they respectively call {@link #handlePopupChange()} and {@link #handleThumbChange()}.
	 * Also, the popup is managed by the {@link PopupManager}, see {@link PopupManager#initPopup()}.
	 * <p> - {@link MFXSlider#bidirectionalProperty()}, {@link MFXSlider#orientationProperty()}, to update the layout accordingly
	 * <p> - {@link MFXSlider#focusedProperty()}, this is a workaround for an issue with the key handling. For some reason
	 * when the value is adjusted with the arrow keys the slider lose the focus. When this happens we check that
	 * the focus is lost and the "keyPressed" flag is true. If both are true we use {@link PauseBuilder#runWhile(BooleanExpression, Runnable, Runnable)}
	 * to ensure that the focus is reacquired as soon as possible, so that next key events can work too.
	 */
	private void sliderListeners() {
		MFXSlider slider = getSkinnable();

		/* VALUE AND BOUNDS HANDLING */
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!isSnapping) {
				updateLayout();
			}
		});
		slider.minProperty().addListener((observable, oldValue, newValue) -> {
			slider.setValue(0);
			ticksAxis.setLowerBound(newValue.doubleValue());
			slider.requestLayout();
		});
		slider.maxProperty().addListener((observable, oldValue, newValue) -> {
			slider.setValue(0);
			ticksAxis.setUpperBound(newValue.doubleValue());
			slider.requestLayout();
		});

		/* NumberAxis HANDLING */
		slider.minorTicksCountProperty().addListener((observable, oldValue, newValue) -> {
			ticksAxis.setMinorTickCount(newValue.intValue());
			ticksAxis.requestAxisLayout();
			slider.requestLayout();
		});
		slider.tickUnitProperty().addListener((observable, oldValue, newValue) -> {
			ticksAxis.setTickUnit(newValue.doubleValue());
			ticksAxis.requestAxisLayout();
			slider.requestLayout();
		});
		slider.showTicksAtEdgesProperty().addListener((observable, oldValue, newValue) -> slider.requestLayout());

		/* SUPPLIERS HANDLING */
		slider.popupSupplierProperty().addListener((observable, oldValue, newValue) -> {
			handlePopupChange();
			slider.requestLayout();
			popupManager.initPopup();
		});
		slider.thumbSupplierProperty().addListener((observable, oldValue, newValue) -> {
			handleThumbChange();
			slider.requestLayout();
		});

		/* LAYOUT HANDLING */
		slider.bidirectionalProperty().addListener((observable, oldValue, newValue) -> slider.requestLayout());
		slider.orientationProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Orientation.VERTICAL) {
				slider.setRotate(-90);
			} else {
				slider.setRotate(0);
			}
		});

		/* FOCUS WORKAROUND HANDLING */
		slider.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && keyPressed) {
				PauseBuilder.build()
						.setDuration(Duration.millis(100))
						.runWhile(slider.focusedProperty(), slider::requestFocus, () -> keyPressed = false);
			}
		});
	}

	/**
	 * Defines the behavior of the skin as follows:
	 * <p></p>
	 * <p> - Adds a MOUSE_PRESSED event handler to the thumb to store layout info needed for the thumb drag before the actual drag begins
	 * <p> - Adds a MOUSE_DRAGGED event handler to the thumb to handle the drag, see {@link #handleDrag(MouseEvent)}
	 * <p> - Adds a MOUSE_PRESSED event handler to the track to update the slider's value according to where the mouse is pressed on the track, see {@link #trackPressed(MouseEvent)}
	 * <p> - Calls {@link PopupManager#initPopup()} for the first time
	 * <p> - Handles the ticks visibility and layout by calling {@link LayoutData#updateTicksData()}
	 */
	private void skinBehavior() {
		MFXSlider slider = getSkinnable();

		/* THUMB AND TRACK HANDLING */
		thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, thumbPressHandler);
		thumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, thumbDragHandler);
		track.addEventHandler(MouseEvent.MOUSE_PRESSED, trackPressedHandler);

		/* POPUP HANDLING */
		popupManager.initPopup();

		/* NumberAxis LAYOUT HANDLING */
		ticksAxis.visibleProperty().bind(slider.showMinorTicksProperty());
		ticksAxis.needsLayoutProperty().addListener((observable, oldValue, newValue) -> layoutData.updateTicksData());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This is responsible for updating the layout when the slider's value changes.
	 * <p>
	 * Before doing anything it checks if the slider mode is set to "SNAP_TO_TICKS" and the value change
	 * is not due to a key press. In this case the value is adjusted by finding the nearest major tick, see {@link LayoutData#findNearestTick()}.
	 * Also the "isSnapping" flag is set to true so that the value change doesn't trigger this methods again, at the end it is reset to false.
	 * <p></p>
	 * After this first check, {@link LayoutData#update(boolean)} is called (argument is false).
	 * <p>
	 * Then if the "mousePressed" flag is false (so the change is due to a key press) the popup is shown and the release timer is started.
	 * <p></p>
	 * If the value change is due to a mouse press on the track or due to the snap mode and the {@link MFXSlider#animateOnPressProperty()} is true,
	 * the thumb and bar layout is updated with an animation, otherwise it's updated immediately.
	 * <p></p>
	 * At the end the "keyWasPressed" flag is reset to false.
	 */
	private void updateLayout() {
		MFXSlider slider = getSkinnable();

		if (slider.getSliderMode() == SliderMode.SNAP_TO_TICKS && !keyWasPressed) {
			isSnapping = true;
			wasSnapping = true;
			double closest = layoutData.findNearestTick();
			slider.setValue(closest);
			isSnapping = false;
		}

		layoutData.update(false);

		if (!mousePressed) {
			showPopup();
			releaseTimer.playFromStart();
		}

		if ((trackPressed || wasSnapping) && slider.isAnimateOnPress()) {
			wasSnapping = false;
			AnimationUtils.ParallelBuilder.build()
					.add(
							new KeyFrame(Duration.millis(200), new KeyValue(bar.layoutXProperty(), layoutData.barX, MFXAnimationFactory.INTERPOLATOR_V1))
					)
					.add(
							new KeyFrame(Duration.millis(200), new KeyValue(thumb.layoutXProperty(), layoutData.thumbX, MFXAnimationFactory.INTERPOLATOR_V1)),
							new KeyFrame(Duration.millis(200), new KeyValue(bar.widthProperty(), Math.abs(layoutData.barW), MFXAnimationFactory.INTERPOLATOR_V1))
					)
					.getAnimation()
					.play();
		} else {
			thumb.setLayoutX(layoutData.thumbX);
			bar.setLayoutX(layoutData.barX);
			bar.setWidth(Math.abs(layoutData.barW));
		}

		keyWasPressed = false;
	}

	/**
	 * Handles the thumb drag and computes the new slider value according to the drag position.
	 */
	private void handleDrag(MouseEvent event) {
		MFXSlider slider = getSkinnable();
		trackPressed = false;

		Point2D curr = thumb.localToParent(event.getX(), event.getY());
		double dragPos = curr.getX() - dragStart.getX();
		double pos = preDragThumbPos + dragPos / slider.getWidth();
		double val = NumberUtils.clamp((pos * (slider.getMax() - slider.getMin())) + slider.getMin(), slider.getMin(), slider.getMax());
		slider.setValue(val);
	}

	/**
	 * Sets the "trackPressed" flag to true, computes the press position and the new slider value.
	 */
	private void trackPressed(MouseEvent event) {
		MFXSlider slider = getSkinnable();
		trackPressed = true;

		double pos = event.getX() / slider.getWidth();
		double val = NumberUtils.clamp((pos * (slider.getMax() - slider.getMin())) + slider.getMin(), slider.getMin(), slider.getMax());
		slider.setValue(val);
	}

	/**
	 * Handles changes of the {@link MFXSlider#popupSupplierProperty()}, removes the old popup, builds the new one and if the supplier or the
	 * returned value are not null adds it to the children list, then calls {@link PopupManager#initPopup()}.
	 */
	private void handlePopupChange() {
		MFXSlider slider = getSkinnable();

		int index = -1;
		if (popup != null) {
			index = group.getChildren().indexOf(popup);
			popup.layoutXProperty().unbind();
			popup.layoutYProperty().unbind();
			group.getChildren().remove(popup);
		}

		Supplier<Region> popupSupplier = slider.getPopupSupplier();
		popup = popupSupplier != null ? popupSupplier.get() : null;

		if (popup != null) {
			popup.setVisible(false);
			popup.setOpacity(0.0);
			group.getChildren().add(index >= 0 ? index : group.getChildren().size() - 1, popup);
			popupManager.initPopup();
		}
	}

	/**
	 * Handles changes of the {@link MFXSlider#thumbSupplierProperty()} ()}, removes the old thumb, builds the new one and if the supplier or the
	 * returned value are not null (this should never happen though) adds it to the children list, then adds the needed handlers to it.
	 */
	private void handleThumbChange() {
		MFXSlider slider = getSkinnable();

		int index = -1;
		if (thumb != null) {
			index = group.getChildren().indexOf(thumb);
			thumb.removeEventHandler(MouseEvent.MOUSE_PRESSED, thumbPressHandler);
			thumb.removeEventHandler(MouseEvent.MOUSE_DRAGGED, thumbDragHandler);
			group.getChildren().remove(thumb);
		}

		Supplier<Node> thumbSupplier = slider.getThumbSupplier();
		thumb = thumbSupplier != null ? thumbSupplier.get() : null;

		if (thumb != null) {
			thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, thumbPressHandler);
			thumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, thumbDragHandler);
			group.getChildren().add(index >= 0 ? index : group.getChildren().size() - 1, thumb);
		}
	}

	/**
	 * If the popup is not null, stops the release timer and shows the popup with a fade in animation.
	 */
	protected void showPopup() {
		if (popup == null) {
			return;
		}

		releaseTimer.stop();
		AnimationUtils.SequentialBuilder.build()
				.add(PauseBuilder.build().setDuration(Duration.ONE).setOnFinished(event -> popup.setVisible(true)).getAnimation())
				.add(new KeyFrame(Duration.millis(200), new KeyValue(popup.opacityProperty(), 1.0, Interpolator.EASE_IN)))
				.getAnimation()
				.play();
	}

	/**
	 * If the popup is not null, hides the popup with a fade out animation.
	 */
	protected void hidePopup() {
		if (popup == null) {
			return;
		}

		AnimationUtils.SequentialBuilder.build()
				.add(new KeyFrame(Duration.millis(200), new KeyValue(popup.opacityProperty(), 0.0, Interpolator.EASE_OUT)))
				.setOnFinished(event -> popup.setVisible(false))
				.getAnimation()
				.play();
	}

	/**
	 * Responsible for building the track and the bars for the slider.
	 */
	protected Rectangle buildRectangle(String styleClass) {
		Rectangle rectangle = new Rectangle();
		rectangle.getStyleClass().setAll(styleClass);
		rectangle.setStroke(Color.TRANSPARENT);
		rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
		rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
		rectangle.setStrokeType(StrokeType.INSIDE);
		rectangle.setStrokeWidth(0);
		return rectangle;
	}

	/**
	 * Responsible for creating the slider's major ticks.
	 */
	protected Node buildTick() {
		return new MFXFontIcon("mfx-circle", 4);
	}

	/**
	 * Checks if the pressed key is a valid increase key.
	 * <p>
	 * UP or RIGHT respectively for VERTICAL and HORIZONTAL orientations.
	 */
	private boolean isIncreaseKey(KeyEvent event) {
		MFXSlider slider = getSkinnable();

		return (event.getCode() == KeyCode.UP && slider.getOrientation() == Orientation.VERTICAL) ||
				(event.getCode() == KeyCode.RIGHT && slider.getOrientation() == Orientation.HORIZONTAL);
	}

	/**
	 * Checks if the pressed key is a valid decrease key.
	 * <p>
	 * DOWN or LEFT respectively for VERTICAL and HORIZONTAL orientations.
	 */
	private boolean isDecreaseKey(KeyEvent event) {
		MFXSlider slider = getSkinnable();

		return (event.getCode() == KeyCode.DOWN && slider.getOrientation() == Orientation.VERTICAL) ||
				(event.getCode() == KeyCode.LEFT && slider.getOrientation() == Orientation.HORIZONTAL);
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Math.max(100, leftInset + bar.prefWidth(getSkinnable().getWidth()) + rightInset);
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Math.max(6, bar.prefHeight(width)) + topInset + bottomInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public void dispose() {
		super.dispose();

		thumb.removeEventHandler(MouseEvent.MOUSE_PRESSED, thumbPressHandler);
		thumb.removeEventHandler(MouseEvent.MOUSE_DRAGGED, thumbDragHandler);
		thumbPressHandler = null;
		thumbDragHandler = null;

		track.removeEventHandler(MouseEvent.MOUSE_PRESSED, trackPressedHandler);
		trackPressedHandler = null;

		releaseTimer = null;
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		super.layoutChildren(x, y, w, h);

		layoutData.update(true);
		thumb.relocate(layoutData.thumbX, layoutData.thumbY);
		bar.relocate(layoutData.barX, 0);
		bar.setWidth(Math.abs(layoutData.barW));

		ticksAxis.resize(w, h);
	}

	//================================================================================
	// Support Classes
	//================================================================================

	/**
	 * Support class to the slider's skin. It helps manage/update info about layout such as:
	 * the x coordinate at zero value (or min if the min is greater than 0, or the max is lesser than 0, or the slider is se
	 * to be non-bidirectional), the thumb x and y coordinates, the bar width and bar x coordinate, the major ticks data (see {@link TickData})
	 * and their y coordinate which is the same for all of them.
	 */
	protected class LayoutData {
		private double zeroPos;
		private double thumbX;
		private double thumbY;
		private double barW;
		private double barX;

		private final ObservableList<TickData> ticksData = FXCollections.observableArrayList();
		private double ticksY;

		/**
		 * Updated all the layout variables of this class. If the isFullUpdate parameter is false the
		 * zeroPos is not recomputed. It also manages the {@link MFXSlider#bidirectionalProperty()} feature.
		 * <p></p>
		 * One thing I'm excited about this system is how the coordinates are computed. We heavily make use
		 * of {@link NumberUtils#mapOneRangeToAnother(double, NumberRange, NumberRange, int)}. The sliders values
		 * can be negative or be way greater than the slider's width, that's why the slider's value should be mapped
		 * from the min-max range to the width range that goes from 0 to slider.getWidth().
		 */
		public void update(boolean isFullUpdate) {
			MFXSlider slider = getSkinnable();

			boolean ignoreBidirectional = slider.getMin() > 0 || slider.getMax() < 0 || !slider.isBidirectional();
			if (isFullUpdate) {
				double val;
				if (ignoreBidirectional) {
					val = slider.getMin();
				} else {
					val = 0;
				}

				zeroPos = NumberUtils.mapOneRangeToAnother(
						val,
						NumberRange.of(slider.getMin(), slider.getMax()),
						NumberRange.of(0.0, slider.getWidth()),
						2
				);
				zeroPos = NumberUtils.clamp(zeroPos, 0, slider.getWidth());
			}

			thumbX = snapPositionX(
					NumberUtils.mapOneRangeToAnother(
							slider.getValue(),
							NumberRange.of(slider.getMin(), slider.getMax()),
							NumberRange.of(0.0, slider.getWidth()),
							2
					) - halfThumbWidth());
			thumbY = snapPositionY(-halfThumbHeight() + (slider.getHeight() / 2));

			if (!slider.isBidirectional() || ignoreBidirectional) {
				barW = thumbX - zeroPos + (halfThumbWidth() * 3);
				barX = zeroPos - halfThumbWidth();
			} else {
				barW = slider.getValue() < 0 ? thumbX - zeroPos - halfThumbWidth() : thumbX - zeroPos + (halfThumbWidth() * 3);
				barX = slider.getValue() < 0 ? zeroPos + barW + halfThumbWidth() : zeroPos - halfThumbWidth();
			}
		}

		/**
		 * Builds the major ticks and sets their style class to "tick-even" or "tick-odd" according to their
		 * index in the list.
		 * <p></p>
		 * After building the ticks and their layout data ({@link TickData}, calls {@link #positionTicks()}.
		 */
		public void updateTicksData() {
			MFXSlider slider = getSkinnable();

			List<Double> ticksX = ticksAxis.getTickMarks().stream()
					.map(TickMark::getPosition)
					.collect(Collectors.toList());

			if (!ticksX.stream().allMatch(d -> d == 0)) {
				ticksGroup.getChildren().removeAll(getTicks());
				ticksData.clear();

				ObservableList<TickMark<Number>> tickMarks = ticksAxis.getTickMarks();
				for (int i = 0; i < tickMarks.size(); i++) {
					TickMark<Number> tickMark = ticksAxis.getTickMarks().get(i);
					TickData tickData = new TickData();
					tickData.tick = buildTick();
					tickData.tick.getStyleClass().setAll(NumberUtils.isEven(i) ? "tick-even" : "tick-odd");
					tickData.tickVal = (double) tickMark.getValue();
					tickData.x = snapPositionX(ticksX.get(i) - (tickData.halfTickWidth() / 1.5));
					ticksData.add(tickData);

					if (i == tickMarks.size() - 1) {
						tickData.x -= tickData.halfTickWidth();
					}
				}
				ticksY = snapPositionY(-ticksData.get(0).halfTickHeight() + (slider.getHeight() / 2));
				positionTicks();
			}
		}

		/**
		 * If the {@link MFXSlider#showMajorTicksProperty()} is set to false, does nothing.
		 * <p></p>
		 * For each previously built {@link TickData} adds the tick to the ticks container and sets their position,
		 * if {@link MFXSlider#showTicksAtEdgesProperty()} is set to false the ticks that represent the min and max values
		 * of the slider are not added.
		 */
		public void positionTicks() {
			MFXSlider slider = getSkinnable();
			if (!slider.isShowMajorTicks()) {
				return;
			}

			for (int i = 0; i < ticksData.size(); i++) {
				TickMark<Number> tickMark = ticksAxis.getTickMarks().get(i);
				TickData tickData = ticksData.get(i);

				if (!slider.isShowTicksAtEdges() &&
						((double) tickMark.getValue() == slider.getMax() || (double) tickMark.getValue() == slider.getMin())
				) {
					continue;
				}

				ticksGroup.getChildren().add(tickData.tick);
				tickData.tick.relocate(tickData.x, ticksY);
			}
		}

		/**
		 * Gets the current slider's value, then creates a list with the ticks value obtained by mapping the {@link TickData} list
		 * with {@link TickData#getTickVal()}, calls {@link NumberUtils#closestValueTo(double, List)}.
		 *
		 * @return the closest value from the current value and the list of the ticks values
		 */
		public double findNearestTick() {
			MFXSlider slider = getSkinnable();

			double currVal = slider.getValue();
			return NumberUtils.closestValueTo(currVal, ticksData.stream().map(TickData::getTickVal).collect(Collectors.toList()));
		}

		/**
		 * Returns all the ticks by mapping the {@link TickData} list with {@link TickData#getTick()}.
		 */
		public List<Node> getTicks() {
			return ticksData.stream().map(TickData::getTick).collect(Collectors.toList());
		}

		/**
		 * @return half the width of the thumb
		 */
		public double halfThumbWidth() {
			return thumb.prefWidth(-1) / 2;
		}

		/**
		 * @return half the height of the thumb
		 */
		public double halfThumbHeight() {
			return thumb.prefHeight(-1) / 2;
		}
	}

	/**
	 * Support class to the slider's skin. It helps manage the popup, by handling {@link MFXSlider#popupSupplierProperty()} changes,
	 * computing it's position and it's rotation according to the current orientation and side.
	 * <p></p>
	 * The class has three bindings fields: for the layout x (xBinding), layout y (yBinding) and rotation (rBinding).
	 */
	@SuppressWarnings("FieldCanBeLocal")
	protected class PopupManager {
		private DoubleBinding xBinding;
		private DoubleBinding yBinding;
		private DoubleBinding rBinding;

		/**
		 * If the popup is not null, re-creates the xBinding (calls {@link #computeXPos()}) which depends on the
		 * thumb's x position, popup's width, thumb supplier, orientation and popup side properties; re-creates the yBinding
		 * (calls {@link #computeYPos()}) which depends on the thumb's y position, popup's height, thumb supplier, orientation and popup side properties;
		 * re-creates the rBinding (calls {@link #computeRotate()}) which depends on the slider's orientation and popup side properties.
		 * <p>
		 * Once the bindings are re-built the popup's layoutX, layoutY and rotate properties are bound to them.
		 */
		private void initPopup() {
			MFXSlider slider = getSkinnable();
			if (popup == null) {
				return;
			}

			xBinding = Bindings.createDoubleBinding(
					this::computeXPos,
					thumb.layoutXProperty(), popup.widthProperty(), slider.thumbSupplierProperty(), slider.orientationProperty(), slider.popupSideProperty()
			);
			yBinding = Bindings.createDoubleBinding(
					this::computeYPos,
					thumb.layoutYProperty(), popup.heightProperty(), slider.thumbSupplierProperty(), slider.orientationProperty(), slider.popupSideProperty()
			);
			rBinding = Bindings.createDoubleBinding(
					this::computeRotate,
					slider.orientationProperty(), slider.popupSideProperty()
			);

			popup.rotateProperty().bind(rBinding);
			popup.layoutXProperty().bind(xBinding);
			popup.layoutYProperty().bind(yBinding);
		}

		/**
		 * Responsible for computing the popup's x position.
		 * <p>
		 * This takes into account the slider's orientation and the popup side properties.
		 */
		private double computeXPos() {
			MFXSlider slider = getSkinnable();

			double x;
			if (slider.getOrientation() == Orientation.HORIZONTAL) {
				x = thumb.getLayoutX() - ((popup.getWidth() - layoutData.halfThumbWidth() * 2) / 2);
				x = slider.getPopupSide() == SliderPopupSide.DEFAULT ? x : x - 1;
			} else {
				x = thumb.getLayoutX() - (popup.getHeight() / 2) + (layoutData.halfThumbWidth() / 2) + 1;
			}

			return snapPositionX(x);
		}

		/**
		 * Responsible for computing the popup's y position.
		 * <p>
		 * This takes into account the slider's orientation and the popup side properties.
		 */
		private double computeYPos() {
			MFXSlider slider = getSkinnable();

			double y;
			if (slider.getOrientation() == Orientation.HORIZONTAL) {
				if (slider.getPopupSide() == SliderPopupSide.DEFAULT) {
					y = -(popup.getHeight() + layoutData.halfThumbHeight() + slider.getPopupPadding());
				} else {
					y = slider.getHeight() + layoutData.halfThumbHeight() + slider.getPopupPadding();
				}
			} else {
				if (slider.getPopupSide() == SliderPopupSide.DEFAULT) {
					y = -(popup.getWidth() + layoutData.halfThumbHeight() + (slider.getPopupPadding() / 1.5));
				} else {
					y = (slider.getHeight() * 1.5) + layoutData.halfThumbHeight() + slider.getPopupPadding();
				}
			}

			return snapPositionY(y);
		}

		/**
		 * Responsible for computing the popup's rotation angle.
		 * <p>
		 * This takes into account the slider's orientation and the popup side properties.
		 */
		private double computeRotate() {
			MFXSlider slider = getSkinnable();

			if (slider.getOrientation() == Orientation.HORIZONTAL && slider.getPopupSide() == SliderPopupSide.OTHER_SIDE) {
				return 180;
			}

			if (slider.getOrientation() == Orientation.VERTICAL) {
				return slider.getPopupSide() == SliderPopupSide.DEFAULT ? 90 : -90;
			}

			return 0;
		}
	}

	/**
	 * Support class to the {@link LayoutData} class, simple bean which contains info
	 * about the slider's major ticks such as: the tick Node, the value represented by the tick and its x position.
	 */
	protected static class TickData {
		private Node tick;
		private double tickVal;
		private double x;

		/**
		 * @return the tick Node
		 */
		public Node getTick() {
			return tick;
		}

		/**
		 * @return the value represented by the tick
		 */
		public double getTickVal() {
			return tickVal;
		}

		/**
		 * @return the tick's x position
		 */
		public double getX() {
			return x;
		}

		/**
		 * @return half the tick's height
		 */
		public double halfTickHeight() {
			return tick == null ? 0 : tick.prefHeight(-1) / 2;
		}

		/**
		 * @return half the tick's width
		 */
		public double halfTickWidth() {
			return tick == null ? 0 : tick.prefWidth(-1) / 2;
		}
	}
}
