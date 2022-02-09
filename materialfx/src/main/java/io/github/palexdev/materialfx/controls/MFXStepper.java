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
import io.github.palexdev.materialfx.controls.MFXStepperToggle.MFXStepperToggleEvent;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import io.github.palexdev.materialfx.skins.MFXStepperSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.materialfx.validation.Validated;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is the implementation of a stepper/wizard following material design guidelines in JavaFX.
 * <p></p>
 * Steppers display progress through a sequence of logical and numbered steps.
 * They may also be used for navigation.
 * <p></p>
 * Every stepper has a number of {@code MFXStepperToggles} that should be added to the list
 * after instantiating the stepper. If the list is changed after the stepper has already been laid out
 * then a {@link #reset()} attempt is made.
 * <p></p>
 * The stepper has two properties to represent the current step index and the progress. The progress
 * is computed as the number of COMPLETED toggles divided by the number of toggles. The progress is
 * updated automatically, can be also forced by calling {@link #updateProgress()}.
 * <p></p>
 * <b>NOTE:</b> the stepper allows you to change the toggles even after it is already shown, it has been
 * tested and it seems to work well. However, the stepper is intended to be a "static" control, that means
 * you should plan ahead of time what toggles to place and their content.
 *
 * @see MFXStepperToggle
 */
public class MFXStepper extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXStepper> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-stepper";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXStepper.css");

	private final ObservableList<MFXStepperToggle> stepperToggles = FXCollections.observableArrayList();
	private final DoubleProperty animationDuration = new SimpleDoubleProperty(700.0);
	private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
	private final ReadOnlyIntegerWrapper currentIndex = new ReadOnlyIntegerWrapper(-1);
	private final ReadOnlyObjectWrapper<Node> currentContent = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyBooleanWrapper lastToggle = new ReadOnlyBooleanWrapper(false);
	private boolean enableContentValidationOnError = true;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXStepper() {
		this(new ArrayList<>());
	}

	public MFXStepper(List<MFXStepperToggle> stepperToggles) {
		setStepperToggles(stepperToggles);
		initialize();
	}

	private void initialize() {
		getStyleClass().setAll(STYLE_CLASS);
		setMinHeight(400);
		addListeners();
	}

	/**
	 * Adds an event handler that listens for {@link MFXStepperToggleEvent#STATE_CHANGED} events to
	 * update the progress property.
	 */
	private void addListeners() {
		addEventHandler(MFXStepperToggleEvent.STATE_CHANGED, event -> updateProgress());
	}


	/**
	 * Goes to the next toggle if the validator's state is valid and
	 * updates the {@link #currentIndexProperty()} accordingly.
	 * <p></p>
	 * Special case: if the last toggle is already selected then the progress bar
	 * reaches the 100%.
	 * <p></p>
	 * This method is also responsible for updating the toggles' state
	 * and firing the following events: {@link MFXStepperEvent#BEFORE_NEXT_EVENT}, {@link MFXStepperEvent#NEXT_EVENT},
	 * {@link MFXStepperEvent#LAST_NEXT_EVENT}, {@link MFXStepperEvent#VALIDATION_FAILED_EVENT}.
	 */
	public void next() {
		if (stepperToggles.isEmpty()) {
			return;
		}
		fireEvent(MFXStepperEvent.BEFORE_NEXT_EVENT);

		int currentIndex = getCurrentIndex();
		if (currentIndex == -1) {
			MFXStepperToggle first = stepperToggles.get(0);
			first.setState(StepperToggleState.SELECTED);
			this.currentIndex.set(0);
			setCurrentContent(first.getContent());
			return;
		}

		MFXStepperToggle current = stepperToggles.get(currentIndex);
		if (!current.isValid()) {
			if (current.getState() != StepperToggleState.ERROR) {
				current.setState(StepperToggleState.ERROR);
			}
			if (isEnableContentValidationOnError()) {
				forceContentValidation();
			}
			fireEvent(MFXStepperEvent.VALIDATION_FAILED_EVENT);
			return;
		}

		if (currentIndex < stepperToggles.size() - 1) {
			MFXStepperToggle next = stepperToggles.get(currentIndex + 1);
			current.setState(StepperToggleState.COMPLETED);
			next.setState(StepperToggleState.SELECTED);
			this.currentIndex.set(currentIndex + 1);
			setCurrentContent(next.getContent());
			fireEvent(MFXStepperEvent.NEXT_EVENT);
		} else {
			setLastToggle(true);
			current.setState(StepperToggleState.COMPLETED);
			fireEvent(MFXStepperEvent.LAST_NEXT_EVENT);
		}
	}

	/**
	 * Goes to the previous toggle and updates the
	 * {@link #currentIndexProperty()} accordingly.
	 * <p></p>
	 * This method is also responsible for updating the toggles' state
	 * and firing the following events: {@link MFXStepperEvent#BEFORE_PREVIOUS_EVENT}, {@link MFXStepperEvent#PREVIOUS_EVENT}.
	 */
	public void previous() {
		if (stepperToggles.isEmpty()) {
			return;
		}
		fireEvent(MFXStepperEvent.BEFORE_PREVIOUS_EVENT);

		int currentIndex = getCurrentIndex();
		if (isLastToggle()) {
			setLastToggle(false);
			MFXStepperToggle last = getCurrentStepperNode();
			last.setState(StepperToggleState.SELECTED);
			return;
		}

		if (currentIndex == -1) {
			MFXStepperToggle stepperNode = stepperToggles.get(0);
			stepperNode.setState(StepperToggleState.SELECTED);
			this.currentIndex.set(0);
			setCurrentContent(stepperNode.getContent());
			return;
		}
		if (currentIndex > 0) {
			MFXStepperToggle current = stepperToggles.get(currentIndex);
			MFXStepperToggle previous = stepperToggles.get(currentIndex - 1);
			current.setState(StepperToggleState.NONE);
			previous.setState(StepperToggleState.SELECTED);
			this.currentIndex.set(currentIndex - 1);
			setCurrentContent(previous.getContent());
			fireEvent(MFXStepperEvent.PREVIOUS_EVENT);
		}
	}

	private void forceContentValidation() {
		if (getCurrentContent() == null) {
			return;
		}

		List<MFXValidator> validators = new ArrayList<>();
		Node currentContent = getCurrentContent();
		if (currentContent instanceof Validated) {
			Validated validated = (Validated) currentContent;
			if (validated.getValidator() != null) {
				validators.add(validated.getValidator());
			}
		} else if (currentContent instanceof Parent) {
			List<Node> allChildren = NodeUtils.getAllNodes((Parent) currentContent);
			allChildren.stream()
					.filter(node -> node instanceof Validated)
					.map(node -> ((Validated) node).getValidator())
					.filter(Objects::nonNull)
					.forEach(validators::add);
		}
		validators.forEach(MFXValidator::update);
	}

	/**
	 * Resets the stepper and all its toggles to the initial state.
	 */
	public void reset() {
		setLastToggle(false);
		currentIndex.set(-1);
		stepperToggles.forEach(stepperToggle -> stepperToggle.setState(StepperToggleState.NONE));
	}

	/**
	 * @return the current selected toggle by using the {@link #currentIndexProperty()},
	 * or null if an exception is captured.
	 */
	public MFXStepperToggle getCurrentStepperNode() {
		try {
			return stepperToggles.get(getCurrentIndex());
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	/**
	 * Updates the {@link #progressProperty()} by counting the number of
	 * COMPLETED toggles and dividing it by the total number of controls, so
	 * the progress values go from 0.0 to 1.0.
	 */
	public void updateProgress() {
		double completed = Math.toIntExact(stepperToggles.stream()
				.filter(s -> s.getState() == StepperToggleState.COMPLETED)
				.count());
		this.progress.set(completed / stepperToggles.size());
	}

	/**
	 * @return the stepper's toggles list
	 */
	public ObservableList<MFXStepperToggle> getStepperToggles() {
		return stepperToggles;
	}

	/**
	 * Replaces the stepper's toggles with the specified ones.
	 */
	public void setStepperToggles(List<MFXStepperToggle> stepperToggles) {
		this.stepperToggles.setAll(stepperToggles);
	}

	public double getAnimationDuration() {
		return animationDuration.get();
	}

	/**
	 * Specifies, in milliseconds, the duration of the progress bar animation.
	 */
	public DoubleProperty animationDurationProperty() {
		return animationDuration;
	}

	public void setAnimationDuration(double animationDuration) {
		this.animationDuration.set(animationDuration);
	}

	public double getProgress() {
		return progress.get();
	}

	/**
	 * Specifies the stepper's progress, the number of COMPLETED toggles
	 * divided by the total number of toggles. The values go from 0.0 to 1.0.
	 */
	public ReadOnlyDoubleProperty progressProperty() {
		return progress.getReadOnlyProperty();
	}

	protected void setProgress(double progress) {
		this.progress.set(progress);
	}

	public int getCurrentIndex() {
		return currentIndex.get();
	}

	/**
	 * Specifies the selected toggle position in the toggles list.
	 * The index is updated by {@link #next()} and {@link #previous()} methods.
	 */
	public ReadOnlyIntegerProperty currentIndexProperty() {
		return currentIndex.getReadOnlyProperty();
	}

	protected void setCurrentIndex(int currentIndex) {
		this.currentIndex.set(currentIndex);
	}

	public Node getCurrentContent() {
		return currentContent.get();
	}

	/**
	 * Convenience property that holds the selected toggle content node.
	 * <p>
	 * In case one of the toggles has a {@code null} content the content pane's
	 * children list is cleared.
	 */
	public ReadOnlyObjectProperty<Node> currentContentProperty() {
		return currentContent.getReadOnlyProperty();
	}

	protected void setCurrentContent(Node content) {
		currentContent.set(content);
	}

	public boolean isLastToggle() {
		return lastToggle.get();
	}

	/**
	 * Convenience property that specifies if the last toggle is selected.
	 */
	public ReadOnlyBooleanProperty lastToggleProperty() {
		return lastToggle.getReadOnlyProperty();
	}

	protected void setLastToggle(boolean lastToggle) {
		this.lastToggle.set(lastToggle);
	}

	public boolean isEnableContentValidationOnError() {
		return enableContentValidationOnError;
	}

	/**
	 * Specifies if all the controls that implement {@link Validated} should be
	 * validated when the next button is pressed and the toggle state is ERROR.
	 */
	public void setEnableContentValidationOnError(boolean enableContentValidationOnError) {
		this.enableContentValidationOnError = enableContentValidationOnError;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty spacing = new SimpleStyleableDoubleProperty(
			StyleableProperties.SPACING,
			this,
			"spacing",
			128.0
	);

	private final StyleableDoubleProperty extraSpacing = new SimpleStyleableDoubleProperty(
			StyleableProperties.EXTRA_SPACING,
			this,
			"extraSpacing",
			64.0
	);

	private final StyleableObjectProperty<Pos> alignment = new SimpleStyleableObjectProperty<>(
			StyleableProperties.ALIGNMENT,
			this,
			"alignment",
			Pos.CENTER
	);

	private final StyleableObjectProperty<Paint> baseColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BASE_COLOR,
			this,
			"baseColor",
			Color.web("#7F0FFF")
	);

	private final StyleableObjectProperty<Paint> altColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.ALT_COLOR,
			this,
			"altColor",
			Color.web("BEBEBE")
	);

	private final StyleableDoubleProperty progressBarBorderRadius = new SimpleStyleableDoubleProperty(
			StyleableProperties.BORDER_RADIUS,
			this,
			"progressBarBorderRadius",
			7.0
	);

	private final StyleableObjectProperty<Paint> progressBarBackground = new SimpleStyleableObjectProperty<>(
			StyleableProperties.PROGRESS_BAR_BACKGROUND,
			this,
			"progressBarBackground",
			Color.web("#F8F8FF")
	);

	private final StyleableObjectProperty<Paint> progressColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.PROGRESS_COLOR,
			this,
			"progressColor",
			Color.web("#7F0FFF")
	);

	private final StyleableBooleanProperty animated = new SimpleStyleableBooleanProperty(
			StyleableProperties.PROGRESS_BAR_ANIMATED,
			this,
			"animated",
			true
	);

	public double getSpacing() {
		return spacing.get();
	}

	/**
	 * Specifies the spacing between toggles.
	 */
	public StyleableDoubleProperty spacingProperty() {
		return spacing;
	}

	public void setSpacing(double spacing) {
		this.spacing.set(spacing);
	}

	public double getExtraSpacing() {
		return extraSpacing.get();
	}

	/**
	 * Specifies the extra length (at the start and at the end) of the progress bar.
	 */
	public StyleableDoubleProperty extraSpacingProperty() {
		return extraSpacing;
	}

	public void setExtraSpacing(double extraSpacing) {
		this.extraSpacing.set(extraSpacing);
	}

	public Pos getAlignment() {
		return alignment.get();
	}

	/**
	 * Specifies the alignment of the toggles. Steppers are usually centered though.
	 */
	public StyleableObjectProperty<Pos> alignmentProperty() {
		return alignment;
	}

	public void setAlignment(Pos alignment) {
		this.alignment.set(alignment);
	}

	public Paint getBaseColor() {
		return baseColor.get();
	}

	/**
	 * Specifies the base color of the stepper.
	 * <p>
	 * In the default CSS file this property affects: the progress color,
	 * the buttons background and borders when focused, also used for the buttons' ripple generators.
	 * <p>
	 * In the {@link MFXStepperToggle} CSS file this property affects:
	 * <p> - State NONE: the icon color
	 * <p> - State SELECTED: the background and borders, the label's text fill
	 * <p> - State COMPLETED: the borders
	 * <p></p>
	 * Default is: #7F0FFF (purple)
	 */
	public StyleableObjectProperty<Paint> baseColorProperty() {
		return baseColor;
	}

	public void setBaseColor(Paint baseColor) {
		this.baseColor.set(baseColor);
	}

	public Paint getAltColor() {
		return altColor.get();
	}

	/**
	 * Specifies the secondary color of the stepper.
	 * <p>
	 * In the {@link MFXStepperToggle} CSS file this property affects:
	 * <p> - State NONE: the label's text fill
	 * <p> - State COMPLETED: the icon's color, the label's text fill
	 * <p></p>
	 * Default is: #BEBEBE (a light gray)
	 */
	public StyleableObjectProperty<Paint> altColorProperty() {
		return altColor;
	}

	public void setAltColor(Paint altColor) {
		this.altColor.set(altColor);
	}

	public double getProgressBarBorderRadius() {
		return progressBarBorderRadius.get();
	}

	/**
	 * Specifies the borders radius of the progress bar.
	 */
	public StyleableDoubleProperty progressBarBorderRadiusProperty() {
		return progressBarBorderRadius;
	}

	public void setProgressBarBorderRadius(double progressBarBorderRadius) {
		this.progressBarBorderRadius.set(progressBarBorderRadius);
	}

	public Paint getProgressBarBackground() {
		return progressBarBackground.get();
	}

	/**
	 * Specifies the progress bar background color (NOT THE PROGRESS COLOR).
	 *
	 * <p></p>
	 * In the {@link MFXStepperToggle} CSS file this property affects:
	 * <p> - State NONE: the toggle's background and borders. This is because
	 * if the toggle's background is transparent you will see the progress bar underneath them
	 * <p></p>
	 * Default is: #F8F8FF (almost white)
	 */
	public StyleableObjectProperty<Paint> progressBarBackgroundProperty() {
		return progressBarBackground;
	}

	public void setProgressBarBackground(Paint progressBarBackground) {
		this.progressBarBackground.set(progressBarBackground);
	}

	public Paint getProgressColor() {
		return progressColor.get();
	}

	/**
	 * Specifies the progress color.
	 * <p></p>
	 * Default is: #7F0FFF (it's set to be the same as the base color)
	 */
	public StyleableObjectProperty<Paint> progressColorProperty() {
		return progressColor;
	}

	public void setProgressColor(Paint progressColor) {
		this.progressColor.set(progressColor);
	}

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies if the progress bar should be animated or not.
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated.set(animated);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXStepper, Number> SPACING =
				FACTORY.createSizeCssMetaData(
						"-mfx-spacing",
						MFXStepper::spacingProperty,
						128.0
				);

		private static final CssMetaData<MFXStepper, Number> EXTRA_SPACING =
				FACTORY.createSizeCssMetaData(
						"-mfx-extra-spacing",
						MFXStepper::extraSpacingProperty,
						64.0
				);

		private static final CssMetaData<MFXStepper, Pos> ALIGNMENT =
				FACTORY.createEnumCssMetaData(
						Pos.class,
						"-mfx-alignment",
						MFXStepper::alignmentProperty,
						Pos.CENTER
				);

		private static final CssMetaData<MFXStepper, Paint> BASE_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-base-color",
						MFXStepper::baseColorProperty,
						Color.web("7F0FFF")
				);

		private static final CssMetaData<MFXStepper, Paint> ALT_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-alt-color",
						MFXStepper::altColorProperty,
						Color.web("BEBEBE")
				);

		private static final CssMetaData<MFXStepper, Number> BORDER_RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-bar-borders-radius",
						MFXStepper::progressBarBorderRadiusProperty,
						7.0
				);

		private static final CssMetaData<MFXStepper, Paint> PROGRESS_BAR_BACKGROUND =
				FACTORY.createPaintCssMetaData(
						"-mfx-bar-background",
						MFXStepper::progressBarBackgroundProperty,
						Color.web("#F8F8FF")
				);

		private static final CssMetaData<MFXStepper, Paint> PROGRESS_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-progress-color",
						MFXStepper::progressColorProperty,
						Color.web("#7F0FFF")
				);

		private static final CssMetaData<MFXStepper, Boolean> PROGRESS_BAR_ANIMATED =
				FACTORY.createBooleanCssMetaData(
						"-mfx-bar-animated",
						MFXStepper::animatedProperty,
						true
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Control.getClassCssMetaData(),
					SPACING, EXTRA_SPACING, ALIGNMENT, BASE_COLOR, ALT_COLOR,
					BORDER_RADIUS, PROGRESS_BAR_BACKGROUND, PROGRESS_COLOR, PROGRESS_BAR_ANIMATED
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
		return new MFXStepperSkin(this);
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXStepper.getControlCssMetaDataList();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Events
	//================================================================================

	/**
	 * Events class for MFXSteppers.
	 * <p>
	 * Defines seven new EventTypes:
	 * <p>
	 * - FORCE_LAYOUT_UPDATE_EVENT: if there is a bug with the stepper layout the user can call {@link #forceLayoutUpdate()} to fire this event and update the layout. <p></p>
	 * - BEFORE_NEXT_EVENT: at the start of the {@link MFXStepper#next()} method but after checking if the toggles list is empty. <p></p>
	 * - NEXT_EVENT: when the {@link MFXStepper#next()} method is called and the index property is updated. <p></p>
	 * - BEFORE_PREVIOUS_EVENT: at the start of the {@link MFXStepper#previous()} method but after checking if the toggles list is empty. <p></p>
	 * - PREVIOUS_EVENT: when the {@link MFXStepper#previous()} method is called and the index property is updated. <p></p>
	 * - LAST_NEXT_EVENT: when the {@link MFXStepper#next()} method is called and the last toggle is selected/already reached. <p></p>
	 * - VALIDATION_FAILED_EVENT: when the {@link MFXStepper#next()} method is called and the validator's state is invalid. <p></p>
	 * <p>
	 * These events are automatically fired by the control so they should not be fired by users.
	 */
	public static class MFXStepperEvent extends Event {

		public static final EventType<MFXStepperEvent> FORCE_LAYOUT_UPDATE_EVENT = new EventType<>(ANY, "FORCE_LAYOUT_UPDATE_EVENT");
		public static final EventType<MFXStepperEvent> BEFORE_NEXT_EVENT = new EventType<>(ANY, "BEFORE_NEXT_EVENT");
		public static final EventType<MFXStepperEvent> NEXT_EVENT = new EventType<>(ANY, "NEXT_EVENT");
		public static final EventType<MFXStepperEvent> BEFORE_PREVIOUS_EVENT = new EventType<>(ANY, "BEFORE_PREVIOUS_EVENT");
		public static final EventType<MFXStepperEvent> PREVIOUS_EVENT = new EventType<>(ANY, "PREVIOUS_EVENT");
		public static final EventType<MFXStepperEvent> LAST_NEXT_EVENT = new EventType<>(ANY, "LAST_NEXT_EVENT");
		public static final EventType<MFXStepperEvent> VALIDATION_FAILED_EVENT = new EventType<>(ANY, "VALIDATION_FAILED_EVENT");

		public MFXStepperEvent(EventType<? extends Event> eventType) {
			super(eventType);
		}
	}

	private final EventHandlerProperty<MFXStepperEvent> onBeforeNext = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.BEFORE_NEXT_EVENT, get());
		}
	};

	private final EventHandlerProperty<MFXStepperEvent> onNext = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.NEXT_EVENT, get());
		}
	};

	private final EventHandlerProperty<MFXStepperEvent> onBeforePrevious = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.BEFORE_PREVIOUS_EVENT, get());
		}
	};

	private final EventHandlerProperty<MFXStepperEvent> onPrevious = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.PREVIOUS_EVENT, get());
		}
	};

	private final EventHandlerProperty<MFXStepperEvent> onLastNext = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.LAST_NEXT_EVENT, get());
		}
	};

	private final EventHandlerProperty<MFXStepperEvent> onValidationFailed = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(MFXStepperEvent.VALIDATION_FAILED_EVENT, get());
		}
	};

	public EventHandler<MFXStepperEvent> getOnBeforeNext() {
		return onBeforeNext.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#BEFORE_NEXT_EVENT} is fired.
	 */
	public EventHandlerProperty<MFXStepperEvent> onBeforeNextProperty() {
		return onBeforeNext;
	}

	public void setOnBeforeNext(EventHandler<MFXStepperEvent> onBeforeNext) {
		this.onBeforeNext.set(onBeforeNext);
	}

	public EventHandler<MFXStepperEvent> getOnNext() {
		return onNext.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#NEXT_EVENT} is fired.
	 *
	 * @see MFXStepperEvent
	 */
	public EventHandlerProperty<MFXStepperEvent> onNextProperty() {
		return onNext;
	}

	public void setOnNext(EventHandler<MFXStepperEvent> onNext) {
		this.onNext.set(onNext);
	}

	public EventHandler<MFXStepperEvent> getOnBeforePrevious() {
		return onBeforePrevious.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#BEFORE_PREVIOUS_EVENT} is fired.
	 */
	public EventHandlerProperty<MFXStepperEvent> onBeforePreviousProperty() {
		return onBeforePrevious;
	}

	public void setOnBeforePrevious(EventHandler<MFXStepperEvent> onBeforePrevious) {
		this.onBeforePrevious.set(onBeforePrevious);
	}

	public EventHandler<MFXStepperEvent> getOnPrevious() {
		return onPrevious.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#PREVIOUS_EVENT} is fired.
	 *
	 * @see MFXStepperEvent
	 */
	public EventHandlerProperty<MFXStepperEvent> onPreviousProperty() {
		return onPrevious;
	}

	public void setOnPrevious(EventHandler<MFXStepperEvent> onPrevious) {
		this.onPrevious.set(onPrevious);
	}

	public EventHandler<MFXStepperEvent> getOnLastNext() {
		return onLastNext.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#LAST_NEXT_EVENT} is fired.
	 *
	 * @see MFXStepperEvent
	 */
	public EventHandlerProperty<MFXStepperEvent> onLastNextProperty() {
		return onLastNext;
	}

	public void setOnLastNext(EventHandler<MFXStepperEvent> onLastNext) {
		this.onLastNext.set(onLastNext);
	}

	public EventHandler<MFXStepperEvent> getOnValidationFailed() {
		return onValidationFailed.get();
	}

	/**
	 * Specifies the action to perform when a {@link MFXStepperEvent#VALIDATION_FAILED_EVENT} is fired.
	 *
	 * @see MFXStepperEvent
	 */
	public EventHandlerProperty<MFXStepperEvent> onValidationFailedProperty() {
		return onValidationFailed;
	}

	public void setOnValidationFailed(EventHandler<MFXStepperEvent> onValidationFailed) {
		this.onValidationFailed.set(onValidationFailed);
	}

	/**
	 * Convenience method to fire {@link MFXStepperEvent} events.
	 */
	public void fireEvent(EventType<MFXStepperEvent> eventType) {
		fireEvent(new MFXStepperEvent(eventType));
	}

	/**
	 * Forces the layout of the stepper to update.
	 */
	public void forceLayoutUpdate() {
		fireEvent(MFXStepperEvent.FORCE_LAYOUT_UPDATE_EVENT);
	}
}
