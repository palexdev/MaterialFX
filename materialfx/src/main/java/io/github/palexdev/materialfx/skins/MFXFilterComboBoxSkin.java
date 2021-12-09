/*
 * Copyright (C) 2021 Parisi Alessandro
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

import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessListCell;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.ComboSelectionModelMock;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.function.Predicate;

// TODO implement StringConverter (low priority)

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXFilterComboBox}.
 */
public class MFXFilterComboBoxSkin<T> extends SkinBase<MFXFilterComboBox<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final MFXLabel valueLabel;
    private final double minWidth = 120;

    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXFlowlessListView<T> listView;
    private final EventHandler<MouseEvent> popupHandler;

    private final Line unfocusedLine;
    private final Line focusedLine;
    private final Label validate;
    private final double padding = 11;

    private final HBox searchContainer;
    private FilteredList<T> filteredList;
    private MFXTextField searchField;

    private Animation arrowAnimation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterComboBoxSkin(MFXFilterComboBox<T> comboBox) {
        super(comboBox);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));
        unfocusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        unfocusedLine.setManaged(false);
        unfocusedLine.setSmooth(true);

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));
        focusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        focusedLine.setManaged(false);
        focusedLine.setScaleX(0.0);
        focusedLine.setSmooth(true);

        MFXFontIcon warnIcon = new MFXFontIcon("mfx-exclamation-triangle", Color.web("#EF6E6B"));
        warnIcon.setId("validationIcon");
        MFXIconWrapper warnWrapper = new MFXIconWrapper(warnIcon, 10);

        validate = new Label();
        validate.setGraphic(warnWrapper);
        validate.getStyleClass().add("validate-label");
        validate.getStylesheets().setAll(comboBox.getUserAgentStylesheet());
        validate.textProperty().bind(comboBox.getValidator().validatorMessageProperty());
        validate.setGraphicTextGap(padding);
        validate.setVisible(false);
        validate.setManaged(false);

        if (comboBox.isValidated() && comboBox.getValidator().isInitControlValidation()) {
            validate.setVisible(!comboBox.isValid());
        }

        filteredList = new FilteredList<>(comboBox.getItems());

        valueLabel = buildLabel();

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24);
        icon.setManaged(false);
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel);
        container.setAlignment(Pos.CENTER_LEFT);

        searchContainer = new HBox(10);
        searchContainer.setId("search-node");
        searchContainer.setPadding(new Insets(0, 5, 0, 5));
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setManaged(false);

        listView = new MFXFlowlessListView<>();
        listView.setCellFactory(item -> new FilterListCell<>(comboBox, item));
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());

        popup = buildPopup();
        popupHandler = event -> {
            if (popup.isShowing() && !NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), comboBox)) {
                buildAndPlayLinesAnimation(false);
                reset();
            }
        };

        if (comboBox.getComboStyle() != Styles.ComboBoxStyles.STYLE2) {
            getChildren().addAll(container, icon, unfocusedLine, focusedLine, validate);
        } else {
            getChildren().addAll(container, icon, validate);
        }

        setBehavior();
        initSelection();
    }

    //================================================================================
    // Methods
    //================================================================================

    public MFXTextField getEditor() {
        return searchField;
    }

    /**
     * Initialized the combo box value if the selected index specified by the selection model is not -1.
     *
     * @see ComboSelectionModelMock
     */
    private void initSelection() {
        MFXComboBox<T> comboBox = getSkinnable();
        ComboSelectionModelMock<T> selectionModel = comboBox.getSelectionModel();

        if (selectionModel.getSelectedIndex() != -1 && comboBox.getItems().isEmpty()) {
            selectionModel.clearSelection();
            return;
        }

        if (selectionModel.getSelectedIndex() != -1) {
            int index = selectionModel.getSelectedIndex();
            if (index < comboBox.getItems().size()) {
                T item = comboBox.getItems().get(index);
                selectionModel.selectItem(item);
                listView.getSelectionModel().select(index, item, null);
                comboBox.setSelectedValue(item);
            } else {
                comboBox.getSelectionModel().clearSelection();
            }
        }
    }

    /**
     * Calls the methods which define the control behavior.
     * <p>
     * See {@link #comboBehavior()}, {@link #selectionBehavior()},
     * {@link #popupBehavior()}, {@link #listBehavior()}, {@link #iconBehavior()}
     */
    private void setBehavior() {
        comboBehavior();
        selectionBehavior();
        popupBehavior();
        listBehavior();
        iconBehavior();
    }

    //================================================================================
    // Behavior
    //================================================================================

    /**
     * Specifies the behavior for comboStyleProperty change, mouse pressed events and focus change.
     */
    private void comboBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();
        MFXDialogValidator validator = comboBox.getValidator();

        // STYLE
        comboBox.comboStyleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Styles.ComboBoxStyles.STYLE2) {
                getChildren().removeAll(unfocusedLine, focusedLine);
            } else {
                getChildren().addAll(unfocusedLine, focusedLine);
            }
        });

        // MOUSE PRESSED
        comboBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            comboBox.requestFocus();

            if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
                forceRipple();
                show();
            }
        });

        // FOCUS
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && comboBox.isValidated()) {
                comboBox.getValidator().update();
                validate.setVisible(!comboBox.isValid());
            }

            boolean fieldCondition = searchField != null && searchField.isFocused();
            if (fieldCondition) {
                return;
            }

            if (comboBox.isAnimateLines()) {
                buildAndPlayLinesAnimation(newValue);
                return;
            }

            if (newValue) {
                focusedLine.setScaleX(1.0);
            } else {
                focusedLine.setScaleX(0.0);
            }
        });

        // VALIDATION
        comboBox.isValidatedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validate.setVisible(false);
            }
        });

        comboBox.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validate.setVisible(false);
            }
        });

        validator.addListener(invalidated -> {
            if (comboBox.isValidated()) {
                validate.setVisible(!comboBox.isValid());
            }
        });

        validate.textProperty().addListener(invalidated -> comboBox.requestLayout());
        validate.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validator.showModal(comboBox.getScene().getWindow()));

    }

    /**
     * Specifies the behavior for combo box selection, listview selection, and selectedValue.
     */
    private void selectionBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();
        ComboSelectionModelMock<T> selectionModel = comboBox.getSelectionModel();

        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listView.getSelectionModel().select(selectionModel.getSelectedIndex(), newValue, null);
            } else {
                listView.getSelectionModel().clearSelection();
            }
            if (oldValue != newValue) {
                comboBox.setSelectedValue(newValue);
            }
        });

        comboBox.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setValueLabel(newValue);
            } else {
                valueLabel.setText("");
                valueLabel.setLeadingIcon(null);
            }
        });
    }

    /**
     * Specifies the behavior for maxPopupHeight and maxPopupWidth properties, also adds the
     * {@link #popupHandler} to the scene to close the popup in case it is open and the mouse is not
     * pressed on the combo box. And resets the control when the popup is hidden.
     *
     * @see #reset()
     */
    private void popupBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        comboBox.maxPopupHeightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == -1) {
                listView.maxHeightProperty().unbind();
            } else {
                listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
            }
        });
        comboBox.maxPopupWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == -1) {
                listView.maxWidthProperty().unbind();
            } else {
                listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());
            }
        });

        NodeUtils.waitForSkin(comboBox, () -> comboBox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler), true, false);
        comboBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
            }
        });

        popup.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            ComboSelectionModelMock<T> selectionModelMock = comboBox.getSelectionModel();
            IListSelectionModel<T> listSelectionModel = listView.getSelectionModel();
            if (selectionModelMock.getSelectedItem() != null) {
                listSelectionModel.select(selectionModelMock.getSelectedIndex(), selectionModelMock.getSelectedItem(), null);
            }
        });

        popup.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> reset());
    }

    /**
     * Specifies the be behavior for the listview, binds its sizes to maxPopupHeight and maxPopupWidth
     * properties and resets the control when the mouse is pressed.
     *
     * @see #reset()
     */
    private void listBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
        listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());
        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                reset();
            }
        });

        if (comboBox.getItems() != null) {
            comboBox.getItems().addListener((InvalidationListener) invalidated -> {
                comboBox.getSelectionModel().clearSelection();
                filteredList = new FilteredList<>(comboBox.getItems());
                listView.setItems(filteredList);
            });
        }
        comboBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.getSelectionModel().clearSelection();
            if (newValue != null) {
                newValue.addListener((InvalidationListener) invalidated -> {
                    filteredList = new FilteredList<>(comboBox.getItems());
                    listView.setItems(filteredList);
                });
                filteredList = new FilteredList<>(comboBox.getItems());
            } else {
                filteredList = new FilteredList<>(FXCollections.observableArrayList());
            }
            listView.setItems(filteredList);
        });
    }

    /**
     * Specifies the behavior of the caret icon, sets up the ripple generator and
     * the popup handling when the mouse is pressed.
     */
    private void iconBehavior() {
        icon.rippleGeneratorBehavior(event ->
                new RipplePosition(icon.getWidth() / 2, icon.getHeight() / 2)
        );

        MFXCircleRippleGenerator rg = icon.getRippleGenerator();
        rg.setAnimationSpeed(1.3);
        rg.setRippleRadius(8);

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (!popup.isShowing()) {
                show();
            } else {
                reset();
            }
        });
        icon.getIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                popup.hide();
                forceRipple();
                getSkinnable().requestFocus();
                event.consume();
            }
        });
    }

    /**
     * This method builds the label used to display the selected value of the combo box.
     */
    protected MFXLabel buildLabel() {
        MFXLabel label = new MFXLabel("");
        label.setAlignment(Pos.CENTER_LEFT);
        label.setContainerPadding(new Insets(0, 0, 0, 2));
        label.setMinWidth(snappedLeftInset() + minWidth + snappedRightInset());
        label.setMouseTransparent(true);
        label.promptTextProperty().bind(getSkinnable().promptTextProperty());
        label.setLineColor(Color.TRANSPARENT);
        label.setUnfocusedLineColor(Color.TRANSPARENT);
        label.getStylesheets().setAll(getSkinnable().getUserAgentStylesheet());

        return label;
    }

    /**
     * This method build the combo box popup and initializes the listview.
     */
    protected PopupControl buildPopup() {
        PopupControl popupControl = new PopupControl();

        listView.setItems(filteredList);
        popupControl.getScene().setRoot(listView);
        popupControl.setOnShowing(event -> buildAnimation(true).play());
        popupControl.setOnHiding(event -> {
            buildAnimation(false).play();
            if (containsEditor()) {
                container.getChildren().remove(searchContainer);
            }
        });

        return popupControl;
    }

    /**
     * Shows the popup.
     */
    private void show() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        if (!containsEditor()) {
            showEditor();
        }
        Point2D point = NodeUtils.pointRelativeTo(
                comboBox,
                listView,
                HPos.CENTER,
                VPos.BOTTOM,
                comboBox.getPopupXOffset(),
                comboBox.getPopupYOffset(),
                false
        );
        popup.show(comboBox, snapPositionX(point.getX()), snapPositionY(point.getY()));
    }

    /**
     * Closes the popup and resets the control.
     * <p>
     * Removes the filter text field, clears the predicate of the filteredList,
     * sets the value label to visible and requests the focus.
     */
    private void reset() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        popup.hide();
        container.getChildren().remove(searchContainer);
        filteredList.setPredicate(null);
        valueLabel.setVisible(true);
        listView.setItems(filteredList);
        comboBox.requestFocus();
    }

    /**
     * This method is quite similar to the one used in MFXLabel.
     * <p>
     * This method build the text field used to filter the listview. When text changes
     * the filteredList is updated with a new predicate.
     * <p>
     * By default when the text field loses the focus the control is reset.
     * <p>
     * The text field is unmanaged so its position is calculated by {@link #computeEditorPosition()}
     * <p>
     * When the popup is shown and the text field is added to the scene the text field is not focused,
     * to change this behavior and force it to be focused you can use {@link MFXFilterComboBox#setForceFieldFocusOnShow(boolean)}
     * and set it to true.
     *
     * @see #reset()
     */
    private void showEditor() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        MFXFontIcon searchIcon = new MFXFontIcon("mfx-search", 16);

        valueLabel.setVisible(false);
        searchField = new MFXTextField("");
        searchField.setMFXContextMenu(null);
        comboBox.editorFocusedProperty().bind(searchField.focusedProperty());
        searchField.setPromptText("Search...");
        searchField.setId("search-field");
        searchField.getStylesheets().setAll(comboBox.getUserAgentStylesheet());
        searchField.setFocusTraversable(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(getPredicate(searchField));
            listView.setItems(filteredList);
        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && popup.isShowing()) {
                reset();
            }
        });

        searchContainer.getChildren().setAll(searchIcon, searchField);
        container.getChildren().add(searchContainer);
        computeEditorPosition();
        if (comboBox.isForceFieldFocusOnShow()) {
            forceFieldFocus();
        }
    }

    /*
     * Responsible for showing the editor correctly, handles its size and location.
     */
    private void computeEditorPosition() {
        double posX = container.getBoundsInParent().getMinX();
        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();
        searchContainer.resizeRelocate(posX, 0, containerWidth, containerHeight);
    }

    /**
     * The only way to force the focus on the text field is to poll its state by running
     * a PauseTransition every n milliseconds (by default 100). If at the end of the transition the text field
     * is not focused yet the transition is played again.
     * <p>
     * Since this may produce an unexpected behavior by running indefinitely for example so I set a max
     * retry count of 10.
     * <p>
     * Uses the new utility {@link PauseBuilder#runWhile(BooleanExpression, Runnable, Runnable, int)}
     */
    protected void forceFieldFocus() {
        if (searchField != null && !searchField.isFocused()) {
            PauseBuilder.build()
                    .setDuration(100)
                    .runWhile(
                            searchField.focusedProperty(),
                            () -> searchField.requestFocus(),
                            () -> {},
                            10
                    );
        }
    }

    /**
     * Used to generate the ripple effect of the icon when an event filter consumes the event.
     */
    private void forceRipple() {
        MFXCircleRippleGenerator rg = icon.getRippleGenerator();
        rg.generateRipple(null);
    }

    /**
     * Checks if the editor is already shown.
     */
    private boolean containsEditor() {
        Node editor = container.getChildren().stream()
                .filter(node -> node.getId() != null && node.getId().equals("search-node"))
                .findFirst()
                .orElse(null);
        return editor != null;
    }

    /**
     * Return the Predicate to filter the popup items based on the search field.
     */
    private Predicate<T> getPredicate(MFXTextField textField) {
        String searchText = textField.getText().trim();
        if (searchText.isEmpty()) {
            return null;
        }
        return getPredicate(searchText);
    }

    /**
     * Return the Predicate to filter the popup items based on the given search text.
     */
    private Predicate<T> getPredicate(String searchText) {
        String[] words = searchText.split(" ");
        return value ->
        {
            String displayText = value.toString().toLowerCase();
            return Arrays.stream(words).allMatch(word -> StringUtils.containsIgnoreCase(displayText, word));
        };
    }

    /**
     * Sets the label text according to the combo box selected item.
     * <p>
     * If the item is instance of {@code Labeled} then whe check if the item has a graphic != null
     * and use the item text. If that's not the case then we call toString on the item.
     */
    private void setValueLabel(T item) {
        if (item instanceof Labeled) {
            Labeled nodeItem = (Labeled) item;
            if (nodeItem.getGraphic() != null) {
                valueLabel.setLeadingIcon(new MFXSnapshotWrapper(nodeItem.getGraphic()).getGraphic());
            }
            valueLabel.setText(nodeItem.getText());
        } else {
            valueLabel.setText(item.toString());
        }
    }

    /**
     * Builds the animation for the combo box arrow.
     */
    private Animation buildAnimation(boolean isShowing) {
        arrowAnimation = AnimationUtils.TimelineBuilder.build()
                .add(KeyFrames.of(150, icon.rotateProperty(), (isShowing ? 180 : 0)))
                .getAnimation();
        return arrowAnimation;
    }

    /**
     * Builds the focus animation.
     */
    private void buildAndPlayLinesAnimation(boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(350), focusedLine);
        if (focused) {
            scaleTransition.setFromX(0.0);
            scaleTransition.setToX(1.0);
        } else {
            scaleTransition.setFromX(1.0);
            scaleTransition.setToX(0.0);
        }
        scaleTransition.setInterpolator(MFXAnimationFactory.getInterpolatorV2());
        scaleTransition.play();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + minWidth + rightInset;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset), 27);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (arrowAnimation != null) {
            arrowAnimation = null;
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        double lw = snapSizeX(LabelUtils.computeLabelWidth(validate));
        double lh = snapSizeY(LabelUtils.computeTextHeight(validate.getFont(), validate.getText()));
        double lx = 0;
        double ly = h + (padding * 0.7);

        validate.resizeRelocate(lx, ly, lw, lh);

        double extraX = getSkinnable().getComboStyle() == Styles.ComboBoxStyles.STYLE3 ? 5 : 3;
        double iconWidth = icon.getPrefWidth();
        double iconHeight = icon.getPrefHeight();
        double center = ((snappedTopInset() + snappedBottomInset()) / 2.0) + ((h - iconHeight) / 2.0);
        icon.resizeRelocate(w - iconWidth + extraX, center, iconWidth, iconHeight);
        focusedLine.relocate(0, h);
        unfocusedLine.relocate(0, h);
    }

    private static class FilterListCell<T> extends MFXFlowlessListCell<T> {
        private final String STYLE_CLASS = "mfx-list-cell";
        private final MFXFilterComboBox<T> comboBox;

        public FilterListCell(MFXFilterComboBox<T> comboBox, T data) {
            this(comboBox, data, 32);
        }

        public FilterListCell(MFXFilterComboBox<T> comboBox, T data, double fixedHeight) {
            super(null, data, fixedHeight);
            this.comboBox = comboBox;

            if (comboBox.getSelectionModel().getSelectedItem() == getData() && !isSelected()) {
                setSelected(true);
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected());
            }
            setBehavior();
            render(getData());
        }

        @Override
        protected void initialize() {
            getStyleClass().add(STYLE_CLASS);
        }

        @Override
        protected void setBehavior() {
            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> comboBox.getSelectionModel().selectItem(getData()));
        }

        @Override
        public HBox getNode() {
            return this;
        }

        @Override
        protected void render(T data) {
            if (data instanceof Node) {
                getChildren().setAll((Node) data);
            } else {
                setEmpty(data.toString().isEmpty());
                Label label = new Label(data.toString());
                label.getStyleClass().add("data-label");
                getChildren().setAll(label);
            }
        }

        @Override
        protected IListSelectionModel<T> getSelectionModel() {
            return null;
        }
    }
}
