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
import io.github.palexdev.materialfx.controls.BoundTextField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import static io.github.palexdev.materialfx.effects.Interpolators.INTERPOLATOR_V1;

/**
 * Skin associated with every {@link MFXTextField} by default.
 * <p>
 * This skin is mainly responsible for managing features such as the
 * leading and trailing icons, the floating text the characters limit and the text fill.
 * <p></p>
 * To avoid reinventing the whole text field from scratch this skin makes use of
 * {@link BoundTextField}, so it is basically a wrapper for a JavaFX's TextField.
 */
public class MFXTextFieldSkin extends SkinBase<MFXTextField> {
	//================================================================================
	// Properties
	//================================================================================
	private final BoundTextField boundField;
	private final Label floatingText;
	private final Label mUnitLabel;

	private static final PseudoClass FOCUS_WITHIN_PSEUDO_CLASS = PseudoClass.getPseudoClass("focus-within");

	private final ObjectProperty<PositionBean> floatingPos = new SimpleObjectProperty<>(PositionBean.of(0, 0));
	private final BooleanExpression floating;
	private final double scaleValue = 0.85;
	private final Scale scale = Transform.scale(scaleValue, scaleValue, 0, 0);
	private final Translate translate = Transform.translate(0, 0);
	private Animation floatAnimation;
	private boolean skipLayout = false; // More accuracy and performance for subsequent layouts

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTextFieldSkin(MFXTextField textField, BoundTextField boundField) {
		super(textField);
		this.boundField = boundField;
		boundField.setManaged(false);

		floatingText = new Label();
		floatingText.getStyleClass().setAll("floating-text");
		floatingText.textProperty().bind(textField.floatingTextProperty());
		floatingText.getTransforms().addAll(scale, translate);
		if (textField.getFloatMode() == FloatMode.DISABLED) floatingText.setVisible(false);

		mUnitLabel = new Label();
		mUnitLabel.getStyleClass().setAll("measure-unit");
		mUnitLabel.textProperty().bind(textField.measureUnitProperty());
		mUnitLabel.visibleProperty().bind(textField.measureUnitProperty().isNotNull().and(textField.measureUnitProperty().isNotEmpty()));

		floating = Bindings.createBooleanBinding(
				() -> getFloatY() != 0,
				floatingPos
		);
		textField.floatingProperty().bind(floating);

		getChildren().setAll(floatingText, boundField, mUnitLabel);

		if (!shouldFloat()) {
			scale.setX(1);
			scale.setY(1);
		}

		if (textField.getLeadingIcon() != null) getChildren().add(textField.getLeadingIcon());
		if (textField.getTrailingIcon() != null) getChildren().add(textField.getTrailingIcon());

		updateTextColor(textField.getTextFill());
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Handles the focus, the floating text, the selected text, the character limit,
	 * the icons and the text fill.
	 */
	private void addListeners() {
		MFXTextField textField = getSkinnable();

		// Event Handling
		textField.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			boundField.requestFocus();
			boundField.deselect();
		});

