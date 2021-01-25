package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.TreeView;
import io.github.palexdev.materialfx.utils.TreeItemStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.util.Callback;

import java.util.List;

public abstract class AbstractTreeItem<T> extends Control {
    protected final T data;
    protected final ObservableList<AbstractTreeItem<T>> items = FXCollections.observableArrayList();
    protected AbstractTreeItem<T> parent;

    private final BooleanProperty startExpanded =  new SimpleBooleanProperty(false);

    protected final ObjectProperty<Callback<AbstractTreeItem<T>, AbstractTreeCell<T>>> cellFactory = new SimpleObjectProperty<>();
    private TreeView<T> treeView;

    public AbstractTreeItem(T data) {
        this.data = data;
    }

    protected abstract void defaultCellFactory();
    protected abstract void updateChildrenParent(List<? extends AbstractTreeItem<T>> treeItems, final AbstractTreeItem<T> newParent);

    public boolean isRoot() {
        return this.parent == null;
    }

    public AbstractTreeItem<T> getRoot() {
        if (isRoot()) {
            return this;
        }

        AbstractTreeItem<T> par = this;
        while (true) {
            par = par.getItemParent();
            if (par.isRoot()) {
                return par;
            }
        }
    }

    public long getIndex() {
        if (isRoot()){
            return 0;
        }

        return TreeItemStream.flattenTree(getRoot())
                .takeWhile(item -> !item.equals(this))
                .count();
    }

    public long getItemsCount(AbstractTreeItem<T> item) {
        return TreeItemStream.stream(item).count();
    }

    public int getLevel() {
        if (isRoot()) {
            return 0;
        }

        int index = 0;
        AbstractTreeItem<T> par = this;
        while (true) {
            par = par.getItemParent();
            index++;
            if (par.isRoot()) {
                return index;
            }
        }
    }

    public TreeView<T> getTreeView() {
        if (isRoot()) {
            if (treeView != null) return treeView;
            throw new NullPointerException("TreeView is not set. Before calling this method set this item as the root of a TreeView");
        } else {
            return getRoot().getTreeView();
        }
    }

    // TODO warning
    public void setTreeView(TreeView<T> treeView) {
        this.treeView = treeView;
    }

    public T getData() {
        return data;
    }

    public ObservableList<AbstractTreeItem<T>> getItems() {
        return items;
    }

    public AbstractTreeItem<T> getItemParent() {
        return this.parent;
    }

    protected void setItemParent(AbstractTreeItem<T> parent) {
        this.parent = parent;
    }

    public boolean isStartExpanded() {
        return startExpanded.get();
    }

    public BooleanProperty startExpandedProperty() {
        return startExpanded;
    }

    public void setStartExpanded(boolean startExpanded) {
        this.startExpanded.set(startExpanded);
    }

    public Callback<AbstractTreeItem<T>, AbstractTreeCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    public ObjectProperty<Callback<AbstractTreeItem<T>, AbstractTreeCell<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Callback<AbstractTreeItem<T>, AbstractTreeCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }
}
