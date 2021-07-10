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

package io.github.palexdev.materialfx.controls.flowless;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;

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
        setPadding(new Insets(0, 5, 0, 0));
    }

    public ScrollBar getVBar() {
        return vBar;
    }

    public ScrollBar getHBar() {
        return hBar;
    }

    @Override
    protected void layoutChildren() {
        double layoutWidth = snapSizeX(getLayoutBounds().getWidth());
        double layoutHeight = snapSizeY(getLayoutBounds().getHeight());
        boolean vbarVisible = vBar.isVisible();
        boolean hbarVisible = hBar.isVisible();
        double vbarWidth = snapSizeX(vbarVisible ? vBar.prefWidth(-1) : 0);
        double hbarHeight = snapSizeY(hbarVisible ? hBar.prefHeight(-1) : 0);

        double w = layoutWidth - vbarWidth;
        double h = layoutHeight - hbarHeight;

        content.resize(w + vbarWidth, h);

        hBar.setVisibleAmount(w);
        vBar.setVisibleAmount(h);

        if (vbarVisible) {
            vBar.resizeRelocate(layoutWidth - vbarWidth - snappedRightInset(), 0, vbarWidth, h);
        }

        if (hbarVisible) {
            hBar.resizeRelocate(0, layoutHeight - hbarHeight, w, hbarHeight);
        }
    }
}
