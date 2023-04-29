package io.github.palexdev.mfxcore.selection;

import io.github.palexdev.mfxcore.enums.SelectionMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.*;

/**
 * Custom implementation and expansion of that pitiful thing that is {@link javafx.scene.control.ToggleGroup}.
 * <p>
 * A {@code SelectionGroup} will work with anything that implements the necessary API described by the {@link Selectable}
 * interface. Not only that, it is also a lot more flexible and convenient.
 * <p>
 * You can set the selection to be single or multiple by just setting the {@link #selectionModeProperty()}, as well as
 * tell the group to always keep at least one {@link Selectable} active, by setting the {@link #atLeastOneSelectedProperty()}
 * to true. All of these can be changed anytime, although you better know the side effects of some particular cases, will
 * be listed below.
 * <p>
 * So you now have a grouping API for everything, not only controls, as long as they implement {@link Selectable}, and you
 * also have capabilities such as 'atMost/atLeast one selected', in single and multiple configurations, in just one class!
 * <p></p>
 * All of this sounds good right? Well, there are some caveats of course.
 * <p>
 * First of all, keep in mind that if you want to use this you will be forced to use the two custom properties:
 * {@link SelectionProperty} and {@link SelectionGroupProperty}, the reason for this is to make the implementation/integration
 * for users less of a pain and less error-prone, more info can be found in the relative's docs.
 * <p>
 * Since this also supports multiple selection, for obvious reasons, the selection is a collection of {@code Selectables}.
 * To be precise, both the collections used to keep the {@code Selectables} that are managed by the group, ant the ones
 * that are currently selected, are {@link ObservableSet} backed by a {@link LinkedHashSet}. As you may have guessed, Sets
 * are not the best for element retrieval unlike Lists (at least until sequenced collections become a thing), for this
 * reason there is a convenience method, {@link #getFirstSelected()}, that allows to get the first selected {@code Selectable}
 * in the Set. The usage of such collections vastly helps to avoid duplicates while also having fast lookups (contains).
 * <p></p>
 * <b>Special cases when changing config</b>
 * <p> 1) When switching from MULTIPLE to SINGLE selection mode, the selection will be the same only and only is there was
 * only {@code Selectable} in the selection Set, in all other cases the selection is cleared!
 * <p> 2) When activating the 'atLeastOneSelected' mode, if there are {@code Selectables} in the group the first will
 * be immediately selected! If none is available, the first added to the group will be!
 * <p> 3) If 'atLeastOneSelected' mode is active and multiple {@code Selectables} are added at the same time to the group,
 * and two or more of them are selected, the last will prevail, the others will be deselected (if in SINGLE selection mode)
 * <p></p>
 * Last but not least, to avoid some if statements, this makes use of polymorphism delegating the selection handling to
 * two internal classes, one for SINGLE selection mode, the other for the MULTIPLE mode.
 */
