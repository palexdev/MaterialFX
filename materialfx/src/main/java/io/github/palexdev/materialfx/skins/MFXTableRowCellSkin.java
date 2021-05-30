package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

/**
 * Default skin implementation for {@link MFXTableRowCell}.
 * <p>
 * Simply an HBox which contains a label used to show the cell's text,
 * the leading and the trailing nodes specified by {@link MFXTableRowCell#leadingGraphicProperty()},
 * {@link MFXTableRowCell#trailingGraphicProperty()}
 */
public class MFXTableRowCellSkin extends SkinBase<MFXTableRowCell> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label label;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableRowCellSkin(MFXTableRowCell rowCell) {
        super(rowCell);

        label = new Label();
        label.setId("dataLabel");
        label.textProperty().bind(rowCell.textProperty());

        container = new HBox(label);
        container.spacingProperty().bind(rowCell.graphicTextGapProperty());
        container.alignmentProperty().bind(rowCell.rowAlignmentProperty());

        if (rowCell.getLeadingGraphic() != null) {
            container.getChildren().add(0, rowCell.getLeadingGraphic());
        }
        if (rowCell.getTrailingGraphic() != null) {
            container.getChildren().add(rowCell.getTrailingGraphic());
        }

        getChildren().setAll(container);

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXTableRowCell#leadingGraphicProperty()}: to allow changing the leading icon.
     * <p> - {@link MFXTableRowCell#trailingGraphicProperty()}: to allow changing the trailing icon.
     */
    private void setListeners() {
        MFXTableRowCell rowCell = getSkinnable();

        rowCell.leadingGraphicProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                container.getChildren().remove(oldValue);
            }
            if (newValue != null) {
                container.getChildren().add(0, newValue);
            }
        });

        rowCell.trailingGraphicProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                container.getChildren().remove(oldValue);
            }
            if (newValue != null) {
                container.getChildren().add(newValue);
            }
        });
    }
}
