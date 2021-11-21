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

import java.util.List;

// TODO documentation
public class MFXTextField extends TextField {
    private final String STYLE_CLASS = "mfx-text-field";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTextField.css");

    private final BooleanProperty selectable = new SimpleBooleanProperty(true);
    private final ObjectProperty<Node> leadingIcon = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

    // TODO add context menu after conversion to MFXPopup
    // TODO add validation

    private final StringProperty floatingText = new SimpleStringProperty();
    protected final ReadOnlyBooleanWrapper floating = new ReadOnlyBooleanWrapper(false);
    private static final PseudoClass FLOATING_PSEUDO_CLASS = PseudoClass.getPseudoClass("floating");

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

    private void initialize() {
        getStyleClass().setAll(STYLE_CLASS);
        floating.addListener(invalidated -> pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, floating.get()));

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

    public boolean isSelectable() {
        return selectable.get();
    }

    public BooleanProperty selectableProperty() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    public Node getLeadingIcon() {
        return leadingIcon.get();
    }

    public ObjectProperty<Node> leadingIconProperty() {
        return leadingIcon;
    }

    public void setLeadingIcon(Node leadingIcon) {
        this.leadingIcon.set(leadingIcon);
    }

    public Node getTrailingIcon() {
        return trailingIcon.get();
    }

    public ObjectProperty<Node> trailingIconProperty() {
        return trailingIcon;
    }

    public void setTrailingIcon(Node trailingIcon) {
        this.trailingIcon.set(trailingIcon);
    }

    public String getFloatingText() {
        return floatingText.get();
    }

    public StringProperty floatingTextProperty() {
        return floatingText;
    }

    public void setFloatingText(String floatingText) {
        this.floatingText.set(floatingText);
    }

    public boolean isFloating() {
        return floating.get();
    }

    public ReadOnlyBooleanProperty floatingProperty() {
        return floating.getReadOnlyProperty();
    }

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

    private final StyleableIntegerProperty textLimit = new StyleableIntegerProperty(
            StyleableProperties.TEXT_LIMIT,
            this,
            "textLimit",
            -1
    );

    public boolean isAnimated() {
        return animated.get();
    }

    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getBorderSpacing() {
        return borderSpacing.get();
    }

    public StyleableDoubleProperty borderSpacingProperty() {
        return borderSpacing;
    }

    public void setBorderSpacing(double borderSpacing) {
        this.borderSpacing.set(borderSpacing);
    }

    public FloatMode getFloatMode() {
        return floatMode.get();
    }

    public StyleableObjectProperty<FloatMode> floatModeProperty() {
        return floatMode;
    }

    public void setFloatMode(FloatMode floatMode) {
        this.floatMode.set(floatMode);
    }

    public double getGap() {
        return gap.get();
    }

    public StyleableDoubleProperty gapProperty() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap.set(gap);
    }

    public double getGraphicTextGap() {
        return graphicTextGap.get();
    }

    public StyleableDoubleProperty graphicTextGapProperty() {
        return graphicTextGap;
    }

    public void setGraphicTextGap(double graphicTextGap) {
        this.graphicTextGap.set(graphicTextGap);
    }

    public int getTextLimit() {
        return textLimit.get();
    }

    public StyleableIntegerProperty textLimitProperty() {
        return textLimit;
    }

    public void setTextLimit(int textLimit) {
        this.textLimit.set(textLimit);
    }

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

        private static final CssMetaData<MFXTextField, Number> TEXT_LIMIT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-text-limit",
                        MFXTextField::textLimitProperty,
                        -1
                );

        static {
            cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
                    TextField.getClassCssMetaData(),
                    ANIMATED, BORDER_SPACING, FLOAT_MODE, GAP, GRAPHIC_TEXT_GAP, TEXT_LIMIT
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

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
