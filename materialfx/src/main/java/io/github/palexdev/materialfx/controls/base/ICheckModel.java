package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import javafx.beans.property.ListProperty;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

/**
 * Public API used by any MFXCheckTreeView.
 */
public interface ICheckModel<T> extends ISelectionModel<T> {
    void scanTree(MFXCheckTreeItem<T> item);
    void check(MFXCheckTreeItem<T> item, CheckTreeItemEvent<?> event);
    void clearChecked();
    ListProperty<MFXCheckTreeItem<T>> getCheckedItems();
}
