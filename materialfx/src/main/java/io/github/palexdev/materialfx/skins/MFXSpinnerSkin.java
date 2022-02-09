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

import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.SpinnerModel;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.TextUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import java.util.function.BiFunction;

/**
 * This is the default skin implementation for {@link MFXSpinner}.
 * <p>
 * There are two main components:
 * <p> 1) The top container, a {@link BorderPane}, which contains the spinner's text field
 * and the two icons. The usage of a {@code BorderPane} simplifies a lot the layout since it's
 * everything automatically handled. This allows to easily switch the spinner's orientation,
 * {@link MFXSpinner#orientationProperty()}, and handle the icons.
 * <p> 2) A {@link MFXTextField} to show the spinner's selected value as text. Here things are a bit
 * more complicated. The field's text is not simply bound but it's computed by a {@link StringBinding}.
 * This is needed because there are several things to consider since the spinner relies on {@link SpinnerModel}
 * to handle the selected value and also the way to convert the value to text.
 * It's needed, for example, to keep track of both the {@link MFXSpinner#spinnerModelProperty()} and
 * the {@link SpinnerModel#converterProperty()} at the same time.
 * <p></p>
 * The skin also introduces a new {@link PseudoClass}: ":focus-within", it is activated when
 * the text field is focused or when one of the icons is focused.
 */
