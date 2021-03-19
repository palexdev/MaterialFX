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

package io.github.palexdev.materialfx.utils;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class DragResizer {
    //================================================================================
    // Properties
    //================================================================================

    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 8;

    private final Region region;
    private boolean initMinHeight;
    private short dragging = 0;

    private final int allowedDirection;

    public static final short NOT_DRAGGING = 0;
    public static final short UP = 1;
    public static final short DOWN = 2;
    public static final short RIGHT = 4;
    public static final short LEFT = 8;
    public static final short ALL_DIRECTIONS = 15;

    //================================================================================
    // Constructors
    //================================================================================
    private DragResizer(Region region, int allowedDirection) {
        this.region = region;
        this.allowedDirection = allowedDirection;
    }

    //================================================================================
    // Methods
    //================================================================================
    public static void makeResizable(Region region, int allowedDirection) {
        final DragResizer resizer = new DragResizer(region, allowedDirection);

        region.addEventFilter(MouseEvent.MOUSE_PRESSED, resizer::mousePressed);
        region.addEventFilter(MouseEvent.MOUSE_DRAGGED, resizer::mouseDragged);
        region.addEventFilter(MouseEvent.MOUSE_MOVED, resizer::mouseOver);
        region.addEventFilter(MouseEvent.MOUSE_RELEASED, resizer::mouseReleased);
    }

    protected void mouseReleased(MouseEvent event) {
        initMinHeight = false; //Reset each time
        dragging = NOT_DRAGGING;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
        if (isInDraggableZoneS(event) || dragging == DOWN) {
            region.setCursor(Cursor.S_RESIZE);
        } else if (isInDraggableZoneE(event) || dragging == RIGHT) {
            region.setCursor(Cursor.E_RESIZE);
        } else if (isInDraggableZoneN(event) || dragging == UP) {
            region.setCursor(Cursor.N_RESIZE);
        } else if (isInDraggableZoneW(event) || dragging == LEFT) {
            region.setCursor(Cursor.W_RESIZE);
        } else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    private boolean isInDraggableZoneN(MouseEvent event) {
        return (this.allowedDirection & UP) > 0 && event.getY() < RESIZE_MARGIN;
    }

    private boolean isInDraggableZoneW(MouseEvent event) {
        return (this.allowedDirection & LEFT) > 0 && event.getX() < RESIZE_MARGIN;
    }

    private boolean isInDraggableZoneS(MouseEvent event) {
        return (this.allowedDirection & DOWN) > 0 && event.getY() > (region.getHeight() - RESIZE_MARGIN);
    }

    private boolean isInDraggableZoneE(MouseEvent event) {
        return (this.allowedDirection & RIGHT) > 0 && event.getX() > (region.getWidth() - RESIZE_MARGIN);
    }


    private void mouseDragged(MouseEvent event) {
        switch (dragging) {
            case UP: { handleUp(event); break; }
            case DOWN: { handleDown(event); break; }
            case LEFT: { handleLeft(event); break; }
            case RIGHT: { handleRight(event); break; }
        }
    }

    private void mousePressed(MouseEvent event) {
        if (isInDraggableZoneE(event)) {
            dragging = RIGHT;
        } else if (isInDraggableZoneS(event)) {
            dragging = DOWN;
        } else if (isInDraggableZoneN(event)) {
            dragging = UP;
        } else if (isInDraggableZoneW(event)) {
            dragging = LEFT;
        } else {
            return;
        }


        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            region.setMinWidth(region.getWidth());
            initMinHeight = true;
        }
        event.consume();
    }

    private void handleUp(MouseEvent event) {
        double prevMin = region.getMinHeight();
        region.setMinHeight(region.getMinHeight() - event.getY());

        if (region.getMinHeight() < region.getPrefHeight()) {
            region.setMinHeight(region.getPrefHeight());
            region.setTranslateY(region.getTranslateY() - (region.getPrefHeight() - prevMin));
            return;
        }

        if (region.getMinHeight() > region.getPrefHeight() || event.getY() < 0) {
            region.setTranslateY(region.getTranslateY() + event.getY());
        }
    }

    private void handleDown(MouseEvent event) {
        region.setMinHeight(event.getY());
    }

    private void handleLeft(MouseEvent event) {
        double prevMin = region.getMinWidth();
        region.setMinWidth(region.getMinWidth() - event.getX());
        if (region.getMinWidth() < region.getPrefWidth()) {
            region.setMinWidth(region.getPrefWidth());
            region.setTranslateX(region.getTranslateX() - (region.getPrefWidth() - prevMin));
            return;
        }
        if (region.getMinWidth() > region.getPrefWidth() || event.getX() < 0)
            region.setTranslateX(region.getTranslateX() + event.getX());
    }

    private void handleRight(MouseEvent event) {
        region.setMinWidth(event.getX());
    }
}
