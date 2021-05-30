package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.MFXContextMenuItem;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXTableView.MFXTableViewEvent;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.enums.SortState;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.filter.IFilterable;
import io.github.palexdev.materialfx.filter.MFXFilterDialog;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.TableSelectionModel;
import io.github.palexdev.materialfx.selection.base.ITableSelectionModel;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTableView}.
 * <p>
 * It's the main class since it handles the graphic of the control and its functionalities.
 * <p><p/>
 * Builds the column header using the specified supplier by {@link MFXTableView#headerSupplierProperty()},
 * builds the column cells container for each column in the {@link MFXTableView#getTableColumns()} list.
 * Each column can sort the table view using the comparator specified by the user.
 * <p><p/>
 * For each column calls the {@link MFXTableColumn#rowCellFunctionProperty()} and adds the build row cells to each row.
 * <p>
 * At the bottom of the table view there are pagination controls which include: filter icons to filter the table and clear the filter,
 * arrows to change the page, a combo box which specifies the number of rows to show (its max value is specified by {@link MFXTableView#maxRowsPerPageProperty()}),
 * a label that indicates the shown rows and the total amount of items.
 * The filter feature is implemented using {@link MFXFilterDialog}.
 * <p></p>
 * Note: for sorting and the filtering two {@link SortedList} are used, the original items list {@link MFXTableView#getItems()} remains untouched.
 * Why two sorted lists and not a filtered list? Because when the list is filtered it's desirable to keep the sort state. For this reason
 * it is much easier to manage two sorted lists instead of a filtered list.
 * <p>
 * For consistency when the table is filtered the filter icon is disabled, to filter the table again it must be reset before using the clear filter icon.
 * <p></p>
 * Note on the selection feature: It is desirable to keep the selection state when changing pages.
 * For this reason the selection model keeps track of the selected item and its index in the table view items list.
 * Since the rows are re-built every time the page changes their selection state is bound to changes in the table selection model.
 * That means that when a change occurs in the selection model, each row checks if the data represented by the row is contained in the selection model,
 * the check is made for both index and data using {@link TableSelectionModel#containSelected(int)} and {@link TableSelectionModel#containsSelected(Object)},
 * only if both methods return true the row state changes to "selected".
 * rows previously added in the selection model list, {@link io.github.palexdev.materialfx.selection.TableSelectionModel}.
 * <p></p>
 * <p> The navigation system is implemented using {@link IndexRange}, more info here {@link #shownRowsRangeProperty()}.
 * <p> The sorting system is implemented with an helper class, {@link MFXTableSortModel}.
 * <p></p>
 * <b>N.B:</b> Although the layout and everything else is well organized and documented, especially considering the JavaFX's counterpart,
 * note that this control is quite complicated and "delicate" since there is a lot going on (bindings, listeners, various computation, layout adjustments, etc...).
 * So when extending this class or creating your table view based on this one, be careful and make sure you have fully understood how all of this works.
 */
public class MFXTableViewSkin<T> extends SkinBase<MFXTableView<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final VBox container;
    private Region header;
    private final HBox columnsBox;
    private final VBox rowsBox;
    private final HBox pgcBox;
    private final Label rowsPerPageLabel;
    private final MFXComboBox<Integer> rowsPerPageCombo;
    private final Label shownRows;

    private final ObjectProperty<IndexRange> shownRowsRange = new SimpleObjectProperty<>();

    private SortedList<T> sortedList;
    private SortedList<T> filteredList;

    private final MFXFilterDialog<T> filterDialog;
    private final MFXStageDialog filterStageDialog;
    private final BooleanProperty tableFiltered = new SimpleBooleanProperty(false);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableViewSkin(MFXTableView<T> tableView) {
        super(tableView);

        sortedList = new SortedList<>(tableView.getItems());

        container = new VBox();
        container.setId("container");

        header = tableView.getHeaderSupplier().get();

        columnsBox = new HBox(10);
        columnsBox.setId("columns-container");
        columnsBox.setPadding(new Insets(0, 5, 0, 5));

        rowsBox = new VBox();
        rowsBox.setId("rows-container");
        rowsBox.setPadding(new Insets(0, 5, 0, 5));
        VBox.setVgrow(rowsBox, Priority.ALWAYS);

        rowsPerPageLabel = new Label("Rows Per Page");

        rowsPerPageCombo = new MFXComboBox<>();
        rowsPerPageCombo.setComboStyle(Styles.ComboBoxStyles.STYLE2);
        rowsPerPageCombo.setMaxPopupHeight(100);
        rowsPerPageCombo.getSelectionModel().selectFirst();

        shownRows = new Label("Shown Rows: ");
        shownRows.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (getShownRowsRange() != null) {
                        return new StringBuilder()
                                .append("Shown Rows:  ")
                                .append(getShownRowsRange().getStart() + 1)
                                .append("-").append(Math.min(getShownRowsRange().getEnd(), tableFiltered.get() ? filteredList.size() : sortedList.size()))
                                .append(" of ")
                                .append(tableFiltered.get() ? filteredList.size() : sortedList.size())
                                .toString();
                    }
                    return "";
                },
                shownRowsRange, tableFiltered, sortedList
        ));

        pgcBox = buildPaginationControls();

        filterDialog = new MFXFilterDialog<>();
        filterDialog.getFilterButton().setOnAction(event -> filterTable());
        filterStageDialog = new MFXStageDialog(filterDialog);
        filterStageDialog.setOwner(tableView.getScene() != null ? tableView.getScene().getWindow() : null);
        filterStageDialog.setCenterInOwner(true);
        filterStageDialog.setModality(Modality.WINDOW_MODAL);

        container.getChildren().setAll(header, columnsBox, rowsBox, pgcBox);
        getChildren().setAll(container);

        tableView.getTableColumns().forEach(this::buildColumn);
        columnsBox.getChildren().setAll(tableView.getTableColumns());
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for:
     * <p>
     * <p> - {@link MFXTableView#headerSupplierProperty()}: to rebuild the table's header is changes.
     * <p> - table view scene property: to update the filter dialog owner when the scene changes.
     * <p> - table view items property and list: reset the table when they change.
     * <p> - table view columns list: to reset the table and rebuild the columns, if columns are added/removed.
     * <p> - {@link MFXTableView#maxRowsPerPageProperty()}: to reset the table view and update the combo box.
     * <p> - combo box selected value: to update the {@link #shownRowsRangeProperty()} and re-build the rows.
     * <p> - {@link #shownRowsRangeProperty()}: to build the rows and initialize the sort model.
     * <p></p>
     * Adds bindings to:
     * <p>
     * <p> - rows container min height property: to update the container's height every time the combo box value changes or the shown range changes.
     * <p></p>
     * Adds event filters/handlers for:
     * <p>
     * <p> - MOUSE_PRESSED: to make the table acquire focus.
     * <p> - FORCE_UPDATE_EVENT: to reset the table.
     *
     * @see #reset(boolean)
     */
    private void setListeners() {
        MFXTableView<T> tableView = getSkinnable();

        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> tableView.requestFocus());
        tableView.addEventFilter(MFXTableViewEvent.FORCE_UPDATE_EVENT, event -> reset(true));

        tableView.headerSupplierProperty().addListener((observable, oldValue, newValue) -> {
            container.getChildren().remove(header);
            if (newValue != null) {
                header = newValue.get();
                container.getChildren().add(0, header);
            }
        });

        tableView.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterStageDialog.setOwner(newValue.getWindow());
            }
        });

        tableView.getItems().addListener((InvalidationListener) listInvalidated -> reset(false));
        tableView.itemsProperty().addListener(propertyInvalidated -> {
            reset(true);
            tableView.getItems().addListener((InvalidationListener) listInvalidated -> reset(false));
        });

        tableView.getTableColumns().addListener((ListChangeListener<? super MFXTableColumn<T>>) change -> {
            List<MFXTableColumn<T>> tmpRemoved = new ArrayList<>();
            List<MFXTableColumn<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }

            if (tmpRemoved.contains(tableView.getSortModel().getSortedColumn().getKey())) {
                tableView.getSortModel().sortBy(null, null);
            }

            tmpAdded.forEach(this::buildColumn);
            columnsBox.getChildren().setAll(tableView.getTableColumns());
            reset(true);
        });

        tableView.getSortModel().sortedColumnProperty().addListener(this::handleSort);

        tableView.maxRowsPerPageProperty().addListener(invalidated -> {
            reset(true);

            List<Integer> tmp = new ArrayList<>();
            for (int i = 5; i <= tableView.getMaxRowsPerPage(); i += 5) {
                tmp.add(i);
            }
            rowsPerPageCombo.getItems().setAll(tmp);
            rowsPerPageCombo.getSelectionModel().clearSelection();
            rowsPerPageCombo.getSelectionModel().selectFirst();
        });

        rowsBox.minHeightProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    if (rowsPerPageCombo.getSelectedValue() != null) {
                        return Math.min(
                                rowsPerPageCombo.getSelectedValue(),
                                tableFiltered.get() ? filteredList.size() : sortedList.size()
                        ) * tableView.getFixedRowsHeight();
                    }
                    return -1.0;
                },
                rowsPerPageCombo.selectedValueProperty(), shownRowsRange, tableFiltered
        ));

        rowsPerPageCombo.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setShownRowsRange(new IndexRange(0, newValue));
                buildRows();
            }
        });

        shownRowsRange.addListener((observable, oldValue, newValue) -> buildRows());
        shownRowsRange.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                tableView.getSortModel().init();
                shownRowsRange.removeListener(this);
            }
        });
    }

    /**
     * Clears the filter, keeps the previous sort state and rebuilds the rows.
     * <p>
     * If it is a full reset the shown range will also be reset.
     *
     * @param fullReset to specify if it's a full reset or a partial reset
     */
    protected void reset(boolean fullReset) {
        MFXTableView<T> tableView = getSkinnable();
        tableFiltered.set(false);
        Comparator<? super T> prevComp = sortedList.getComparator();
        sortedList = new SortedList<>(tableView.getItems(), prevComp);
        if (fullReset) {
            setShownRowsRange(new IndexRange(0, rowsPerPageCombo.getSelectedValue()));
        }
        buildRows();
    }

    /**
     * Sets the behavior of the specified column.
     * <p></p>
     * Adds the context menu to the columns and adds a MOUSE_PRESSED event filter for sorting.
     */
    protected void buildColumn(MFXTableColumn<T> tableColumn) {
        MFXTableView<T> tableView = getSkinnable();

        addContextMenu(tableColumn);
        tableColumn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                tableView.getSortModel().sortBy(tableColumn, tableColumn.getSortState().next());
            }
        });
    }

    /**
     * Builds the rows, from the given items list, the shown rows is specifies by {@link #shownRowsRangeProperty()}.
     * <p>
     * First builds a {@link MFXTableRow} then for each column in {@link MFXTableView#getTableColumns()} ()} it calls the
     * column row cell factory to build the row cells.
     * <p>
     * This method is also responsible for setting the rows selection behavior:
     * <p> - adds a MOUSE_PRESSED event filter that checks if the row's data is present or not in the table selection model.
     * If present the data is removed from the selection model, otherwise it is added.
     * <p> - Binds the row's {@link MFXTableRow#selectedProperty()} to changes of the selection model. When the selection
     * changes the row checks if its data is present or not in the selection model and updates the selectedProperty accordingly.
     */
    protected void buildRows(List<T> items) {
        MFXTableView<T> tableView = getSkinnable();

        List<HBox> rows = new ArrayList<>();
        int start = shownRowsRange.get().getStart();
        int end = Math.min(shownRowsRange.get().getEnd(), items.size());

        for (int i = start; i < end; i++) {
            T item = items.get(i);
            MFXTableRow<T> row = new MFXTableRow<>(item, 10);
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        ITableSelectionModel<T> selectionModel = tableView.getSelectionModel();
                        T data = row.getData();
                        int index = tableView.getItems().indexOf(data);
                        if (selectionModel.containsSelected(data) && selectionModel.containSelected(index)) {
                            selectionModel.clearSelectedItem(index);
                        } else {
                            selectionModel.select(index, data, event);
                        }
                    }
            );
            row.selectedProperty().bind(Bindings.createBooleanBinding(
                    () -> {
                        ObservableList<T> tableItems = tableView.getItems();
                        ITableSelectionModel<T> selectionModel = tableView.getSelectionModel();
                        T data = row.getData();
                        int index = tableItems.indexOf(data);
                        return selectionModel.containsSelected(data) && selectionModel.containSelected(index);
                    },
                    tableView.selectionModelProperty(), tableView.getSelectionModel().selectedItemsProperty()
            ));
            row.setMinHeight(tableView.getFixedRowsHeight());
            rows.add(row);

            for (MFXTableColumn<T> tableColumn : tableView.getTableColumns()) {
                MFXTableRowCell rowCell = tableColumn.getRowCellFunction().apply(item);
                rowCell.prefWidthProperty().bind(tableColumn.widthProperty());
                row.getChildren().add(rowCell);
            }
        }

        rowsBox.getChildren().setAll(rows);
    }

    /**
     * Used to call the {@link #buildRows(List)} method with the right items list.
     * If the table is filtered then the used list will be the filteredList otherwise the sortedList will be used instead.
     */
    private void buildRows() {
        buildRows(tableFiltered.get() ? filteredList : sortedList);
    }

    /**
     * Builds the HBox at the bottom of the table which contains all the pagination controls.
     */
    protected HBox buildPaginationControls() {
        MFXTableView<T> tableView = getSkinnable();

        MFXIconWrapper filterIcon = buildIcon("mfx-filter-alt", 16);
        MFXIconWrapper clearFilterIcon = buildIcon("mfx-filter-alt-clear", 16);
        MFXIconWrapper firstIcon = buildIcon("mfx-first-page", 18);
        MFXIconWrapper previousIcon = buildIcon("mfx-arrow-back", 10);
        MFXIconWrapper nextIcon = buildIcon("mfx-arrow-forward", 10);
        MFXIconWrapper lastIcon = buildIcon("mfx-last-page", 18);

        NodeUtils.makeRegionCircular(filterIcon);
        NodeUtils.makeRegionCircular(clearFilterIcon);
        NodeUtils.makeRegionCircular(firstIcon);
        NodeUtils.makeRegionCircular(previousIcon);
        NodeUtils.makeRegionCircular(nextIcon);
        NodeUtils.makeRegionCircular(lastIcon);


        BooleanBinding listEmpty = Bindings.createBooleanBinding(
                () -> tableView.getItems().isEmpty(),
                tableView.getItems()
        );
        filterIcon.disableProperty().bind(tableFiltered.or(listEmpty));
        clearFilterIcon.disableProperty().bind(tableFiltered.not().or(listEmpty));
        filterIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> filterStageDialog.show());
        clearFilterIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (tableFiltered.get()) {
                tableFiltered.set(false);
                buildRows();
            }
        });

        ((MFXFontIcon) filterIcon.getIcon()).colorProperty().bind(Bindings.createObjectBinding(
                () -> filterIcon.isDisabled() ? Color.web("#BEBEBE") : Color.web("#4D4D4D"),
                filterIcon.disabledProperty()
        ));
        ((MFXFontIcon) clearFilterIcon.getIcon()).colorProperty().bind(Bindings.createObjectBinding(
                () -> clearFilterIcon.isDisabled() ? Color.web("#BEBEBE") : Color.web("#4D4D4D"),
                clearFilterIcon.disabledProperty()
        ));

        firstIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> setShownRowsRange(new IndexRange(0, rowsPerPageCombo.getSelectedValue())));
        previousIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (getShownRowsRange().getStart() > 0) {
                IndexRange oldRange = getShownRowsRange();
                int start = Math.max(0, oldRange.getStart() - rowsPerPageCombo.getSelectedValue());
                IndexRange newRange = new IndexRange(start, start + rowsPerPageCombo.getSelectedValue());
                setShownRowsRange(newRange);
            }
        });
        nextIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            int size = tableFiltered.get() ? filteredList.size() : sortedList.size();
            if (getShownRowsRange().getEnd() < size) {
                IndexRange oldRange = getShownRowsRange();
                IndexRange newRange = new IndexRange(oldRange.getEnd(), oldRange.getEnd() + rowsPerPageCombo.getSelectedValue());
                setShownRowsRange(newRange);
            }
        });
        lastIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            int size = tableFiltered.get() ? filteredList.size() : sortedList.size();
            if (size > rowsPerPageCombo.getSelectedValue() && getShownRowsRange().getEnd() < size) {
                int start;
                if (size % rowsPerPageCombo.getSelectedValue() == 0) {
                    start = size - rowsPerPageCombo.getSelectedValue();
                } else {
                    start = rowsPerPageCombo.getSelectedValue() * (size / rowsPerPageCombo.getSelectedValue());
                }
                IndexRange newRange = new IndexRange(start, start + rowsPerPageCombo.getSelectedValue());
                setShownRowsRange(newRange);
            }
        });

        for (int i = 5; i <= tableView.getMaxRowsPerPage(); i += 5) {
            rowsPerPageCombo.getItems().add(i);
        }

        HBox box1 = new HBox(5, firstIcon, previousIcon, nextIcon, lastIcon);
        HBox box2 = new HBox(10, rowsPerPageLabel, rowsPerPageCombo);
        box1.setAlignment(Pos.CENTER_LEFT);
        box2.setAlignment(Pos.CENTER_LEFT);


        HBox pgcBox = new HBox(15,
                filterIcon, clearFilterIcon, new Separator(Orientation.VERTICAL),
                box1, new Separator(Orientation.VERTICAL), box2, new Separator(Orientation.VERTICAL), shownRows
        );
        pgcBox.getStyleClass().setAll("pagination-controls-container");
        pgcBox.setAlignment(Pos.CENTER);
        pgcBox.setPadding(new Insets(10, 20, 10, 20));
        pgcBox.setMinHeight(Region.USE_PREF_SIZE);
        pgcBox.setMaxHeight(Region.USE_PREF_SIZE);
        pgcBox.setPrefHeight(48);
        pgcBox.setMinWidth(700);
        pgcBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        pgcBox.setMaxWidth(Double.MAX_VALUE);

        shownRows.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        shownRows.setPadding(new Insets(5));
        HBox.setHgrow(shownRows, Priority.ALWAYS);

        return pgcBox;
    }

    /**
     * Handles changes in the {@link MFXTableSortModel}.
     * <p></p>
     * When the model {@link MFXTableSortModel#sortedColumnProperty()} changes the following evaluations occur:
     * <p>
     * <p> - If the old sorted column is not null, if the new sorted column is not the same as the old one, then the old one is reset (both sort state and sort icon).
     * <p> - If the new sorted column is null then the sortedList comparator is reset (set to null) and the rows are rebuilt. At this point the method exits and the following points are not executed.
     * <p> - Updates the new sorted column to the new sort state.
     * <p> - Animates the new sorted column icon. {@link #animateSortIcon(MFXTableColumn, SortState)}
     * <p> - Updates the sortedList comparator accordingly. (ASCENDING - column's comparator), (DESCENDING - column's comparator reversed), (UNSORTED - null).
     * <p> - Re-builds the rows.
     */
    private void handleSort(ObservableValue<? extends Pair<MFXTableColumn<T>, SortState>> observable, Pair<MFXTableColumn<T>, SortState> oldValue, Pair<MFXTableColumn<T>, SortState> newValue) {
        if (oldValue.getKey() != null) {
            if (oldValue.getKey() != newValue.getKey()) {
                oldValue.getKey().setSortState(SortState.UNSORTED);
                animateSortIcon(oldValue.getKey(), SortState.UNSORTED);
            }
        }

        if (newValue.getKey() == null) {
            sortedList.setComparator(null);
            buildRows();
            return;
        }

        newValue.getKey().setSortState(newValue.getValue());
        animateSortIcon(newValue.getKey(), newValue.getValue());

        switch (newValue.getValue()) {
            case ASCENDING:
                sortedList.setComparator(newValue.getKey().getComparator());
                break;
            case DESCENDING:
                sortedList.setComparator(newValue.getKey().getComparator().reversed());
                break;
            case UNSORTED:
                sortedList.setComparator(null);
                break;
        }
        buildRows();
    }

    /**
     * Animates the specified column's sort icon according to the specifies sort state.
     * <p></p>
     * <p> - ASCENDING: fade in
     * <p> - DESCENDING: rotate to 180°
     * <p> - UNSORTED: fade out
     */
    protected void animateSortIcon(MFXTableColumn<T> column, SortState sortState) {
        Node icon = column.getSortIcon();

        Timeline animation = new Timeline();
        switch (sortState) {
            case ASCENDING: {
                icon.setVisible(true);
                animation = MFXAnimationFactory.FADE_IN.build(icon, 250);
                break;
            }
            case DESCENDING: {
                icon.setVisible(true);
                KeyFrame kf = new KeyFrame(Duration.millis(150),
                        new KeyValue(icon.rotateProperty(), 180)
                );
                animation = new Timeline(kf);
                break;
            }
            case UNSORTED: {
                animation = MFXAnimationFactory.FADE_OUT.build(icon, 250);
                animation.setOnFinished(event -> {
                    icon.setVisible(false);
                    icon.setRotate(0.0);
                });
                break;
            }
        }
        animation.play();
    }

    /**
     * Responsible for filtering the table with the conditions specified in the {@link MFXFilterDialog}
     * <p></p>
     * The filteredList is built from the sortedList and its comparator property is bound to it, that's why even by
     * filtering the table the sort state is maintained.
     * <p></p>
     * The evaluation is done by calling the item's toString method or, if the items implements {@link IFilterable},
     * by calling {@link IFilterable#toFilterString()}.
     * <p></p>
     * <b>N.B: if the toString method is not overridden or does not contain any useful information for filtering it won't work.</b>
     */
    protected void filterTable() {
        ObservableList<T> list = filterDialog.filter(sortedList);
        filteredList = new SortedList<>(list);
        filteredList.comparatorProperty().bind(sortedList.comparatorProperty());
        tableFiltered.set(true);
        buildRows();
        filterStageDialog.close();
    }

    /**
     * Builds and adds a context menu for the specified column.
     * <p></p>
     * The default options are:
     * <p> - "Restore this column width"
     * <p> - "Restore all columns width"
     * <p> - "Autosize this column"
     * <p> - "Autosize all columns"
     * <p> - "Lock this column size"
     * <p> - "Unlock this column size"
     */
    protected void addContextMenu(MFXTableColumn<T> column) {
        MFXTableView<T> tableView = getSkinnable();

        MFXContextMenuItem restoreWidthThis = new MFXContextMenuItem(
                "Restore this column width",
                event -> column.setMinWidth(column.getInitialWidth())
        );

        MFXContextMenuItem restoreWidthAll = new MFXContextMenuItem(
                "Restore all columns width",
                event -> tableView.getTableColumns().forEach(c -> c.setMinWidth(c.getInitialWidth()))
        );

        MFXContextMenuItem autoSizeThis = new MFXContextMenuItem(
                "Autosize this column",
                event -> autoSizeColumn(column)
        );

        MFXContextMenuItem autoSizeAll = new MFXContextMenuItem(
                "Autosize all columns",
                event -> tableView.getTableColumns().forEach(this::autoSizeColumn)
        );

        MFXContextMenuItem lockSize = new MFXContextMenuItem(
                "Lock this column size",
                event -> column.setResizable(false)
        );

        MFXContextMenuItem unlockSize = new MFXContextMenuItem(
                "Unlock this column size",
                event -> column.setResizable(true)
        );

        new MFXContextMenu.Builder()
                .addMenuItem(autoSizeAll)
                .addMenuItem(autoSizeThis)
                .addSeparator()
                .addMenuItem(restoreWidthAll)
                .addMenuItem(restoreWidthThis)
                .addSeparator()
                .addMenuItem(lockSize)
                .addMenuItem(unlockSize)
                .install(column);
    }

    /**
     * Autosizes the specified column by getting all the corresponding row cells, filtering them
     * if they are truncated, {@link MFXTableRowCell#isTruncated()}.
     * At the end sets the column's min width to the value computed by {@link #getMaxCellWidth(List)}
     */
    @SuppressWarnings("unchecked")
    protected void autoSizeColumn(MFXTableColumn<T> column) {
        MFXTableView<T> tableView = getSkinnable();

        int index = tableView.getTableColumns().indexOf(column);
        if (index > -1) {
            List<MFXTableRow<T>> tableRows = rowsBox.getChildren().stream()
                    .filter(node -> node instanceof MFXTableRow)
                    .map(node -> (MFXTableRow<T>) node)
                    .collect(Collectors.toList());
            List<MFXTableRowCell> rowCells = new ArrayList<>();
            tableRows.forEach(row -> {
                MFXTableRowCell rowCell = (MFXTableRowCell) row.getChildren().get(index);
                rowCell.requestLayout();
                if (rowCell.isTruncated()) {
                    rowCells.add(rowCell);
                }
            });
            double max = getMaxCellWidth(rowCells);
            if (max != -1) {
                column.setMinWidth(max);
            }
        }
    }

    /**
     * Iterates over the specified row cells list, computes the minimum width so that the content is not truncated ({@link MFXTableRowCell#computeWidth()})
     * for each one and returns the max computed value.
     */
    protected double getMaxCellWidth(List<MFXTableRowCell> rowCells) {
        double max = -1;
        for (MFXTableRowCell rowCell : rowCells) {
            double computed = rowCell.computeWidth();
            computed += rowCell.snappedRightInset() + rowCell.snappedLeftInset();
            if (computed > max) {
                max = computed;
            }
        }
        return max;
    }

    protected IndexRange getShownRowsRange() {
        return shownRowsRange.get();
    }

    /**
     * Specifies the range of rows to show.
     * <p>
     * For example, let's assume that the combo box value is 5. For the first page
     * the range will be [0, 5], for the second page [5, 10] and so on.
     * <p>
     * The value is updated by the navigation icons at the bottom of the table and it's used by the
     * {@link #buildRows(List)} method to build only the needed rows. Also the end value is corrected in that
     * method applying {@link Math#min(int, int)} to the range end value and the table view items list size.
     * That is because the range may specify and end index that is greater than the number of items.
     */
    protected ObjectProperty<IndexRange> shownRowsRangeProperty() {
        return shownRowsRange;
    }

    protected void setShownRowsRange(IndexRange shownRowsRange) {
        this.shownRowsRange.set(shownRowsRange);
    }

    /**
     * Convenience method to build an {@link MFXIconWrapper} with an {@link MFXFontIcon}
     * using the specified description and size.
     */
    private MFXIconWrapper buildIcon(String description, double size) {
        return new MFXIconWrapper(new MFXFontIcon(description, size), 24).defaultRippleGeneratorBehavior();
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 600;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset +
                header.getHeight() +
                columnsBox.getHeight() +
                snapSizeY(Math.min(
                        rowsPerPageCombo.getSelectedValue() != null ? rowsPerPageCombo.getSelectedValue() : 0,
                        tableFiltered.get() ? filteredList.size() : sortedList.size()
                        ) * getSkinnable().getFixedRowsHeight()
                ) +
                pgcBox.getHeight() +
                bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
}
