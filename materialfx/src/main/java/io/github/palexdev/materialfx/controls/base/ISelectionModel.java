package io.github.palexdev.materialfx.controls.base;

import javafx.beans.property.ListProperty;
import javafx.scene.input.MouseEvent;

public interface ISelectionModel<T> {
    void select(AbstractTreeItem<T> item, MouseEvent mouseEvent);
    void clearSelection(boolean all);
    AbstractTreeItem<T> getSelectedItem();
    ListProperty<AbstractTreeItem<T>> getSelectedItems();
    boolean allowsMultipleSelection();
    void setAllowsMultipleSelection(boolean multipleSelection);
}
