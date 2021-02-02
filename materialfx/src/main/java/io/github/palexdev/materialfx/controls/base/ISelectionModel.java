package io.github.palexdev.materialfx.controls.base;

import javafx.beans.property.ListProperty;
import javafx.scene.input.MouseEvent;

/**
 * Public API used by any MFXTreeView.
 */
public interface ISelectionModel<T> {
    void scanTree(AbstractMFXTreeItem<T> item);

    void select(AbstractMFXTreeItem<T> item, MouseEvent mouseEvent);

    void clearSelection();

    AbstractMFXTreeItem<T> getSelectedItem();

    ListProperty<AbstractMFXTreeItem<T>> getSelectedItems();

    boolean allowsMultipleSelection();

    void setAllowsMultipleSelection(boolean multipleSelection);
}
