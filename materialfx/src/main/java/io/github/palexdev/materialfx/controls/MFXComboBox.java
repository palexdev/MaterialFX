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
import io.github.palexdev.materialfx.beans.MFXContextMenuItem;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.selection.ComboSelectionModelMock;
import io.github.palexdev.materialfx.skins.MFXComboBoxSkin;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import io.github.palexdev.materialfx.validation.base.Validated;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.function.Supplier;

import static io.github.palexdev.materialfx.controls.enums.Styles.ComboBoxStyles;

/**
 * This is the implementation of a combo box following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Control} and provides a new skin since it is built from scratch.
 * <p>
 * Side note: unlike JavaFX's one this is NOT editable.
 *
 * @param <T> The type of the value that has been selected
 * @see ComboSelectionModelMock
 */
public class MFXComboBox<T> extends Control implements Validated<MFXDialogValidator> {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXComboBox<?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-combo-box";
    private String STYLESHEET;

    private final StringProperty promptText = new SimpleStringProperty("");
    private final ObjectProperty<T> selectedValue = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>();

    private final DoubleProperty maxPopupWidth = new SimpleDoubleProperty();
    private final DoubleProperty maxPopupHeight = new SimpleDoubleProperty(190);
    private final DoubleProperty popupXOffset = new SimpleDoubleProperty(0);
    private final DoubleProperty popupYOffset = new SimpleDoubleProperty(2);

    private final ComboSelectionModelMock<T> mockSelection;

    private MFXDialogValidator validator;
    private final ObjectProperty<Paint> invalidLineColor = new SimpleObjectProperty<>(Color.web("#EF6E6B"));
    protected static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

    private final ObjectProperty<MFXContextMenu> mfxContextMenu = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBox() {
        this(FXCollections.observableArrayList());
    }

    public MFXComboBox(ObservableList<T> items) {
        this.STYLESHEET = MFXResourcesLoader.load(getComboStyle().getStyleSheetPath());
        this.items.set(items);
        this.mockSelection = new ComboSelectionModelMock<>(this);

        initialize();
    }

    //================================================================================
    // Validation
    //================================================================================

