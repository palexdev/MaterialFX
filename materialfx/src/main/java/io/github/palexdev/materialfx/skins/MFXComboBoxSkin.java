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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.ComboSelectionModelMock;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * This is the implementation of the Skin associated with every {@code MFXComboBox}.
 */
public class MFXComboBoxSkin<T> extends SkinBase<MFXComboBox<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label valueLabel;
    private final double minWidth = 100;

    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXListView<T> listView;
    private final EventHandler<MouseEvent> popupHandler;

    private final Line unfocusedLine;
    private final Line focusedLine;

    private Timeline arrowAnimation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBoxSkin(MFXComboBox<T> comboBox) {
        super(comboBox);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.setManaged(false);
        unfocusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        unfocusedLine.strokeProperty().bind(comboBox.unfocusedLineColorProperty());
        unfocusedLine.setSmooth(true);
        unfocusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setManaged(false);
        focusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        focusedLine.strokeProperty().bind(comboBox.lineColorProperty());
        focusedLine.setSmooth(true);
        focusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));
        focusedLine.setScaleX(0.0);

        valueLabel = buildLabel();

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24).addRippleGenerator();
        icon.setManaged(false);
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel);
        container.setAlignment(Pos.CENTER_LEFT);

        listView = new MFXListView<>();
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());
        popup = buildPopup();

        popupHandler = event -> {
            if (popup.isShowing() && !NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), comboBox)) {
                popup.hide();
            }
        };

        if (comboBox.getComboStyle() == Styles.ComboBoxStyles.STYLE1) {
            getChildren().addAll(container, icon, unfocusedLine, focusedLine);
        } else {
            getChildren().addAll(container, icon);
        }

        if (comboBox.getMaxPopupHeight() == -1) {
            listView.maxHeightProperty().unbind();
        }
        if (comboBox.getMaxPopupWidth() == -1) {
            listView.maxWidthProperty().unbind();
        }

        setBehavior();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: focus, selected value, maxPopupHeight and maxPopupWidth,
     * selectedIndex and selectedItem properties, parent property.
     * <p>
     * Adds bindings for: selected value, maxPopupHeight and maxPopupWidth,
     * <p>
     * Adds handlers for: focus, show/hide the popup.
     *
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

            if(event.getTarget().equals(icon.getIcon())) {
                return;
            }
            if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
                if (popup.isShowing()) {
                    icon.getRippleGenerator().createRipple();
                    popup.hide();
                    return;
                }
                NodeUtils.fireDummyEvent(icon);
            }
        });

        // FOCUS
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && popup.isShowing()) {
                popup.hide();
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
    }

    /**
     * Specifies the behavior for selectedValue, listview selection, combo box selection and filtered list change.
     */
    private void selectionBehavior() {
        MFXComboBox<T> comboBox = getSkinnable();
        ComboSelectionModelMock<T> selectionModel = comboBox.getSelectionModel();

        comboBox.selectedValueProperty().bind(listView.getSelectionModel().selectedItemProperty());
        comboBox.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setValueLabel(newValue);
            } else {
                valueLabel.setText("");
                valueLabel.setGraphic(null);
            }
        });

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> selectionModel.selectedIndexProperty().set(newValue.intValue()));
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectionModel.selectedItemProperty().set(newValue));
        selectionModel.selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                if (newValue.intValue() == -1) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue.intValue());
                }
            }
        });
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                if (newValue == null) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue);
                }
            }
        });
    }

    /**
     * Specifies the behavior for maxPopupHeight and maxPopupWidth properties, also adds the
     * {@link #popupHandler} to the scene to close the popup in case it is open and the mouse is not
     * pressed on the combo box.
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

        comboBox.skinProperty().addListener((observable, oldValue, newValue) -> comboBox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler));
        comboBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
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
    }

    /**
     * Specifies the behavior of the caret icon, sets up the ripple generator and
     * the popup handling when the mouse is pressed.
     */
    private void iconBehavior() {
        MFXComboBox<T> comboBox = getSkinnable();

        RippleGenerator rg = icon.getRippleGenerator();
        rg.setRippleRadius(8);
        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rg.setGeneratorCenterX(icon.getWidth() / 2);
            rg.setGeneratorCenterY(icon.getHeight() / 2);
            rg.createRipple();
        });

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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
        });
    }

    /**
     * This method builds the label used to display the selected value of the combo box.
     */
    protected Label buildLabel() {
        Label label = new Label("");
        label.setMinWidth(snappedLeftInset() + minWidth + snappedRightInset());
        label.setMouseTransparent(true);

        return label;
    }

    /**
     * This method build the combo box popup and initializes the listview.
     */
    protected PopupControl buildPopup() {
        MFXComboBox<T> comboBox = getSkinnable();

        PopupControl popupControl = new PopupControl();

        listView.itemsProperty().bind(comboBox.itemsProperty());
        popupControl.getScene().setRoot(listView);
        popupControl.setOnShowing(event -> buildAnimation(true).play());
        popupControl.setOnHiding(event -> buildAnimation(false).play());

        return popupControl;
    }

    /**
     * Builds the animation for the combo box arrow.
     */
    private Timeline buildAnimation(boolean isShowing) {
        KeyFrame kf0 = new KeyFrame(Duration.millis(150),
                new KeyValue(icon.rotateProperty(), (isShowing ? 180 : 0))
        );
        arrowAnimation = new Timeline(kf0);
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
                valueLabel.setGraphic(new MFXSnapshotWrapper(nodeItem.getGraphic()).getGraphic());
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
        return Math.max(super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset), topInset + icon.getHeight() + bottomInset);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (arrowAnimation != null) {
            arrowAnimation = null;
        }
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        double iconWidth = icon.getPrefWidth();
        double iconHeight = icon.getPrefHeight();
        double center = ((snappedTopInset() + snappedBottomInset()) / 2.0) + ((contentHeight - iconHeight) / 2.0);
        icon.resizeRelocate(contentWidth - iconWidth, center, iconWidth, iconHeight);
        focusedLine.relocate(0, contentHeight);
        unfocusedLine.relocate(0, contentHeight);
    }
}