		// Handle Floating
		floatingPos.addListener((observable, oldValue, newValue) -> handleFloatingText());
		boundField.layoutBoundsProperty().addListener(invalidated -> {
			skipLayout = true;
			textField.requestLayout();
		});
		floatingText.layoutBoundsProperty().addListener(invalidated -> skipLayout = true);
		textField.scaleOnAboveProperty().addListener((observable, oldValue, newValue) -> textField.requestLayout());
		textField.promptTextProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty() && !isFloating()) textField.requestLayout();
		});
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty() && !isFloating()) textField.requestLayout();
		});
		textField.floatModeProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == FloatMode.DISABLED) floatingText.setVisible(false);
			textField.requestLayout();
		});
		textField.floatingTextGapProperty().addListener((observable, oldValue, newValue) -> textField.requestLayout());
		textField.measureUnitGapProperty().addListener((observable, oldValue, newValue) -> textField.requestLayout());
		textField.borderGapProperty().addListener((observable, oldValue, newValue) -> textField.requestLayout());

		// Focus Handling
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> boundField.requestFocus());
		boundField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			textField.requestLayout();
			pseudoClassStateChanged(FOCUS_WITHIN_PSEUDO_CLASS, newValue);
		});

		// Icons
		textField.leadingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) getChildren().remove(oldValue);
			if (newValue != null) getChildren().add(newValue);
		});
		textField.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) getChildren().remove(oldValue);
			if (newValue != null) getChildren().add(newValue);
		});

		// Misc Features
		boundField.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
			if (!textField.isSelectable() && !newValue.isEmpty()) boundField.deselect();
		});
		boundField.textProperty().addListener((observable, oldValue, newValue) -> {
			int limit = textField.getTextLimit();
			if (limit == -1) return;

			if (newValue.length() > limit) {
				boundField.setText(oldValue);
			}
		});
		textField.textFillProperty().addListener((observable, oldValue, newValue) -> updateTextColor(newValue));
	}

	/**
	 * Responsible for positioning the floating text node.
	 */
	private void handleFloatingText() {
		MFXTextField textField = getSkinnable();
		double targetX = getFloatX();
		double targetY = getFloatY();
		double targetScale = shouldFloat() ? scaleValue : 1;
		if (textField.getFloatMode() == FloatMode.ABOVE && !textField.scaleOnAbove()) targetScale = 1;

		if (textField.isAnimated()) {
			if (floatAnimation != null && AnimationUtils.isPlaying(floatAnimation)) {
				floatAnimation.stop();
			}

			floatAnimation = TimelineBuilder.build()
					.add(
							KeyFrames.of(150, scale.xProperty(), targetScale, INTERPOLATOR_V1),
							KeyFrames.of(150, scale.yProperty(), targetScale, INTERPOLATOR_V1),
							KeyFrames.of(150, translate.xProperty(), targetX, INTERPOLATOR_V1),
							KeyFrames.of(150, translate.yProperty(), targetY, INTERPOLATOR_V1)
					)
					.getAnimation();
			floatAnimation.play();
		} else {
			scale.setX(targetScale);
			scale.setY(targetScale);
			translate.setX(targetX);
			translate.setY(targetY);
		}
	}

	/**
	 * Computes whether the floating text node must float or not.
	 */
	private boolean shouldFloat() {
		MFXTextField textField = getSkinnable();
		boolean modeCondition = textField.getFloatMode() == FloatMode.ABOVE;
		boolean floatTextCondition = (textField.getFloatingText() != null && !textField.getFloatingText().isEmpty());
		boolean textCondition = (textField.getText() != null && !textField.getText().isEmpty());
		boolean promptTextCondition = (textField.getPromptText() != null && !textField.getPromptText().isEmpty());
		return (floatTextCondition && textCondition || promptTextCondition) || modeCondition || boundField.isFocused();
	}

	/**
	 * Responsible for updating the text's color.
	 * <p>
	 * Simply sets inline styles for "-fx-text-inner-color" and
	 * "-fx-highlight-text-fill" on the actual TextField.
	 */
	protected void updateTextColor(Color color) {
		String colorString = ColorUtils.rgba(color);
		boundField.setStyle(
				"-fx-text-inner-color: " + colorString + ";\n" + "-fx-highlight-text-fill: " + colorString + ";\n"
		);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXTextField textField = getSkinnable();
		FloatMode floatMode = textField.getFloatMode();
		Node leading = textField.getLeadingIcon();
		Node trailing = textField.getTrailingIcon();
		double iconsMax = topInset + Math.max(
				(leading != null ? leading.prefHeight(-1) : 0),
				(trailing != null ? trailing.prefHeight(-1) : 0)
		) + bottomInset;

		double height = 0;
		switch (floatMode) {
			case DISABLED:
			case ABOVE: {
				height = topInset + boundField.prefHeight(-1) + bottomInset;
				break;
			}
			case BORDER: {
				height = topInset + (floatingText.prefHeight(-1) / 2) + boundField.prefHeight(-1) + bottomInset;
				break;
			}
			case INLINE: {
				double gap = textField.getFloatingTextGap();
				height = topInset + (floatingText.prefHeight(-1) * scaleValue) + gap + boundField.prefHeight(-1) + bottomInset;
				break;
			}
		}
		return Math.max(iconsMax, height);
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXTextField textField = getSkinnable();
		Node leading = textField.getLeadingIcon();
		Node trailing = textField.getTrailingIcon();
		double gap = textField.getGraphicTextGap();
		double mUnitGap = textField.getMeasureUnitGap();
		return leftInset +
				(leading != null ? leading.prefWidth(-1) + gap : 0) +
				Math.max(boundField.prefWidth(-1) + mUnitLabel.prefWidth(-1) + mUnitGap, floatingText.prefWidth(-1)) +
				(trailing != null ? trailing.prefWidth(-1) + gap : 0) +
				rightInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (getSkinnable().getMaxWidth() == Double.MAX_VALUE) return Double.MAX_VALUE;
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (getSkinnable().getMaxHeight() == Double.MAX_VALUE) return Double.MAX_VALUE;
		return getSkinnable().prefHeight(-1);
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXTextField textField = getSkinnable();
		Node leading = textField.getLeadingIcon();
		Node trailing = textField.getTrailingIcon();
		double graphicTextGap = textField.getGraphicTextGap();
		double mUnitGap = textField.getMeasureUnitGap();
		FloatMode floatMode = textField.getFloatMode();
		VPos textVAlignment = (floatMode != FloatMode.INLINE) ? VPos.CENTER : VPos.BOTTOM;
		double scaleValue = (floatMode == FloatMode.ABOVE && !textField.scaleOnAbove()) ? 1 : this.scaleValue;

		// Before positioning the text it's important to layout the leading icon
		// if present so that the actual minX (offsetX) starts after the icon + the specified gap
		// Insets are already included in the x and y variables
		double xOffset = x;
		if (leading != null) {
			PositionBean leadingPos = PositionUtils.computePosition(
					textField,
					leading,
					x, y, w, h, 0,
					Insets.EMPTY,
					HPos.LEFT, VPos.CENTER
			);
			double leadingW = leading.prefWidth(-1);
			double leadingH = leading.prefHeight(-1);
			leading.resizeRelocate(leadingPos.getX(), leadingPos.getY(), leadingW, leadingH);
			xOffset += leadingW + graphicTextGap;
		}

		if (trailing != null) {
			PositionBean trailingPos = PositionUtils.computePosition(
					textField,
					trailing,
					x, y, w, h, 0,
					Insets.EMPTY,
					HPos.RIGHT,
					VPos.CENTER
			);
			double trailingW = trailing.prefWidth(-1);
			double trailingH = trailing.prefHeight(-1);
			trailing.resizeRelocate(trailingPos.getX(), trailingPos.getY(), trailingW, trailingH);
		}

		// The floating text is always positioned at the center of the Pane
		// and then translated to the correct position
		//
		// Exception for the x coordinate in case of FloatMode.ABOVE, the
		// offsetX computed previously is ignored and the floating text is positioned
		// at the start of the Pane
		double floatW = floatingText.prefWidth(-1);
		double floatH = floatingText.prefHeight(-1);
		double floatX = (floatMode == FloatMode.ABOVE) ? 1 : xOffset;
		PositionBean floatPos = PositionUtils.computePosition(
				textField,
				floatingText,
				floatX, y, w, h, 0,
				Insets.EMPTY,
				HPos.LEFT, VPos.CENTER
		);
		floatingText.resizeRelocate(floatPos.getX(), floatPos.getY(), floatW, floatH);

		// Position the Label responsible for showing the measure unit
		double unitW = mUnitLabel.prefWidth(-1);
		double unitH = mUnitLabel.prefHeight(-1);
		PositionBean unitPos = PositionUtils.computePosition(
				textField,
				mUnitLabel,
				x, y, w, h, 0,
				Insets.EMPTY,
				HPos.RIGHT, textVAlignment
		);
		mUnitLabel.resizeRelocate(unitPos.getX(), unitPos.getY(), unitW, unitH);

		// The text is always positioned to the LEFT of the Pane, the vertical alignment
		// depends on the FloatMode, BOTTOM if FloatMode.INLINE, CENTER in every other mode
		//
		// The width of the text is computed as the remaining space, so the control's width
		// minus the icon's width and gap
		double textW = w -
				(leading != null ? leading.prefWidth(-1) + graphicTextGap : 0) -
				(trailing != null ? trailing.prefWidth(-1) + graphicTextGap : 0) -
				unitW - mUnitGap;
		double textH = boundField.prefHeight(-1);
		PositionBean textPos = PositionUtils.computePosition(
				textField,
				boundField,
				xOffset, y, w, h, 0,
				Insets.EMPTY,
				HPos.LEFT, textVAlignment
		);
		boundField.resizeRelocate(textPos.getX(), textPos.getY(), textW, textH);

		// Sometimes subsequent layouts are not necessary for the floating text position.
		// This flag avoids those unnecessary layout requests.
		if (skipLayout) {
			skipLayout = false;
			return;
		}

		// The code below is responsible for computing the final x and y positions for the
		// floating text.
		// If the text should float (see shouldFloat() method) then the TOP position is computed
		// as a starting point.
		// Then according to the FloatMode adjustments are made to the computed coordinates.
		// Note that the applied scale is NOT included in the measurements so certain values
		// must be divided/multiplied for the scaleValue.
		//
		// In case the text should not float both the coordinates are 0.
		double targetX = 0;
		double targetY = 0;
		if (shouldFloat()) {
			if (floatMode == FloatMode.BORDER) floatX = 0;

			PositionBean floatTopPos = PositionUtils.computePosition(
					textField,
					floatingText,
					floatX, y, w, h, 0,
					Insets.EMPTY,
					HPos.LEFT, VPos.TOP
			);
			targetY = floatTopPos.getY() - floatPos.getY() / scaleValue + 1;
			targetX = floatTopPos.getX() - floatPos.getX() / scaleValue + 1;

			switch (floatMode) {
				case ABOVE: {
					targetX += textField.getBorderGap();
					targetY -= snappedTopInset() + textField.getFloatingTextGap() + floatH / scaleValue;
					break;
				}
				case BORDER: {
					targetX += textField.getBorderGap();
					targetY -= snappedTopInset() + floatH / 2 / scaleValue;
					break;
				}
			}
		} else {
			if (floatMode == FloatMode.BORDER) {
				targetX = (leading != null ? leading.prefWidth(-1) + graphicTextGap : 0);
			}
		}
		setFloatPos(PositionBean.of(targetX, targetY));
	}

	private double getFloatX() {
		return floatingPos.get().getX();
	}

	private double getFloatY() {
		return floatingPos.get().getY();
	}

	private void setFloatPos(PositionBean pos) {
		floatingPos.set(pos);
	}

	private boolean isFloating() {
		return floating.get();
	}
}