    /**
     * Configures the validator. The first time the error label can appear in two cases:
     * <p></p>
     * 1) The validator {@link AbstractMFXValidator#isInitControlValidation()} flag is true,
     * in this case as soon as the control is laid out in the scene the label visible property is
     * set accordingly to the validator state. (by default is false) <p>
     * 2) When the control lose the focus and the the validator's state is invalid.
     * <p></p>
     * Then the label visible property is automatically updated when the validator state changes.
     * <p></p>
     * The validator is also responsible for updating the ":invalid" PseudoClass.
     */
    private void setupValidator() {
        validator = new MFXDialogValidator("Error");
        validator.setDialogType(DialogType.ERROR);
        validator.validProperty().addListener(invalidated -> {
            if (isValidated()) {
                pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !isValid());
            }
        });

        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if (isValidated()) {
                    if (getValidator().isInitControlValidation()) {
                        pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !isValid());
                    } else {
                        pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                }
        });
    }

    @Override
    public MFXComboBox<T> installValidator(Supplier<MFXDialogValidator> validatorSupplier) {
        if (validatorSupplier == null) {
            throw new IllegalArgumentException("The supplier cannot be null!");
        }
        this.validator = validatorSupplier.get();
        return this;
    }

    @Override
    public MFXDialogValidator getValidator() {
        return validator;
    }

    /**
     * Delegate method to get the validator's title.
     */
    public String getValidatorTitle() {
        return validator.getTitle();
    }

    /**
     * Delegate method to get the validator's title property.
     */
    public StringProperty validatorTitleProperty() {
        return validator.titleProperty();
    }

    /**
     * Delegate method to set the validator's title.
     */
    public void setValidatorTitle(String title) {
        validator.setTitle(title);
    }

    public Paint getInvalidLineColor() {
        return invalidLineColor.get();
    }

    /**
     * Specifies the color of the focused line when the validator state is invalid.
     * <p></p>
     * This workaround is needed because I discovered a rather surprising/shocking bug.
     * If you set the line color in SceneBuilder (didn't test in Java code) and the validator state is invalid,
     * the line won't change color as specified in the CSS file, damn you JavaFX :)
     */
    public ObjectProperty<Paint> invalidLineColorProperty() {
        return invalidLineColor;
    }

    public void setInvalidLineColor(Paint invalidLineColor) {
        this.invalidLineColor.set(invalidLineColor);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        /* Makes possible to choose the control style without depending on the constructor,
         *  it seems to work well but to be honest it would be way better if JavaFX would give us
         * the possibility to change the user agent stylesheet at runtime (I mean by re-calling getUserAgentStylesheet)
         */
        comboStyle.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                STYLESHEET = MFXResourcesLoader.load(newValue.getStyleSheetPath());
                getStylesheets().setAll(STYLESHEET);
            }
        });
        maxPopupWidthProperty().bind(widthProperty());

        mfxContextMenu.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.dispose();
            }
            if (newValue != null) {
                newValue.install(this);
            }
        });

        setupValidator();
        defaultContextMenu();
    }

    protected void defaultContextMenu() {
        MFXContextMenuItem selectFirst = new MFXContextMenuItem(
                "Select First",
                event -> mockSelection.selectFirst()
        );

        MFXContextMenuItem selectNext = new MFXContextMenuItem(
                "Select Next",
                event -> mockSelection.selectNext()
        );

        MFXContextMenuItem selectPrevious = new MFXContextMenuItem(
                "Select Previous",
                event -> mockSelection.selectPrevious()
        );

        MFXContextMenuItem selectLast = new MFXContextMenuItem(
                "Select Last",
                event -> mockSelection.selectLast()
        );

        MFXContextMenuItem resetSelection = new MFXContextMenuItem(
                "Clear Selection",
                event -> mockSelection.clearSelection()
        );

        setMFXContextMenu(
                new MFXContextMenu.Builder()
                        .addMenuItem(selectFirst)
                        .addMenuItem(selectNext)
                        .addMenuItem(selectPrevious)
                        .addMenuItem(selectLast)
                        .addSeparator()
                        .addMenuItem(resetSelection)
                        .get()
        );
    }

    public String getPromptText() {
        return promptText.get();
    }

    public StringProperty promptTextProperty() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    public T getSelectedValue() {
        return selectedValue.get();
    }

    /**
     * The currently selected item.
     */
    public ObjectProperty<T> selectedValueProperty() {
        return selectedValue;
    }

    public void setSelectedValue(T selectedValue) {
        this.selectedValue.set(selectedValue);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    /**
     * The list of items to show within the ComboBox popup.
     */
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public double getMaxPopupWidth() {
        return maxPopupWidth.get();
    }

    /**
     * Specifies the max popup width. Set to -1 to autosize.
     */
    public DoubleProperty maxPopupWidthProperty() {
        return maxPopupWidth;
    }

    public void setMaxPopupWidth(double maxPopupWidth) {
        this.maxPopupWidth.set(maxPopupWidth);
    }

    public double getMaxPopupHeight() {
        return maxPopupHeight.get();
    }

    /**
     * Specifies the max popup height. Set to -1 to autosize.
     */
    public DoubleProperty maxPopupHeightProperty() {
        return maxPopupHeight;
    }

    public void setMaxPopupHeight(double maxPopupHeight) {
        this.maxPopupHeight.set(maxPopupHeight);
    }

    public double getPopupXOffset() {
        return popupXOffset.get();
    }

    /**
     * Specifies the x offset.
     */
    public DoubleProperty popupXOffsetProperty() {
        return popupXOffset;
    }

    public void setPopupXOffset(double popupXOffset) {
        this.popupXOffset.set(popupXOffset);
    }

    public double getPopupYOffset() {
        return popupYOffset.get();
    }

    /**
     * Specifies the y offset.
     */
    public DoubleProperty popupYOffsetProperty() {
        return popupYOffset;
    }

    public void setPopupYOffset(double popupYOffset) {
        this.popupYOffset.set(popupYOffset);
    }

    /**
     * @return the selection model associated to this combo box
     */
    public ComboSelectionModelMock<T> getSelectionModel() {
        return mockSelection;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies the style of the MFXComboBox.
     */
    private final StyleableObjectProperty<ComboBoxStyles> comboStyle = new SimpleStyleableObjectProperty<>(
            StyleableProperties.STYLE,
            this,
            "comboStyle",
            ComboBoxStyles.STYLE3
    );

    /**
     * Specifies if focus lines should be animated.
     */
    private final StyleableBooleanProperty animateLines = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_LINES,
            this,
            "animateLines",
            true
    );

    /**
     * Specifies the focusedLine color.
     */
    private final StyleableObjectProperty<Paint> lineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_COLOR,
            this,
            "lineColor",
            Color.rgb(82, 0, 237)
    );

    /**
     * Specifies the unfocusedLine color.
     */
    private final StyleableObjectProperty<Paint> unfocusedLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNFOCUSED_LINE_COLOR,
            this,
            "unfocusedLineColor",
            Color.rgb(159, 159, 159)
    );

    /**
     * Specifies the lines' stroke width.
     */
    private final StyleableDoubleProperty lineStrokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.LINE_STROKE_WIDTH,
            this,
            "lineStrokeWidth",
            1.0
    );

    private final StyleableBooleanProperty isValidated = new SimpleStyleableBooleanProperty(
            StyleableProperties.IS_VALIDATED,
            this,
            "isValidated",
            false
    );

    public ComboBoxStyles getComboStyle() {
        return comboStyle.get();
    }

    /**
     * Specifies the style used by the combo box.
     */
    public StyleableObjectProperty<ComboBoxStyles> comboStyleProperty() {
        return comboStyle;
    }

    public void setComboStyle(ComboBoxStyles comboStyle) {
        this.comboStyle.set(comboStyle);
    }

    public boolean isAnimateLines() {
        return animateLines.get();
    }

    public StyleableBooleanProperty animateLinesProperty() {
        return animateLines;
    }

    public void setAnimateLines(boolean animateLines) {
        this.animateLines.set(animateLines);
    }

    public Paint getLineColor() {
        return lineColor.get();
    }

    public StyleableObjectProperty<Paint> lineColorProperty() {
        return lineColor;
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor.set(lineColor);
    }

    public Paint getUnfocusedLineColor() {
        return unfocusedLineColor.get();
    }

    public StyleableObjectProperty<Paint> unfocusedLineColorProperty() {
        return unfocusedLineColor;
    }

    public void setUnfocusedLineColor(Paint unfocusedLineColor) {
        this.unfocusedLineColor.set(unfocusedLineColor);
    }

    public double getLineStrokeWidth() {
        return lineStrokeWidth.get();
    }

    public StyleableDoubleProperty lineStrokeWidthProperty() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(double lineStrokeWidth) {
        this.lineStrokeWidth.set(lineStrokeWidth);
    }

    public boolean isValidated() {
        return isValidated.get();
    }

    /**
     * Specifies if validation is required for the control.
     */
    public StyleableBooleanProperty isValidatedProperty() {
        return isValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated.set(isValidated);
    }

    public MFXContextMenu getMFXContextMenu() {
        return mfxContextMenu.get();
    }

    /**
     * Specifies the combobox's {@link MFXContextMenu}.
     */
    public ObjectProperty<MFXContextMenu> mfxContextMenuProperty() {
        return mfxContextMenu;
    }

    public void setMFXContextMenu(MFXContextMenu mfxContextMenu) {
        this.mfxContextMenu.set(mfxContextMenu);
    }
