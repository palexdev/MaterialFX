package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.selection.base.IListCheckModel;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;

import java.util.List;
import java.util.Map;

public class ListCheckModel<T> extends ListSelectionModel<T> implements IListCheckModel<T> {
    private final MapProperty<Integer, T> checkedItems = new SimpleMapProperty<>(getObservableTreeMap());

    @Override
    public void check(int index, T data) {
        checkedItems.put(index, data);
    }

    @Override
    public void updateIndex(T data, int index) {
        super.updateIndex(data, index);

        int mapIndex = checkedItems.entrySet()
                .stream()
                .filter(entry -> data.equals(entry.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(-1);
        if (mapIndex != -1) {
            checkedItems.put(mapIndex, data);
        }
    }

    @Override
    public void clearCheckedItem(int index) {
        checkedItems.remove(index);
    }

    @Override
    public void clearCheckedItem(T data) {
        checkedItems.entrySet().stream()
                .filter(entry -> entry.getValue().equals(data))
                .findFirst()
                .ifPresent(entry -> checkedItems.remove(entry.getKey()));
    }

    @Override
    public void clearChecked() {
        checkedItems.clear();
    }

    @Override
    public T getCheckedItem(int index) {
        if (checkedItems.isEmpty()) {
            return null;
        }

        try {
            return checkedItems.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> getCheckedItems() {
        return List.copyOf(checkedItems.values());
    }

    @Override
    public MapProperty<Integer, T> checkedItemsProperty() {
        return checkedItems;
    }
}
