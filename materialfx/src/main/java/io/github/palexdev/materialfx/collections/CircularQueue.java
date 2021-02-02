package io.github.palexdev.materialfx.collections;

import java.util.LinkedList;

/**
 * This is the implementation of a circular FIFO queue.
 * When the maximum size is reached the oldest element is removed and replaced
 * by the new one.
 */
public class CircularQueue<E> extends LinkedList<E> {
    //================================================================================
    // Properties
    //================================================================================
    private int size;

    //================================================================================
    // Constructors
    //================================================================================
    public CircularQueue(int size) {
        super();
        this.size = size;
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sets the maximum size of the queue and removes exceeding elements
     * if the specified size is lesser than the number of elements.
     *
     * @param size The new desired size
     * @throws IllegalArgumentException if the desired size is 0
     */
    public void setSize(int size) {
        if (size == 0) {
            throw new IllegalArgumentException("Size cannot be 0!!");
        }

        if (size < super.size()) {
            for (int i = 0; i < (super.size() - size); i++) {
                super.remove();
            }
        }
        this.size = size;
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Adds the specified element to the queue and if it is full removes the oldest element
     * and then adds the new one.
     * <p></p>
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        if (super.size() == this.size) {
            super.remove();
        }
        return super.add(e);
    }
}
