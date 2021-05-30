package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.enums.SortState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper class makes managing {@link MFXTableView} sort state easier.
 * <p>
 * Keeps track of the current sorted column and its sort state using a read only object
 * property, {@link #sortedColumnProperty()}, and JavaFX {@link Pair} class.
 * <p></p>
 * Why Pair? Because this class needs to inform the table view of changes even when the sorted column is the same.
 * The JavaFX change listeners fire the change event only when the value is different.
 * So when I tried to use this {@code ReadOnlyObjectWrapper<TableColumn<T>>}, the table would not update if the
 * sort column was the same (but with a different sort state of course). There are different solutions to this issue,
 * for example I could have modified/expanded the JavaFX property class but having this helper class is better as it adds
 * other features as well.
 * <p>
 * So to have the needed behavior every time a column changes its state {@link #sortBy(MFXTableColumn, SortState)} is called.
 * This method simply sets the {@link #sortedColumnProperty()} to a new {@link Pair} with the specified arguments, and since it is
 * a new instance JavaFX will fire the change event even if the column is the same.
 * <p></p>
 * This mechanism also allows the user to sort the table manually, since you can retrieve the columns with {@link MFXTableView#getTableColumns()}.
 * <p></p>
 * It also does two checks: one is done by the {@link #init()} method, the other one is done everytime a change in the columns list occurs.
 * Both those checks call {@link #useLast()}
 */
public class MFXTableSortModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private boolean init = false;
    private final ObservableList<MFXTableColumn<T>> tableColumns;
    private final ReadOnlyObjectWrapper<Pair<MFXTableColumn<T>, SortState>> sortedColumn = new ReadOnlyObjectWrapper<>(new Pair<>(null, null));

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableSortModel(ObservableList<MFXTableColumn<T>> tableColumns) {
        this.tableColumns = tableColumns;
    }

    //================================================================================
    // Methods
    //================================================================================
    public void init() {
        if (init) {
            return;
        }

        tableColumns.addListener((ListChangeListener<? super MFXTableColumn<T>>) change -> useLast());
        useLast();
        init = true;
    }

    /**
     * Sets the {@link #sortedColumnProperty()} with a new {@link Pair} with the specified
     * column and sort state.
     */
    public void sortBy(MFXTableColumn<T> column, SortState sortState) {
        if (!tableColumns.contains(column)) {
            throw new IllegalArgumentException("The specified column is not present in the TableView's columns list!!");
        }

        sortedColumn.set(new Pair<>(column, sortState));
    }

    /**
     * Collects all the table columns whose sort state is not UNSORTED in a temporary list.
     * The last column is the column chosen for {@link #sortBy(MFXTableColumn, SortState)}, the other ones
     * are reset to UNSORTED.
     */
    private void useLast() {
        List<MFXTableColumn<T>> sorted = tableColumns.stream()
                .filter(column -> column.getSortState() != SortState.UNSORTED)
                .collect(Collectors.toList());
        if (!sorted.isEmpty()) {
            MFXTableColumn<T> last = sorted.remove(sorted.size() - 1);
            sorted.forEach(column -> column.setSortState(SortState.UNSORTED));
            sortBy(last, last.getSortState());
        }
    }

    public Pair<MFXTableColumn<T>, SortState> getSortedColumn() {
        return sortedColumn.get();
    }

    /**
     * Specifies the current sorted column and its sort state.
     */
    public ReadOnlyObjectProperty<Pair<MFXTableColumn<T>, SortState>> sortedColumnProperty() {
        return sortedColumn.getReadOnlyProperty();
    }
}
