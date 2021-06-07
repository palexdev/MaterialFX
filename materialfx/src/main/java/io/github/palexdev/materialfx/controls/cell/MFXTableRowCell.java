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

import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.skins.MFXTableRowCellSkin;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * This is the implementation of the row cells used by {@link MFXTableView} to fill a {@link MFXTableRow}.
 * <p>
 * The cell is built by the corresponding column that defines the function, {@link MFXTableColumn#rowCellFunctionProperty()}
 * <p>
 * Extends {@code Control} so that anyone can implement their own skin if needed.
 * <p>
 * The default skin, {@link MFXTableRowCellSkin}, also allows to place up to two nodes in the cell. These nodes are specified by
 * the following properties, {@link #leadingGraphicProperty()}, {@link #trailingGraphicProperty()}.
 * <p>
 * A little side note, also to respond to some Github issues. It is not recommended to use big nodes. It is not recommended to
 * use too many nodes, that's why it's limited to two. If you need a lot of controls then consider having specific columns which build cells only with graphic
 * like here <a href="https://bit.ly/2SzjrVu">Example</a>.
 * <p>
 * Since it now extends {@code Control} you can easily define your own skin and do whatever you like with the
 * control, just keep in mind that tables are designed to mostly show text.
 * <p>
 * Has two constructors, one with a String and one with a {@link StringExpression}. The first one simply sets the cell's text to the given string,
 * the other one binds the cell's text property to the given string expression.
 * <p>
 * That allows to use {@link MFXTableView} with models which don't use JavaFX's properties. Of course the data won't change automatically in that case,
 * so the table must be updated manually after the data has changed, {@link MFXTableView#updateTable()}.
 */
public class MFXTableRowCell extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "custom-row-cell";

    private final StringProperty text = new SimpleStringProperty();
    private final ObjectProperty<Node> leadingGraphic = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> trailingGraphic = new SimpleObjectProperty<>();
    private final DoubleProperty graphicTextGap = new SimpleDoubleProperty(10);
    private final ObjectProperty<Pos> rowAlignment = new SimpleObjectProperty<>(Pos.CENTER_LEFT);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableRowCell(String text) {
        setText(text);
        initialize();
    }

    public MFXTableRowCell(StringExpression text) {
        this.text.bind(text);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(STYLE_CLASS);
    }

    /**
     * Computes the minimum needed width so that the text is not truncated.
     * By default it calls {@link #computePrefWidth(double)}
     */
    public double computeWidth() {
        return computePrefWidth(-1);
    }

    /**
     * By default checks if the current width is less than {@link #computeWidth()}.
     */
    public boolean isTruncated() {
        return getWidth() < computeWidth();
    }

    public String getText() {
        return text.get();
    }

    /**
     * Specifies the cell's text. Can also be empty to show only the graphic.
     */
    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public Node getLeadingGraphic() {
        return leadingGraphic.get();
    }

    /**
     * Specifies leading graphic of the cell.
     */
    public ObjectProperty<Node> leadingGraphicProperty() {
        return leadingGraphic;
    }

    public void setLeadingGraphic(Node leadingGraphic) {
        this.leadingGraphic.set(leadingGraphic);
    }

    public Node getTrailingGraphic() {
        return trailingGraphic.get();
    }

    /**
     * Specifies the trailing graphic of the cell.
     */
    public ObjectProperty<Node> trailingGraphicProperty() {
        return trailingGraphic;
    }

    public void setTrailingGraphic(Node trailingGraphic) {
        this.trailingGraphic.set(trailingGraphic);
    }

    public double getGraphicTextGap() {
        return graphicTextGap.get();
    }

    /**
     * Specifies the gap between the graphic nodes and the text.
     */
    public DoubleProperty graphicTextGapProperty() {
        return graphicTextGap;
    }

    public void setGraphicTextGap(double graphicTextGap) {
        this.graphicTextGap.set(graphicTextGap);
    }

    public Pos getRowAlignment() {
        return rowAlignment.get();
    }

    /**
     * Specifies the cell alignment.
     */
    public ObjectProperty<Pos> rowAlignmentProperty() {
        return rowAlignment;
    }

    public void setRowAlignment(Pos rowAlignment) {
        this.rowAlignment.set(rowAlignment);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableRowCellSkin(this);
    }
}
