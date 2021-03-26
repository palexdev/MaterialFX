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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.enums.SortState;
import io.github.palexdev.materialfx.skins.MFXTableColumnCellSkin;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.Comparator;

/**
 * This is the implementation of the column cells used in the {@link io.github.palexdev.materialfx.controls.MFXTableView} columns header.
 * <p>
 * Each column cell is a {@code Label}, has a name and has the following responsibilities:
 * - Has a row cell factory because each column knows how to build the corresponding row cell in each table row<p>
 * - Has a sort state and a comparator because each column knows how to sort the rows based on the given comparator, also
 * retains its sort state thus allowing switching between ASCENDING, DESCENDING, UNSORTED<p>
 */
public class MFXTableColumnCell<T> extends Label {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-table-column-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-table-column-cell.css");

    private final ReadOnlyDoubleWrapper initialWidth = new ReadOnlyDoubleWrapper();

    private final ObjectProperty<Callback<T, ? extends MFXTableRowCell>> rowCellFactory = new SimpleObjectProperty<>();
    private final StringProperty columnName = new SimpleStringProperty("");
    private final BooleanProperty hasTooltip = new SimpleBooleanProperty(true);
    private final StringProperty tooltipText = new SimpleStringProperty();

    private SortState sortState = SortState.UNSORTED;
    private Comparator<T> comparator;

    private static final PseudoClass DRAG_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragged");
    private final BooleanProperty dragged = new SimpleBooleanProperty(false);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableColumnCell(String columnName) {
        textProperty().bind(columnNameProperty());
        setColumnName(columnName);
        initialize();
    }

    public MFXTableColumnCell(String columnName, Comparator<T> comparator) {
        textProperty().bind(columnNameProperty());
        setColumnName(columnName);
        this.comparator = comparator;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setTooltipText(getColumnName());

        dragged.addListener(invalidate -> pseudoClassStateChanged(DRAG_PSEUDO_CLASS, dragged.get()));
        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> dragged.set(true));
        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> dragged.set(false));

        widthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && newValue.doubleValue() > 0) {
                    setInitialWidth(newValue.doubleValue());
                    widthProperty().removeListener(this);
                }
            }
        });
    }

    public double getInitialWidth() {
        return initialWidth.get();
    }

    public ReadOnlyDoubleProperty initialWidthProperty() {
        return initialWidth.getReadOnlyProperty();
    }

    protected void setInitialWidth(double initialWidth) {
        this.initialWidth.set(initialWidth);
    }

    public Callback<T, ? extends MFXTableRowCell> getRowCellFactory() {
        return rowCellFactory.get();
    }

    public ObjectProperty<Callback<T, ? extends MFXTableRowCell>> rowCellFactoryProperty() {
        return rowCellFactory;
    }

    public void setRowCellFactory(Callback<T, ? extends MFXTableRowCell> rowCellFactory) {
        this.rowCellFactory.set(rowCellFactory);
    }

    public String getColumnName() {
        return columnName.get();
    }

    /**
     * Specifies the name of the column.
     */
    public StringProperty columnNameProperty() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public boolean hasTooltip() {
        return hasTooltip.get();
    }

    /**
     * Specifies if the column cell should show a tooltip or not.
     * <p>
     * By default the tooltip is initialized with the column's name.
     */
    public BooleanProperty hasTooltipProperty() {
        return hasTooltip;
    }

    public void setHasTooltip(boolean hasTooltip) {
        this.hasTooltip.set(hasTooltip);
    }

    public String getTooltipText() {
        return tooltipText.get();
    }

    /**
     * Specifies the text shown by the tooltip.
     */
    public StringProperty tooltipTextProperty() {
        return tooltipText;
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText.set(tooltipText);
    }

    public SortState getSortState() {
        return sortState;
    }

    public void setSortState(SortState sortState) {
        this.sortState = sortState;
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableColumnCellSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
