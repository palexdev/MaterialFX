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

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.popups.notifications.MFXNotificationPane;
import io.github.palexdev.mfxcore.popups.notifications.Notification;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.uniform;

public class Playground extends Application {

    @Override
    public void start(Stage stage) {
        Deque<Notification> notifications = IntStream.range(0, 10)
            .mapToObj(_ -> new TestNotification().timed(3000))
            .collect(Collectors.toCollection(LinkedList::new));

        MFXNotificationPane np = new MFXNotificationPane();
        np.setAlignment(Pos.TOP_RIGHT);
        np.setPadding(uniform(10.0).get());
        StackPane.setAlignment(np, Pos.TOP_RIGHT);
        np.setMaxVisible(3);

        StackPane root = new StackPane();
        root.getChildren().add(np);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();

        WhenEvent.intercept(stage, MouseEvent.MOUSE_CLICKED)
            .condition(e -> !notifications.isEmpty())
            .handle(e -> {
                if (e.isShiftDown()) {
                    np.clear(true);
                    return;
                }

                if (e.getButton() == MouseButton.SECONDARY) {
                    Node node = e.getPickResult().getIntersectedNode();
                    while (node != null) {
                        if (node instanceof TestNotification n) {
                            n.requestDismiss();
                            break;
                        }
                        node = node.getParent();
                    }
                    return;
                }

                np.show(notifications.pop());
            })
            .asFilter()
            .register();
    }

    static class TestNotification extends StackPane implements Notification {
        static int count = 1;
        final Label label;

        {
            label = new Label("Notification n." + (count++));
            getChildren().add(label);

            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 5px;
                -fx-border-color: gray;
                -fx-border-radius: 5px;
                -fx-padding: 6px;
                """);
        }

        @Override
        public Node toNode() {
            return this;
        }

        @Override
        public String toString() {
            return "Notification {" + label.getText() + "}";
        }
    }
}
