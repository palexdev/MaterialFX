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

package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * This dialog allows implements a filtering mechanism based on boolean
 * expressions on strings.
 * <p>
 * Since {@link MFXStageDialog} can't be extended in this case because the constructors need
 * a {@code MFXDialog}, it extends {@link MFXDialog}, builds the stage dialog passing itself as reference
 * and overrides the open() and close() methods to use the {@link MFXStageDialog} ones.
 * <p></p>
 * <b>Structure</b>
 * <p>
 * A label allows to add a new HBox which contains: an icon to remove itself if not needed anymore,
 * two toggles to specify if the expression should an "AND" or an "OR" condition, a combo box to choose
 * the function to apply on the passed string and the given string, one or more text fields
 * which contain the text used in the evaluations, and a button, {@link #getFilterButton()}.
 * <p>
 * <b>Functioning</b>
 * <p>
 * The main method is {@link #filter(String)}. It takes a string and evaluates all the given conditions
 * on that string, returning the computed boolean result.
 */
public class MFXFilterDialog extends MFXDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-filter-dialog";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-filter-dialog.css");

    private final VBox container;
    private final VBox textFieldsContainer;
    private final MFXButton filterButton;

    private final MFXStageDialog stage;
    private final MFXIconWrapper closeIcon;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterDialog() {
        setTitle("Filter Dialog");
        setPrefWidth(550);

        Separator s1 = new Separator(Orientation.HORIZONTAL);
        Separator s2 = new Separator(Orientation.HORIZONTAL);
        container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.getStylesheets().addAll(STYLESHEET);

        MFXLabel label = new MFXLabel();
        label.setLabelStyle(Styles.LabelStyles.STYLE2);
        label.textProperty().bind(title);
        label.setAlignment(Pos.CENTER);
        label.setLabelAlignment(Pos.CENTER);
        label.getStylesheets().setAll(STYLESHEET);
        VBox.setMargin(label, new Insets(7, 0, 7, 0));

        MFXIconWrapper add = new MFXIconWrapper(new MFXFontIcon("mfx-search-plus"), 20).addRippleGenerator();
        NodeUtils.makeRegionCircular(add);
        RippleGenerator rg = add.getRippleGenerator();
        add.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rg.setGeneratorCenterX(event.getX());
            rg.setGeneratorCenterY(event.getX());
            rg.createRipple();
        });
        add.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> addTextField());
        label.setTrailingIcon(add);
        label.skinProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                if (newValue != null) {
                    addTextField();
                    label.skinProperty().removeListener(this);
                }
            }
        });

        textFieldsContainer = new VBox();
        MFXScrollPane scrollPane = new MFXScrollPane(textFieldsContainer);
        scrollPane.setPrefHeight(400);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-insets: 3");

        filterButton = new MFXButton("Filter");
        filterButton.setButtonType(ButtonType.FLAT);
        filterButton.setMinWidth(80);
        VBox.setMargin(filterButton, new Insets(5, 0, 5, 0));

        container.getChildren().addAll(label, s1, scrollPane, s2, filterButton);

        setScrimBackground(false);
        setCenter(container);

        initialize();

        stage = new MFXStageDialog(this);
        stage.setAllowDrag(false);

        closeIcon = new MFXIconWrapper(new MFXFontIcon("mfx-x"), 40);
        closeIcon.setManaged(false);
        closeIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> close());
        getChildren().add(closeIcon);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    /**
     * Adds a new {@code FilterField} to the dialog.
     */
    protected void addTextField() {
        FilterField filterField = new FilterField();
        filterField.getRemoveIcon().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (textFieldsContainer.getChildren().size() == 1) {
                return;
            }
            textFieldsContainer.getChildren().remove(filterField);
        });
        textFieldsContainer.getChildren().add(filterField);
    }

    /**
     * Evaluates all the conditions specified by the dialog's {@code FilterFields} on
     * the given item and returns if it meets the specified conditions or not.
     *
     * @param item the string on which evaluate the conditions
     */
    public boolean filter(String item) {
        List<FilterField> filterFields = textFieldsContainer.getChildren().stream()
                .filter(node -> node instanceof FilterField)
                .map(node -> (FilterField) node)
                .collect(Collectors.toList());

        Boolean expression = null;
        for (FilterField field : filterFields) {
            if (expression == null) {
                expression = field.callEvaluation(item);
                continue;
            }

            boolean newExpr;
            if (field.isAnd()) {
                newExpr = expression && field.callEvaluation(item);
            } else {
                newExpr = expression || field.callEvaluation(item);
            }
            expression = newExpr;
        }

        return expression != null ? expression : false;
    }

    /**
     * Returns the dialog's button reference.
     * <p>
     * In {@link io.github.palexdev.materialfx.skins.MFXTableViewSkin} for example, it is used
     * to add an event handler to the button which starts the filtering and closes the dialog.
     */
    public MFXButton getFilterButton() {
        return filterButton;
    }

    /**
     * @return the stage dialog reference.
     */
    public MFXStageDialog getStage() {
        return stage;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        closeIcon.relocate(getWidth() - 17, 17);
    }

    /**
     * Shows the stage dialog.
     */
    @Override
    public void show() {
        stage.show();
    }

    /**
     * Closes the stage dialog.
     */
    @Override
    public void close() {
        stage.close();
    }

    /**
     * This is the class used in the filter dialog for the filter boxes.
     * <p>
     * It's this node which contains the remove icon, the toggles, the combo box and the text field.
     * <p>
     * It uses a {@code Map<String, BiPredicate<String, String>>} to map the combo box choice to the function
     * which will we applied on the given strings, {@link MFXFilterDialog#filter(String)}.
     */
    private class FilterField extends HBox {
        //================================================================================
        // Properties
        //================================================================================
        private final Map<String, BiPredicate<String, String>> evaluators = new LinkedHashMap<>();

        private final MFXIconWrapper icon;
        private final MFXTextField textField;
        private final MFXComboBox<String> evaluationCombo;

        private final BooleanProperty isAnd = new SimpleBooleanProperty(true);

        //================================================================================
        // Constructors
        //================================================================================
        public FilterField() {
            populateMap();
            getStylesheets().addAll(STYLESHEET);

            setAlignment(Pos.CENTER);
            setSpacing(5);
            setPadding(new Insets(5));

            MFXFontIcon minus = new MFXFontIcon("mfx-minus");
            icon = new MFXIconWrapper(minus, 12);
            icon.setOpacity(0.0);

            textField = new MFXTextField();
            textField.getStyleClass().add("text-filter");
            textField.setPromptText("String filter...");
            textField.setAlignment(Pos.CENTER);
            textField.setLineColor(Color.rgb(82, 0, 237));

            evaluationCombo = new MFXComboBox<>();

            //box.setMinWidth(width);
            hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    MFXAnimationFactory.FADE_IN.build(icon, 300).play();
                } else {
                    MFXAnimationFactory.FADE_OUT.build(icon, 300).play();
                }
            });

            getChildren().addAll(icon, buildOptions(), textField);
        }

        //================================================================================
        // Methods
        //================================================================================
        private Node buildOptions() {
            HBox box = new HBox(5);

            MFXToggleButton and = new MFXToggleButton("And");
            and.setSelected(true);
            and.setAutomaticColorAdjustment(true);
            and.setToggleColor(Color.rgb(82, 0, 237));

            MFXToggleButton or = new MFXToggleButton("Or");
            or.setAutomaticColorAdjustment(true);
            or.setToggleColor(Color.rgb(82, 0, 237));

            ToggleGroup group = new ToggleGroup();
            and.setToggleGroup(group);
            or.setToggleGroup(group);
            ToggleButtonsUtil.addAlwaysOneSelectedSupport(group);

            isAnd.bind(and.selectedProperty());

            ObservableList<String> evaluatorsKeys = FXCollections.observableArrayList(evaluators.keySet());
            evaluationCombo.setItems(evaluatorsKeys);
            evaluationCombo.setMinWidth(150);
            evaluationCombo.setComboStyle(Styles.ComboBoxStyles.STYLE2);
            evaluationCombo.setMaxPopupHeight(-1);
            evaluationCombo.skinProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                    if (newValue != null) {
                        evaluationCombo.getSelectionModel().selectFirst();
                        evaluationCombo.skinProperty().removeListener(this);
                    }
                }
            });
            HBox.setMargin(evaluationCombo, new Insets(10, 10, 0, 0));

            box.getChildren().addAll(and, or, evaluationCombo);
            return box;
        }

        /**
         * Populates the map with:
         * <p>
         * - Contains -> String::contains <p>
         * - Contains Ignore Case -> StringUtils::containsIgnoreCase <p>
         * - Starts With -> String::startsWith <p>
         * - Ends With -> String::endsWith <p>
         * - Equals -> String::equals <p>
         * - Equals Ignore Case -> String::equalsIgnoreCase <p>
         *
         * @see StringUtils
         */
        private void populateMap() {
            evaluators.put("Contains", String::contains);
            evaluators.put("Contains Ignore Case", StringUtils::containsIgnoreCase);
            evaluators.put("Starts With", String::startsWith);
            evaluators.put("Ends With", String::endsWith);
            evaluators.put("Equals", String::equals);
            evaluators.put("Equals Ignore Case", String::equalsIgnoreCase);
        }

        public MFXIconWrapper getRemoveIcon() {
            return icon;
        }

        /**
         * Retrieves the BiPredicate from the map with the combo box selected value as key and
         * applies the function to the given string.
         */
        public Boolean callEvaluation(String item) {
            return evaluators.get(evaluationCombo.getSelectedValue()).test(item, textField.getText());
        }

        /**
         * Returns whether the selected toggle is the "And" toggle,
         * if it is false then the selected toggle is the "Or" toggle because
         * the toggles are in a group which uses {@link ToggleButtonsUtil#addAlwaysOneSelectedSupport(ToggleGroup)}
         */
        public boolean isAnd() {
            return isAnd.get();
        }
    }
}
