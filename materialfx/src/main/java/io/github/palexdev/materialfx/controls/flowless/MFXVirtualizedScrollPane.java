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

package io.github.palexdev.materialfx.controls.flowless;


import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MFXVirtualizedScrollPane<V extends Node & Virtualized> extends VirtualizedScrollPane<V> {

    public MFXVirtualizedScrollPane(V content, ScrollPane.ScrollBarPolicy hPolicy, ScrollPane.ScrollBarPolicy vPolicy) {
        super(content, hPolicy, vPolicy);
        initialize();
    }

    public MFXVirtualizedScrollPane(V content) {
        super(content);
        initialize();
    }

    private void initialize() {
        sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                Set<ScrollBar> scrollBars = lookupAll(".scroll-bar").stream()
                        .filter(node -> node instanceof ScrollBar)
                        .map(node -> (ScrollBar) node)
                        .collect(Collectors.toSet());
                for (ScrollBar scrollBar : scrollBars) {
                    manageScrollBarBackground(scrollBar);
                    NodeUtils.updateBackground(scrollBar, Color.WHITE);
                }
                sceneProperty().removeListener(this);
            }
        });

        try {
            Field vbarField = VirtualizedScrollPane.class.getDeclaredField("vBar");
            vbarField.setAccessible(true);
            ScrollBar scrollBar = (ScrollBar) vbarField.get(this);
            scrollBar.getStyleClass().add("vvbar");
        } catch (Exception ignored) {
        }
    }

    private void manageScrollBarBackground(ScrollBar scrollBar) {
        scrollBar.backgroundProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !containsFill(newValue.getFills(), Color.WHITE)) {
                NodeUtils.updateBackground(scrollBar, Color.WHITE);
            }
        });
    }

    private boolean containsFill(List<BackgroundFill> backgroundFills, Color fill) {
        List<Paint> paints = backgroundFills.stream().map(BackgroundFill::getFill).collect(Collectors.toList());
        return paints.contains(fill);
    }
}
