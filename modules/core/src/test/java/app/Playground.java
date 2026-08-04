/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package app;

import io.github.palexdev.mfxcore.utils.fx.resize.Resizer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Playground extends Application {

    @Override
    public void start(Stage stage) {
        Circle c = new Circle(150);
        c.setStroke(Color.RED);

        Region r = new Region();
        r.setPrefSize(100, 100);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setBorder(Border.stroke(Color.RED));

        StackPane root = new StackPane(r);
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();

//        Resizer.resizer(c)
//            .hitSource(root)
//            .install();


        Resizer.resizer(r)
            .hitSource(root)
            .install();
    }
}
