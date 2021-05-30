package io.github.palexdev.materialfx.selection.base;

import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Public API for selection used by any {@link MFXTableView}.
 */
public interface ITableSelectionModel<T> {
    boolean containsSelected(T data);
    boolean containSelected(int index);
    void select(int index, T data, MouseEvent mouseEvent);
    void clearSelectedItem(int index);
    void clearSelectedItem(T item);
    void clearSelection();
    int getSelectedIndex();
    List<Integer> getSelectedIndexes();
    T getSelectedItem();
    T getSelectedItem(int index);
    List<T> getSelectedItems();
    MapProperty<Integer, T> selectedItemsProperty();
    boolean allowsMultipleSelection();
    void setAllowsMultipleSelection(boolean multipleSelection);
    boolean isUpdating();
    BooleanProperty updatingProperty();
    void setUpdating(boolean updating);
}
