package io.github.palexdev.mfxcore.selection;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Extension of {@link SimpleObjectProperty} meant to be used by classes implementing {@link Selectable}.
 * <p></p>
 * This property overrides the {@link #set(SelectionGroup)} method to correctly handle the assignment/removal/switch of a
 * {@link Selectable} from a {@link SelectionGroup}, see {@link #set(SelectionGroup)} for more info.
 * <p>
 * Note that for this purpose this property needs the reference of the {@link Selectable} in which it will operate, in order
 * to add/remove the {@code Selectable} to/from the {@link SelectionGroup}.
 */
public class SelectionGroupProperty extends SimpleObjectProperty<SelectionGroup> {
    //================================================================================
    // Properties
    //================================================================================
    private final Selectable selectable;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionGroupProperty(Selectable selectable) {
        this.selectable = selectable;
    }

    public SelectionGroupProperty(SelectionGroup initialValue, Selectable selectable) {
        super(initialValue);
        this.selectable = selectable;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to correctly handle the addition/removal of a {@link Selectable} to/from a {@link SelectionGroup}.
     * <p>
     * If the {@code Selectable} was already in a group, it first needs to be removed from it by calling
     * {@link SelectionGroup#remove(Selectable)}.
     * <p>
     * Then it's added to the new group with {@link SelectionGroup#add(Selectable)} and finally the {@code super.set(...)}
     * method is invoked.
     */
    @Override
    public void set(SelectionGroup newValue) {
        SelectionGroup oldValue = get();
        if (oldValue != null) oldValue.remove(selectable);
        if (newValue != null) {
            newValue.add(selectable);
            super.set(newValue);
            return;
        }
        super.set(null);
    }
}
