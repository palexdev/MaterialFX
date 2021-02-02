package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXTextFieldSkin;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.css.*;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * This is the implementation of a TextField restyled to comply with modern standards.
 * <p>
 * Extends {@code TextField}, redefines the style class to "mfx-text-field" for usage in CSS and
 * includes a {@code MFXDialogValidator} for input validation.
 * <p>
 * <b>Note: validator conditions are empty by default</b>
 */
public class MFXTextField extends TextField {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXTextField> FACTORY = new StyleablePropertyFactory<>(TextField.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-text-field";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-textfield.css").toString();
    private final StyleableIntegerProperty textLimit = new SimpleStyleableIntegerProperty(
            StyleableProperties.TEXT_LIMIT,
            this,
            "maxLength",
            -1
    );
    /**
     * Specifies the line's color when the control is focused.
     */
    private final StyleableObjectProperty<Paint> lineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_COLOR,
            this,
            "lineColor",
            Color.rgb(50, 120, 220)
    );
    /**
     * Specifies the line's color when the control is not focused.
     */
    private final StyleableObjectProperty<Paint> unfocusedLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNFOCUSED_LINE_COLOR,
            this,
            "unfocusedLineColor",
            Color.rgb(77, 77, 77)
    );
    /**
     * Specifies the lines' width.
     */
    private final StyleableDoubleProperty lineStrokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.LINE_STROKE_WIDTH,
            this,
            "lineStrokeWidth",
            1.5
    );
    /**
     * Specifies if the lines switch between focus/un-focus should be animated.
     */
    private final StyleableBooleanProperty animateLines = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_LINES,
            this,
            "animateLines",
            true
    );
    /**
     * Specifies if validation is required for the control.
     */
    private final StyleableBooleanProperty validated = new SimpleStyleableBooleanProperty(
            StyleableProperties.IS_VALIDATED,
            this,
            "isValidated",
            false
    );

    //================================================================================
    // Styleable Properties
    //================================================================================
    private MFXDialogValidator validator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTextField() {
        this("");
    }

    public MFXTextField(String text) {
        super(text);
        initialize();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setupValidator();

        textProperty().addListener((observable, oldValue, newValue) -> {
            int limit = getTextLimit();
            if (limit == -1) {
                return;
            }

            if (newValue.length() > limit) {
                String s = newValue.substring(0, limit);
                setText(s);
            }
        });
    }

    /**
     * Configures the validator. If {@link #isValidated()} is true, by default shows a warning
     * if no item is selected. The warning is showed as soon as the control is out of focus.
     */
    private void setupValidator() {
        validator = new MFXDialogValidator("Warning");
    }

    /**
     * Returns the validator instance of this control.
     */
    public MFXDialogValidator getValidator() {
        return validator;
    }

    public int getTextLimit() {
        return textLimit.get();
    }

    public void setTextLimit(int textLimit) {
        this.textLimit.set(textLimit);
    }

    public StyleableIntegerProperty textLimitProperty() {
        return textLimit;
    }

    public Paint getLineColor() {
        return lineColor.get();
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor.set(lineColor);
    }

    public StyleableObjectProperty<Paint> lineColorProperty() {
        return lineColor;
    }

    public Paint getUnfocusedLineColor() {
        return unfocusedLineColor.get();
    }

    public void setUnfocusedLineColor(Paint unfocusedLineColor) {
        this.unfocusedLineColor.set(unfocusedLineColor);
    }

    public StyleableObjectProperty<Paint> unfocusedLineColorProperty() {
        return unfocusedLineColor;
    }

    public double getLineStrokeWidth() {
        return lineStrokeWidth.get();
    }

    public void setLineStrokeWidth(double lineStrokeWidth) {
        this.lineStrokeWidth.set(lineStrokeWidth);
    }

    public StyleableDoubleProperty lineStrokeWidthProperty() {
        return lineStrokeWidth;
    }

    public boolean isAnimateLines() {
        return animateLines.get();
    }

    public void setAnimateLines(boolean animateLines) {
        this.animateLines.set(animateLines);
    }

    public StyleableBooleanProperty animateLinesProperty() {
        return animateLines;
    }

    public boolean isValidated() {
        return validated.get();
    }

    public StyleableBooleanProperty isValidatedProperty() {
        return validated;
    }

    public void setIsValidated(boolean isValidated) {
        this.validated.set(isValidated);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTextFieldSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXTextField.getControlCssMetaDataList();
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXTextField, Number> TEXT_LIMIT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-text-limit",
                        MFXTextField::textLimitProperty,
                        -1
                );

        private static final CssMetaData<MFXTextField, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXTextField::lineColorProperty,
                        Color.rgb(50, 150, 205)
                );

        private static final CssMetaData<MFXTextField, Paint> UNFOCUSED_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unfocused-line-color",
                        MFXTextField::unfocusedLineColorProperty,
                        Color.rgb(77, 77, 77)
                );

        private final static CssMetaData<MFXTextField, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXTextField::lineStrokeWidthProperty,
                        1.5
                );

        private static final CssMetaData<MFXTextField, Boolean> ANIMATE_LINES =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-lines",
                        MFXTextField::animateLinesProperty,
                        true
                );

        private static final CssMetaData<MFXTextField, Boolean> IS_VALIDATED =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-validate",
                        MFXTextField::isValidatedProperty,
                        false
                );

        static {
            cssMetaDataList = List.of(TEXT_LIMIT, LINE_COLOR, UNFOCUSED_LINE_COLOR, LINE_STROKE_WIDTH, IS_VALIDATED);
        }

    }
}
