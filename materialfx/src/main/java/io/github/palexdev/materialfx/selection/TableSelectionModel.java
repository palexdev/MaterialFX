package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.controls.MFXTableRow;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class TableSelectionModel<T> implements ITableSelectionModel<T> {
    private final ListProperty<MFXTableRow<T>> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private boolean allowsMultipleSelection = false;

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

    @SuppressWarnings("unchecked")
    protected void select(MFXTableRow<T> item) {
        if (!allowsMultipleSelection) {
            clearSelection();
            selectedItems.setAll(item);
        } else {
            selectedItems.add(item);
        }
    }

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

    @Override
    public void clearSelection() {
        if (selectedItems.isEmpty()) {
            return;
        }

        selectedItems.forEach(item -> item.setSelected(false));
        selectedItems.clear();
    }

    @Override
    public MFXTableRow<T> getSelectedRow() {
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    @Override
    public ListProperty<MFXTableRow<T>> getSelectedRows() {
        return this.selectedItems;
    }

    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    @Override
    public void setAllowsMultipleSelection(boolean multipleSelection) {
        this.allowsMultipleSelection = multipleSelection;
    }
}
