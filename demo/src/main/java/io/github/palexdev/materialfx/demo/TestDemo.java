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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.demo.model.FilterablePerson;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.Random;

@SuppressWarnings("All")
public class TestDemo extends Application {
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox(100);
        box.setAlignment(Pos.CENTER);

        HBox bbox = new HBox(20);
        bbox.setAlignment(Pos.CENTER);

        MFXButton b1 = new MFXButton("Set Leading");
        MFXButton b2 = new MFXButton("Set Trailing");
        MFXButton b3 = new MFXButton("Set Graphic");
        MFXButton b4 = new MFXButton("Remove Graphic");
        MFXButton b5 = new MFXButton("Remove Label Graphic");
        MFXRectangleToggleNode rtn = new MFXRectangleToggleNode("");
        rtn.setPrefSize(32, 32);
        rtn.setAlignment(Pos.CENTER);

        b1.setOnAction(event -> {
            rtn.setLabelLeadingIcon(MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor()));
        });
        b2.setOnAction(event -> {
            rtn.setLabelTrailingIcon(MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor()));
        });
        b3.setOnAction(event -> {
            rtn.setGraphic(MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor()));
        });
        b4.setOnAction(event -> {
            rtn.setGraphic(null);
        });
        b5.setOnAction(event -> {
            rtn.setLabelLeadingIcon(null);
            rtn.setLabelTrailingIcon(null);
        });

        bbox.getChildren().addAll(b1, b2, b3, b4, b5);

        box.getChildren().addAll(rtn, bbox);
        box.getStylesheets().add(MFXDemoResourcesLoader.load("css/TestDemo.css"));

        Scene scene = new Scene(box, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }

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
        return new MFXIconWrapper(new MFXFontIcon(desc, size, ColorUtils.getRandomColor()), size * 1.5);
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
}
