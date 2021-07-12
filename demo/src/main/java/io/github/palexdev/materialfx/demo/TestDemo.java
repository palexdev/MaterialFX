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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.beans.properties.ResettableDoubleProperty;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.enums.SliderEnums.SliderPopupSide;
import io.github.palexdev.materialfx.demo.model.FilterablePerson;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

@SuppressWarnings("All")
public class TestDemo extends Application {
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox(100);
        box.setAlignment(Pos.CENTER);

        MFXSlider slider = new MFXSlider();
        //slider.setSliderMode(SliderEnums.SliderMode.SNAP_TO_TICKS);
        slider.setBidirectional(false);
        slider.setPrefWidth(200);
        slider.setMin(10);
        slider.setMax(100);
        slider.setValue(-50);
        slider.setBidirectional(false);

        slider.getRanges1().add(NumberRange.of(slider.getMin()));
        slider.getRanges2().add(NumberRange.of(50.0));
        slider.getRanges3().add(NumberRange.of(slider.getMax()));

        HBox bbox = new HBox(20);
        bbox.setAlignment(Pos.CENTER);

        ResettableDoubleProperty property = new ResettableDoubleProperty(0.0, 10.5);
        property.setFireChangeOnReset(true);

        MFXButton b1 = new MFXButton("Change Thumb");
        MFXButton b2 = new MFXButton("Change Popup");
        MFXButton b3 = new MFXButton("Change Min");
        MFXButton b4 = new MFXButton("Change Max");
        MFXButton b5 = new MFXButton("Change Value");

        b1.setOnAction(event -> slider.setThumbSupplier(() -> {
            MFXButton button = new MFXButton("Thumb");
            button.setPrefSize(40, 30);
            return button;
        }));
        b2.setOnAction(event -> slider.setPopupSupplier(() -> {
            Label label = new Label();
            label.textProperty().bind(Bindings.createStringBinding(
                    () -> NumberUtils.formatToString(slider.getValue(), slider.getDecimalPrecision()),
                    slider.valueProperty()
            ));
            label.setStyle("-fx-border-color: orange");
            return label;
        }));
        b3.setOnAction(event -> {
            slider.setOrientation(slider.getOrientation() == Orientation.HORIZONTAL ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        });
        b4.setOnAction(event -> {
            slider.setPopupSide(slider.getPopupSide() == SliderPopupSide.DEFAULT ? SliderPopupSide.OTHER_SIDE : SliderPopupSide.DEFAULT);
        });
        b5.setOnAction(event -> {
            property.reset();
        });

        property.addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        bbox.getChildren().addAll(b1, b2, b3, b4, b5);

        box.getChildren().addAll(slider, bbox);
        box.getStylesheets().add(MFXDemoResourcesLoader.load("css/TestDemo.css"));

        Scene scene = new Scene(box, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        //ScenicView.show(scene);
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