public class SelectionGroup {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<SelectionMode> selectionMode = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            SelectionMode mode = get();
            if (mode == SelectionMode.SINGLE) {
                handler = new SingleSelectionHandler();
                if (selection.size() > 1) selection.clear();
                return;
            }
            handler = new MultipleSelectionHandler();
        }
    };
    private final BooleanProperty atLeastOneSelected = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            boolean val = get();
            if (val && selection.isEmpty() && !selectables.isEmpty()) {
                getFirstSelectable().ifPresent(s -> s.setSelected(true));
            }
        }
    };
    private final ObservableSet<Selectable> selectables = FXCollections.observableSet(new LinkedHashSet<>());
    private final ObservableSet<Selectable> selection = FXCollections.observableSet(new LinkedHashSet<>());
    private SelectionHandler handler;

    private boolean isSwitching = false;
    private boolean isRemoval = false;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionGroup() {
        this(SelectionMode.SINGLE);
    }

    public SelectionGroup(SelectionMode selectionMode) {
        this(selectionMode, false);
    }

    public SelectionGroup(SelectionMode selectionMode, boolean atLeastOneSelected) {
        setSelectionMode(selectionMode);
        setAtLeastOneSelected(atLeastOneSelected);

        selectables.addListener(this::onSelectablesChanged);
        selection.addListener(this::onSelectionChanged);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds the given {@link Selectable} to this group (if not already present).
     * <p></p>
     * If the given {@code Selectable}'s group is not the same as this, {@link Selectable#setSelectionGroup(SelectionGroup)}
     * is also called.
     * <p>
     * Last but not least, {@link #refresh(Selectable)} is called to ensure that the group' state is right.
     */
    public SelectionGroup add(Selectable selectable) {
        if (selectable == null || selectables.contains(selectable)) return this;
        selectables.add(selectable);

        SelectionGroup group = selectable.getSelectionGroup();
        if (group != this) selectable.setSelectionGroup(this);
        refresh(selectable);
        return this;
    }

    /**
     * Calls {@link #add(Selectable)} on each given {@code Selectable}.
     */
    public SelectionGroup addAll(Selectable... selectables) {
        for (Selectable selectable : selectables) {
            add(selectable);
        }
        return this;
    }

    /**
     * Calls {@link #add(Selectable)} on each given {@code Selectable}.
     */
    public SelectionGroup addAll(Collection<? extends Selectable> selectables) {
        for (Selectable selectable : selectables) {
            add(selectable);
        }
        return this;
    }

    /**
     * Removes the given {@link Selectable} from the group (if present).
     * <p>
     * The removal will also trigger {@link #onSelectablesChanged(SetChangeListener.Change)}.
     */
    public SelectionGroup remove(Selectable selectable) {
        if (!selectables.contains(selectable)) return this;
        isRemoval = true;
        selectables.remove(selectable);
        isRemoval = false;
        return this;
    }

    /**
     * Calls {@link #remove(Selectable)} on each given {@code Selectable}.
     */
    public SelectionGroup removeAll(Selectable... selectables) {
        for (Selectable selectable : selectables) {
            remove(selectable);
        }
        return this;
    }

    /**
     * Calls {@link #remove(Selectable)} on each given {@code Selectable}.
     */
    public SelectionGroup removeAll(Collection<? extends Selectable> selectables) {
        for (Selectable selectable : selectables) {
            remove(selectable);
        }
        return this;
    }

    /**
     * Removes all the {@link Selectable}s from the group.
     */
    public SelectionGroup clear() {
        selectables.clear();
        return this;
    }

    /**
     * Given a {@link Selectable} and its current or 'requested' state returns a value that won't break the rules
     * of the {@code SelectionGroup}.
     * <p>
     * For example (but there are many other cases), if the 'atLeastOneSelected' mode is on, the given {@code Selectable}
     * is the last selected one, and the requested selection state is 'false', the group won't allow it and return 'true'
     * instead.
     * <p>
     * This is the same mechanism used by {@link SelectionProperty} to avoid 'illegal' selection states.
     * <p>
     * Delegates to the current selection handler.
     */
    public boolean check(Selectable selectable, boolean state) {
        return handler.check(selectable, state);
    }

    //================================================================================
    // Protected Methods
    //================================================================================

    /**
     * Given a {@link Selectable} and its current or 'requested' state returns a value that won't break the rules
     * of the {@code SelectionGroup}.
     * <p>
     * This is used by {@link SelectionProperty} to not feed the {@link SelectionProperty#set(boolean)} method values that
     * would break the rules of the {@link SelectionGroup}. In other words, when the state is requested to switch to
     * selected/deselected, the property first asks the group if it is allowed, in case it is not, the {@code newValue}
     * parameter is "corrected".
     */
    protected boolean handleSelection(Selectable selectable, boolean state) {
        assert selectable != null;
        if (!selectables.contains(selectable)) return state;
        return handler.handle(selectable, state);
    }

    /**
     * Triggers when a {@link Selectable} is removed from the {@link ObservableSet} containing all the {@code Selectables}
     * managed by the group.
     * <p>
     * This will cause the {@link Selectable} to be also removed from the selection {@link ObservableSet} (meaning that
     * {@link #onSelectionChanged(SetChangeListener.Change)} will also be triggered) as well as its {@code SelectionGroup}
     * to be set to {@code null}.
     */
    protected void onSelectablesChanged(SetChangeListener.Change<? extends Selectable> c) {
        Selectable removed = c.getElementRemoved();
        if (c.wasRemoved()) {
            selection.remove(removed);
            removed.setSelectionGroup(null);
        }
    }

    /**
     * Triggers when a {@link Selectable} is removed from the {@link ObservableSet} containing all the {@code Selectables}
     * that are currently selected.
     * <p>
     * This executes two actions in two specific occasions:
     * <p> 1) If the removed {@code Selectable} is selected and the removal has not been triggered by any of the
     * 'remove' methods then the {@code Selectable} is deselected ({@code selectable.setSelected(false)})
     * <p> 2) If the selection is now empty, the 'atLeastOneSelected' mode is on and the removal was triggered by one of
     * the 'remove' methods, then ensures that there's at least one {@code Selectable} that is selected by using
     * {@link #getFirstSelectable()} and then if present {@code selectable.setSelected(true)}
     */
    protected void onSelectionChanged(SetChangeListener.Change<? extends Selectable> c) {
        Selectable removed = c.getElementRemoved();
        if (c.wasRemoved() && removed.isSelected() && !isRemoval) {
            removed.setSelected(false);
        }
        if (c.getSet().isEmpty() && isAtLeastOneSelected() && isRemoval) {
            getFirstSelectable().ifPresent(s -> s.setSelected(true));
        }
    }

    /**
     * Forces the {@link SelectionProperty} of the given {@link Selectable} to re-execute its {@code set(...)} method,
     * which will then trigger the {@link #handleSelection(Selectable, boolean)} method and thus ensuring that the
     * group' state is correct.
     */
    protected void refresh(Selectable selectable) {
        selectable.setSelected(selectable.isSelected());
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    interface SelectionHandler {

        boolean check(Selectable selectable, boolean state);

        boolean handle(Selectable selectable, boolean state);
    }

    class SingleSelectionHandler implements SelectionHandler {
        @Override
        public boolean check(Selectable selectable, boolean state) {
            if (!selectables.contains(selectable)) return state;
            if (!state) {
                if (isAtLeastOneSelected()) {
                    return !isSwitching && (selection.size() == 1 && selection.contains(selectable) || selection.isEmpty());
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean handle(Selectable selectable, boolean state) {
            if (!state) {
                if (isAtLeastOneSelected()) {
                    if (isSwitching) {
                        selection.remove(selectable);
                        return false;
                    }
                    if (selection.size() == 1 && selection.contains(selectable)) {
                        return true;
                    }
                    if (selection.isEmpty()) {
                        selection.add(selectable);
                        return true;
                    }
                }
                selection.remove(selectable);
                return false;
            }

            if (selection.contains(selectable)) return true;
            isSwitching = true;
            selection.clear();
            selection.add(selectable);
            isSwitching = false;
            return true;
        }
    }

    class MultipleSelectionHandler implements SelectionHandler {
        @Override
        public boolean check(Selectable selectable, boolean state) {
            return !selectables.contains(selectable) ? state : state || isAtLeastOneSelected() && selection.size() == 1;
        }

        @Override
        public boolean handle(Selectable selectable, boolean state) {
            if (!state) {
                if (isAtLeastOneSelected() && selection.size() == 1) return true;
                selection.remove(selectable);
                return false;
            }
            selection.add(selectable);
            return true;
        }
    }

    //================================================================================
    // Getters
    //================================================================================

    /**
     * @return an unmodifiable {@link ObservableSet} which contains all the {@code Selectables} managed by the group
     */
    public ObservableSet<Selectable> getSelectables() {
        return FXCollections.unmodifiableObservableSet(selectables);
    }

    /**
     * @return an unmodifiable {@link ObservableSet} which contains all the {@code Selectables} that are currently selected
     */
    public ObservableSet<Selectable> getSelection() {
        return FXCollections.unmodifiableObservableSet(selection);
    }

    /**
     * @return {@link #getSelectables()} but as a modifiable List, changes to this collection won't have any effect on the
     * group
     */
    public List<Selectable> getSelectablesList() {
        return new ArrayList<>(selectables);
    }

    /**
     * @return {@link #getSelection()} but as a modifiable List, changes to this collection won't have any effect on the
     * group
     */
    public List<Selectable> getSelectionList() {
        return new ArrayList<>(selection);
    }

    /**
     * Convenience method to get the first added {@link Selectable} of this group. As the group may contain no
     * {@code Selectables}, this returns an {@link Optional} instead of raising an Exception.
     */
    protected Optional<Selectable> getFirstSelectable() {
        try {
            return Optional.of(getSelectablesList().get(0));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Convenience method to get the first selected {@link Selectable} of this group. As the group selection may be empty,
     * this returns an {@link Optional} instead of raising an Exception.
     */
    public Optional<Selectable> getFirstSelected() {
        try {
            return Optional.of(getSelectionList().get(0));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    /**
     * Specifies the selection mode of the group, can be set to single or multiple selection.
     */
    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode mode) {
        this.selectionMode.set(mode);
    }

    public boolean isAtLeastOneSelected() {
        return atLeastOneSelected.get();
    }

    /**
     * Specifies whether the group should always keep at least one of its {@code Selectables} selected.
     * <p>
     * This may be useful for use cases in which a user is forced to pick a choice, not matter what as long as it is one
     * of the offered.
     *
     * @see SelectionGroup
     */
    public BooleanProperty atLeastOneSelectedProperty() {
        return atLeastOneSelected;
    }

    public void setAtLeastOneSelected(boolean atLeastOneSelected) {
        this.atLeastOneSelected.set(atLeastOneSelected);
    }
}
