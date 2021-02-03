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
import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.skins.MFXComboBoxSkin;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * This is the implementation of a combo box following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code ComboBox}, redefines the style class to "mfx-combo-box" for usage in CSS and
 * includes a {@link MFXDialogValidator}.
 * <p></p>
 * A few notes on features and usage:
 * <p>
 * If you check {@link ComboBox} documentation you will see a big warning about using nodes as content
 * because the scenegraph only allows for Nodes to be in one place at a time.
 * I found a workaround to this issue using {@link #snapshot(SnapshotParameters, WritableImage)}.
 * Basically I make a "screenshot" of the graphic and then I use an {@code ImageView} to show it.
 * <p>
 * So let's say you have a combo box of labels with icons as graphic, when you select an item, it won't disappear anymore
 * from the list because what you are seeing it's not the real graphic but a screenshot of it.
 * <p>
 * I recommend to use only nodes which are instances of {@code Labeled} since the {@code toString()} method is overridden
 * to return the control's text.
 * @see MFXSnapshotWrapper
 */
public class MFXComboBox<T> extends ComboBox<T> {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXComboBox<?>> FACTORY = new StyleablePropertyFactory<>(ComboBox.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-combo-box";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-combobox.css").toString();

    private MFXDialogValidator validator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBox() {
        initialize();
    }

    public MFXComboBox(ObservableList<T> observableList) {
        super(observableList);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setCellFactory(listCell -> new MFXListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                getChildren().remove(lookup(".ripple-generator"));
            }
        });

        setButtonCell(new ListCell<>() {
            {
                valueProperty().addListener(observable -> {
                    if (getValue() == null) {
                        updateItem(null, true);
                    }
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                updateComboItem(this, item, empty);
            }
        });

        setupValidator();
    }

    /**
     * Defines the behavior of the button cell.
     * <p>
     * If it's empty or the item is null, shows the prompt text.
     * <p>
     * If the item is instanceof {@code Labeled} makes a "screenshot" of the graphic if not null,
     * and gets item's text. Otherwise calls {@code toString()} on the item.
     */
    private void updateComboItem(ListCell<T> cell, T item, boolean empty) {

        if (empty || item == null) {
            cell.setGraphic(null);
            cell.setText(getPromptText());
            return;
        }

        if (item instanceof Labeled) {
            Labeled nodeItem = (Labeled) item;
            if (nodeItem.getGraphic() != null) {
                cell.setGraphic(new MFXSnapshotWrapper(nodeItem.getGraphic()).getGraphic());
            }
            cell.setText(nodeItem.getText());
        } else {
            cell.setText(item.toString());
        }
    }

    /**
     * Configures the validator. If {@link #isValidated()} is true, by default shows a warning
     * if no item is selected. The warning is showed as soon as the control is out of focus.
     */
    private void setupValidator() {
        BooleanProperty validIndex = new SimpleBooleanProperty(false);
        validIndex.bind(getSelectionModel().selectedIndexProperty().isNotEqualTo(-1));
        validator = new MFXDialogValidator("Warning");
        validator.add(validIndex, "Selected index is not valid");
    }

    /**
     * Returns the validator instance of this control.
     */
    public MFXDialogValidator getValidator() {
        return validator;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

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
    private final StyleableBooleanProperty isValidated = new SimpleStyleableBooleanProperty(
            StyleableProperties.IS_VALIDATED,
            this,
            "isValidated",
            false
    );

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

    public boolean isAnimateLines() {
        return animateLines.get();
    }

    public StyleableBooleanProperty animateLinesProperty() {
        return animateLines;
    }

    public void setAnimateLines(boolean animateLines) {
        this.animateLines.set(animateLines);
    }

    public boolean isValidated() {
        return isValidated.get();
    }

    public StyleableBooleanProperty isValidatedProperty() {
        return isValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated.set(isValidated);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXComboBox<?>, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXComboBox::lineColorProperty,
                        Color.rgb(50, 150, 205)
                );

        private static final CssMetaData<MFXComboBox<?>, Paint> UNFOCUSED_LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unfocused-line-color",
                        MFXComboBox::unfocusedLineColorProperty,
                        Color.rgb(77, 77, 77)
                );

        private final static CssMetaData<MFXComboBox<?>, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXComboBox::lineStrokeWidthProperty,
                        1.5
                );

        private static final CssMetaData<MFXComboBox<?>, Boolean> ANIMATE_LINES =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-lines",
                        MFXComboBox::animateLinesProperty,
                        true
                );

        private static final CssMetaData<MFXComboBox<?>, Boolean> IS_VALIDATED =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-validate",
                        MFXComboBox::isValidatedProperty,
                        false
                );

        static {
            cssMetaDataList = List.of(LINE_COLOR, UNFOCUSED_LINE_COLOR, LINE_STROKE_WIDTH, IS_VALIDATED);
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
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXComboBox.getControlCssMetaDataList();
    }
}
