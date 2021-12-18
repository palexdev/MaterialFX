package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.BoundTextField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Skin associated with every {@link MFXTextField} by default.
 * <p>
 * This skin is mainly responsible for managing features such as the
 * leading and trailing icons, the floating text and the characters limit.
 * <p></p>
 * To avoid reinventing the whole text field from scratch this skin makes use of
 * {@link BoundTextField}, so it is basically a wrapper for a JavaFX's TextField.
 */
public class MFXTextFieldSkin extends SkinBase<MFXTextField> {
	//================================================================================
	// Properties
	//================================================================================
	private final HBox container;
	private final StackPane textContainer;
	private final Label floatingText;
	private final BoundTextField field;

	private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");

	private final ReadOnlyBooleanWrapper floating;
	private final double scaleMultiplier = 0.85;
	private final Scale scale = Transform.scale(1, 1, 0, 0);
	private final Translate translate = Transform.translate(0, 0);
	private boolean init = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTextFieldSkin(MFXTextField textField, ReadOnlyBooleanWrapper floating) {
		super(textField);
		this.floating = floating;

		floatingText = new Label();
		floatingText.textProperty().bind(textField.floatingTextProperty());
		floatingText.getStyleClass().setAll("floating-text");
		floatingText.getTransforms().addAll(scale, translate);

		field = new BoundTextField(textField);
		if (textField.getFloatMode() == FloatMode.INLINE) {
			StackPane.setAlignment(field, Pos.BOTTOM_LEFT);
		} else if (textField.getFloatMode() == FloatMode.DISABLED) {
			floatingText.setVisible(false);
		}

		textContainer = new StackPane(floatingText, field) {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				double floatingTextY = floatingText.getBoundsInParent().getMinY();
				if (!init && floatingTextY >= 0) initText();
			}
		};
		textContainer.setAlignment(Pos.CENTER_LEFT);

		container = new HBox(textContainer);
		container.setAlignment(Pos.CENTER_LEFT);
		container.spacingProperty().bind(textField.graphicTextGapProperty());
		if (textField.getLeadingIcon() != null) container.getChildren().add(0, textField.getLeadingIcon());
		if (textField.getTrailingIcon() != null) container.getChildren().add(textField.getTrailingIcon());

