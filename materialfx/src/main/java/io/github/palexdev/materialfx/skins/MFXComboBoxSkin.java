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
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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

    private Timeline arrowAnimation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBoxSkin(MFXComboBox<T> comboBox) {
        super(comboBox);

        valueLabel = new Label();
        valueLabel.setMinWidth(snappedLeftInset() + minWidth + snappedRightInset());

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24).addRippleGenerator();
        icon.setManaged(false);
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel);
        container.setAlignment(Pos.CENTER_LEFT);

        listView = new MFXListView<>();
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());
        popup = new PopupControl();
        buildPopup();

        popupHandler = event -> {
            if (popup.isShowing() && !NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), comboBox)) {
                popup.hide();
            }
        };

        getChildren().addAll(container, icon);
        setListeners();

        if (comboBox.getMaxPopupHeight() == -1) {
            listView.maxHeightProperty().unbind();
        }
        if (comboBox.getMaxPopupWidth() == -1) {
            listView.maxWidthProperty().unbind();
        }
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
    private void setListeners() {
        MFXComboBox<T> comboBox = getSkinnable();
        RippleGenerator rg = icon.getRippleGenerator();
        rg.setRippleRadius(8);

        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && popup.isShowing()) {
                popup.hide();
            }
        });

        comboBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            comboBox.requestFocus();

            if(event.getTarget().equals(icon.getIcon())) {
                return;
            }
            if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
                NodeUtils.fireDummyEvent(icon);
            }
        });

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

        comboBox.selectedValueProperty().bind(listView.getSelectionModel().selectedItemProperty());
        comboBox.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setValueLabel(newValue);
            } else {
                valueLabel.setText("");
                valueLabel.setGraphic(null);
            }
        });

        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });

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
        listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
        listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> comboBox.getSelectionModel().selectedIndexProperty().set(newValue.intValue()));
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> comboBox.getSelectionModel().selectedItemProperty().set(newValue));
        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                if (newValue.intValue() == -1) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue.intValue());
                }
            }
        });
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                if (newValue == null) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue);
                }
            }
        });

        comboBox.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                if (oldValue != null) {
                    oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                }
                if (newValue != null) {
                    newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                }
            }
        });
    }

    /**
     * Builds the popup content.
     */
    protected void buildPopup() {
        MFXComboBox<T> comboBox = getSkinnable();

        listView.itemsProperty().bind(comboBox.itemsProperty());
        popup.getScene().setRoot(listView);
        popup.setOnShowing(event -> buildAnimation(true).play());
        popup.setOnHiding(event -> buildAnimation(false).play());
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
    }
}
