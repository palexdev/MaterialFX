/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListView;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessListCell;
import io.github.palexdev.materialfx.selection.ListSelectionModel;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.skins.MFXFlowlessListViewSkin;
import javafx.beans.property.MapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Implementation of a list view based on Flowless.
 * <p>
 * Extends {@link AbstractMFXFlowlessListView}.
 * <p></p>
 * Default cell: {@link MFXFlowlessListCell}.
 * <p>
 * Default selection model: {@link ListSelectionModel}.
 * <p>
 * Default skin: {@link MFXFlowlessListViewSkin}.
 */
public class MFXFlowlessListView<T> extends AbstractMFXFlowlessListView<T, MFXFlowlessListCell<T>, IListSelectionModel<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-list-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFlowlessListView.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFlowlessListView() {
        this(List.of());
    }

    public MFXFlowlessListView(List<T> items) {
        super(items);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void initialize() {
        super.initialize();
        getStyleClass().setAll(STYLE_CLASS);

        items.addListener((ListChangeListener<? super T>) change -> {
            if (getSelectionModel().getSelectedItems().isEmpty()) {
                return;
            }
            if (change.getList().isEmpty()) {
                getSelectionModel().clearSelection();
                return;
            }

            getSelectionModel().setUpdating(true);
            Map<Integer, Integer> addedOffsets = new HashMap<>();
            Map<Integer, Integer> removedOffsets = new HashMap<>();

            while (change.next()) {
                if (change.wasAdded()) {
                    int from = change.getFrom();
                    int to = change.getTo();
                    int offset = to - from;
                    addedOffsets.put(from, offset);
                }
                if (change.wasRemoved()) {
                    int from = change.getFrom();
                    int offset = change.getRemovedSize();
                    IntStream.range(from, from + offset)
                            .filter(getSelectionModel()::containSelected)
                            .forEach(getSelectionModel()::clearSelectedItem);
                    removedOffsets.put(from, offset);
                }
            }
            updateSelection(addedOffsets, removedOffsets);
        });
    }

    protected void updateSelection(Map<Integer, Integer> addedOffsets, Map<Integer, Integer> removedOffsets) {
        MapProperty<Integer, T> selectedItems = getSelectionModel().selectedItemsProperty();
        ObservableMap<Integer, T> updatedMap = FXCollections.observableHashMap();
        selectedItems.forEach((key, value) -> {
            int sum = addedOffsets.entrySet().stream()
                    .filter(entry -> entry.getKey() <= key)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            int diff = removedOffsets.entrySet().stream()
                    .filter(entry -> entry.getKey() < key)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            int shift = sum - diff;
            updatedMap.put(key + shift, value);
        });
        if (!selectedItems.equals(updatedMap)) {
            selectedItems.set(updatedMap);
        }
        getSelectionModel().setUpdating(false);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void setDefaultCellFactory() {
        setCellFactory(item -> new MFXFlowlessListCell<>(this, item));
    }

    @Override
    protected void setDefaultSelectionModel() {
        IListSelectionModel<T> selectionModel = new ListSelectionModel<>();
        setSelectionModel(selectionModel);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXFlowlessListViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
