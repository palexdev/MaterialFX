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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumnCell;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.layout.Region;

public class MFXTableColumnCellSkin<T> extends LabelSkin {
    public MFXTableColumnCellSkin(MFXTableColumnCell<T> column) {
        super(column);

        column.setMinWidth(Region.USE_PREF_SIZE);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        column.setPadding(new Insets(0, 5, 0, 5));
        column.setGraphic(createLeadingIcon());
        column.setGraphicTextGap(5);
        addIcons();
    }

    protected Node createLeadingIcon() {
        MFXFontIcon caret = new MFXFontIcon("mfx-caret-up", 12);
        caret.setMouseTransparent(true);
        MFXIconWrapper icon = new MFXIconWrapper(caret, 18);
        icon.setVisible(false);
        NodeUtils.makeRegionCircular(icon);
        return icon;
    }

    private void addIcons() {
        Label column = getSkinnable();
        Node leading = column.getGraphic();

        if (isRightAlignment(column.getAlignment())) {
            if (leading != null) {
                column.setContentDisplay(ContentDisplay.LEFT);
            }
        } else {
            if (leading != null) {
                column.setContentDisplay(ContentDisplay.RIGHT);
            }
        }
    }

    private boolean isRightAlignment(Pos alignment) {
        return alignment == Pos.BASELINE_RIGHT || alignment == Pos.BOTTOM_RIGHT ||
                alignment == Pos.CENTER_RIGHT || alignment == Pos.TOP_RIGHT;
    }
}
