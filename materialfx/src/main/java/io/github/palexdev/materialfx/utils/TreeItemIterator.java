package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;

import java.util.Iterator;
import java.util.Stack;

public class TreeItemIterator<T> implements Iterator<AbstractTreeItem<T>> {
    private final Stack<AbstractTreeItem<T>> stack = new Stack<>();

    public TreeItemIterator(AbstractTreeItem<T> item) {
        stack.push(item);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public AbstractTreeItem<T> next() {
        AbstractTreeItem<T> nextItem = stack.pop();
        nextItem.getItems().forEach(stack::push);

        return nextItem;
    }
}