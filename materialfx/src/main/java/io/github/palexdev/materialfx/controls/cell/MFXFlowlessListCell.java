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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractFlowlessListView;
import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListCell;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class MFXFlowlessListCell<T> extends AbstractMFXFlowlessListCell<T> {
    private final String STYLE_CLASS = "mfx-list-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-flowless-listcell.css").toString();
    protected final RippleGenerator rippleGenerator = new RippleGenerator(this);

    public MFXFlowlessListCell(AbstractFlowlessListView<T, ?, ?> listView, T  data) {
        super(listView, data);
        initialize();
    }

    public MFXFlowlessListCell(AbstractFlowlessListView<T, ?, ?> listView, T data, double fixedHeight) {
        super(listView, data, fixedHeight);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setupRippleGenerator();
    }

    protected void setupRippleGenerator() {
        rippleGenerator.setManaged(false);
        rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
        rippleGenerator.setInDuration(Duration.millis(400));

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
    }

    @Override
    protected void render(T data) {
        if (data instanceof Node) {
            getChildren().setAll((Node) data);
        } else {
            Label label = new Label(data.toString());
            label.getStyleClass().add("data-label");
            getChildren().setAll(label);
        }
        getChildren().add(0, rippleGenerator);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public String toString() {
        String className = getClass().getName();
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(simpleName);
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append("]");
        sb.append("[Data:").append(getData()).append("]");
        if (getId() != null) {
            sb.append("[id:").append(getId()).append("]");
        }

        return sb.toString();
    }
}
