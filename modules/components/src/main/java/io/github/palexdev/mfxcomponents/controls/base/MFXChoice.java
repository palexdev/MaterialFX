/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.controls.base;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.cells.MFXCell;
import io.github.palexdev.mfxcomponents.popups.ExtendedPopoverConfig;
import io.github.palexdev.mfxcore.base.properties.functional.ConsumerProperty;
import io.github.palexdev.mfxcore.base.properties.functional.FunctionProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.MultipleSelectionHandler;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.SelectionEventHandler;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.SingleSelectionHandler;
import io.github.palexdev.mfxcore.selection.model.SelectionModel;
import io.github.palexdev.mfxcore.selection.model.WithSelectionModel;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.y;

/// Base class for all `MaterialFX` components which allow the user to make a choice among a list of options, e.g.:
/// combo boxes, split buttons.
///
/// Implements the [WithSelectionModel] interface and uses a [SelectionModel] in single selection mode to keep track of
/// the selection.
///
/// Options are usually presented in a popup as a list. This component offers:
/// - the [#cellFactoryProperty()] to allow customizing how choices are rendered
/// - the [#popupConfigProperty()] to configure the popup
///
/// It's possible to specify an action to run when the selection changes by using the [#onSelectionChangedProperty()].
@Deprecated(since = "Will become MFXComboBox")
public abstract class MFXChoice<T> extends MFXControl implements WithSelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    // TODO we may change this to a RefineList in the future for filter and order functionality
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final FunctionProperty<T, VFXCell<T>> cellFactory = new FunctionProperty<>(MFXCell::new);

    private final ISelectionModel<T> selectionModel = new SelectionModel<>(items);
    private final ConsumerProperty<T> onSelectionChanged = new ConsumerProperty<>(_ -> {}) {
        @Override
        public void set(Consumer<T> newValue) {
            if (newValue == null) newValue = _ -> {};
            super.set(newValue);
        }
    };

    private final BooleanProperty open = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            PseudoClasses.OPEN.setOn(MFXChoice.this, get());
        }
    };
    private final ObjectProperty<ExtendedPopoverConfig> popupConfig = new SimpleObjectProperty<>(
        ExtendedPopoverConfig.builder().offset(y(4.0)).build()
    );

    //================================================================================
    // Constructors
    //================================================================================

    protected MFXChoice() {}

    @SafeVarargs
    protected MFXChoice(T... items) {
        this.items.addAll(items);
    }

    protected MFXChoice(Collection<T> items) {
        this.items.addAll(items);
    }

    {
        selectionModel.setAllowsMultipleSelection(false);
        selectionModel.setEventHandler(this::buildSelectionEventHandler);
        selection().addListener((InvalidationListener) _ -> onSelectionChanged.get().accept(getSelectedItem()));
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This method is responsible for building the [SelectionEventHandler] for the selection model used by the component.
    ///
    /// Defaults may not be ideal depending on the component and the use case.
    protected SelectionEventHandler buildSelectionEventHandler(ISelectionModel<T> selectionModel) {
        if (selectionModel.allowsMultipleSelection()) return new MultipleSelectionHandler(selectionModel);
        return new SingleSelectionHandler(selectionModel) {
            @Override
            public void handle(MouseEvent me, int index) {
                if (me.getButton() != MouseButton.PRIMARY) return;
                boolean selected = selectionModel.contains(index);
                if (!selected) selectionModel.selectIndex(index);
            }

            @Override
            public void handle(KeyEvent ke, int index) {
                if (ke.getCode() != KeyCode.ENTER && ke.getCode() != KeyCode.SPACE) return;
                boolean selected = selectionModel.contains(index);
                if (!selected) selectionModel.selectIndex(index);
            }
        };
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXBehavior<>(this) {};
    }

    @Override
    public ISelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the list containing all the possible choices
    public ObservableList<T> getItems() {
        return items;
    }

    public Function<T, VFXCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    /// Specifies the factory to use to build the cells used to display the choices.
    public FunctionProperty<T, VFXCell<T>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Function<T, VFXCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public Consumer<T> getOnSelectionChanged() {
        return onSelectionChanged.get();
    }

    /// Specifies the action to perform when the selection changes, carries the current selected item or `null`.
    public ConsumerProperty<T> onSelectionChangedProperty() {
        return onSelectionChanged;
    }

    public void setOnSelectionChanged(Consumer<T> onSelectionChanged) {
        this.onSelectionChanged.set(onSelectionChanged);
    }

    public boolean isOpen() {
        return open.get();
    }

    /// Specifies whether the choice menu is open.
    ///
    /// This will also set the `:open` pseudo state on component accordingly.
    public ReadOnlyBooleanProperty openProperty() {
        return open;
    }

    public ExtendedPopoverConfig getPopupConfig() {
        return popupConfig.get();
    }

    /// Specifies the settings for the popup used to display the choices.
    public ObjectProperty<ExtendedPopoverConfig> popupConfigProperty() {
        return popupConfig;
    }

    public void setPopupConfig(ExtendedPopoverConfig popupConfig) {
        this.popupConfig.set(popupConfig);
    }
}
