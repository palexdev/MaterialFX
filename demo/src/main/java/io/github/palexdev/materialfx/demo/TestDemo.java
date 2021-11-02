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

package io.github.palexdev.materialfx.demo;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.demo.model.FilterablePerson;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.selection.SingleSelectionModel;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@SuppressWarnings("All")
public class TestDemo extends Application {
    private final Random random = new Random(System.currentTimeMillis());
    private boolean indexBound;


    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();

        ObservableList<String> strings = FXCollections.observableArrayList();
        IntStream.range(0, 1000).forEach(value -> strings.add(String.valueOf(value)));

        MFXListView<String> listView = new MFXListView<>(strings);
        listView.setHideScrollBars(true);
        ListView<String> flowlessListView = new ListView<>(strings);
        HBox box = new HBox(50, listView, flowlessListView);
        box.setAlignment(Pos.CENTER);

        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                strings.set(0, "90");
            }
        });

        listView.getSelectionModel().getSelection().addListener((MapChangeListener<? super Integer, ? super String>) change -> System.out.println(change));

        stackPane.getChildren().addAll(box);

        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }

/*    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox(100);
        box.setAlignment(Pos.CENTER);
        Scene mainScene = new Scene(box, 800, 600);

        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add("/mfx/light.css");

        box.getChildren().add(new Label("MFXComboBox"));
        var nmfx = new MFXComboBox<>();
        nmfx.getItems().add("a");
        nmfx.getItems().add("b");
        nmfx.getItems().add("c");
        box.getChildren().add(nmfx);

        nmfx.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println(nmfx.getSelectionModel().getSelectedIndex());
            }
        });

        MFXComboBox<String> bind = new MFXComboBox<>();
        bind.getItems().add("a");
        bind.getItems().add("b");
        bind.getItems().add("c");
        box.getChildren().add(bind);

        nmfx.getSelectionModel().bind(bind.getSelectionModel().selectedItemProperty());
        nmfx.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> System.out.println("I: " + newValue));



        primaryStage.setScene(mainScene);
        primaryStage.show();
    }*/

    public static void main(String[] args) {
        launch(args);
    }

    private static boolean isInvalidCharacter(char c) {
        if (c == 0x7F) return true;
        if (c == 0xA) return true;
        if (c == 0x9) return true;
        return c < 0x20;
    }

    public MFXIconWrapper getRandomIcon(double size) {
        FontResources[] resources = FontResources.values();
        String desc = resources[random.nextInt(resources.length)].getDescription();
        return new MFXIconWrapper(desc, size, ColorUtils.getRandomColor(), size * 1.5);
    }

    private void setupTable(MFXTableView<FilterablePerson> tableView) {
        MFXTableColumn<FilterablePerson> firstName = new MFXTableColumn<>("FName");
        MFXTableColumn<FilterablePerson> lastName = new MFXTableColumn<>("LName");
        MFXTableColumn<FilterablePerson> address = new MFXTableColumn<>("Address");
        MFXTableColumn<FilterablePerson> age = new MFXTableColumn<>("age");

        firstName.setRowCellFunction(person -> new MFXTableRowCell(person.firstNameProperty()));
        lastName.setRowCellFunction(person -> new MFXTableRowCell(person.lastNameProperty()));
        address.setRowCellFunction(person -> new MFXTableRowCell(person.addressProperty()));
        age.setRowCellFunction(person -> new MFXTableRowCell(person.ageProperty().asString()));

        tableView.getTableColumns().addAll(firstName, lastName, address, age);
        //tableView.setItems(people);
    }

    public String randStr() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 30;
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void selectNext(SingleSelectionModel<String> selectionModel) {
        int index = selectionModel.getSelectedIndex();
        List<String> items = selectionModel.getUnmodifiableItems();
        if (index >= (items.size())) {
            return;
        }

        if (!indexBound) {
            String item = items.get(index + 1);
            selectionModel.selectItem(item);
            return;
        }
        selectionModel.selectIndex(index + 1);
    }

    private void selectPrevious(SingleSelectionModel<String> selectionModel) {
        int index = selectionModel.getSelectedIndex();
        List<String> items = selectionModel.getUnmodifiableItems();
        if (index == 0) {
            return;
        }

        if (!indexBound) {
            String item = items.get(index - 1);
            selectionModel.selectItem(item);
            return;
        }
        selectionModel.selectIndex(index - 1);
    }
}
