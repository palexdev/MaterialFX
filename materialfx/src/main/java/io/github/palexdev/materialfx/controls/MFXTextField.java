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
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.controls.base.MFXMenuControl;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.skins.MFXTextFieldSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.materialfx.validation.Validated;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * A modern text field restyled to follow material design principles and with many
 * new features.
 * <p>
 * Unlike Swing and JavaFX (which copied Swing duh), I followed Google's Material Design guidelines.
 * They do not have anything like a Label but only TextFields. After all, a TextField has all the features a
 * Label has and even more.
 * <p>
 * {@code MFXTextField} allows you to make it behave like a Label by setting the {@link #editableProperty()} and
 * the {@link #selectableProperty()} to false.
 * <p>
 * Allows you to specify up to two icons (leading and trailing) and the gap between them and the text.
 * <p>
 * Unlike JavaFX's TextField, it also allows to easily change the text color (even with CSS).
 * <p>
 * But... the most important and requested feature is the floating text. You can decide between
 * four modes: DISABLED (no floating text), INLINE (the floating text is inside the field), BORDER
 * (the floating text is placed on the field's border, and ABOVE (the floating text is outside the field, above it).
 * <p>
 * You can also specify the distance between the text and the floating text (for INLINE and ABOVE modes).
 * In ABOVE and BORDER modes you can control the floating text distance from the origin by modifying the left padding in CSS
 * or by modifying the {@link #borderGapProperty()}.
 * <p></p>
 * {@code MFXTextField} now also introduces a new PseudoClass, ":floating" that activates
 * when the floating text node is floating.
 * <p>
 * As with the previous MFXTextField it's also possible to specify the maximum number of characters for the text.
 * <p></p>
 * Some little side notes on the floating text:
 * <p>
 * Please note that because of the extra node to show the floating text, {@code MFXTextField} now takes more space.
 * There are several things you can do to make it more compact:
 * <p> 1) You can lower the {@link #floatingTextGapProperty()} (for INLINE and ABOVE mode)
 * <p> 2) You can lower the padding (set in CSS) but I would not recommend it to be honest, a little
 * bit of padding makes the control more appealing
 * <p> 3) You can switch mode. The DISABLED mode requires the least space of course. The BORDER mode
 * requires some more space, the ABOVE mode is visually equal to the DISABLED state but keep in mind
 * that the floating text is still there, above the field, and the INLINE mode is the one that requires the most space.
 * <p></p>
 * The layout strategy now should be super solid and efficient, making possible to switch float modes even
 * at runtime.
 * <p></p>
 * <b>Note 1: </b> in case of BORDER mode to make it really work as intended
 * a condition must be met. The background colors of the text field, the floating text and the parent
 * container of the field must be the same. You see, on the material.io website you can see the floating
 * text cut the field's borders but that's not what it is happening. If you look more carefully the
 * demo background is white, and the field's background as well. The floating text just sits on top of the
 * field's border and has the same background color, creating that 'cut' effect.
 * <p></p>
 * <b>Note 2: </b> since JavaFX devs are shitheads making everything private/readonly/final, the only way to
 * make the caret position and the selection consistent is to delegate related methods to the {@link BoundTextField} instance.
 * This means that most if not all methods related to the caret and the selection WON'T work, instead you should use
 * the methods that start with "delegate", e.g. {@link #delegateCaretPositionProperty()}, {@link #delegateSelectionProperty()}, etc...
 * <p>
 * Also note that the same applies to the focus property, {@link #delegateFocusedProperty()}.
 * <p>
 * Some methods that do not start with "delegate" may work as they've been overridden to be delegates,
 * e.g. {@link #positionCaret(int)}, {@link #selectRange(int, int)}, etc...
 * <p>
 * If that's not the case then maybe I missed something so please report it back and I'll see if it's fixable.
 * <p>
 * Considering that the other option would have been re-implementing a TextField completely from scratch (really hard task)
 * this is the best option as of now. Even just a custom skin would not work (yep I tried) since black magic is involved
 * in the default one, better not mess with that or something will break for sure, yay for spaghetti coding JavaFX devs :D
 * <p></p>
 * <b>Note 3: </b>Since MFXTextFields (and all subclasses) are basically a wrapper for a TextField, and considered how focus
 * works for them. To make focus behavior consistent in CSS, MFXTextField introduces a new PseudoClass "focus-within" which will
 * be activated every time the inner TextField is focused and deactivated when it loses focus
 */
public class MFXTextField extends TextField implements Validated, MFXMenuControl {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-text-field";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTextField.css");
	protected final BoundTextField boundField;

	public static final Color DEFAULT_TEXT_COLOR = Color.rgb(0, 0, 0, 0.87);

	private final BooleanProperty selectable = new SimpleBooleanProperty(true);
	private final ObjectProperty<Node> leadingIcon = new SimpleObjectProperty<>();
	private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

	private final StringProperty floatingText = new SimpleStringProperty();
	protected final BooleanProperty floating = new SimpleBooleanProperty() {
		@Override
		public void unbind() {
		}
	};
	private static final PseudoClass FLOATING_PSEUDO_CLASS = PseudoClass.getPseudoClass("floating");

	private final StringProperty measureUnit = new SimpleStringProperty("");

	protected final MFXValidator validator = new MFXValidator();
	protected MFXContextMenu contextMenu;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTextField() {
		this("");
	}

	public MFXTextField(String text) {
		this(text, "");
	}

	public MFXTextField(String text, String promptText) {
		this(text, promptText, "");
	}

	public MFXTextField(String text, String promptText, String floatingText) {
		super(text);
		boundField = new BoundTextField(this);
		setPromptText(promptText);
		setFloatingText(floatingText);
		initialize();
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Calls {@link #asLabel(String)} with empty text.
	 */
	public static MFXTextField asLabel() {
		return asLabel("");
	}

	/**
	 * Calls {@link #asLabel(String, String)} with empty promptText.
	 */
	public static MFXTextField asLabel(String text) {
		return asLabel(text, "");
	}

	/**
	 * Calls {@link #asLabel(String, String, String)} with empty floatingText.
	 */
	public static MFXTextField asLabel(String text, String promptText) {
		return asLabel(text, promptText, "");
	}

	/**
	 * Creates a text field that is not editable nor selectable to act just like a label.
	 */
	public static MFXTextField asLabel(String text, String promptText, String floatingText) {
		MFXTextField textField = new MFXTextField(text, promptText, floatingText);
		textField.setEditable(false);
		textField.setSelectable(false);
		return textField;
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().setAll(STYLE_CLASS);
		setPrefColumnCount(6);
		floating.addListener(invalidated -> pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, floating.get()));
		allowEditProperty().bindBidirectional(editableProperty());

		addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
		defaultContextMenu();
	}

	public void defaultContextMenu() {
		MFXContextMenuItem copyItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-copy", 14))
				.setText(I18N.getOrDefault("textField.contextMenu.copy"))
				.setAccelerator("Ctrl + C")
				.setOnAction(event -> copy())
				.get();

		MFXContextMenuItem cutItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-cut", 14))
				.setText(I18N.getOrDefault("textField.contextMenu.cut"))
				.setAccelerator("Ctrl + X")
				.setOnAction(event -> cut())
				.get();

		MFXContextMenuItem pasteItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-content-paste", 14))
				.setText(I18N.getOrDefault("textField.contextMenu.paste"))
				.setAccelerator("Ctrl + V")
				.setOnAction(event -> paste())
				.get();

		MFXContextMenuItem deleteItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-delete-alt", 16))
				.setText(I18N.getOrDefault("textField.contextMenu.delete"))
				.setAccelerator("Ctrl + D")
				.setOnAction(event -> deleteText(getSelection()))
				.get();

		MFXContextMenuItem selectAllItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-select-all", 16))
				.setText(I18N.getOrDefault("textField.contextMenu.selectAll"))
				.setAccelerator("Ctrl + A")
				.setOnAction(event -> selectAll())
				.get();

		MFXContextMenuItem redoItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-redo", 12))
				.setText(I18N.getOrDefault("textField.contextMenu.redo"))
				.setAccelerator("Ctrl + Y")
				.setOnAction(event -> redo())
				.get();

		MFXContextMenuItem undoItem = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-undo", 12))
				.setText(I18N.getOrDefault("textField.contextMenu.undo"))
				.setAccelerator("Ctrl + Z")
				.setOnAction(event -> undo())
				.get();

		contextMenu = MFXContextMenu.Builder.build(this)
				.addItems(copyItem, cutItem, pasteItem, deleteItem, selectAllItem)
				.addLineSeparator()
				.addItems(redoItem, undoItem)
				.setPopupStyleableParent(this)
				.installAndGet();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public MFXContextMenu getMFXContextMenu() {
		return contextMenu;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTextFieldSkin(this, boundField);
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXTextField.getClassCssMetaData();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Workaround Methods
	//================================================================================
	@Override
	public void cut() {
		boundField.cut();
	}

	@Override
	public void copy() {
		boundField.copy();
	}

	@Override
	public void paste() {
		boundField.paste();
	}

	@Override
	public void selectBackward() {
		boundField.selectBackward();
	}

	@Override
	public void selectForward() {
		boundField.selectForward();
	}

	@Override
	public void previousWord() {
		boundField.previousWord();
	}

	@Override
	public void nextWord() {
		boundField.nextWord();
	}

	@Override
	public void endOfNextWord() {
		boundField.endOfNextWord();
	}

	@Override
	public void selectPreviousWord() {
		boundField.selectPreviousWord();
	}

	@Override
	public void selectNextWord() {
		boundField.selectNextWord();
	}

	@Override
	public void selectEndOfNextWord() {
		boundField.selectEndOfNextWord();
	}

	@Override
	public void selectAll() {
		boundField.selectAll();
	}

	@Override
	public void home() {
		boundField.home();
	}

	@Override
	public void end() {
		boundField.end();
	}

	@Override
	public void selectHome() {
		boundField.selectHome();
	}

	@Override
	public void selectEnd() {
		boundField.selectEnd();
	}

	@Override
	public void forward() {
		boundField.forward();
	}

	@Override
	public void backward() {
		boundField.backward();
	}

	@Override
	public void positionCaret(int pos) {
		boundField.positionCaret(pos);
	}

	@Override
	public void selectPositionCaret(int pos) {
		boundField.selectPositionCaret(pos);
	}

	@Override
	public void selectRange(int anchor, int caretPosition) {
		boundField.selectRange(anchor, caretPosition);
	}

	@Override
	public void extendSelection(int pos) {
		boundField.extendSelection(pos);
	}

	@Override
	public void clear() {
		boundField.clear();
	}

	@Override
	public void deselect() {
		boundField.deselect();
	}

	@Override
	public void replaceSelection(String replacement) {
		boundField.replaceSelection(replacement);
	}

	public TextFormatter<?> delegateGetTextFormatter() {
		return boundField.getTextFormatter();
	}

	/**
	 * Specifies the {@link BoundTextField} text formatter.
	 */
	public ObjectProperty<TextFormatter<?>> delegateTextFormatterProperty() {
		return boundField.textFormatterProperty();
	}

	public void delegateSetTextFormatter(TextFormatter<?> textFormatter) {
		boundField.setTextFormatter(textFormatter);
	}

	public int delegateGetAnchor() {
		return boundField.getAnchor();
	}

	/**
	 * Specifies the {@link BoundTextField} anchor position.
	 */
	public ReadOnlyIntegerProperty delegateAnchorProperty() {
		return boundField.anchorProperty();
	}

	public int delegateGetCaretPosition() {
		return boundField.getCaretPosition();
	}

	/**
	 * Specifies the {@link BoundTextField} caret position.
	 */
	public ReadOnlyIntegerProperty delegateCaretPositionProperty() {
		return boundField.caretPositionProperty();
	}

	public String delegateGetSelectedText() {
		return boundField.getSelectedText();
	}

	/**
	 * Specifies the {@link BoundTextField} selected text.
	 */
	public ReadOnlyStringProperty delegateSelectedTextProperty() {
		return boundField.selectedTextProperty();
	}

	public IndexRange delegateGetSelection() {
		return boundField.getSelection();
	}

	/**
	 * Specifies the {@link BoundTextField} selection.
	 */
	public ReadOnlyObjectProperty<IndexRange> delegateSelectionProperty() {
		return boundField.selectionProperty();
	}

	public boolean delegateIsRedoable() {
		return boundField.isRedoable();
	}

	/**
	 * Delegates to {@link BoundTextField}, see {@link BoundTextField#redoableProperty()}.
	 */
	public ReadOnlyBooleanProperty delegateRedoableProperty() {
		return boundField.redoableProperty();
	}

	public boolean delegateIsUndoable() {
		return boundField.isUndoable();
	}

	/**
	 * Delegates to {@link BoundTextField}, see {@link BoundTextField#undoableProperty()}.
	 */
	public ReadOnlyBooleanProperty delegateUndoableProperty() {
		return boundField.undoableProperty();
	}

	public boolean delegateIsFocused() {
		return boundField.isFocused();
	}

	/**
	 * Specifies whether the {@link BoundTextField} is focused.
	 */
	public ReadOnlyBooleanProperty delegateFocusedProperty() {
		return boundField.focusedProperty();
	}

	//================================================================================
	// Validation
	//================================================================================
	@Override
	public MFXValidator getValidator() {
		return validator;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public boolean isSelectable() {
		return selectable.get();
	}

	/**
	 * Specifies whether selection is allowed.
	 */
	public BooleanProperty selectableProperty() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable.set(selectable);
	}

	public Node getLeadingIcon() {
		return leadingIcon.get();
	}

	/**
	 * Specifies the icon placed before the input field.
	 */
	public ObjectProperty<Node> leadingIconProperty() {
		return leadingIcon;
	}

	public void setLeadingIcon(Node leadingIcon) {
		this.leadingIcon.set(leadingIcon);
	}

	public Node getTrailingIcon() {
		return trailingIcon.get();
	}

	/**
	 * Specifies the icon placed after the input field.
	 */
	public ObjectProperty<Node> trailingIconProperty() {
		return trailingIcon;
	}

	public void setTrailingIcon(Node trailingIcon) {
		this.trailingIcon.set(trailingIcon);
	}

	public String getFloatingText() {
		return floatingText.get();
	}

	/**
	 * Specifies the text of the floating text node.
	 */
	public StringProperty floatingTextProperty() {
		return floatingText;
	}

	public void setFloatingText(String floatingText) {
		this.floatingText.set(floatingText);
	}

	public boolean isFloating() {
		return floating.get();
	}

	/**
	 * Specifies if the floating text node is currently floating or not.
	 */
	public BooleanProperty floatingProperty() {
		return floating;
	}

	public String getMeasureUnit() {
		return measureUnit.get();
	}

	/**
	 * Specifies the unit of measure of the field.
	 * <p></p>
	 * This is useful of course when dealing with numeric fields that represent for example:
	 * weight, volume, length and so on...
	 */
	public StringProperty measureUnitProperty() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit.set(measureUnit);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty allowEdit = new StyleableBooleanProperty(
			StyleableProperties.EDITABLE,
			this,
			"allowEdit",
			true
	);

	private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
			StyleableProperties.ANIMATED,
			this,
			"animated",
			true
	);

	private final StyleableDoubleProperty borderGap = new StyleableDoubleProperty(
			StyleableProperties.BORDER_GAP,
			this,
			"borderGap",
			10.0
	);

	private final StyleableBooleanProperty caretVisible = new StyleableBooleanProperty(
			StyleableProperties.CARET_VISIBLE,
			this,
			"caretAnimated",
			true
	);

	private final StyleableObjectProperty<FloatMode> floatMode = new StyleableObjectProperty<>(
			StyleableProperties.FLOAT_MODE,
			this,
			"floatMode",
			FloatMode.INLINE
	);

	private final StyleableDoubleProperty floatingTextGap = new StyleableDoubleProperty(
			StyleableProperties.FLOATING_TEXT_GAP,
			this,
			"gap",
			5.0
	);

	private final StyleableDoubleProperty graphicTextGap = new StyleableDoubleProperty(
			StyleableProperties.GRAPHIC_TEXT_GAP,
			this,
			"graphicTextGap",
			10.0
	);

	private final StyleableDoubleProperty measureUnitGap = new StyleableDoubleProperty(
			StyleableProperties.MEASURE_UNIT_GAP,
			this,
			"measureUnitGap",
			5.0
	);

	private final StyleableBooleanProperty scaleOnAbove = new StyleableBooleanProperty(
			StyleableProperties.SCALE_ON_ABOVE,
			this,
			"scaleOnAbove",
			false
	);

	private final StyleableObjectProperty<Color> textFill = new StyleableObjectProperty<>(
			StyleableProperties.TEXT_FILL,
			this,
			"textFill",
			DEFAULT_TEXT_COLOR
	);

	private final StyleableIntegerProperty textLimit = new StyleableIntegerProperty(
			StyleableProperties.TEXT_LIMIT,
			this,
			"textLimit",
			-1
	);

	public boolean isAllowEdit() {
		return allowEdit.get();
	}

	/**
	 * Specifies whether the field is editable.
	 * <p>
	 * This property is bound bidirectionally to {@link TextField#editableProperty()},
	 * it's here just to be set via CSS.
	 */
	public StyleableBooleanProperty allowEditProperty() {
		return allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit.set(allowEdit);
	}

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies whether the floating text positioning is animated.
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated.set(animated);
	}

	public double getBorderGap() {
		return borderGap.get();
	}

	/**
	 * For {@link FloatMode#BORDER} and {@link FloatMode#ABOVE} modes, this specifies the distance from
	 * the control's x origin (padding not included).
	 */
	public StyleableDoubleProperty borderGapProperty() {
		return borderGap;
	}

	public void setBorderGap(double borderGap) {
		this.borderGap.set(borderGap);
	}

	public boolean getCaretVisible() {
		return caretVisible.get();
	}

	/**
	 * Specifies whether the caret should be visible.
	 */
	public StyleableBooleanProperty caretVisibleProperty() {
		return caretVisible;
	}

	public void setCaretVisible(boolean caretVisible) {
		this.caretVisible.set(caretVisible);
	}

	public FloatMode getFloatMode() {
		return floatMode.get();
	}

	/**
	 * Specifies how the floating text is positioned when floating.
	 */
	public StyleableObjectProperty<FloatMode> floatModeProperty() {
		return floatMode;
	}

	public void setFloatMode(FloatMode floatMode) {
		this.floatMode.set(floatMode);
	}

	public double getFloatingTextGap() {
		return floatingTextGap.get();
	}

	/**
	 * For {@link FloatMode#INLINE} mode, this specifies the gap between
	 * the floating text node and the input field node.
	 */
	public StyleableDoubleProperty floatingTextGapProperty() {
		return floatingTextGap;
	}

	public void setFloatingTextGap(double floatingTextGap) {
		this.floatingTextGap.set(floatingTextGap);
	}

	public double getGraphicTextGap() {
		return graphicTextGap.get();
	}

	/**
	 * Specifies the gap between the input field and the icons.
	 */
	public StyleableDoubleProperty graphicTextGapProperty() {
		return graphicTextGap;
	}

	public void setGraphicTextGap(double graphicTextGap) {
		this.graphicTextGap.set(graphicTextGap);
	}

	public boolean scaleOnAbove() {
		return scaleOnAbove.get();
	}

	/**
	 * Specifies whether the floating text node should be scaled or not when
	 * the float mode is set to {@link FloatMode#ABOVE}.
	 */
	public StyleableBooleanProperty scaleOnAboveProperty() {
		return scaleOnAbove;
	}

	public void setScaleOnAbove(boolean scaleOnAbove) {
		this.scaleOnAbove.set(scaleOnAbove);
	}

	public double getMeasureUnitGap() {
		return measureUnitGap.get();
	}

	/**
	 * Specifies the gap between the field and the measure unit label.
	 */
	public StyleableDoubleProperty measureUnitGapProperty() {
		return measureUnitGap;
	}

	public void setMeasureUnitGap(double measureUnitGap) {
		this.measureUnitGap.set(measureUnitGap);
	}

	public Color getTextFill() {
		return textFill.get();
	}

	/**
	 * Specifies the text color.
	 */
	public StyleableObjectProperty<Color> textFillProperty() {
		return textFill;
	}

	public void setTextFill(Color textFill) {
		this.textFill.set(textFill);
	}

	public int getTextLimit() {
		return textLimit.get();
	}

	/**
	 * Specifies the maximum number of characters the field's text can have.
	 */
	public StyleableIntegerProperty textLimitProperty() {
		return textLimit;
	}

	public void setTextLimit(int textLimit) {
		this.textLimit.set(textLimit);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXTextField> FACTORY = new StyleablePropertyFactory<>(TextField.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXTextField, Boolean> ANIMATED =
				FACTORY.createBooleanCssMetaData(
						"-mfx-animated",
						MFXTextField::animatedProperty,
						true
				);

		private static final CssMetaData<MFXTextField, Number> BORDER_GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-border-gap",
						MFXTextField::borderGapProperty,
						10.0
				);

		private static final CssMetaData<MFXTextField, Boolean> CARET_VISIBLE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-caret-visible",
						MFXTextField::caretVisibleProperty,
						true
				);

		private static final CssMetaData<MFXTextField, Boolean> EDITABLE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-editable",
						MFXTextField::allowEditProperty,
						true
				);

		private static final CssMetaData<MFXTextField, FloatMode> FLOAT_MODE =
				FACTORY.createEnumCssMetaData(
						FloatMode.class,
						"-mfx-float-mode",
						MFXTextField::floatModeProperty,
						FloatMode.INLINE
				);

		private static final CssMetaData<MFXTextField, Number> FLOATING_TEXT_GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-gap",
						MFXTextField::floatingTextGapProperty,
						5.0
				);

		private static final CssMetaData<MFXTextField, Number> GRAPHIC_TEXT_GAP =
				FACTORY.createSizeCssMetaData(
						"-fx-graphic-text-gap",
						MFXTextField::graphicTextGapProperty,
						10.0
				);

		private static final CssMetaData<MFXTextField, Number> MEASURE_UNIT_GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-measure-unit-gap",
						MFXTextField::measureUnitGapProperty,
						5.0
				);

		private static final CssMetaData<MFXTextField, Boolean> SCALE_ON_ABOVE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-scale-on-above",
						MFXTextField::scaleOnAboveProperty,
						false
				);

		private static final CssMetaData<MFXTextField, Color> TEXT_FILL =
				FACTORY.createColorCssMetaData(
						"-fx-text-fill",
						MFXTextField::textFillProperty,
						DEFAULT_TEXT_COLOR
				);

		private static final CssMetaData<MFXTextField, Number> TEXT_LIMIT =
				FACTORY.createSizeCssMetaData(
						"-mfx-text-limit",
						MFXTextField::textLimitProperty,
						-1
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					TextField.getClassCssMetaData(),
					ANIMATED, CARET_VISIBLE, BORDER_GAP,
					EDITABLE, FLOAT_MODE, FLOATING_TEXT_GAP, GRAPHIC_TEXT_GAP, MEASURE_UNIT_GAP,
					SCALE_ON_ABOVE, TEXT_FILL, TEXT_LIMIT
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}
}
