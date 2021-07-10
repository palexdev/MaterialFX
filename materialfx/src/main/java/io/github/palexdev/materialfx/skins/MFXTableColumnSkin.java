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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.DragResizer;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTableColumn}.
 * <p></p>
 * Simply an HBox with a label, an icon for sorting and an icon for locking/unlocking the column's width,
 * both positioned manually based on the column's alignment.
 * It also has support for resizing the column on drag.
 */
public class MFXTableColumnSkin<T> extends SkinBase<MFXTableColumn<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label label;
    private final MFXIconWrapper lockIcon;

    private final DragResizer dragResizer;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableColumnSkin(MFXTableColumn<T> column) {
        super(column);

        label = new Label();
        label.textProperty().bind(column.textProperty());

        MFXFontIcon icon = new MFXFontIcon(column.isResizable() ? "mfx-lock" : "mfx-lock-open", 12);
        icon.descriptionProperty().bind(Bindings.createStringBinding(
                () -> column.isResizable() ? "mfx-lock" : "mfx-lock-open",
                column.resizableProperty()
        ));
        lockIcon = new MFXIconWrapper(icon, 20).defaultRippleGeneratorBehavior();
        lockIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                column.setResizable(!column.isResizable());
            }
            event.consume();
        });
        lockIcon.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> column.isShowLockIcon() && (column.isHover() || !column.isResizable()),
                column.showLockIconProperty(), column.hoverProperty(), column.resizableProperty()
        ));
        lockIcon.setManaged(false);
        NodeUtils.makeRegionCircular(lockIcon);

        container = new HBox(label, column.getSortIcon(), lockIcon);
        container.setMinWidth(Region.USE_PREF_SIZE);
        container.setPadding(new Insets(0,10, 0, 0));
        container.alignmentProperty().bind(column.columnAlignmentProperty());
        container.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getMinX() <= 0) {
                container.relocate(1, 1);
            }
        });

        dragResizer = new DragResizer(column, DragResizer.RIGHT);

        if (column.isResizable()) {
            dragResizer.makeResizable();
        }

        setListeners();

        getChildren().setAll(container);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXTableColumn#resizableProperty()} ()}: to enable/disable this column {@link DragResizer} by calling
     * {@link DragResizer#makeResizable()} or {@link DragResizer#uninstall()} depending on its value.
     */
    private void setListeners() {
        MFXTableColumn<T> tableColumn = getSkinnable();

        tableColumn.resizableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                dragResizer.uninstall();
            } else {
                dragResizer.makeResizable();
            }
        });
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double computed;
        MFXTableColumn<T> column = getSkinnable();
        if (NodeUtils.isRightAlignment(column.getColumnAlignment())) {
            computed = leftInset + label.getWidth() + column.getSortIcon().getSize() + lockIcon.getSize() + rightInset + 20;
        } else {
            computed = leftInset + label.getWidth() + column.getSortIcon().getSize() + lockIcon.getSize() + rightInset + 10;
        }
        return computed;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        // SORT ICON
        MFXTableColumn<T> column = getSkinnable();
        MFXIconWrapper sortIcon = column.getSortIcon();
        Pos alignment = column.getColumnAlignment();

        double sortSize = sortIcon.getSize();
        double sX;
        double iconsY = snapPositionY((h / 2) - (sortSize / 2));

        if (!NodeUtils.isRightAlignment(alignment)) {
            sX = snapPositionX(w - sortSize - 5);
        } else {
            sX = 5;
        }

        sortIcon.resizeRelocate(sX, iconsY, sortSize, sortSize);

        // LOCK ICON
        double lockSize = lockIcon.getSize();
        double lX;

        if (!NodeUtils.isRightAlignment(alignment)) {
            lX = snapPositionX(w - sortSize - lockSize - 10);
        } else {
            lX = 10 + sortSize;
        }

        lockIcon.resizeRelocate(lX, iconsY, lockSize, lockSize);
    }
}
