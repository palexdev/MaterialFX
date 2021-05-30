package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.utils.DragResizer;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTableColumn}.
 * <p></p>
 * Simply an HBox with a label and an icon for sorting positioned manually based on the column's alignment.
 * It also has support for resizing the column on drag.
 */
public class MFXTableColumnSkin<T> extends SkinBase<MFXTableColumn<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label label;

    private final DragResizer dragResizer;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableColumnSkin(MFXTableColumn<T> column) {
        super(column);

        label = new Label();
        label.textProperty().bind(column.textProperty());

        container = new HBox(label, column.getSortIcon());
        container.setMinWidth(Region.USE_PREF_SIZE);
        container.setPadding(new Insets(0,10, 0, 0));
        container.alignmentProperty().bind(column.columnAlignmentProperty());
        container.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getMinX() <= 0) {
                container.relocate(1, 1);
            }
        });

        dragResizer = new DragResizer(column, DragResizer.RIGHT);

        if (column.isResizable()) {
            dragResizer.makeResizable();
        }

        setListeners();

        getChildren().setAll(container);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXTableColumn#resizableProperty()} ()}: to enable/disable this column {@link DragResizer} by calling
     * {@link DragResizer#makeResizable()} or {@link DragResizer#uninstall()} depending on its value.
     */
    private void setListeners() {
        MFXTableColumn<T> tableColumn = getSkinnable();

        tableColumn.resizableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                dragResizer.uninstall();
            } else {
                dragResizer.makeResizable();
            }
        });
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double computed;
        MFXTableColumn<T> column = getSkinnable();
        if (NodeUtils.isRightAlignment(column.getColumnAlignment())) {
            computed = leftInset + label.getWidth() + column.getSortIcon().getSize() + rightInset + 20;
        } else {
            computed = leftInset + label.getWidth() + getSkinnable().getSortIcon().getSize() + rightInset + 10;
        }
        return computed;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        MFXTableColumn<T> column = getSkinnable();
        MFXIconWrapper sortIcon = column.getSortIcon();
        Pos alignment = column.getColumnAlignment();

        double sortSize = sortIcon.getSize();
        double sX;
        double sY = snapPositionY((h / 2) - (sortSize / 2));

        if (!NodeUtils.isRightAlignment(alignment)) {
            sX = snapPositionX(w - sortSize - 5);
        } else {
            sX = 5;
        }

        sortIcon.resizeRelocate(sX, sY, sortSize, sortSize);
    }
}
