package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.controls.base.ICheckModel;
import io.github.palexdev.materialfx.controls.cell.MFXCheckTreeCell;
import io.github.palexdev.materialfx.skins.MFXCheckTreeItemSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.lang.ref.WeakReference;

public class MFXCheckTreeItem<T> extends MFXTreeItem<T> {
    private final String STYLE_CLASS = "mfx-check-tree-item";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-treeitem.css").toString();

    private final BooleanProperty checked = new SimpleBooleanProperty(false);
    private final BooleanProperty indeterminate = new SimpleBooleanProperty(false);

    public MFXCheckTreeItem(T data) {
        super(data);
        initialize();
    }

    public MFXCheckTreeItem(T data, Callback<AbstractMFXTreeItem<T>, AbstractMFXTreeCell<T>> cellFactory) {
        super(data, cellFactory);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        treeViewProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && isRoot()) {
                CheckModel<T> checkModel = (CheckModel<T>) getSelectionModel();
                checkModel.scanTree((MFXCheckTreeItem<T>) getRoot());
            }
        });
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public boolean isIndeterminate() {
        return indeterminate.get();
    }

    public BooleanProperty indeterminateProperty() {
        return indeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate.set(indeterminate);
    }

    @Override
    public ICheckModel<T> getSelectionModel() {
        return (ICheckModel<T>) super.getSelectionModel();
    }

    @Override
    protected void defaultCellFactory() {
        super.cellFactory.set(cell -> new MFXCheckTreeCell<>(this));
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXCheckTreeItemSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    public static final class CheckTreeItemEvent<T> extends Event {
        private final WeakReference<AbstractMFXTreeItem<T>> itemRef;

        public static final EventType<CheckTreeItemEvent<?>> CHECK_EVENT = new EventType<>(ANY, "CHECK_EVENT");

        public CheckTreeItemEvent(EventType<? extends Event> eventType, AbstractMFXTreeItem<T> item) {
            super(eventType);
            this.itemRef = new WeakReference<>(item);
        }

        public AbstractMFXTreeItem<T> getItemRef() {
            return itemRef.get();
        }
    }
}