public class MFXSpinnerSkin<T> extends SkinBase<MFXSpinner<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final BorderPane container;
	private final MFXTextField field;
	private Node nextIcon;
	private Node prevIcon;

	private ChangeListener<SpinnerModel<T>> modelListener;
	private ChangeListener<StringConverter<T>> converterListener;
	private StringBinding textBinding;

	private static final PseudoClass FOCUS_WITHIN_PSEUDO_CLASS = PseudoClass.getPseudoClass("focus-within");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXSpinnerSkin(MFXSpinner<T> spinner) {
		super(spinner);

		// Text Field
		field = new MFXTextField() {
			@Override
			public String getUserAgentStylesheet() {
				return spinner.getUserAgentStylesheet();
			}
		};
		field.setFloatMode(FloatMode.DISABLED);
		field.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		field.allowEditProperty().bind(spinner.editableProperty());
		field.selectableProperty().bind(spinner.selectableProperty());
		field.promptTextProperty().bind(spinner.promptTextProperty());

		textBinding = Bindings.createStringBinding(
				() -> {
					T value = spinner.getValue();
					SpinnerModel<T> model = spinner.getSpinnerModel();
					String s;
					if (model == null) {
						s = value != null ? value.toString() : "";
					} else {
						s = model.getConverter().toString(value);
					}

					BiFunction<Boolean, String, String> textTransformer = spinner.getTextTransformer();
					return (textTransformer != null) ? textTransformer.apply(field.delegateIsFocused(), s) : s;
				},
				spinner.valueProperty(), spinner.spinnerModelProperty(), field.delegateFocusedProperty()
		);
		if (textBinding.get() != null && !textBinding.get().isEmpty()) field.setText(textBinding.get());

		// Model Listeners
		modelListener = (observable, oldValue, newValue) -> initializeModel(oldValue, newValue);
		converterListener = (observable, oldValue, newValue) -> textBinding.invalidate();

		// Icons Initialization
		nextIcon = (spinner.getNextIconSupplier() != null) ? spinner.getNextIconSupplier().get() : null;
		prevIcon = (spinner.getPrevIconSupplier() != null) ? spinner.getPrevIconSupplier().get() : null;

		// Top Container Initialization
		container = new BorderPane();
		manageContainer();
		manageGap();

		// Skin Initialization
		getChildren().setAll(container);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners/handlers:
	 * <p> - A listener to the {@link MFXSpinner#spinnerModelProperty()} to call {@link #initializeModel(SpinnerModel, SpinnerModel)} when it changes
	 * <p> - A listener to update the field's text
	 * <p> - A listener to activate the ":focus-within" PseudoClass when the text field is focused
	 * <p> - A listener to manage the {@link MFXSpinner#orientationProperty()}
	 * <p> - A listener to manage the {@link MFXSpinner#graphicTextGapProperty()}
	 * <p> - Two listeners to handle {@link MFXSpinner#prevIconSupplierProperty()} and {@link MFXSpinner#nextIconSupplierProperty()}
	 * <p> - A MOUSE_PRESSED handler to handle the spinner's focus and also activate the ":focus-within" PseudoClass if needed
	 * <p> - A KEY_PRESSED filter to handle the edit/cancel features. Pressing ENTER will trigger the {@link MFXSpinner#commit(String)}
	 * method. Pressing Ctrl+Shift+Z will cancel the edit and reset the text for the current selected value
	 */
	private void addListeners() {
		MFXSpinner<T> spinner = getSkinnable();

		spinner.spinnerModelProperty().addListener(modelListener);
		textBinding.addListener(invalidated -> field.setText(textBinding.getValue()));
		field.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> spinner.pseudoClassStateChanged(FOCUS_WITHIN_PSEUDO_CLASS, newValue));

		spinner.orientationProperty().addListener(invalidated -> manageContainer());
		spinner.graphicTextGapProperty().addListener(invalidated -> manageGap());
		spinner.prevIconSupplierProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				prevIcon = newValue.get();
			} else {
				prevIcon = null;
			}
			manageContainer();
		});
		spinner.nextIconSupplierProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				nextIcon = newValue.get();
			} else {
				nextIcon = null;
			}
			manageContainer();
		});

		spinner.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			if (NodeUtils.inHierarchy(event, nextIcon) || NodeUtils.inHierarchy(event, prevIcon)) {
				boolean iconsFocused = (nextIcon != null && nextIcon.isFocused()) || (prevIcon != null && prevIcon.isFocused());
				spinner.pseudoClassStateChanged(FOCUS_WITHIN_PSEUDO_CLASS, iconsFocused);
				return;
			}
			spinner.pseudoClassStateChanged(FOCUS_WITHIN_PSEUDO_CLASS, false);
			spinner.requestFocus();
		});
		spinner.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER: {
					spinner.commit(field.getText());
					break;
				}
				case Z: {
					if (event.isControlDown() && event.isShiftDown()) {
						SpinnerModel<T> model = spinner.getSpinnerModel();
						if (model != null) {
							String s = model.getConverter().toString(spinner.getValue());
							field.setText(s);
						}
					}
					break;
				}
			}
		});
	}

	/**
	 * Manages the top container, {@link BorderPane}, according to the spinner's current
	 * {@link Orientation}.
	 * <p>
	 * Also calls {@link #manageGap()}.
	 */
	private void manageContainer() {
		MFXSpinner<T> spinner = getSkinnable();
		Orientation orientation = spinner.getOrientation();

		container.getChildren().clear();
		if (orientation == Orientation.HORIZONTAL) {
			if (prevIcon != null) container.setLeft(prevIcon);
			if (nextIcon != null) container.setRight(nextIcon);
		} else {
			if (nextIcon != null) container.setTop(nextIcon);
			if (prevIcon != null) container.setBottom(prevIcon);
		}
		container.setCenter(field);
		manageGap();
	}

	/**
	 * Responsible for applying the {@link MFXSpinner#graphicTextGapProperty()} to the
	 * the spinner's icons.
	 */
	private void manageGap() {
		MFXSpinner<T> spinner = getSkinnable();
		Orientation orientation = spinner.getOrientation();
		double gap = spinner.getGraphicTextGap();

		if (orientation == Orientation.HORIZONTAL) {
			if (prevIcon != null) BorderPane.setMargin(prevIcon, InsetsFactory.right(gap));
			if (nextIcon != null) BorderPane.setMargin(nextIcon, InsetsFactory.left(gap));
		} else {
			if (prevIcon != null) BorderPane.setMargin(prevIcon, InsetsFactory.top(gap));
			if (nextIcon != null) BorderPane.setMargin(nextIcon, InsetsFactory.bottom(gap));
		}
	}

	/**
	 * Responsible for adding a listener to the spinner's {@link SpinnerModel#converterProperty()}
	 * when it changes.
	 * <p></p>
	 * The listener is responsible for updating the text when the converter changes.
	 */
	private void initializeModel(SpinnerModel<T> oldModel, SpinnerModel<T> newModel) {
		if (oldModel != newModel) {
			oldModel.converterProperty().removeListener(converterListener);
		}
		if (newModel != null) {
			newModel.converterProperty().addListener(converterListener);
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + 5;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXSpinner<T> spinner = getSkinnable();
		Orientation orientation = spinner.getOrientation();
		double gap = spinner.getGraphicTextGap();

		double textW = field.snappedLeftInset() + Math.max(
				TextUtils.computeTextWidth(field.getFont(), field.getText()),
				TextUtils.computeTextWidth(field.getFont(), field.getPromptText())
		) + field.snappedRightInset();
		double prevIconW = prevIcon.prefWidth(-1);
		double nextIconW = nextIcon.prefWidth(-1);

		if (orientation == Orientation.HORIZONTAL) {
			return leftInset +
					(prevIcon != null ? prevIconW + gap : 0) +
					textW +
					(nextIcon != null ? nextIconW + gap : 0) +
					rightInset;
		} else {
			return leftInset +
					Math.max(textW, Math.max(prevIconW, nextIconW)) +
					rightInset;
		}
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}

	@Override
	public void dispose() {
		super.dispose();
		textBinding = null;
		modelListener = null;
		converterListener = null;
	}
}
