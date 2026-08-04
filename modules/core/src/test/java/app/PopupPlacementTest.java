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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.popups.*;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.top;

public class PopupPlacementTest extends Application {
    static final Label placementInfoLabel = new Label("");

    @Override
    public void start(Stage stage) {
        TestRegion region = new TestRegion();

        CSSFragment.applyOn(
            """
                .mfx-popover > Rectangle {
                  -fx-arc-width: 12px;
                  -fx-arc-height: 12px;
                  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.5), 20, 0, 0, 0);
                  -fx-fill: rgba(0, 0, 0, 0.5);
                  -fx-stroke: rgba(255, 255, 255, 0.3);
                }
                """,
            region
        );

        StackPane.setAlignment(placementInfoLabel, Pos.TOP_CENTER);
        StackPane.setMargin(placementInfoLabel, top(8.0).get());

        StackPane root = new StackPane(region, placementInfoLabel);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    static class TestRegion extends Region {
        static final Placement[] allPlacements = new Placement[]{
            Placement.IN_CENTER,
            Placement.Inside.TOP_LEFT, Placement.Inside.TOP_CENTER, Placement.Inside.TOP_RIGHT,
            Placement.Inside.CENTER_LEFT, Placement.Inside.CENTER_RIGHT,
            Placement.Inside.BOTTOM_LEFT, Placement.Inside.BOTTOM_CENTER, Placement.Inside.BOTTOM_RIGHT,
            Placement.Outside.TOP_LEFT, Placement.Outside.TOP_CENTER, Placement.Outside.TOP_RIGHT,
            Placement.Outside.CENTER_LEFT, Placement.Outside.CENTER_RIGHT,
            Placement.Outside.BOTTOM_LEFT, Placement.Outside.BOTTOM_CENTER, Placement.Outside.BOTTOM_RIGHT
        };
        final Map<Placement, Node> actuationPoints = new HashMap<>();

        ToggleGroup group = new ToggleGroup();
        Tuple3<RadioButton, Placement, MFXPopup<?>> shown = null;

        {
            setPrefSize(200.0, 200.0);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

            for (Placement placement : allPlacements) {
                actuationPoints.put(placement, createActuationPoint(placement));
            }
            getChildren().addAll(actuationPoints.values());

            setStyle("-fx-border-color: #FF0000; -fx-border-width: 2px;");
        }

        Node createActuationPoint(Placement placement) {
            RadioButton btn = new RadioButton();
            btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btn.setToggleGroup(group);
            onInvalidated(btn.selectedProperty())
                .condition(Function.identity())
                .then(_ -> shown = new Tuple3<>(btn, placement, showPopup(placement)))
                .listen();
            return btn;
        }

        MFXPopup<?> showPopup(Placement placement) {
            if (shown != null) {
                shown.c().hide();
                shown = null;
            }

            placementInfoLabel.setText(placement.toString());
            MFXPopover p = MFXPopups.popover(cfg -> cfg
                    .styleableParent(this))
                .setContent(_ -> new Rectangle(32.0, 32.0))
                .show(this, placement);
            //ScenicView.show(p.getRoot());
            return p;
        }

        @Override
        protected void layoutChildren() {
            for (Placement placement : allPlacements) {
                Node node = actuationPoints.get(placement);
                node.autosize();

                AnchorHandlers.AnchorHandler handler = AnchorHandlers.handler(placement.anchor());
                Position pos = handler.compute(getLayoutBounds(), node.getLayoutBounds(), placement.xDirection(), placement.yDirection());
                node.relocate(pos.x(), pos.y());
            }
        }
    }

    record Tuple3<A, B, C>(A a, B b, C c) {}
}
