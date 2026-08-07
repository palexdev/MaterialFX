/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import io.github.palexdev.mfxcore.base.Disposable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static javafx.scene.input.MouseEvent.*;

public class WindowMover {

    //================================================================================
    // Properties
    //================================================================================

    protected Window window;
    protected Node anchor;
    private final List<Disposable> disposables = new ArrayList<>();

    private double deltaX;
    private double deltaY;
    private boolean canMove = true;

    private Predicate<MouseEvent> condition;

    //================================================================================
    // Methods
    //================================================================================

    public void install(Window window, Node anchor) {
        if (window == null || anchor == null)
            throw new IllegalArgumentException("Window and anchor Node must not be null");
        if (this.window != null)
            throw new IllegalStateException("WindowMover is already installed on window: " + window);
        this.window = window;
        this.anchor = anchor;
        Collections.addAll(disposables,
            intercept(anchor, MOUSE_PRESSED)
                .condition(this::canMove)
                .handle(e -> {
                    deltaX = window.getX() - e.getScreenX();
                    deltaY = window.getY() - e.getScreenY();
                    setCursor(Cursor.MOVE);
                })
                .asFilter()
                .register(),
            intercept(anchor, MOUSE_RELEASED)
                .condition(this::canMove)
                .handle(_ -> setCursor(Cursor.HAND))
                .asFilter()
                .register(),
            intercept(anchor, MOUSE_DRAGGED)
                .condition(this::canMove)
                .handle(e -> move(e, deltaX, deltaY))
                .asFilter()
                .register(),
            intercept(anchor, MOUSE_MOVED)
                .handle(e -> {
                    canMove = NodeUtils.isDescendantOf(e, anchor);
                    if (condition != null && !condition.test(e)) return;
                    setCursor(canMove ? Cursor.HAND : null);
                })
                .asFilter()
                .register()
        );
    }

    public void uninstall() {
        if (window == null) return;
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        deltaX = 0;
        deltaY = 0;
        canMove = true;
        window = null;
        anchor = null;
    }

    protected void move(MouseEvent e, double deltaX, double deltaY) {
        window.setX(e.getScreenX() + deltaX);
        window.setY(e.getScreenY() + deltaY);
    }

    protected void setCursor(Cursor cursor) {
        anchor.setCursor(cursor);
    }

    private boolean canMove(MouseEvent me) {
        return canMove && (condition == null || condition.test(me));
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public Predicate<MouseEvent> condition() {
        return condition;
    }

    public WindowMover condition(Predicate<MouseEvent> condition) {
        this.condition = condition;
        return this;
    }
}
