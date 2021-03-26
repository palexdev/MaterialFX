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
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.selection.base.IListCheckModel;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class MFXFlowlessCheckListCell<T> extends AbstractMFXFlowlessListCell<T> {
    private final String STYLE_CLASS = "mfx-check-list-cell";
    private final String STYLESHHET = MFXResourcesLoader.load("css/mfx-flowless-check-listcell.css");
    protected final RippleGenerator rippleGenerator = new RippleGenerator(this);

    protected final MFXCheckbox checkbox;

    private final BooleanProperty checked = new SimpleBooleanProperty();
    private static final PseudoClass CHECKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("checked");

    private boolean clearSelectionOnCheck = false;

    public MFXFlowlessCheckListCell(MFXFlowlessCheckListView<T> listView, T data) {
        this(listView, data, 32);
    }

    public MFXFlowlessCheckListCell(MFXFlowlessCheckListView<T> listView, T data, double fixedHeight) {
        super(listView, data, fixedHeight);
        
        checkbox = new MFXCheckbox("");
        initialize();
    }
    
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        checked.addListener(invalidated -> pseudoClassStateChanged(CHECKED_PSEUDO_CLASS, checked.get()));
        checked.bind(checkbox.selectedProperty());

        checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (clearSelectionOnCheck) {
                listView.getSelectionModel().clearSelection();
            }
            updateCheckModel(newValue);
        });
        ((IListCheckModel<T>) listView.getSelectionModel()).checkedItemsProperty().addListener((InvalidationListener) invalidated -> {
            if (!containsEqualsBothCheck() && checkbox.isSelected()) {
                checkbox.setSelected(false);
            }
        });

        setupRippleGenerator();
    }

    protected void setupRippleGenerator() {
        rippleGenerator.setManaged(false);
        rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
        rippleGenerator.setInDuration(Duration.millis(400));

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
    }

    public boolean isClearSelectionOnCheck() {
        return clearSelectionOnCheck;
    }

    public void setClearSelectionOnCheck(boolean clearSelectionOnCheck) {
        this.clearSelectionOnCheck = clearSelectionOnCheck;
    }

    public void updateCheckModel(boolean checked) {
        IListCheckModel<T> checkModel = (IListCheckModel<T>) listView.getSelectionModel();
        if (checked) {
            checkModel.check(getIndex(), getData());
        } else {
            checkModel.clearCheckedItem(getIndex());
        }
    }

    protected boolean containsEqualsBothCheck() {
        IListCheckModel<T> checkModel = (IListCheckModel<T>) listView.getSelectionModel();
        return checkModel.checkedItemsProperty().containsKey(getIndex()) &&
                checkModel.checkedItemsProperty().get(getIndex()).equals(getData());
    }

    protected boolean containsNotEqualsIndexCheck() {
        IListCheckModel<T> checkModel = (IListCheckModel<T>) listView.getSelectionModel();
        return checkModel.checkedItemsProperty().containsValue(getData()) &&
                checkModel.checkedItemsProperty().entrySet()
                        .stream()
                        .anyMatch(entry -> entry.getKey() != getIndex() && entry.getValue().equals(getData()));
    }

    protected boolean containsNotEqualsDataCheck() {
        IListCheckModel<T> checkModel = (IListCheckModel<T>) listView.getSelectionModel();
        return checkModel.checkedItemsProperty().containsKey(getIndex()) &&
                !checkModel.checkedItemsProperty().get(getIndex()).equals(getData());
    }

    @Override
    protected void render(T data) {
        if (data instanceof Node) {
            getChildren().setAll(checkbox, (Node) data);
        } else {
            Label label = new Label(data.toString());
            label.getStyleClass().add("data-label");
            getChildren().setAll(checkbox, label);
        }
        getChildren().add(0, rippleGenerator);
    }

    @Override
    public void updateIndex(int index) {
        super.updateIndex(index);

        IListCheckModel<T> checkModel = (IListCheckModel<T>) listView.getSelectionModel();
        if (containsEqualsBothCheck() && !checkbox.isSelected()) {
            checkbox.setSelected(true);
            return;
        }
        if (containsNotEqualsIndexCheck()) {
            checkModel.updateIndex(getData(), index);
            checkbox.setSelected(true);
            return;
        }
        if (containsNotEqualsDataCheck()) {
            checkModel.clearCheckedItem(index);
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHHET;
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
