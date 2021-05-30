package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * This is the HBox that contains the table row cells built by each column.
 * <p></p>
 * This little class is needed to select rows.
 */
public class MFXTableRow<T> extends HBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-table-row";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-tablerow.css");

    protected static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    private final T data;
    private final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableRow(T data) {
        this.data = data;
        initialize();
    }

    public MFXTableRow(T data, double spacing) {
        super(spacing);
        this.data = data;
        initialize();
    }

    public MFXTableRow(T data, Node... children) {
        super(children);
        this.data = data;
        initialize();
    }

    public MFXTableRow(T data, double spacing, Node... children) {
        super(spacing, children);
        this.data = data;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(STYLE_CLASS);
        selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected()));
        setupRippleGenerator();
    }

    protected void setupRippleGenerator() {
        getChildren().add(0, rippleGenerator);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.RECTANGLE).setOffsetW(10).build(this));
        rippleGenerator.setComputeRadiusMultiplier(true);
        rippleGenerator.setManaged(false);
        rippleGenerator.setRipplePositionFunction(event -> new RipplePosition(event.getX(), event.getY()));
        rippleGenerator.setTranslateX(-5);
        rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
        addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
    }

    /**
     * @return the data represented by this row (by its cells to be more precise)
     */
    public T getData() {
        return data;
    }

    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Specifies the selection state of the row.
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
