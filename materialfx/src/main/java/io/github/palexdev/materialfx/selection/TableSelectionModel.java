package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.selection.base.ITableSelectionModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@code ITableSelectionModel} interface.
 * <p>
 * Basic selection model, allows to: clear the selection, single and multiple selection of {@link MFXTableRow}s.
 */
public class TableSelectionModel<T> implements ITableSelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ListProperty<MFXTableRow<T>> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private boolean allowsMultipleSelection = false;

    //================================================================================
    // Constructors
    //================================================================================
    public TableSelectionModel() {
        selectedItems.addListener((ListChangeListener<MFXTableRow<T>>) change -> {
            List<MFXTableRow<T>> tmpRemoved = new ArrayList<>();
            List<MFXTableRow<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }
            tmpRemoved.forEach(item -> item.setSelected(false));
            tmpAdded.forEach(item -> item.setSelected(true));
        });
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This method is called when the mouseEvent argument passed to
     * {@link #select(MFXTableRow, MouseEvent)} is null.
     * <p>
     * If the model is set to not allow multiple selection then we clear the list
     * and then add the item to it.
     *
     * @param row the row to select
     */
    @SuppressWarnings("unchecked")
    protected void select(MFXTableRow<T> row) {
        if (!allowsMultipleSelection) {
            selectedItems.setAll(row);
        } else {
            selectedItems.add(row);
        }
    }

    //================================================================================
    // Methods Implementation
    //================================================================================

    /**
     * This method is called by {@link io.github.palexdev.materialfx.skins.MFXTableViewSkin} when
     * the mouse is pressed on a row. We need the mouse event as a parameter in case multiple selection is
     * allowed because we need to check if the Shift key or Ctrl key were pressed.
     * <p>
     * If the mouseEvent is null we call the other {@link #select(MFXTableRow)} method.
     * <p>
     * If the selection is single {@link #clearSelection()} we clear the selection
     * and add the new selected item to the list.
     * <p>
     * If the selection is multiple we check if the item was already selected,
     * if that is the case by default the item is deselected.
     * <p>
     * In case neither Shift nor Ctrl are pressed we clear the selection.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void select(MFXTableRow<T> row, MouseEvent mouseEvent) {
        if (mouseEvent == null) {
            select(row);
            return;
        }

        if (!allowsMultipleSelection) {
            clearSelection();
            selectedItems.setAll(row);
            return;
        }


        if (mouseEvent.isShiftDown() || mouseEvent.isControlDown()) {
            if (row.isSelected()) {
                selectedItems.remove(row);
            } else {
                selectedItems.add(row);
            }
        } else {
            clearSelection();
            selectedItems.setAll(row);
        }
    }

    /**
     * Resets every item in the list to selected false and then clears the list.
     */
    @Override
    public void clearSelection() {
        if (selectedItems.isEmpty()) {
            return;
        }

        selectedItems.forEach(item -> item.setSelected(false));
        selectedItems.clear();
    }

    /**
     * Gets the selected row. If the selection is multiple {@link #getSelectedRows()} ()} should be
     * called instead, as this method will only return the first item of the list.
     *
     * @return the first selected item of the list
     */
    @Override
    public MFXTableRow<T> getSelectedRow() {
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    /**
     * @return the ListProperty which contains all the selected items.
     */
    @Override
    public ListProperty<MFXTableRow<T>> getSelectedRows() {
        return this.selectedItems;
    }

    /**
     * @return true if allows multiple selection, false if not.
     */
    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    /**
     * Sets the selection mode of the model, single or multiple.
     */
    @Override
    public void setAllowsMultipleSelection(boolean multipleSelection) {
        this.allowsMultipleSelection = multipleSelection;
    }
}
