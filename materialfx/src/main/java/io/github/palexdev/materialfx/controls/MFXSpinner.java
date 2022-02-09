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
import io.github.palexdev.materialfx.beans.properties.functional.BiFunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.controls.models.spinner.SpinnerModel;
import io.github.palexdev.materialfx.skins.MFXSpinnerSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * MaterialFX implementation of {@link javafx.scene.control.Spinner} with a modern UI.
 * <p>
 * The spinner can work on any object you want but you will have to implement your own
 * {@link SpinnerModel}. MaterialFX just like JavaFX offers 4 default models for: doubles,
 * integers, local dates and lists.
 * <p>
 * <b>Without a {@link SpinnerModel} the spinner is useless!</b>
 * <p></p>
 * {@code MFXSpinner} offers the following features:
 * <p> - You can set a prompt text for the field, {@link #promptTextProperty()}
 * <p> - You can set the spinner to be editable or not, {@link #editableProperty()}
 * <p> - You can allow/disallow the selection of the text, {@link #selectableProperty()}
 * <p> - You can specify the action to run when the spinner is editable and ENTER is pressed,
 * {@link #onCommitProperty()}
 * <p> You can specify a function to transform the spinner's text. This can be useful for example when
 * you want to add unit of measures to the text. The function carries the focus state of the editor
 * (this way you can remove/add text according to the focus state) and the T value converted to a String
 * <p> - You can easily change the orientation of the spinner, {@link #orientationProperty()}
 * <p> - You can easily change the icons, {@link #prevIconSupplierProperty()}, {@link #nextIconSupplierProperty()}
 * <p> - You can specify the gap between the text and the icon, {@link #graphicTextGapProperty()}
 */
public class MFXSpinner<T> extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-spinner";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXSpinner.css");

	private final ReadOnlyObjectWrapper<T> value = new ReadOnlyObjectWrapper<>();
	private final ObjectProperty<SpinnerModel<T>> spinnerModel = new SimpleObjectProperty<>();
	private final StringProperty promptText = new SimpleStringProperty("");
	private final BooleanProperty editable = new SimpleBooleanProperty(false);
	private final BooleanProperty selectable = new SimpleBooleanProperty(true);
	private final ConsumerProperty<String> onCommit = new ConsumerProperty<>();
	private final BiFunctionProperty<Boolean, String, String> textTransformer = new BiFunctionProperty<>();

	private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);
	private final SupplierProperty<Node> prevIconSupplier = new SupplierProperty<>();
	private final SupplierProperty<Node> nextIconSupplier = new SupplierProperty<>();
	private final DoubleProperty graphicTextGap = new SimpleDoubleProperty(10.0);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXSpinner() {
		this(null);
	}

	public MFXSpinner(SpinnerModel<T> spinnerModel) {
		initialize();
		setSpinnerModel(spinnerModel);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		spinnerModelProperty().addListener((observable, oldValue, newValue) -> {
			value.unbind();
			if (newValue != null) {
				value.bind(newValue.valueProperty());
			}
		});

		defaultIcons();
	}

	/**
	 * Restores the defaults for {@link #prevIconSupplierProperty()} and {@link #nextIconSupplierProperty()}.
	 */
	public void defaultIcons() {
		setPrevIconSupplier(() -> {
			MFXIconWrapper icon = new MFXIconWrapper("mfx-minus", 16, -1).defaultRippleGeneratorBehavior();
			icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
				SpinnerModel<T> model = getSpinnerModel();
				if (model != null) model.previous();
			});
			NodeUtils.makeRegionCircular(icon);
			return icon;
		});
		setNextIconSupplier(() -> {
			MFXIconWrapper icon = new MFXIconWrapper("mfx-plus", 16, -1).defaultRippleGeneratorBehavior();
			icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
				SpinnerModel<T> model = getSpinnerModel();
				if (model != null) model.next();
			});
			NodeUtils.makeRegionCircular(icon);
			return icon;
		});
	}

	/**
	 * If the spinner is editable, {@link #editableProperty()}, pressing the ENTER key will
	 * trigger the action specified by {@link #onCommitProperty()}.
	 */
	public void commit(String text) {
		Consumer<String> onCommit = getOnCommit();
		if (onCommit != null) onCommit.accept(text);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXSpinnerSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	public T getValue() {
		return value.get();
	}

	/**
	 * Specifies the current selected value for the spinner.
	 * <p>
	 * Note that this property is read-only, you can set the value with {@link #setValue(Object)}
	 * but it will fail with an exception if the {@link #spinnerModelProperty()} is null.
	 */
	public ReadOnlyObjectProperty<T> valueProperty() {
		return value.getReadOnlyProperty();
	}

	public void setValue(T value) {
		getSpinnerModel().setValue(value);
	}

	public SpinnerModel<T> getSpinnerModel() {
		return spinnerModel.get();
	}

	/**
	 * Specifies the spinner's model, responsible for handling the spinner's value
	 * according to the data type.
	 */
	public ObjectProperty<SpinnerModel<T>> spinnerModelProperty() {
		return spinnerModel;
	}

	public void setSpinnerModel(SpinnerModel<T> spinnerModel) {
		this.spinnerModel.set(spinnerModel);
	}

	public String getPromptText() {
		return promptText.get();
	}

	/**
	 * Specifies the prompt text for the spinner's text field.
	 */
	public StringProperty promptTextProperty() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText.set(promptText);
	}

	public boolean isEditable() {
		return editable.get();
	}

	/**
	 * Specifies whether the spinner's text field is editable.
	 * <p>
	 * If you edit the text you must confirm the change by pressing the ENTER
	 * key, this will trigger the {@link #commit(String)} method, more info here {@link #onCommitProperty()}.
	 */
	public BooleanProperty editableProperty() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable.set(editable);
	}

	public boolean isSelectable() {
		return selectable.get();
	}

	/**
	 * Specifies whether the spinner's text is selectable.
	 */
	public BooleanProperty selectableProperty() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable.set(selectable);
	}

	public Consumer<String> getOnCommit() {
		return onCommit.get();
	}

	/**
	 * Specifies the action to perform when editing the spinner's text
	 * and confirming the changes by pressing ENTER.
	 * <p>
	 * The action is a {@link Consumer} which carries the modified text.
	 * To change the spinner's value with that for example you probably want to validate
	 * the text, parse a valid T object and then set the value.
	 */
	public ConsumerProperty<String> onCommitProperty() {
		return onCommit;
	}

	public void setOnCommit(Consumer<String> onCommit) {
		this.onCommit.set(onCommit);
	}

	public BiFunction<Boolean, String, String> getTextTransformer() {
		return textTransformer.get();
	}

	/**
	 * The text transformer is a {@link BiFunction} that allows you to change the
	 * spinner's text when the spinner's text field acquires/loses focus.
	 * <p>
	 * This can be useful for example when you want to add the unit of measure to the
	 * spinner's text. Usually in such controls the unit of measure is added when the control
	 * is not focused and removed when editing the text.
	 * <p></p>
	 * An example could be:
	 * <pre>
	 * {@code
	 *      MFXSpinner spinner = ...;
	 *      spinner.setTextTransformer((focused, text) -> (!focused || !spinner.isEditable()) ? text + " meters" : text);
	 * }
	 * </pre>
	 */
	public BiFunctionProperty<Boolean, String, String> textTransformerProperty() {
		return textTransformer;
	}

	public void setTextTransformer(BiFunction<Boolean, String, String> textTransformer) {
		this.textTransformer.set(textTransformer);
	}

	public Orientation getOrientation() {
		return orientation.get();
	}

	/**
	 * Specifies the spinner's orientation.
	 */
	public ObjectProperty<Orientation> orientationProperty() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}

	public Supplier<Node> getPrevIconSupplier() {
		return prevIconSupplier.get();
	}

	/**
	 * The {@link Supplier} used to build the icons which should
	 * trigger {@link SpinnerModel#previous()}.
	 * <p>
	 * Note that the {@link #defaultIcons()} add the needed event handlers
	 * to use the {@link SpinnerModel}, it is not handled automatically!
	 */
	public SupplierProperty<Node> prevIconSupplierProperty() {
		return prevIconSupplier;
	}

	public void setPrevIconSupplier(Supplier<Node> prevIconSupplier) {
		this.prevIconSupplier.set(prevIconSupplier);
	}

	public Supplier<Node> getNextIconSupplier() {
		return nextIconSupplier.get();
	}

	/**
	 * The {@link Supplier} used to build the icons which should
	 * trigger {@link SpinnerModel#next()}.
	 * <p>
	 * Note that the {@link #defaultIcons()} add the needed event handlers
	 * to use the {@link SpinnerModel}, it is not handled automatically!
	 */
	public SupplierProperty<Node> nextIconSupplierProperty() {
		return nextIconSupplier;
	}

	public void setNextIconSupplier(Supplier<Node> nextIconSupplier) {
		this.nextIconSupplier.set(nextIconSupplier);
	}

	public double getGraphicTextGap() {
		return graphicTextGap.get();
	}

	/**
	 * Specifies the space between the spinner's text and the two icons.
	 */
	public DoubleProperty graphicTextGapProperty() {
		return graphicTextGap;
	}

	public void setGraphicTextGap(double graphicTextGap) {
		this.graphicTextGap.set(graphicTextGap);
	}
}
