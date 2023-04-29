package io.github.palexdev.mfxcore.selection;

/**
 * Public API for components that are selectable (checks, radios, toggles and such).
 * <p></p>
 * Ideally such controls should have two things:
 * <p> 1) A property for the selection state obviously
 * <p> 2) A way to group such controls, see {@link SelectionGroup}
 * <p></p>
 * Note how the API enforces the use of {@link SelectionProperty} and {@link SelectionGroupProperty}.
 * <p>
 * The reason for this decision is that: compared to the pitiful JavaFX API, {@code SelectionGroups} allow multiple selection,
 * the possibility of always having at least one selection, as well as allowing the grouping of different components
 * as long as they implement this. Those custom properties handle some hassles (like what happens when switching groups,
 * or delegating the selection state to the group if the component is assigned to one) automatically so that neither developers,
 * neither users need to worry about that. It's worth mentioning though that this is experimental, meaning that
 * the API may not take into account all the user cases, if such is the case, in the future the API may change and use
 * standard properties.
 * <p></p>
 * Last but not least: I mentioned above usage solely with 'components' but this API can actually be used with anything.
 */
public interface Selectable {

    boolean isSelected();

    /**
     * Specifies the selection state.
     *
     * @see SelectionProperty
     */
    SelectionProperty selectedProperty();

    void setSelected(boolean selected);

    SelectionGroup getSelectionGroup();

    /**
     * Specifies the {@link SelectionGroup} at which this {@code Selectable} is assigned.
     */
    SelectionGroupProperty selectionGroupProperty();

    void setSelectionGroup(SelectionGroup group);
}