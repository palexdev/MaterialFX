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

package io.github.palexdev.materialfx.beans;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MFXContextMenuItem {
    private final Node node;
    private EventHandler<MouseEvent> action;

    public MFXContextMenuItem(Node node) {
        this.node = node;
    }

    public MFXContextMenuItem(Node node, EventHandler<MouseEvent> action) {
        this.node = node;
        this.action = action;
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, action);
    }

    public MFXContextMenuItem(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        node = new Label(text);
    }

    public MFXContextMenuItem(String text, EventHandler<MouseEvent> action) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        node = label;
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, action);
    }

    public Node getNode() {
        return node;
    }

    public EventHandler<MouseEvent> getAction() {
        return action;
    }

    public void setAction(EventHandler<MouseEvent> action) {
        if (this.action != null) {
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.action);
        }
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, action);
        this.action = action;
    }
}
