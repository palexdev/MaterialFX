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

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.MFXCheckTreeView;
import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.MFXTreeView;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TreeviewsDemoController implements Initializable {

    @FXML
    private MFXTreeView<String> treeView;

    @FXML
    private MFXTreeView<HBox> treeViewHide;

    @FXML
    private MFXCheckTreeView<String> checkTreeView;

    @FXML
    private Text text1;

    @FXML
    private Text text2;

    @FXML
    private Text text3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeView.setRoot(createRoot());

        treeViewHide.setRoot(createNodeRoot());
        treeViewHide.setShowRoot(false);

        checkTreeView.setRoot(createCheckRoot());

        ScrollUtils.addSmoothScrolling(treeView);
        ScrollUtils.addSmoothScrolling(treeViewHide);
        ScrollUtils.addSmoothScrolling(checkTreeView);

        treeView.getSelectionModel().getSelectedItems().addListener(
                (observable, oldValue, newValue) -> text1.setText("Selected Items Count: " + treeView.getSelectionModel().getSelectedItems().size()));
        treeViewHide.getSelectionModel().getSelectedItems().addListener(
                (observable, oldValue, newValue) -> text2.setText("Selected Items Count: " + newValue.size()));
        checkTreeView.getCheckModel().getCheckedItems().addListener(
                (observable, oldValue, newValue) -> text3.setText("Checked Items Count: " + newValue.size()));
    }

    public MFXTreeItem<String> createRoot() {
        MFXTreeItem<String> root = new MFXTreeItem<>("Tree View Root");

        MFXTreeItem<String> item1 = new MFXTreeItem<>("ITEM1");
        item1.getItems().addAll(List.of(
                new MFXTreeItem<>("ITEM1-Sub1"),
                new MFXTreeItem<>("ITEM1-Sub2")
                )
        );

        MFXTreeItem<String> item2 = new MFXTreeItem<>("ITEM2");
        item2.getItems().addAll(List.of(
                new MFXTreeItem<>("ITEM2-Sub1"),
                new MFXTreeItem<>("ITEM2-Sub2"),
                new MFXTreeItem<>("ITEM2-Sub3"),
                new MFXTreeItem<>("ITEM2-Sub4")
                )
        );

        MFXTreeItem<String> item3 = new MFXTreeItem<>("ITEM3");

        MFXTreeItem<String> item4 = new MFXTreeItem<>("ITEM4");
        item2.getItems().add(
                new MFXTreeItem<>("ITEM4-Sub1")
        );

        MFXTreeItem<String> item5 = new MFXTreeItem<>("ITEM5");
        item2.getItems().addAll(List.of(
                new MFXTreeItem<>("ITEM5-Sub1"),
                new MFXTreeItem<>("ITEM5-Sub2"),
                new MFXTreeItem<>("ITEM5-Sub3")
                )
        );

        root.getItems().addAll(List.of(item1, item2, item3, item4, item5));
        return root;
    }

    public MFXTreeItem<HBox> createNodeRoot() {
        MFXTreeItem<HBox> root = new MFXTreeItem<>(createBox("mfx-google", "Google Root"));

        MFXTreeItem<HBox> item1 = new MFXTreeItem<>(createBox("mfx-google", "ITEM1"));
        item1.getItems().addAll(List.of(
                new MFXTreeItem<>(createBox("mfx-google", "ITEM1-Sub1")),
                new MFXTreeItem<>(createBox("mfx-google", "ITEM1-Sub2"))
                )
        );

        MFXTreeItem<HBox> item2 = new MFXTreeItem<>(createBox("mfx-calendar-black", "ITEM2"));
        item2.getItems().addAll(List.of(
                new MFXTreeItem<>(createBox("mfx-calendar-black", "ITEM2-Sub1")),
                new MFXTreeItem<>(createBox("mfx-calendar-black", "ITEM2-Sub2")),
                new MFXTreeItem<>(createBox("mfx-calendar-black", "ITEM2-Sub3")),
                new MFXTreeItem<>(createBox("mfx-calendar-black", "ITEM2-Sub4"))
                )
        );

        MFXTreeItem<HBox> item3 = new MFXTreeItem<>(createBox("mfx-exclamation-triangle", "ITEM3"));

        MFXTreeItem<HBox> item4 = new MFXTreeItem<>(createBox("mfx-circle", "ITEM4"));
        item4.getItems().add(
                new MFXTreeItem<>(createBox("mfx-info-circle", "ITEM4-Sub1"))
        );

        MFXTreeItem<HBox> item5 = new MFXTreeItem<>(createBox("mfx-circle", "ITEM5"));
        item5.getItems().addAll(List.of(
                new MFXTreeItem<>(createBox("mfx-circle", "ITEM5-Sub1")),
                new MFXTreeItem<>(createBox("mfx-circle", "ITEM5-Sub2")),
                new MFXTreeItem<>(createBox("mfx-circle", "ITEM5-Sub3"))
                )
        );

        root.getItems().addAll(List.of(item1, item2, item3, item4, item5));
        return root;
    }

    public MFXCheckTreeItem<String> createCheckRoot() {
        MFXCheckTreeItem<String> root = new MFXCheckTreeItem<>("ROOT");

        MFXCheckTreeItem<String> i1 = new MFXCheckTreeItem<>("I1");
        MFXCheckTreeItem<String> i1a = new MFXCheckTreeItem<>("I1A");
        i1a.getItems().add(new MFXCheckTreeItem<>("I11A"));

        MFXCheckTreeItem<String> i1b = new MFXCheckTreeItem<>("I1B");
        i1.getItems().addAll(List.of(i1a, i1b));

        MFXCheckTreeItem<String> i2 = new MFXCheckTreeItem<>("I2");
        MFXCheckTreeItem<String> i2a = new MFXCheckTreeItem<>("I2A");
        i2.getItems().add(i2a);

        MFXCheckTreeItem<String> i3 = new MFXCheckTreeItem<>("I3");
        MFXCheckTreeItem<String> i3a = new MFXCheckTreeItem<>("I3A");
        MFXCheckTreeItem<String> i3b = new MFXCheckTreeItem<>("I3B");
        i3.getItems().addAll(List.of(i3a, i3b));

        MFXCheckTreeItem<String> i4 = new MFXCheckTreeItem<>("I4");
        MFXCheckTreeItem<String> i4a = new MFXCheckTreeItem<>("I4A");
        i4.getItems().add(i4a);

        root.getItems().addAll(List.of(i1, i2, i3, i4));
        return root;
    }

    private HBox createBox(String iconDescription, String text) {
        MFXFontIcon icon = new MFXFontIcon(iconDescription, ColorUtils.getRandomColor());
        HBox hBox = new HBox(10, icon, new Label(text));
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }
}
