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
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXFlowlessCheckListView;
import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListCell;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.selection.ListCheckModel;
import io.github.palexdev.materialfx.selection.base.IListCheckModel;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Implementation of an {@link AbstractMFXFlowlessListCell} which has a combo box
 * for usage in {@link MFXFlowlessCheckListView}, has the checked property and PseudoClass
 * ":checked" for usage in CSS.
 */
public class MFXFlowlessCheckListCell<T> extends AbstractMFXFlowlessListCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-check-list-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFlowlessCheckListCell.css");
    protected final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

    private final MFXFlowlessCheckListView<T> listView;
    protected final MFXCheckbox checkbox;

    private final ReadOnlyBooleanWrapper checked = new ReadOnlyBooleanWrapper();
    protected static final PseudoClass CHECKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("checked");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFlowlessCheckListCell(MFXFlowlessCheckListView<T> listView, T data) {
        this(listView, data, 32);
    }

    public MFXFlowlessCheckListCell(MFXFlowlessCheckListView<T> listView, T data, double fixedHeight) {
        super(data, fixedHeight);
        this.listView = listView;
        checkbox = new MFXCheckbox("");
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void initialize() {
        super.initialize();
        getStyleClass().add(STYLE_CLASS);
        setupRippleGenerator();
        render(getData());
    }

    /**
     * Sets up the properties of the ripple generator and adds the mouse pressed filter.
     */
    protected void setupRippleGenerator() {
        rippleGenerator.setManaged(false);
        rippleGenerator.setRipplePositionFunction(event -> new RipplePosition(event.getX(), event.getY()));
        rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
        addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
    }

    /**
     * Inherited behavior:
     * <p>
     * {@inheritDoc}
     * <p></p>
     *
     * <p>
     * Sets the following additional behaviors:
     * <p>
     * - Binds the checked property to the selected property of the combo box.<p>
     * - Adds a listener to {@link IListCheckModel#allowsSelectionProperty()} to properly handle selection.
     */
    @Override
    protected void setBehavior() {
        super.setBehavior();

        getSelectionModel().allowsSelectionProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                getSelectionModel().clearSelection();
            }
        });

        checked.bind(checkbox.selectedProperty());
        checked.addListener(invalidated -> {
            pseudoClassStateChanged(CHECKED_PSEUDO_CLASS, checked.get());
            updateCheck();
        });
    }

    /**
     * Updates the check model accordingly to the new state of the checked property.
     * <p></p>
     * If true and the check model doesn't already contain the cell index then calls
     * {@link ListCheckModel#check(int, Object)} with the cell's index and data.
     * <p></p>
     * If false calls {@link ListCheckModel#clearCheckedItem(int)} with the cell's index.
     */
    protected void updateCheck() {
        IListCheckModel<T> checkModel = getSelectionModel();

        boolean checked = isChecked();
        int index = getIndex();
        if (!checked && checkModel.containsChecked(index)) {
            checkModel.clearCheckedItem(index);
        } else if (checked && !checkModel.containsChecked(index)) {
            checkModel.check(index, getData());
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p></p>
     * Overridden to manage the selection properly according to {@link IListCheckModel#allowsSelectionProperty()}
     */
    @Override
    protected void updateSelection(MouseEvent mouseEvent) {
        if (!getSelectionModel().allowsSelection()) {
            return;
        }

        if (NodeUtils.inHierarchy(mouseEvent.getPickResult().getIntersectedNode(), checkbox)) {
            getSelectionModel().clearSelection();
            return;
        }

        IListSelectionModel<T> selectionModel = getSelectionModel();
        setSelected(!isSelected());

        boolean selected = isSelected();
        int index = getIndex();
        if (!selected && selectionModel.containSelected(index)) {
            selectionModel.clearSelectedItem(index);
        } else if (selected && !selectionModel.containSelected(index)) {
            selectionModel.select(index, getData(), mouseEvent);
        }
    }

    /**
     * Inherited doc:
     * <p>
     * {@inheritDoc}
     *
     * <p>
     * Additional behavior:
     * <p>
     * After the index property is updated by {@link #updateIndex(int)} this method
     * is called to set the checked property state accordingly to the check model state.
     * <p></p>
     * If the cell is not checked but the check model contains the cell's index then
     * sets the checked property to true.
     * <p></p>
     * If the cell is checked but the check model doesn't contain the cell's index then
     * sets the checked property to false.
     */
    @Override
    protected void afterUpdateIndex() {
        super.afterUpdateIndex();

        IListCheckModel<T> checkModel = getSelectionModel();

        boolean checked = isChecked();
        int index = getIndex();
        if (!checked && checkModel.containsChecked(index)) {
            checkbox.setSelected(true);
        } else if (checked && !checkModel.containsChecked(index)) {
            checkbox.setSelected(false);
        }
    }

    public boolean isChecked() {
        return checked.get();
    }

    /**
     * Specifies the check state of the cell.
     */
    public ReadOnlyBooleanProperty checkedProperty() {
        return checked.getReadOnlyProperty();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public HBox getNode() {
        return this;
    }

    /**
     * Inherited doc:
     * <p>
     * {@inheritDoc}
     * <p></p>
     * <p>
     * If the given data is instance of Node then the data is cast
     * to Node and added to the children list with the checkbox as well.
     * Otherwise a Label is created and toString is called on the data.
     * The {@link #emptyProperty()} is updated accordingly to the generated string.
     * The label has style class: "data-label".
     */
    @Override
    protected void render(T data) {
        if (data instanceof Node) {
            getChildren().setAll(checkbox, (Node) data);
        } else {
            setEmpty(data.toString().isEmpty());
            Label label = new Label(data.toString());
            label.getStyleClass().add("data-label");
            getChildren().setAll(checkbox, label);
        }
        getChildren().add(0, rippleGenerator);
    }

    @Override
    protected IListCheckModel<T> getSelectionModel() {
        return listView.getSelectionModel();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public String toString() {
        String className = getClass().getName();
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(simpleName);
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append("]");
        sb.append("[Data:").append(getData()).append("]");
        if (getId() != null) {
            sb.append("[id:").append(getId()).append("]");
        }

        return sb.toString();
    }
}
