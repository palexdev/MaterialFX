package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.cell.MFXCheckTreeCell;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

public class MFXCheckTreeItemSkin<T> extends MFXTreeItemSkin<T> {

    public MFXCheckTreeItemSkin(MFXCheckTreeItem<T> item) {
        super(item);

        item.addEventHandler(CheckTreeItemEvent.CHECK_EVENT, event -> item.getSelectionModel().check(item, event));
    }

    @Override
    protected AbstractMFXTreeCell<T> createCell() {
        MFXCheckTreeItem<T> item = (MFXCheckTreeItem<T>) getSkinnable();

        MFXCheckTreeCell<T> cell = (MFXCheckTreeCell<T>) super.createCell();
        CheckBox checkbox = cell.getCheckbox();
        checkbox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            item.fireEvent(new CheckTreeItemEvent<>(CheckTreeItemEvent.CHECK_EVENT, item));
            event.consume();
        });
        return cell;
    }
}
