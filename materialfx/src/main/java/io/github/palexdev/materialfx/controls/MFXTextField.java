package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.skins.MFXTextFieldSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
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
 * three modes: DISABLED (no floating text), INLINE (the floating text is inside the field), BORDER
 * (the floating text is placed on the field's border.
 * <p>
 * You can also specify the distance between the text and the floating text (for INLINE mode) and
 * the distance from the x origin (for BORDER mode). The floating text is animated by default but
 * you can also disable it.
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
 * <p> 1) You can lower the {@link #gapProperty()} (for INLINE mode)
 * <p> 2) You can lower the padding (set in CSS) but I would not recommend it to be honest, a little
 * bit of padding makes the control more appealing
 * <p> 3) You can switch mode. The DISABLED mode requires the least space of course. The BORDER mode
 * requires some more space, and the INLINE mode is the one that requires the most space (because it acts
 * as a VBox)
 * <p></p>
 * Also, note that while it is allowed, it's highly discouraged to switch mode at runtime.
 * I put a lot of effort in making the layout as stable as possible in every condition but, as
 * I always said floating text is a quite hard to implement feature so, just be careful with that.
 * <p></p>
 * Last but not least... Keep in mind that in case of BORDER mode to make it really work as intended
 * a condition must be met. The background colors of the text field, the floating text and the parent
 * container of the field must be the same. You see, on the material.io website you can see the floating
 * text cut the field's borders but that's not what it is happening. If you look more carefully the
 * demo background is white, and the field's background as well. The floating text just sits on top of the
 * field's border and has the same background color, creating that 'cut' effect.
 */
public class MFXTextField extends TextField {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-text-field";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTextField.css");

    public static final Color DEFAULT_TEXT_COLOR = Color.rgb(0, 0, 0, 0.87);

    private final BooleanProperty selectable = new SimpleBooleanProperty(true);
    private final ObjectProperty<Node> leadingIcon = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

    // TODO add context menu after conversion to MFXPopup
    // TODO add validation

    private final StringProperty floatingText = new SimpleStringProperty();
    protected final ReadOnlyBooleanWrapper floating = new ReadOnlyBooleanWrapper(false);
    private static final PseudoClass FLOATING_PSEUDO_CLASS = PseudoClass.getPseudoClass("floating");

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
        setPromptText(promptText);
        setFloatingText(floatingText);
        initialize();
    }

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
        floating.addListener(invalidated -> pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, floating.get()));
        allowEditProperty().bindBidirectional(editableProperty());

        // TODO may be useful for context menu
/*        EventDispatcher original = getEventDispatcher();
        setEventDispatcher((event, tail) -> {
            if (getMFXContextMenu() != null
                    && event instanceof MouseEvent &&
                    ((MouseEvent) event).getButton() == MouseButton.SECONDARY &&
                    !getSelectedText().isEmpty()
            ) {
                MFXContextMenu contextMenu = getMFXContextMenu();
                contextMenu.show((MouseEvent) event);
                event.consume();
            }
            return original.dispatchEvent(event, tail);
        });
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);*/
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
    public ReadOnlyBooleanProperty floatingProperty() {
        return floating.getReadOnlyProperty();
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
            StyleableProperties.ANIMATED,
            this,
            "animated",
            true
    );

    private final StyleableDoubleProperty borderSpacing = new StyleableDoubleProperty(
            StyleableProperties.BORDER_SPACING,
            this,
            "borderSpacing",
            10.0
    );

    private final StyleableBooleanProperty caretVisible = new StyleableBooleanProperty(
            StyleableProperties.CARET_VISIBLE,
            this,
            "caretAnimated",
            true
    );

    private final StyleableBooleanProperty allowEdit = new StyleableBooleanProperty(
            StyleableProperties.EDITABLE,
            this,
            "allowEdit",
            true
    );

    private final StyleableObjectProperty<FloatMode> floatMode = new StyleableObjectProperty<>(
            StyleableProperties.FLOAT_MODE,
            this,
            "floatMode",
            FloatMode.INLINE
    );

    private final StyleableDoubleProperty gap = new StyleableDoubleProperty(
            StyleableProperties.GAP,
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

    public double getBorderSpacing() {
        return borderSpacing.get();
    }

    /**
     * For {@link FloatMode#BORDER} mode, this specifies the distance from
     * the control's x origin (padding not included).
     */
    public StyleableDoubleProperty borderSpacingProperty() {
        return borderSpacing;
    }

    public void setBorderSpacing(double borderSpacing) {
        this.borderSpacing.set(borderSpacing);
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

    public double getGap() {
        return gap.get();
    }

    /**
     * For {@link FloatMode#INLINE} mode, this specifies the gap between
     * the floating text node and the input field node.
     */
    public StyleableDoubleProperty gapProperty() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap.set(gap);
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
    // CssMetaData
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

        private static final CssMetaData<MFXTextField, Number> BORDER_SPACING =
                FACTORY.createSizeCssMetaData(
                        "-mfx-border-spacing",
                        MFXTextField::borderSpacingProperty,
                        10.0
                );

        private static final CssMetaData<MFXTextField, Boolean> CARET_VISIBLE =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-caret-animated",
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

        private static final CssMetaData<MFXTextField, Number> GAP =
                FACTORY.createSizeCssMetaData(
                        "-mfx-gap",
                        MFXTextField::gapProperty,
                        5.0
                );

        private static final CssMetaData<MFXTextField, Number> GRAPHIC_TEXT_GAP =
                FACTORY.createSizeCssMetaData(
                        "-fx-graphic-text-gap",
                        MFXTextField::graphicTextGapProperty,
                        10.0
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
                    ANIMATED, BORDER_SPACING, CARET_VISIBLE,
                    EDITABLE, FLOAT_MODE, GAP, GRAPHIC_TEXT_GAP,
                    TEXT_FILL, TEXT_LIMIT
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTextFieldSkin(this, floating);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXTextField.getClassCssMetaData();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
