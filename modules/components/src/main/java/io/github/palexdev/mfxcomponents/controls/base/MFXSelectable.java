package io.github.palexdev.mfxcomponents.controls.base;

import io.github.palexdev.mfxcomponents.behaviors.MFXSelectableBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import javafx.scene.Node;

/**
 * Base class for MaterialFX components that are selectables and buttons. Extends {@link MFXButtonBase} since
 * almost every kind of 'selectable' are buttons accompanied by text (checks, radios, switches,...).
 * An exception being {@link MFXIconButton} that only shows an icon.
 * <p></p>
 * Implements the selection API/Behavior through the {@link Selectable} interface.
 * Expects behaviors of type {@link MFXSelectableBehaviorBase}.
 * <p></p>
 * Implementations of this may need to control the way selection works, for this reason there are two methods that are basically
 * hooks for pre/post selection change, see {@link #changeSelection(boolean)} and {@link #onSelectionChanged(boolean)}.
 *
 * @see SelectionProperty
 * @see SelectionGroupProperty
 * @see SelectionGroup
 */
public abstract class MFXSelectable<B extends MFXSelectableBehaviorBase<?>> extends MFXButtonBase<B> implements Selectable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);
    private final SelectionProperty selected = new SelectionProperty(this) {
        @Override
        public void set(boolean newValue) {
            boolean state = changeSelection(newValue);
            super.set(state);
        }

        @Override
        protected void invalidated() {
            onSelectionChanged(get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSelectable() {
    }

    public MFXSelectable(String text) {
        super(text);
    }

    public MFXSelectable(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This is automatically called by the {@link #selectedProperty()} when selection is about to change.
     * <p>
     * The given parameter is the new requested selection state.
     * <p></p>
     * One can alter the return value according to its needs.
     * This for example, by default, will return {@code false} if the component has been disabled, following
     * the logic that a disabled node cannot be interacted with, neither its state changed.
     * <p></p>
     * BEWARE! Disabling these components when in a {@link SelectionGroup} may produce unexpected results,
     * especially when using the {@link SelectionGroup#atLeastOneSelectedProperty()} mode.
     */
    protected boolean changeSelection(boolean selected) {
        if (isDisabled()) return false;
        return selected;
    }

    /**
     * This is automatically called by {@link #selectedProperty()} after the selection has changed and has become invalid.
     * <p></p>
     * Allows to execute any action given the new selection state.
     */
    protected void onSelectionChanged(boolean selected) {
        PseudoClasses.SELECTED.setOn(this, selected);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    @Override
    public SelectionGroup getSelectionGroup() {
        return selectionGroup.get();
    }

    @Override
    public SelectionGroupProperty selectionGroupProperty() {
        return selectionGroup;
    }

    public void setSelectionGroup(SelectionGroup selectionGroup) {
        this.selectionGroup.set(selectionGroup);
    }

    public boolean isSelected() {
        return selected.get();
    }

    @Override
    public SelectionProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
