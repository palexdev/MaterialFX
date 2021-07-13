/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.enums.SortState;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXTableColumnSkin;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is the implementation of the column cells used in the {@link MFXTableView} control.
 * <p></p>
 * Extends {@code Control} so a new skin is also provided and can be also easily changed.
 * <p>
 * Defines the following new PseudoClasses for usage in CSS:
 * <p> - ":dragged", to customize the column when it is dragged
 * <p> - ":resizable", to customize the column depending on {@link #resizableProperty()}
 * <p></p>
 * Each column cell has the following responsibilities:
 * - Has a row cell factory because each column knows how to build the corresponding row cell in each table row<p>
 * - Has a sort state and a comparator because each column knows how to sort the rows based on the given comparator, also
 * retains its sort state thus allowing switching between ASCENDING, DESCENDING, UNSORTED<p>
 *
 * @see MFXTableColumnSkin
 */
public class MFXTableColumn<T> extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-table-column";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTableColumn.css");

    private final ReadOnlyDoubleWrapper initialWidth = new ReadOnlyDoubleWrapper();

    private final StringProperty text = new SimpleStringProperty();
    private final ObjectProperty<Pos> columnAlignment = new SimpleObjectProperty<>(Pos.CENTER_LEFT);
    private final ObjectProperty<Function<T, MFXTableRowCell>> rowCellFunction = new SimpleObjectProperty<>();

    private final ObjectProperty<SortState> sortState = new SimpleObjectProperty<>(SortState.UNSORTED);
    private MFXIconWrapper sortIcon;
    private Comparator<T> comparator;

    private final BooleanProperty showLockIcon = new SimpleBooleanProperty(true);
    private final ObjectProperty<Supplier<Tooltip>> tooltipSupplier = new SimpleObjectProperty<>();

    protected static final PseudoClass RESIZABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("resizable");
    protected static final PseudoClass DRAG_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragged");
    private final BooleanProperty dragged = new SimpleBooleanProperty(false);
    private final BooleanProperty resizable = new SimpleBooleanProperty(true);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableColumn(String text) {
        this(text, null);
    }

    public MFXTableColumn(StringExpression text) {
        this(text, null);
    }

    public MFXTableColumn(String text, Comparator<T> comparator) {
        setText(text);
        this.comparator = comparator;
        initialize();
    }

    public MFXTableColumn(StringExpression text, Comparator<T> comparator) {
        this.text.bind(text);
        this.comparator = comparator;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(STYLE_CLASS);
        setMinSize(180, 30);

        resizable.addListener(invalidated -> pseudoClassStateChanged(RESIZABLE_PSEUDO_CLASS, resizable.get()));
        pseudoClassStateChanged(RESIZABLE_PSEUDO_CLASS, resizable.get());
        dragged.addListener(invalidate -> pseudoClassStateChanged(DRAG_PSEUDO_CLASS, dragged.get()));
        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> dragged.set(true));
        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> dragged.set(false));

        sortIcon = new MFXIconWrapper(new MFXFontIcon("mfx-caret-up", 14), 18);
        sortIcon.setManaged(false);
        sortIcon.setVisible(false);
        NodeUtils.makeRegionCircular(sortIcon);

        needsLayoutProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && getWidth() > 0) {
                    setInitialWidth(getWidth());
                    needsLayoutProperty().removeListener(this);
                }
            }
        });

        tooltipSupplier.addListener((observable, oldValue, newValue) -> setTooltip(newValue.get()));
        defaultTooltipSupplier();
    }

    /**
     * Defines the default column's tooltip.
     * <p></p>
     * The default tooltip has its text property bound to the column's text property.
     */
    protected void defaultTooltipSupplier() {
        setTooltipSupplier(() -> {
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(textProperty());
            return tooltip;
        });
    }

    public double getInitialWidth() {
        return initialWidth.get();
    }

    /**
     * Specifies what was the initial width assigned to the control by JavaFX.
     * We keep this value to use it the the context menu of the column,
     * see {@link MFXTableViewSkin}
     */
    public ReadOnlyDoubleProperty initialWidthProperty() {
        return initialWidth.getReadOnlyProperty();
    }

    protected void setInitialWidth(double initialWidth) {
        this.initialWidth.set(initialWidth);
    }

    public String getText() {
        return text.get();
    }

    /**
     * Specifies the column text/name.
     */
    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public Pos getColumnAlignment() {
        return columnAlignment.get();
    }

    /**
     * Specifies the column's alignment.
     */
    public ObjectProperty<Pos> columnAlignmentProperty() {
        return columnAlignment;
    }

    public void setColumnAlignment(Pos columnAlignment) {
        this.columnAlignment.set(columnAlignment);
    }

    public Function<T, MFXTableRowCell> getRowCellFunction() {
        return rowCellFunction.get();
    }

    /**
     * Specifies the function responsible for building the table row cells.
     */
    public ObjectProperty<Function<T, MFXTableRowCell>> rowCellFunctionProperty() {
        return rowCellFunction;
    }

    public void setRowCellFunction(Function<T, MFXTableRowCell> rowCellFunction) {
        this.rowCellFunction.set(rowCellFunction);
    }

    public SortState getSortState() {
        return sortState.get();
    }

    /**
     * Specifies the sort state of the column: UNSORTED, ASCENDING, DESCENDING.
     */
    public ObjectProperty<SortState> sortStateProperty() {
        return sortState;
    }

    public void setSortState(SortState sortState) {
        this.sortState.set(sortState);
    }

    /**
     * @return the instance of the sort icon
     */
    public MFXIconWrapper getSortIcon() {
        return sortIcon;
    }

    public boolean isShowLockIcon() {
        return showLockIcon.get();
    }

    /**
     * Specifies if the lock icon should be visible.
     */
    public BooleanProperty showLockIconProperty() {
        return showLockIcon;
    }

    public void setShowLockIcon(boolean showLockIcon) {
        this.showLockIcon.set(showLockIcon);
    }

    /**
     * @return the user specifies comparator for this column
     */
    public Comparator<T> getComparator() {
        return comparator;
    }

    /**
     * Sets this column's comparator
     */
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public Supplier<Tooltip> getTooltipSupplier() {
        return tooltipSupplier.get();
    }

    /**
     * Specifies the supplier used to build the column's tooltip.
     */
    public ObjectProperty<Supplier<Tooltip>> tooltipSupplierProperty() {
        return tooltipSupplier;
    }

    public void setTooltipSupplier(Supplier<Tooltip> tooltipSupplier) {
        this.tooltipSupplier.set(tooltipSupplier);
    }

    public boolean isResizable() {
        return resizable.get();
    }

    /**
     * Specifies is this column should be resizable.
     */
    public BooleanProperty resizableProperty() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable.set(resizable);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableColumnSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
