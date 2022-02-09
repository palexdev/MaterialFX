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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepper.MFXStepperEvent;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXStepperToggle.MFXStepperToggleEvent;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXStepper}.
 * <p>
 * It is basically a {@link BorderPane} with three sections: top, center, bottom.
 * <p>
 * At the top there is the {@link HBox} that contains the {@code MFXStepperToggles} and the progress bar
 * which is realized by using a group and two rectangles. One rectangle is for the background/track and the other is the progress/bar.
 * The bar is manually adjusted according to the current selected toggle, its width is set using {@link MFXStepperToggle#getGraphicBounds()}
 * (+10 to ensure that there's no white space between the bar and the toggle).
 * <p>
 * At the center there is a {@link StackPane}, it is the content pane namely the node that
 * will contain the content specifies by each stepper toggle. The style class is set to "content-pane".
 * <p>
 * At the bottom there is the {@link HBox} that contains the previous and next buttons. The style class is set to "buttons-box".
 * <p></p>
 * The stepper skin is rather delicate because the progress bar is quite hard to manage since every layout change can
 * potentially break it. The skin updates the layout by adding a listener to the {@link MFXStepper#needsLayoutProperty()}.
 * When it changes the progress must be computed again with {@link #computeProgress()}.
 * A workaround is also needed in case the progress bar is animated and the layout changes. Without the workaround the
 * progress bar layout is re-computed by using the animation so the reposition process is not instantaneous.
 * To fix this annoying UI issue a boolean flag (buttonWasPressed) is set to true only when buttons are pressed and then set to false right after the layout update,
 * so every layout change is done without playing the animation.
 *
 * @see MFXStepperToggle
 */
public class MFXStepperSkin extends SkinBase<MFXStepper> {
	//================================================================================
	// Properties
	//================================================================================
	private final StackPane contentPane;
	private final HBox stepperBar;
	private final HBox buttonsBox;
	private final MFXButton nextButton;
	private final MFXButton previousButton;

	// Progressbar
	private final Group progressBarGroup;
	private final double height = 7;
	private final Rectangle bar;
	private final Rectangle track;
	private Timeline progressAnimation;
	private boolean buttonWasPressed = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXStepperSkin(MFXStepper stepper) {
		super(stepper);

		track = buildRectangle("track");
		track.setHeight(height);
		track.widthProperty().bind(stepper.widthProperty());

		bar = buildRectangle("bar");
		bar.setHeight(height);

		Rectangle clip = new Rectangle();
		clip.setHeight(height);
		clip.widthProperty().bind(stepper.widthProperty());
		clip.arcHeightProperty().bind(stepper.progressBarBorderRadiusProperty());
		clip.arcWidthProperty().bind(stepper.progressBarBorderRadiusProperty());

		progressBarGroup = new Group(track, bar);
		progressBarGroup.setManaged(false);
		progressBarGroup.setClip(clip);

		progressAnimation = new Timeline();
		progressAnimation.setOnFinished(event -> buttonWasPressed = false);

		stepperBar = new HBox(progressBarGroup);
		stepperBar.spacingProperty().bind(stepper.spacingProperty());
		stepperBar.alignmentProperty().bind(stepper.alignmentProperty());
		stepperBar.getChildren().addAll(stepper.getStepperToggles());
		stepperBar.setMinHeight(100);
		stepperBar.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

		progressBarGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
				() -> snapPositionY((stepperBar.getHeight() / 2.0) - (height / 2.0)),
				stepperBar.heightProperty()
		));

		nextButton = new MFXButton(I18N.getOrDefault("stepper.next"));
		nextButton.setManaged(false);
		nextButton.getRippleGenerator().setClipSupplier(() ->
				new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE, 34, 34).build(nextButton)
		);

		previousButton = new MFXButton(I18N.getOrDefault("stepper.previous"));
		previousButton.setManaged(false);
		previousButton.getRippleGenerator().setClipSupplier(() ->
				new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE, 34, 34).build(previousButton)
		);

		buttonsBox = new HBox(64, previousButton, nextButton);
		buttonsBox.getStyleClass().setAll("buttons-box");
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.setMinHeight(50);

		contentPane = new StackPane();
		contentPane.getStyleClass().setAll("content-pane");

		BorderPane container = new BorderPane();
		container.getStylesheets().setAll(stepper.getUserAgentStylesheet());
		container.setTop(stepperBar);
		container.setCenter(contentPane);
		container.setBottom(buttonsBox);
		getChildren().add(container);

		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners and handlers/filters.
	 * <p>
	 * <p> - Adds a filter for FORCE_LAYOUT_UPDATE_EVENT events to updated the layout and the progress bar.
	 * <p> - Adds a filter for MOUSE_PRESSED events to acquire the focus.
	 * <p> - Adds a filter for STATE_CHANGES events to re-compute the progress, {@link #computeProgress()}.
	 * <p> - Adds a listener to the stepper's toggles list, when it is invalidated the stepper state is reset with
	 * {@link MFXStepper#reset()} and the new toggles are placed. For some reason the progress bar width may be
	 * miscalculated so a workaround is needed. A {@link PauseTransition} is played, after 250ms it is requested to
	 * re-compute the layout.
	 * <p> - Adds a listener to the {@link MFXStepper#currentContentProperty()} to update the content pane children.
	 * <p> - Adds a listener to the {@link MFXStepper#lastToggleProperty()} to call {@link #computeProgress()}.
	 * <p> - Specifies the buttons behavior to set the buttonWasPressed to true and call the {@link MFXStepper#next()} and
	 * {@link MFXStepper#previous()} methods.
	 * <p></p>
	 * Calls {@link #manageScene()}.
	 */
	private void setListeners() {
		MFXStepper stepper = getSkinnable();

		stepper.addEventFilter(MFXStepperEvent.FORCE_LAYOUT_UPDATE_EVENT, event -> {
			stepper.requestLayout();
			computeProgress();
		});

		stepper.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> stepper.requestFocus());
		stepper.addEventFilter(MFXStepperToggleEvent.STATE_CHANGED, event -> computeProgress());

		stepper.getStepperToggles().addListener((InvalidationListener) invalidated -> {
			stepper.reset();
			stepperBar.getChildren().setAll(stepper.getStepperToggles());
			stepperBar.getChildren().add(0, progressBarGroup);
			stepper.next();

			AnimationUtils.PauseBuilder.build()
					.setDuration(250)
					.setOnFinished(event -> stepper.requestLayout())
					.getAnimation()
					.play();
		});
		stepper.currentContentProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				contentPane.getChildren().setAll(newValue);
			} else {
				contentPane.getChildren().clear();
			}
		});
		stepper.lastToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				computeProgress();
			}
		});

		nextButton.setOnAction(event -> {
			buttonWasPressed = true;
			stepper.next();
		});
		previousButton.setOnAction(event -> {
			buttonWasPressed = true;
			stepper.previous();
		});

		manageScene();
	}

	/**
	 * Responsible for managing the stepper' scene. Adds listeners to the scene to initialize the stepper
	 * by calling {@link MFXStepper#next()} the first time thus selecting the first toggle, to the
	 * {@link MFXStepper#needsLayoutProperty()} to updated the layout and the progress bar.
	 */
	private void manageScene() {
		MFXStepper stepper = getSkinnable();

		Scene scene = stepper.getScene();
		if (scene != null) {
			stepper.next();
		}

		NodeUtils.waitForScene(stepper, () -> {
			if (stepper.getCurrentIndex() == -1) {
				stepper.next();
			}
		}, true, false);

		stepper.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
			if (!buttonWasPressed) {
				stepper.requestLayout();
			}
			computeProgress();
		});
	}

	/**
	 * Responsible for computing the width of the rectangle(bar) used to show the progress.
	 * <p></p>
	 * Three cases are evaluated:
	 * <p> - The stepper {@link MFXStepper#lastToggleProperty()} is true, so the bar's width is set to the stepper's width.
	 * <p> - The current stepper toggle is not null, so the bar's width is computed as follows:
	 * The toggle's circle bounds are retrieved using {@link MFXStepperToggle#getGraphicBounds()}. The X is computed
	 * as the minX of those Bounds converted from local to parent using {@link Node#localToParent(Bounds)}.
	 * This value, +10 to ensure that there is not white space between the bar and the toggle, will be the bar's width.
	 * <p> - The current stepper toggle is null so the width is set to 0.
	 * <p></p>
	 * The computed values are used by {@link #updateProgressBar(double)}
	 */
	private void computeProgress() {
		MFXStepper stepper = getSkinnable();

		if (stepper.isLastToggle()) {
			updateProgressBar(stepper.getWidth());
			return;
		}

		MFXStepperToggle stepperToggle = stepper.getCurrentStepperNode();
		if (stepperToggle != null) {
			Bounds bounds = stepperToggle.getGraphicBounds();
			if (bounds != null) {
				double minX = snapSizeX(stepperToggle.localToParent(bounds).getMinX());
				updateProgressBar(minX + 10);
			}
		} else {
			updateProgressBar(0);
		}
	}

	/**
	 * Sets the bar's width property to the given value.
	 * If the {@link MFXStepper#animatedProperty()} or the buttonWasPressed flag are false
	 * then the properties are updated immediately (without the animation). Otherwise they are updated by a timeline.
	 */
	private void updateProgressBar(double width) {
		MFXStepper stepper = getSkinnable();
		if (!stepper.isAnimated() || !buttonWasPressed) {
			bar.setWidth(width);
			buttonWasPressed = false;
			return;
		}

		KeyFrame kf = new KeyFrame(Duration.millis(stepper.getAnimationDuration()), new KeyValue(bar.widthProperty(), width, MFXAnimationFactory.INTERPOLATOR_V2));
		progressAnimation.getKeyFrames().setAll(kf);
		progressAnimation.playFromStart();
	}

	/**
	 * Responsible for building the track and the bar for the progress bar.
	 */
	protected Rectangle buildRectangle(String styleClass) {
		MFXStepper stepper = getSkinnable();

		Rectangle rectangle = new Rectangle();
		rectangle.getStyleClass().setAll(styleClass);
		rectangle.setStroke(Color.TRANSPARENT);
		rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
		rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
		rectangle.setStrokeType(StrokeType.INSIDE);
		rectangle.setStrokeWidth(0);
		rectangle.arcHeightProperty().bind(stepper.progressBarBorderRadiusProperty());
		rectangle.arcWidthProperty().bind(stepper.progressBarBorderRadiusProperty());
		return rectangle;
	}

	//================================================================================
	// Override Methods
	//================================================================================

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Math.max(super.computeMinWidth(height, topInset, leftInset, bottomInset, rightInset) + (getSkinnable().getExtraSpacing() * 2), 300);
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return topInset + stepperBar.getHeight() + buttonsBox.getHeight() + bottomInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (progressAnimation.getStatus() != Animation.Status.STOPPED) {
			progressAnimation.stop();
		}
		progressAnimation = null;
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		super.layoutChildren(x, y, w, h);

		progressBarGroup.resize(w, height);

		double bw = 125;
		double bh = 34;
		double pbx = snapPositionX(15);
		double nbx = snapPositionX(w - bw - 15);
		double by = snapPositionY((buttonsBox.getHeight() / 2.0) - (bh / 2.0));

		previousButton.resizeRelocate(pbx, by, bw, bh);
		nextButton.resizeRelocate(nbx, by, bw, bh);
	}
}
