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
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.enums.Styles;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.ComboBoxSelectionModel;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.LabelUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * This is the implementation of the Skin associated with every {@link MFXComboBox}.
 */
public class MFXComboBoxSkin<T> extends SkinBase<MFXComboBox<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final MFXLabel valueLabel;
    private final double minWidth = 120;

    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXListView<T> listView;

    private final Line unfocusedLine;
    private final Line focusedLine;
    private final Label validate;
    private final double padding = 11;

    private Animation arrowAnimation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBoxSkin(MFXComboBox<T> comboBox) {
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

        MFXFontIcon warnIcon = new MFXFontIcon("mfx-exclamation-triangle", Color.RED);
        MFXIconWrapper warnWrapper = new MFXIconWrapper(warnIcon, 10);

        validate = new Label();
        validate.setGraphic(warnWrapper);
        validate.getStyleClass().add("validate-label");
        validate.textProperty().bind(comboBox.getValidator().validatorMessageProperty());
        validate.setGraphicTextGap(padding);
        validate.setVisible(false);
        validate.setManaged(false);

        if (comboBox.isValidated() && comboBox.getValidator().isInitControlValidation()) {
            validate.setVisible(!comboBox.isValid());
        }

        valueLabel = buildLabel();

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24);
        icon.setManaged(false);
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel);
        container.setAlignment(Pos.CENTER_LEFT);

        listView = new MFXListView<>();
        listView.setCellFactory(item -> new MFXListCell<>(listView, item) {
            @Override
            protected void updateSelection(MouseEvent event) {
                if (comboBox.getSelectionModel().isBound() || comboBox.selectedValueProperty().isBound()) return;
                super.updateSelection(event);
            }
        });
        listView.getSelectionModel().setAllowsMultipleSelection(false);
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());

        popup = buildPopup();

        if (comboBox.getComboStyle() != Styles.ComboBoxStyles.STYLE2) {
            getChildren().addAll(container, icon, unfocusedLine, focusedLine, validate);
        } else {
            getChildren().addAll(container, icon, validate);
        }

        if (comboBox.getMaxPopupHeight() == -1) {
            listView.maxHeightProperty().unbind();
        }
        if (comboBox.getMaxPopupWidth() == -1) {
            listView.maxWidthProperty().unbind();
        }

        setBehavior();
        initSelection();
    }

    /**
     * Initialized the combo box value if the selected index specified by the selection model is not -1.
     *
     * @see ComboBoxSelectionModel
     */
    private void initSelection() {
        MFXComboBox<T> comboBox = getSkinnable();
        ComboBoxSelectionModel<T> selectionModel = comboBox.getSelectionModel();

        if (selectionModel.getSelectedIndex() != -1 && comboBox.getItems().isEmpty()) {
            selectionModel.clearSelection();
            return;
        }

        if (selectionModel.getSelectedIndex() != -1) {
            int index = selectionModel.getSelectedIndex();
            if (index < comboBox.getItems().size()) {
                listView.getSelectionModel().selectIndex(index);
            } else {
                comboBox.getSelectionModel().clearSelection();
            }
        }
    }

    //================================================================================
    // Methods
    //================================================================================

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
        MFXComboBox<T> comboBox = getSkinnable();
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
                managePopup();
            }
        });

        // FOCUS
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && comboBox.isValidated()) {
                comboBox.getValidator().update();
                validate.setVisible(!comboBox.isValid());
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
     * Specifies the behavior for combo box selection, listview selection and selectedValue.
     */
    private void selectionBehavior() {
        MFXComboBox<T> comboBox = getSkinnable();
        ComboBoxSelectionModel<T> selectionModel = comboBox.getSelectionModel();

        selectionModel.selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                listView.getSelectionModel().selectIndex(newValue.intValue());
            } else {
                listView.getSelectionModel().clearSelection();
            }
            comboBox.setSelectedValue(selectionModel.getSelectedItem());
        });

        listView.getSelectionModel().selectionProperty().addListener((MapChangeListener<? super Integer, ? super T>) change -> {
            if (change.wasRemoved() || selectionModel.isBound() || comboBox.selectedValueProperty().isBound()) return;

            Integer index = change.getKey();
            if (index != -1) {
                selectionModel.selectIndex(index);
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
     * Specifies the behavior for maxPopupHeight and maxPopupWidth properties.
     */
    private void popupBehavior() {
        MFXComboBox<T> comboBox = getSkinnable();

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
    }

    /**
     * Specifies the be behavior for the listview, binds its sizes to maxPopupHeight and maxPopupWidth
     * properties and closes the popup when the mouse is pressed.
     */
    private void listBehavior() {
        MFXComboBox<T> comboBox = getSkinnable();

        listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
        listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());
        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });

        if (comboBox.getItems() != null) {
            comboBox.getItems().addListener((InvalidationListener) invalidated -> {
                comboBox.getSelectionModel().clearSelection();
                listView.setItems(comboBox.getItems());
            });
        }
        comboBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.getSelectionModel().clearSelection();
            if (newValue != null) {
                newValue.addListener((InvalidationListener) invalidated -> listView.setItems(newValue));
                listView.setItems(newValue);
            } else {
                listView.setItems(FXCollections.observableArrayList());
            }
        });

        NodeUtils.waitForScene(comboBox, () -> listView.getStylesheets().setAll(comboBox.getStylesheets()), true, true);
        comboBox.getStylesheets().addListener((ListChangeListener<? super String>) changed -> listView.getStylesheets().setAll(comboBox.getStylesheets()));
    }

    /**
     * Specifies the behavior of the caret icon, sets up the ripple generator and
     * the popup handling when the mouse is pressed.
     */
    private void iconBehavior() {
        icon.rippleGeneratorBehavior(event ->
                new PositionBean(icon.getWidth() / 2, icon.getHeight() / 2)
        );

        MFXCircleRippleGenerator rg = icon.getRippleGenerator();
        rg.setAnimationSpeed(1.3);
        rg.setRippleRadius(8);

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> managePopup());
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
     * This method builds the combo box popup and initializes the listview.
     */
    protected PopupControl buildPopup() {
        MFXComboBox<T> comboBox = getSkinnable();

        PopupControl popupControl = new PopupControl();
        popupControl.setAutoHide(true);

        listView.setItems(comboBox.getItems());
        popupControl.getScene().setRoot(listView);
        popupControl.setOnShowing(event -> buildAnimation(true).play());
        popupControl.setOnHiding(event -> buildAnimation(false).play());

        return popupControl;
    }

    /**
     * Convenience method to manage the popup. If the popup is not showing
     * gets the coordinates and calls show() otherwise hides it.
     */
    private void managePopup() {
        MFXComboBox<T> comboBox = getSkinnable();

        if (!popup.isShowing()) {
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
        } else {
            popup.hide();
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
        scaleTransition.setInterpolator(MFXAnimationFactory.INTERPOLATOR_V2);
        scaleTransition.play();
    }

    /**
     * Sets the label text according to the combo box selected item.
     * <p>
     * If the item is instance of {@code Labeled} then we check if the item has a graphic != null
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
}
