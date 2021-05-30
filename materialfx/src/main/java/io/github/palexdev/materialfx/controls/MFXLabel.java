/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXLabelSkin;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.List;

import static io.github.palexdev.materialfx.controls.enums.Styles.LabelStyles;

/**
 * This is the implementation of a label following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Control} and provides a new skin since it is built from scratch.
 * <p>
 * Side note: lacks some features like text wrapping, overrun and ellipsis but there are also
 * new features like leading and trailing icons support, prompt text, changeable styles at runtime
 * and it can also be set to editable like a text field (double click on the label to edit).
 */
public class MFXLabel extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXLabel> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-label";
    private String STYLESHEET;

    private final StringProperty text = new SimpleStringProperty();
    private final StringProperty promptText = new SimpleStringProperty("Label");

    private final ObjectProperty<Pos> labelAlignment = new SimpleObjectProperty<>(Pos.CENTER);
    private final ObjectProperty<Node> leadingIcon = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

    private final ObjectProperty<Pos> alignment = new SimpleObjectProperty<>(Pos.CENTER);

    private static final PseudoClass EDITOR_FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("editor");
    private final BooleanProperty editorFocused = new SimpleBooleanProperty();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabel() {
        this("");
    }

    public MFXLabel(String text) {
        setText(text);
        this.STYLESHEET = MFXResourcesLoader.load(getLabelStyle().getStyleSheetPath());
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        editorFocused.addListener(invalidated ->pseudoClassStateChanged(EDITOR_FOCUSED_PSEUDO_CLASS, editorFocused.get()));

        /* Makes possible to choose the control style without depending on the constructor,
         * it seems to work well but to be honest it would be way better if JavaFX would give us
         * the possibility to change the user agent stylesheet at runtime (I mean by re-calling getUserAgentStylesheet)
         */
        labelStyle.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                STYLESHEET = MFXResourcesLoader.load(newValue.getStyleSheetPath());
                getStylesheets().setAll(STYLESHEET);
            }
        });
    }

    /**
     * @return the Label node wrapped by MFXLabel which has the text
     */
    public Label getTextNode() {
        return (Label) lookup(".text-node");
    }

    public String getText() {
        return text.get();
    }

    /**
     * The text to display in the label.
     */
    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getPromptText() {
        return promptText.get();
    }

    /**
     * The prompt text to display in case the {@link #textProperty()} is empty.
     */
    public StringProperty promptTextProperty() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    public Pos getLabelAlignment() {
        return labelAlignment.get();
    }

    /**
     * The alignment of the label's text.
     */
    public ObjectProperty<Pos> labelAlignmentProperty() {
        return labelAlignment;
    }

    public void setLabelAlignment(Pos labelAlignment) {
        this.labelAlignment.set(labelAlignment);
    }

    public Node getLeadingIcon() {
        return leadingIcon.get();
    }

    /**
     * The leading icon node.
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
     * The trailing icon node.
     */
    public ObjectProperty<Node> trailingIconProperty() {
        return trailingIcon;
    }

    public void setTrailingIcon(Node trailingIcon) {
        this.trailingIcon.set(trailingIcon);
    }

    public Pos getAlignment() {
        return alignment.get();
    }

    /**
     * The alignment of the container.
     */
    public ObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    public boolean isEditorFocused() {
        return editorFocused.get();
    }

    /**
     * Bound to the editor focus property. This allows to keep the focused style specified
     * by css when the focus is acquired by the editor. The PseudoClass to use in css is ":editor"
     */
    public BooleanProperty editorFocusedProperty() {
        return editorFocused;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    private final StyleableObjectProperty<Font> font = new SimpleStyleableObjectProperty<>(
            StyleableProperties.FONT,
            this,
            "font",
            Font.getDefault()
    );

    private final StyleableObjectProperty<Color> textFill = new SimpleStyleableObjectProperty<>(
            StyleableProperties.TEXT_FILL,
            this,
            "textFill",
            Color.BLACK
    );

    private final StyleableObjectProperty<LabelStyles> labelStyle = new SimpleStyleableObjectProperty<>(
            StyleableProperties.STYLE,
            this,
            "labelStyle",
            LabelStyles.STYLE1
    );

    private final StyleableDoubleProperty graphicTextGap = new SimpleStyleableDoubleProperty(
            StyleableProperties.GRAPHIC_TEXT_GAP,
            this,
            "graphicTextGap",
            5.0
    );

    private final StyleableBooleanProperty editable = new SimpleStyleableBooleanProperty(
            StyleableProperties.EDITABLE,
            this,
            "editable",
            false
    );

    private final StyleableBooleanProperty animateLines = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_LINES,
            this,
            "animateLines",
            true
    );

    private final StyleableObjectProperty<Paint> lineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_COLOR,
            this,
            "lineColor",
            Color.rgb(82, 0, 237)
    );

    private final StyleableObjectProperty<Paint> unfocusedLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNFOCUSED_LINE_COLOR,
            this,
            "unfocusedLineColor",
            Color.rgb(159, 159, 159)
    );

    private final StyleableDoubleProperty lineStrokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.LINE_STROKE_WIDTH,
            this,
            "lineStrokeWidth",
            1.0
    );

    public Font getFont() {
        return font.get();
    }

    /**
     * The font to use for the text.
     */
    public StyleableObjectProperty<Font> fontProperty() {
        return font;
    }

    public void setFont(Font font) {
        this.font.set(font);
    }

    public Color getTextFill() {
        return textFill.get();
    }

    /**
     * Specifies the color of the text.
     */
    public StyleableObjectProperty<Color> textFillProperty() {
        return textFill;
    }

    public void setTextFill(Color textFill) {
        this.textFill.set(textFill);
    }

    public LabelStyles getLabelStyle() {
        return labelStyle.get();
    }

    /**
     * Specifies the label css style.
     */
    public StyleableObjectProperty<LabelStyles> labelStyleProperty() {
        return labelStyle;
    }

    public void setLabelStyle(LabelStyles labelStyle) {
        this.labelStyle.set(labelStyle);
    }

    public double getGraphicTextGap() {
        return graphicTextGap.get();
    }

    /**
     * Specifies the space between the label's leading and trailing icons.
     */
    public StyleableDoubleProperty graphicTextGapProperty() {
        return graphicTextGap;
    }

    public void setGraphicTextGap(double graphicTextGap) {
        this.graphicTextGap.set(graphicTextGap);
    }

    public boolean isEditable() {
        return editable.get();
    }

    /**
     * Specifies whether the label is editable or not.
     */
    public StyleableBooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public boolean isAnimateLines() {
        return animateLines.get();
    }

    /**
     * Specifies if the line should be animated when focus changes. (works only with STYLE1)
     */
    public StyleableBooleanProperty animateLinesProperty() {
        return animateLines;
    }

    public void setAnimateLines(boolean animateLines) {
        this.animateLines.set(animateLines);
    }

    public Paint getLineColor() {
        return lineColor.get();
    }

    /**
     * Specifies the focused line color.
     */
    public StyleableObjectProperty<Paint> lineColorProperty() {
        return lineColor;
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor.set(lineColor);
    }

    public Paint getUnfocusedLineColor() {
        return unfocusedLineColor.get();
    }

    /**
     * Specifies the unfocused line color.
     */
    public StyleableObjectProperty<Paint> unfocusedLineColorProperty() {
        return unfocusedLineColor;
    }

    public void setUnfocusedLineColor(Paint unfocusedLineColor) {
        this.unfocusedLineColor.set(unfocusedLineColor);
    }

    public double getLineStrokeWidth() {
        return lineStrokeWidth.get();
    }

    /**
     * Specifies the lines stroke width.
     */
    public StyleableDoubleProperty lineStrokeWidthProperty() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(double lineStrokeWidth) {
        this.lineStrokeWidth.set(lineStrokeWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXLabel, Font> FONT =
                FACTORY.createFontCssMetaData(
                        "-mfx-font",
                        MFXLabel::fontProperty,
                        Font.getDefault()
                );

        private static final CssMetaData<MFXLabel, Color> TEXT_FILL =
                FACTORY.createColorCssMetaData(
                        "-mfx-text-fill",
                        MFXLabel::textFillProperty,
                        Color.BLACK
                );

        private static final CssMetaData<MFXLabel, LabelStyles> STYLE =
                FACTORY.createEnumCssMetaData(
                        LabelStyles.class,
                        "-mfx-style",
                        MFXLabel::labelStyleProperty,
                        LabelStyles.STYLE1
                );

        private static final CssMetaData<MFXLabel, Number> GRAPHIC_TEXT_GAP =
                FACTORY.createSizeCssMetaData(
                        "-mfx-gap",
                        MFXLabel::graphicTextGapProperty,
                        5.0
                );

        private static final CssMetaData<MFXLabel, Boolean> EDITABLE =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-editable",
                        MFXLabel::editableProperty,
                        false
                );

        private static final CssMetaData<MFXLabel, Boolean> ANIMATE_LINES =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-lines",
                        MFXLabel::animateLinesProperty,
                        true
                );

        private static final CssMetaData<MFXLabel, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXLabel::lineColorProperty,
                        Color.rgb(82, 0, 237)
                );

        private static final CssMetaData<MFXLabel, Paint> UNFOCUSED_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unfocused-line-color",
                        MFXLabel::unfocusedLineColorProperty,
                        Color.rgb(159, 159, 159)
                );

        private static final CssMetaData<MFXLabel, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXLabel::lineStrokeWidthProperty,
                        1.0
                );

        static {
            cssMetaDataList = List.of(
                    FONT, TEXT_FILL, STYLE, GRAPHIC_TEXT_GAP, EDITABLE,
                    ANIMATE_LINES, LINE_COLOR, UNFOCUSED_LINE_COLOR, LINE_STROKE_WIDTH
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
        return new MFXLabelSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXLabel.getControlCssMetaDataList();
    }
}
