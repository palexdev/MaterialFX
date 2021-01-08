package io.github.palexdev.materialfx.controls;

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
    private final String STYLE_CLASS = "mfx-date-cell";

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selectedDate");
    private static final PseudoClass CURRENT_DAY_PSEUDO_CLASS = PseudoClass.getPseudoClass("current");

    private final BooleanProperty selectedDate = new SimpleBooleanProperty(false);
    private final BooleanProperty current = new SimpleBooleanProperty(false);

    private final RippleGenerator rippleGenerator = new RippleGenerator(this);

    private boolean drawGraphic = false;

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

    private void initialize() {
        rippleGenerator.setRippleColor(Color.rgb(220, 220, 220, 0.6));
        getStyleClass().setAll(STYLE_CLASS);
        addListeners();
    }

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
