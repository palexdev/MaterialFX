package io.github.palexdev.mfxcore.collections;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A crude implementation of an observable queue with limited capacity, the backing data structure is {@link CircularQueue}.
 *
 * @param <E> Any type
 */
public class ObservableCircularQueue<E> extends SimpleListProperty<E> {
    //================================================================================
    // Properties
    //================================================================================
    private final CircularQueue<E> queue;

    //================================================================================
    // Constructors
    //================================================================================
    public ObservableCircularQueue(int capacity) {
        this.queue = new CircularQueue<>(capacity);
        super.set(FXCollections.observableList(queue));
    }

    //================================================================================
    // Methods
    //================================================================================
    @Override
    public boolean add(E element) {
        if (size() == queue.getCapacity()) {
            queue.remove();
        }
        return super.add(element);
    }

    @Override
    public void add(int i, E element) {
        if (size() == queue.getCapacity()) {
            queue.remove();
        }
        int clamped = Math.min(i, getCapacity() - 1);
        super.add(clamped, element);
    }

    @Override
    public boolean addAll(E... elements) {
        boolean res = false;
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        boolean res = false;
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> elements) {
        List<E> toList = new ArrayList<>(elements);
        for (int j = 0; j < toList.size(); j++) {
            E e = toList.get(j);
            add(i + j, e);
        }
        return true;
    }

    @Override
    public boolean setAll(E... elements) {
        boolean res = false;
        clear();
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean setAll(Collection<? extends E> elements) {
        boolean res = false;
        clear();
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /**
     * Delegate of {@link CircularQueue#getCapacity()}.
     */
    public int getCapacity() {
        return queue.getCapacity();
    }

    /**
     * Delegate of {@link CircularQueue#setCapacity(int)}.
     */
    public void setCapacity(int capacity) {
        queue.setCapacity(capacity);
    }
}
