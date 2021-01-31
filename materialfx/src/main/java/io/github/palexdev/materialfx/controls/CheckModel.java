package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.controls.base.ICheckModel;
import io.github.palexdev.materialfx.utils.TreeItemStream;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

public class CheckModel<T> extends SelectionModel<T> implements ICheckModel<T> {
    private final ListProperty<MFXCheckTreeItem<T>> checkedItems = new SimpleListProperty<>(FXCollections.observableArrayList());

    public CheckModel() {
        super();
        checkedItems.addListener((ListChangeListener<MFXCheckTreeItem<T>>) change -> {
            List<MFXCheckTreeItem<T>> tmpRemoved = new ArrayList<>();
            List<MFXCheckTreeItem<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }
            tmpRemoved.forEach(item -> item.setChecked(false));
            tmpAdded.forEach(item -> item.setChecked(true));
        });
    }

    @Override
    public void scanTree(MFXCheckTreeItem<T> item) {
        clearChecked();
        TreeItemStream.flattenTree(item).forEach(treeItem -> {
            if (((MFXCheckTreeItem<T>) treeItem).isChecked()) check(item, null);
        });
    }

    @Override
    public void check(MFXCheckTreeItem<T> item, CheckTreeItemEvent<?> event) {
        if (event == null) {
            List<MFXCheckTreeItem<T>> items = TreeItemStream.flattenTree(item).map(cItem -> (MFXCheckTreeItem<T>) cItem).collect(Collectors.toList());
            checkAll(items);
            return;
        }

        if (event.getItemRef() != null && event.getItemRef() == item) {
            List<MFXCheckTreeItem<T>> items = TreeItemStream.flattenTree(item).map(cItem -> (MFXCheckTreeItem<T>) cItem).collect(Collectors.toList());
            if (!item.isChecked() || item.isIndeterminate()) {
                checkAll(items);
            } else {
                uncheckAll(items);
            }
            return;
        }

        if (checkedChildren(item) == item.getItems().size()) {
            item.setIndeterminate(false);
            check(item);
        } else if (indeterminateChildren(item) != 0) {
            checkedItems.remove(item);
            item.setIndeterminate(true);
        } else if (checkedChildren(item) == 0){
            checkedItems.remove(item);
            item.setIndeterminate(false);
        } else {
            checkedItems.remove(item);
            item.setIndeterminate(true);
        }
    }

    private void check(MFXCheckTreeItem<T> item) {
        if (item.isChecked()) {
            checkedItems.remove(item);
        } else {
            checkedItems.add(item);
        }
    }

    private void checkAll(List<MFXCheckTreeItem<T>> items) {
        checkedItems.addAll(items);
    }

    private void uncheckAll(List<MFXCheckTreeItem<T>> items) {
        checkedItems.removeAll(items);
    }

    private int checkedChildren(MFXCheckTreeItem<T> item) {
        int cnt = 0;
        for (AbstractMFXTreeItem<T> treeItem : item.getItems()) {
            MFXCheckTreeItem<T> cItem = (MFXCheckTreeItem<T>) treeItem;
            if (cItem.isChecked()) {
                cnt++;
            }
        }
        return cnt;
    }

    private int indeterminateChildren(MFXCheckTreeItem<T> item) {
        int cnt = 0;
        for (AbstractMFXTreeItem<T> treeItem : item.getItems()) {
            MFXCheckTreeItem<T> cItem = (MFXCheckTreeItem<T>) treeItem;
            if (cItem.isIndeterminate()) {
                cnt++;
            }
        }
        return cnt;
    }

    @Override
    public void clearChecked() {
        if (checkedItems.isEmpty()) {
            return;
        }

        checkedItems.forEach(item -> item.setChecked(false));
        checkedItems.clear();
    }

    @Override
    public MFXCheckTreeItem<T> getCheckedItem() {
        if (checkedItems.isEmpty()) {
            return null;
        }
        return checkedItems.get(0);
    }

    @Override
    public ListProperty<MFXCheckTreeItem<T>> getCheckedItems() {
        return this.checkedItems;
    }
}
