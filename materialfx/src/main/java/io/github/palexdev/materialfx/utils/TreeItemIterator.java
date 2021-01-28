package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;

import java.util.Iterator;
import java.util.Stack;

public class TreeItemIterator<T> implements Iterator<AbstractMFXTreeItem<T>> {
    private final Stack<AbstractMFXTreeItem<T>> stack = new Stack<>();

    public TreeItemIterator(AbstractMFXTreeItem<T> item) {
        stack.push(item);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public AbstractMFXTreeItem<T> next() {
        AbstractMFXTreeItem<T> nextItem = stack.pop();
        nextItem.getItems().forEach(stack::push);

        return nextItem;
    }
}