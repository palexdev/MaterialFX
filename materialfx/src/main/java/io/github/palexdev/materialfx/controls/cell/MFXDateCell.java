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

import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.skins.MFXDateCellSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Custom implementation of a {@code DateCell} for easily distinguish selected dates and
 * current dates. Includes ripple effects.
 */
public class MFXDateCell extends DateCell {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-date-cell";

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selectedDate");
    private static final PseudoClass CURRENT_DAY_PSEUDO_CLASS = PseudoClass.getPseudoClass("current");

    private final BooleanProperty selectedDate = new SimpleBooleanProperty(false);
    private final BooleanProperty current = new SimpleBooleanProperty(false);

    private final RippleGenerator rippleGenerator = new RippleGenerator(this);

    private boolean drawGraphic = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDateCell() {
        initialize();
    }

    public MFXDateCell(String text) {
        setText(text);
        initialize();
    }

    public MFXDateCell(String text, boolean drawGraphic) {
        setText(text);
        this.drawGraphic = drawGraphic;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        rippleGenerator.setRippleColor(Color.rgb(220, 220, 220, 0.6));
        getStyleClass().setAll(STYLE_CLASS);
        addListeners();
    }

    /**
     * Adds listeners to selected and current date properties.
     * <p>
     * Adds event handler for ripple generator.
     */
    private void addListeners() {
        selectedDate.addListener(invalidate -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selectedDate.get()));
        current.addListener(invalidate -> pseudoClassStateChanged(CURRENT_DAY_PSEUDO_CLASS, current.get()));

        selectedDate.addListener((observable, oldValue, newValue) -> {
            if (getGraphic() != null) {
                getGraphic().setVisible(!newValue || !current.get());
            }
        });

        current.addListener((observable, oldValue, newValue) -> {
            if (newValue && !selectedDate.get() && drawGraphic) {
                Circle circle = new Circle(getPrefWidth() / 3.5);
                circle.setFill(Color.TRANSPARENT);
                circle.getStyleClass().add("cell-stroke");

                setContentDisplay(ContentDisplay.CENTER);
                setGraphic(circle);
            }
        });

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
    }

    public boolean isSelectedDate() {
        return selectedDate.get();
    }

    public BooleanProperty selectedDateProperty() {
        return selectedDate;
    }

    public void setSelectedDate(boolean selectedDate) {
        this.selectedDate.set(selectedDate);
    }

    public boolean isCurrent() {
        return current.get();
    }

    public BooleanProperty currentProperty() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current.set(current);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXDateCellSkin(this);
    }

}
