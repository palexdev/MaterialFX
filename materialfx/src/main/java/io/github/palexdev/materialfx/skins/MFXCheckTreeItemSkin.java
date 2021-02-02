package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.cell.MFXCheckTreeCell;
import javafx.scene.control.CheckBox;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXCheckTreeItemSkin}.
 *
 * @see MFXCheckTreeItem
 * @see io.github.palexdev.materialfx.controls.CheckModel
 */
public class MFXCheckTreeItemSkin<T> extends MFXTreeItemSkin<T> {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckTreeItemSkin(MFXCheckTreeItem<T> item) {
        super(item);

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a listener for handling CHECK_EVENTs and call {@link io.github.palexdev.materialfx.controls.CheckModel#check(MFXCheckTreeItem, CheckTreeItemEvent)}.
     */
    private void setListeners() {
        MFXCheckTreeItem<T> item = (MFXCheckTreeItem<T>) getSkinnable();

        item.addEventHandler(CheckTreeItemEvent.CHECK_EVENT, event -> item.getSelectionModel().check(item, event));
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Overridden method to create a MFXCheckTreeCell and fire a CHECK_EVENT
     * on checkbox action.
     */
    @Override
    protected AbstractMFXTreeCell<T> createCell() {
        MFXCheckTreeItem<T> item = (MFXCheckTreeItem<T>) getSkinnable();

        MFXCheckTreeCell<T> cell = (MFXCheckTreeCell<T>) super.createCell();
        CheckBox checkbox = cell.getCheckbox();
        checkbox.setOnAction(event -> {
            item.fireEvent(new CheckTreeItemEvent<>(CheckTreeItemEvent.CHECK_EVENT, item));
            event.consume();
        });
        return cell;
    }
}