		getChildren().add(container);
		updateTextColor(textField.getTextFill());
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Handles the focus, the floating text, the selected text, the character limit,
	 * the icons, and the disabled state.
	 */
	private void addListeners() {
		MFXTextField textField = getSkinnable();

		textField.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			field.requestFocus();
			field.deselect();
		});
		field.focusedProperty().addListener((observable, oldValue, newValue) -> {
			pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, newValue);
			if (shouldFloat()) positionText();
		});
		field.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
			if (!textField.isSelectable() && !newValue.isEmpty()) field.deselect();
		});
		field.textProperty().addListener((observable, oldValue, newValue) -> {
			int limit = textField.getTextLimit();
			if (limit == -1) return;

			if (newValue.length() > limit) {
				String s = newValue.substring(0, limit);
				field.setText(s);
			}
		});

		textField.promptTextProperty().addListener((observable, oldValue, newValue) -> {
			if (!textField.getText().isEmpty()) return;
			if (!isFloating() && !newValue.isEmpty()) positionText();
		});

		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!isFloating() && !newValue.isEmpty()) positionText();
		});

		textField.disabledProperty().addListener((observable, oldValue, newValue) -> handleDisabled(newValue));

		textField.borderSpacingProperty().addListener(invalidated -> repositionText());
		textField.floatModeProperty().addListener((observable, oldValue, newValue) -> {
			switch (newValue) {
				case DISABLED:
					floatingText.setVisible(false);
					StackPane.setAlignment(field, Pos.CENTER_LEFT);
					break;
				case INLINE: {
					StackPane.setAlignment(field, Pos.BOTTOM_LEFT);
					repositionText();
					break;
				}
				case BORDER: {
					StackPane.setAlignment(field, Pos.CENTER_LEFT);
					repositionText();
					break;
				}
			}
		});
		textField.gapProperty().addListener(invalidated -> repositionText());

		textField.leadingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) container.getChildren().remove(oldValue);
			if (newValue != null) container.getChildren().add(0, newValue);
		});
		textField.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) container.getChildren().remove(oldValue);
			if (newValue != null) container.getChildren().add(newValue);
		});
		textField.textFillProperty().addListener((observable, oldValue, newValue) -> updateTextColor(newValue));
	}

	/**
	 * When the control is laid out it's needed to initialize the position of the floating text,
	 * and this is needed only the first time, as sizes and bounds are computed for the first time.
	 */
	protected void initText() {
		MFXTextField textField = getSkinnable();
		if (textField.getFloatMode() == FloatMode.DISABLED) {
			init = true;
			return;
		}

		if (textField.isDisabled()) {
			floatingText.setVisible(false);
			positionText();
			handleDisabled(true);
		} else if (!isFloating() && !shouldFloat()) {
			positionText();
		}
		init = true;
	}

	/**
	 * Responsible for positioning the floating text node.
	 */
	protected void positionText() {
		MFXTextField textField = getSkinnable();
		if (textField.getFloatMode() == FloatMode.DISABLED) return;

		double targetScale = isFloating() ? 1 : scaleMultiplier;
		double targetX = computeTargetX();
		double targetY = computeTargetY();
		switchFloating();

		if (textField.isAnimated()) {
			TimelineBuilder.build()
					.add(
							KeyFrames.of(150, scale.xProperty(), targetScale, Interpolators.INTERPOLATOR_V1),
							KeyFrames.of(150, scale.yProperty(), targetScale, Interpolators.INTERPOLATOR_V1),
							KeyFrames.of(150, translate.xProperty(), targetX, Interpolators.INTERPOLATOR_V1),
							KeyFrames.of(150, translate.yProperty(), targetY, Interpolators.INTERPOLATOR_V1)
					)
					.getAnimation()
					.play();
		} else {
			scale.setX(targetScale);
			scale.setY(targetScale);
			translate.setX(targetX);
			translate.setY(targetY);
		}
	}

	/**
	 * Responsible for repositioning the floating text node when needed.
	 * For example this is called when the gap changes, the border spacing changes or
	 * when the {@link FloatMode} changes.
	 */
	protected void repositionText() {
		floatingText.setVisible(false);
		setFloating(false);

		scale.setX(1);
		scale.setY(1);
		translate.setX(0);
		translate.setY(0);

		// Delay the actual reposition, so that coordinates are settled
		// Also, don't use position text, better not use any animation
		PauseBuilder.build()
				.setDuration(300)
				.setOnFinished(event -> {
					double targetScale = isFloating() ? 1 : scaleMultiplier;
					double targetX = computeTargetX();
					double targetY = computeTargetY();
					switchFloating();
					scale.setX(targetScale);
					scale.setY(targetScale);
					translate.setX(targetX);
					translate.setY(targetY);
					floatingText.setVisible(true);
				})
				.getAnimation()
				.play();
	}

	/**
	 * Computes the x coordinate at which the floating text node will be positioned.
	 */
	protected double computeTargetX() {
		MFXTextField textField = getSkinnable();
		double targetX = 0;
		if (isFloating() || textField.getFloatMode() == FloatMode.INLINE) return targetX;

		if (!isFloating()) {
			double iconWidth = textField.getLeadingIcon() != null ? textField.getLeadingIcon().prefWidth(-1) : 0;
			targetX = textField.getBorderSpacing() - iconWidth - textField.getGraphicTextGap();
		}
		return snapPositionX(targetX);
	}

	/**
	 * Computes the y coordinate at which the floating text node will be positioned.
	 */
	protected double computeTargetY() {
		MFXTextField textField = getSkinnable();
		double targetY = 0;
		if (textField.getFloatMode() == FloatMode.INLINE) {
			if (!isFloating()) {
				targetY = -floatingText.getLayoutY();
			}
		} else {
			if (!isFloating()) {
				double zeroPos = -textContainer.getLayoutY() - (floatingText.getLayoutY() * scaleMultiplier);
				targetY = zeroPos - (floatingText.prefHeight(-1));
			}
		}
		return snapPositionY(targetY);
	}

	/**
	 * Computes whether the floating text node must float or not.
	 */
	private boolean shouldFloat() {
		MFXTextField textField = getSkinnable();
		return !textField.getFloatingText().isBlank() &&
				textField.getText() != null &&
				textField.getText().isEmpty() &&
				textField.getPromptText() != null &&
				textField.getPromptText().isEmpty();
	}

	/**
	 * When the control is disabled the floating text may be hidden in some cases.
	 */
	protected void handleDisabled(boolean disabled) {
		if (disabled) {
			if (isFloating()) {
				if (shouldFloat()) {
					repositionText();
				} else {
					floatingText.setVisible(false);
				}
			}
		}
	}

	/**
	 * Responsible for updating the text's color.
	 * <p>
	 * Simply sets inline styles for "-fx-text-inner-color" and
	 * "-fx-highlight-text-fill" on the actual TextField.
	 */
	protected void updateTextColor(Color color) {
		String colorString = ColorUtils.rgba(color);
		field.setStyle(
				"-fx-text-inner-color: " + colorString + ";\n" +
						"-fx-highlight-text-fill: " + colorString + ";\n"
		);
	}

	public boolean isFloating() {
		return floating.get();
	}

	public void setFloating(boolean floating) {
		this.floating.set(floating);
	}

	public void switchFloating() {
		this.floating.set(!isFloating());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXTextField textField = getSkinnable();
		Node leadingIcon = textField.getLeadingIcon();
		Node trailingIcon = textField.getLeadingIcon();
		double spacing = textField.getGraphicTextGap();

		return leftInset +
				(leadingIcon != null ? leadingIcon.prefWidth(-1) : 0) +
				(leadingIcon != null ? spacing : 0) +
				Math.max(floatingText.prefWidth(-1), field.prefWidth(-1)) +
				(trailingIcon != null ? spacing : 0) +
				(trailingIcon != null ? trailingIcon.prefWidth(-1) : 0) +
				rightInset;
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXTextField textField = getSkinnable();
		Node leadingIcon = textField.getLeadingIcon();
		Node trailingIcon = textField.getTrailingIcon();
		FloatMode floatMode = textField.getFloatMode();

		double iconsHeight = Math.max(leadingIcon != null ? leadingIcon.prefHeight(-1) : 0, trailingIcon != null ? trailingIcon.prefHeight(-1) : 0);
		double textNodesHeight = 0;
		switch (floatMode) {
			case INLINE: {
				textNodesHeight += textField.getGap() + floatingText.prefHeight(-1) + field.prefHeight(-1);
				break;
			}
			case BORDER: {
				textNodesHeight += floatingText.prefHeight(-1) + field.prefHeight(-1);
				break;
			}
			case DISABLED: {
				textNodesHeight = field.prefHeight(-1);
				break;
			}
		}
		return topInset + Math.max(iconsHeight, textNodesHeight) + bottomInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
