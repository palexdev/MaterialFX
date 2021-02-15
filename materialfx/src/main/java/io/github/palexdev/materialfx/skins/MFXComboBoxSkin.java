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
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MFXComboBoxSkin<T> extends SkinBase<MFXComboBox<T>> {
    private final HBox container;
    private final Label valueLabel;
    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXListView<T> listView;
    private final EventHandler<MouseEvent> popupHandler;

    private Timeline arrowAnimation;

    public MFXComboBoxSkin(MFXComboBox<T> comboBox) {
        super(comboBox);

        valueLabel = new Label();

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24).addRippleGenerator();
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel, icon);
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

        getChildren().add(container);
        setListeners();
    }

    private void setListeners() {
        MFXComboBox<T> comboBox = getSkinnable();
        RippleGenerator rg = icon.getRippleGenerator();
        rg.setRippleRadius(8);

        comboBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> comboBox.requestFocus());

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
                valueLabel.setText(newValue.toString());
            } else {
                valueLabel.setText("");
            }
        });

        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                popup.hide();
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

        comboBox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
        comboBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                if (newValue != null) {
                    newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                }
            }
        });
    }

    protected void buildPopup() {
        MFXComboBox<T> comboBox = getSkinnable();

        listView.itemsProperty().bind(comboBox.itemsProperty());
        popup.getScene().setRoot(listView);
        popup.setOnShowing(event -> buildAnimation(true).play());
        popup.setOnHiding(event -> buildAnimation(false).play());
    }

    private Timeline buildAnimation(boolean isShowing) {
        KeyFrame kf0 = new KeyFrame(Duration.millis(150),
                new KeyValue(icon.rotateProperty(), (isShowing ? 180 : 0))
        );
        arrowAnimation = new Timeline(kf0);
        return arrowAnimation;
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double value = leftInset + 50 + rightInset;
        valueLabel.setMinWidth(value);
        return value;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (arrowAnimation != null) {
            arrowAnimation = null;
        }
    }
}
