package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;
import io.github.palexdev.materialfx.controls.base.ISelectionModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class SelectionModel<T> implements ISelectionModel<T> {
    private final ListProperty<AbstractTreeItem<T>> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private boolean allowsMultipleSelection = false;

    public SelectionModel() {
        selectedItems.addListener((ListChangeListener<AbstractTreeItem<T>>) change -> {
            List<AbstractTreeItem<T>> tmpRemoved = new ArrayList<>();
            List<AbstractTreeItem<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }
            tmpRemoved.forEach(item -> item.setSelected(false));
            tmpAdded.forEach(item -> item.setSelected(true));
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void select(AbstractTreeItem<T> item, MouseEvent mouseEvent) {
        if (!allowsMultipleSelection) {
            clearSelection(false);
            selectedItems.setAll(item);
            return;
        }

        if (mouseEvent.isShiftDown()) {
            selectedItems.add(item);
        } else {
            clearSelection(true);
            selectedItems.setAll(item);
        }

        item.getItems().forEach(i -> System.out.println("ITEM:" + i.getData() + " is " + i.isSelected()));
    }

    @Override
    public void clearSelection(boolean all) {
        if (selectedItems.isEmpty()) {
            return;
        }

        if (all) {
            selectedItems.forEach(item -> item.setSelected(false));
        } else {
            selectedItems.get().get(0).setSelected(false);
        }
    }

    @Override
    public AbstractTreeItem<T> getSelectedItem() {
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    @Override
    public ListProperty<AbstractTreeItem<T>> getSelectedItems() {
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
