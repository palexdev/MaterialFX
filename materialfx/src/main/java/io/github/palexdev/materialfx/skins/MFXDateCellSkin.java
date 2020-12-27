package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXDateCell;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.RippleClipType;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.scene.control.skin.DateCellSkin;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXDateCell}.
 * <p>
 * This is necessary to make the {@code RippleGenerator work properly}.
 */
public class MFXDateCellSkin extends DateCellSkin {
    //================================================================================
    // Properties
    //================================================================================

    private final RippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDateCellSkin(MFXDateCell dateCell) {
        super(dateCell);

        rippleGenerator = new RippleGenerator(dateCell, new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(15, 15));
        rippleGenerator.setOutDuration(Duration.millis(500));
        dateCell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });

        updateChildren();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippleGenerator != null) {
            getChildren().add(0, rippleGenerator);
        }
    }
}
