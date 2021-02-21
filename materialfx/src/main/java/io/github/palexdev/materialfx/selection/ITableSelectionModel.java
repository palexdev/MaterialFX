package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.controls.MFXTableRow;
import javafx.beans.property.ListProperty;
import javafx.scene.input.MouseEvent;

/**
 * Public API used by any {@code MFXTableView}.
 */
public interface ITableSelectionModel<T> {
    void select(MFXTableRow<T> row, MouseEvent mouseEvent);
    void clearSelection();
    MFXTableRow<T> getSelectedRow();
    ListProperty<MFXTableRow<T>> getSelectedRows();
    boolean allowsMultipleSelection();
    void setAllowsMultipleSelection(boolean multipleSelection);
}