//================================================================================
    // CssMetaData
    //================================================================================

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXComboBox<?>, ComboBoxStyles> STYLE =
                FACTORY.createEnumCssMetaData(
                        ComboBoxStyles.class,
                        "-mfx-style",
                        MFXComboBox::comboStyleProperty,
                        ComboBoxStyles.STYLE1
                );

        private static final CssMetaData<MFXComboBox<?>, Boolean> ANIMATE_LINES =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-lines",
                        MFXComboBox::animateLinesProperty,
                        true
                );

        private static final CssMetaData<MFXComboBox<?>, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXComboBox::lineColorProperty,
                        Color.rgb(82, 0, 237)
                );

        private static final CssMetaData<MFXComboBox<?>, Paint> UNFOCUSED_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unfocused-line-color",
                        MFXComboBox::unfocusedLineColorProperty,
                        Color.rgb(159, 159, 159)
                );

        private static final CssMetaData<MFXComboBox<?>, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXComboBox::lineStrokeWidthProperty,
                        1.0
                );

        private static final CssMetaData<MFXComboBox<?>, Boolean> IS_VALIDATED =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-validate",
                        MFXComboBox::isValidatedProperty,
                        false
                );


        static {
            cssMetaDataList = List.of(
                    STYLE,
                    ANIMATE_LINES, LINE_COLOR, UNFOCUSED_LINE_COLOR, LINE_STROKE_WIDTH,
                    IS_VALIDATED
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
        return new MFXComboBoxSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXComboBox.getControlCssMetaDataList();
    }
}
