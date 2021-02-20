/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumnCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.enums.SortState;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.filter.IFilterable;
import io.github.palexdev.materialfx.filter.MFXFilterDialog;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.DragResizer;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MFXTableViewSkin<T> extends SkinBase<MFXTableView<T>> {
    private final VBox container;
    private final HBox columnsContainer;
    private final VBox rowsContainer;

    private final HBox paginationControls;
    private final MFXIconWrapper filterIcon;
    private final MFXIconWrapper clearFilterIcon;
    private final MFXLabel rowsPerPageLabel;
    private final MFXComboBox<Integer> rowsPerPageCombo;
    private final MFXLabel shownRows;
    private int index = 0;

    private SortedList<T> sortedList;
    private SortedList<T> filteredList;

    private final MFXFilterDialog filterDialog;
    private final BooleanProperty tableFiltered = new SimpleBooleanProperty(false);

    public MFXTableViewSkin(MFXTableView<T> tableView) {
        super(tableView);

        sortedList = new SortedList<>(tableView.getItems());

        container = new VBox();
        container.getStyleClass().setAll("container");

        columnsContainer = new HBox(10);
        columnsContainer.getStyleClass().setAll("columns-container");
        columnsContainer.prefWidthProperty().bindBidirectional(container.prefWidthProperty());
        columnsContainer.setPrefHeight(30);
        columnsContainer.setPadding(new Insets(5));

        rowsContainer = new VBox();
        rowsContainer.getStyleClass().setAll("rows-container");
        rowsContainer.setPadding(new Insets(3, 5, 3, 5));
        rowsContainer.prefWidthProperty().bind(columnsContainer.widthProperty());

        paginationControls = new HBox(10);
        paginationControls.getStyleClass().setAll("pagination");
        paginationControls.prefWidthProperty().bind(container.widthProperty());
        paginationControls.setPrefHeight(40);
        paginationControls.setMaxHeight(Region.USE_PREF_SIZE);
        paginationControls.setAlignment(Pos.CENTER_RIGHT);
        paginationControls.setPadding(new Insets(8, 5, 5, 5));

        filterIcon = buildFilterIcon();
        clearFilterIcon = buildClearFilterIcon();
        filterDialog = new MFXFilterDialog();
        filterDialog.setTitle("Filter TableView");
        filterDialog.getStage().setCenterInOwner(false);
        filterDialog.getStage().setManualPosition(true);
        filterDialog.getStage().setOwner(tableView.getScene().getWindow());
        filterDialog.getStage().setModality(Modality.WINDOW_MODAL);
        filterDialog.getFilterButton().setOnAction(event -> filterTable());

        rowsPerPageLabel = new MFXLabel("Rows per page");
        rowsPerPageLabel.setLabelStyle(Styles.LabelStyles.STYLE2);
        rowsPerPageLabel.setStyle("-fx-border-color: transparent");
        rowsPerPageLabel.setLabelAlignment(Pos.CENTER);
        rowsPerPageLabel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> System.out.println(rowsPerPageLabel.getWidth()));
        HBox.setMargin(rowsPerPageLabel, new Insets(0, -10, 0, 0));

        rowsPerPageCombo = new MFXComboBox<>();
        rowsPerPageCombo.setComboStyle(Styles.ComboBoxStyles.STYLE2);
        rowsPerPageCombo.setMaxPopupWidth(100);
        rowsPerPageCombo.setMaxPopupHeight(100);
        HBox.setMargin(rowsPerPageCombo, new Insets(0, -5, 0, 0));
        tableView.maxRowsProperty().bind(rowsPerPageCombo.selectedValueProperty());

        shownRows = new MFXLabel();
        shownRows.setLabelStyle(Styles.LabelStyles.STYLE2);
        shownRows.setStyle("-fx-border-color: transparent");
        shownRows.setLabelAlignment(Pos.CENTER);
        shownRows.setMinWidth(85);
        shownRows.setPrefHeight(30);
        HBox.setMargin(shownRows, new Insets(0, 5, 0, 15));

        setupPaginationControls();

        container.getChildren().addAll(columnsContainer, rowsContainer, paginationControls);
        getChildren().add(container);

        buildColumns();
        buildRows();

        setListeners();
    }

    private void setListeners() {
        MFXTableView<T> tableView = getSkinnable();

        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> tableView.requestFocus());
        tableView.getItems().addListener((InvalidationListener) invalidated -> {
            tableView.getSelectionModel().clearSelection();
            if (tableFiltered.get()) {
                tableFiltered.set(false);
            }
            Comparator<? super T> prevComp = sortedList.getComparator();
            sortedList = new SortedList<>(tableView.getItems(), prevComp);
            buildRows();
        });

        rowsPerPageCombo.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                index = 0;
                buildRows();
            }
        });
        rowsPerPageCombo.skinProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                if (newValue != null) {
                    rowsPerPageCombo.getSelectionModel().selectItem(10);
                    rowsPerPageCombo.skinProperty().removeListener(this);
                }
            }
        });

        filterIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            Bounds bounds = tableView.getBoundsInLocal();
            Bounds screenBounds = tableView.localToScreen(bounds);
            double x = screenBounds.getMinX() - Math.abs((tableView.getWidth() - filterDialog.getPrefWidth()) / 2.0);
            double y = screenBounds.getMinY() + Math.abs((tableView.getHeight() - filterDialog.getPrefHeight()) / 2.0);
            filterDialog.getStage().setManualX(x);
            filterDialog.getStage().setManualY(y);
            filterDialog.show();
        });
        clearFilterIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (tableFiltered.get()) {
                tableFiltered.set(false);
                buildRows();
            }
        });
        filterIcon.disableProperty().bind(tableFiltered);
        clearFilterIcon.disableProperty().bind(tableFiltered.not());
    }

    protected void buildColumns() {
        MFXTableView<T> tableView = getSkinnable();

        for (MFXTableColumnCell<T> column : tableView.getColumns()) {
            column.setMaxHeight(Double.MAX_VALUE);
            DragResizer.makeResizable(column, DragResizer.RIGHT);
            column.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sortColumn(column));
            columnsContainer.getChildren().add(column);
        }
    }

    protected MFXTableRow<T> buildRowBox(T item) {
        MFXTableView<T> tableView = getSkinnable();

        MFXTableRow<T> row = new MFXTableRow<>(10, Pos.CENTER_LEFT, item);
        row.prefWidthProperty().bind(container.widthProperty());
        row.setMinHeight(tableView.getFixedRowsHeight());
        row.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> tableView.getSelectionModel().select(row, event));
        return row;
    }

    protected void buildRows(List<T> items) {
        MFXTableView<T> tableView = getSkinnable();

        List<MFXTableRow<T>> rows = new ArrayList<>();
        int i;
        int size = items.size();
        for (i = index; i < (tableView.getMaxRows() + index) && size > 0 && i < size; i++) {
            T item = items.get(i);
            MFXTableRow<T> row = buildRowBox(item);
            rows.add(row);

            for (MFXTableColumnCell<T> column : tableView.getColumns()) {
                MFXTableRowCell rowCell = column.getRowCellFactory().call(item);
                if (isRightAlignment(column.getAlignment())) {
                    rowCell.setPadding(new Insets(0, 5, 0, 0));
                    rowCell.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    rowCell.setPadding(new Insets(0, 0, 0, 5));
                    rowCell.setAlignment(Pos.CENTER_LEFT);
                }
                rowCell.prefWidthProperty().bind(column.widthProperty());
                rowCell.setMouseTransparent(true);
                row.getChildren().add(rowCell);
            }
        }

        rowsContainer.getChildren().setAll(rows);
        shownRows.setText((index + 1) + "-" + (index + rowsContainer.getChildren().size()) + " of " + size);
        updateSelection();
    }

    protected void buildRows() {
        if (tableFiltered.get()) {
            buildRows(filteredList);
        } else {
            buildRows(sortedList);
        }
    }

    protected MFXIconWrapper buildIcon(String description, double size) {
        MFXIconWrapper icon = new MFXIconWrapper(new MFXFontIcon(description, size), 22).addRippleGenerator();
        RippleGenerator rippleGenerator = icon.getRippleGenerator();
        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
        return icon;
    }

    protected MFXIconWrapper buildFilterIcon() {
        MFXIconWrapper icon = new MFXIconWrapper(new MFXFontIcon("mfx-filter", 16), 22).addRippleGenerator();
        icon.getStylesheets().addAll(getSkinnable().getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon);
        HBox.setMargin(icon, new Insets(0, 0, 0, 10));
        RippleGenerator rippleGenerator = icon.getRippleGenerator();
        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
        return icon;
    }

    protected MFXIconWrapper buildClearFilterIcon() {
        MFXIconWrapper icon = new MFXIconWrapper(new MFXFontIcon("mfx-filter-clear", 16), 22).addRippleGenerator();
        icon.getStylesheets().addAll(getSkinnable().getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon);
        HBox.setMargin(icon, new Insets(0, 0, 0, 10));
        RippleGenerator rippleGenerator = icon.getRippleGenerator();
        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
        return icon;
    }

    private void setupPaginationControls() {
        MFXIconWrapper firstIcon = buildIcon("mfx-first-page", 18);
        MFXIconWrapper previousIcon = buildIcon("mfx-arrow-back", 10);
        MFXIconWrapper nextIcon = buildIcon("mfx-arrow-forward", 10);
        MFXIconWrapper lastIcon = buildIcon("mfx-last-page", 18);

        NodeUtils.makeRegionCircular(firstIcon);
        NodeUtils.makeRegionCircular(previousIcon);
        NodeUtils.makeRegionCircular(nextIcon);
        NodeUtils.makeRegionCircular(lastIcon);

        firstIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> goFirstPage());
        previousIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> changePage(-1));
        nextIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> changePage(1));
        lastIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> goLastPage());

        int i;
        for (i = 5; i <= getSkinnable().getMaxRowsCombo(); i+=5) {
            rowsPerPageCombo.getItems().add(i);
        }

        paginationControls.getChildren().addAll(
                filterIcon, clearFilterIcon, rowsPerPageLabel, rowsPerPageCombo, shownRows,
                firstIcon, previousIcon, nextIcon, lastIcon
        );
    }

    private void changePage(int offset) {
        MFXTableView<T> tableView = getSkinnable();

        if (offset == -1) {
            if (index == 0 || index < tableView.getMaxRows()) {
                return;
            }
            index -= tableView.getMaxRows();
        } else {
            if (tableFiltered.get()) {
                if (filteredList.size() < tableView.getMaxRows()) {
                    return;
                }
                if ((index + tableView.getMaxRows()) > filteredList.size()) {
                    return;
                }
            } else {
                if (sortedList.size() < tableView.getMaxRows()) {
                    return;
                }
                if ((index + tableView.getMaxRows()) > sortedList.size()) {
                    return;
                }
            }
            index += tableView.getMaxRows();
        }
        buildRows();
    }

    private void goFirstPage() {
        MFXTableView<T> tableView = getSkinnable();

        if (index == 0 || index < tableView.getMaxRows()) {
            return;
        }
        index = 0;
        buildRows();
    }

    private void goLastPage() {
        MFXTableView<T> tableView = getSkinnable();

        int size;
        if (tableFiltered.get()) {
            size = filteredList.size();
        } else {
            size = sortedList.size();
        }

        if (size < tableView.getMaxRows()) {
            return;
        }

        int tmp = index;
        while (tmp + tableView.getMaxRows() < sortedList.size()) {
            tmp += tableView.getMaxRows();
        }
        index = tmp;
        buildRows();
    }

    @SuppressWarnings("unchecked")
    private void updateSelection() {
        MFXTableView<T> tableView = getSkinnable();

        List<MFXTableRow<T>> selectedRows = tableView.getSelectionModel().getSelectedRows();
        List<T> selectedItems = selectedRows.stream().map(MFXTableRow::getItem).collect(Collectors.toList());
        List<MFXTableRow<T>> shownRows = rowsContainer.getChildren().stream()
                .filter(node -> node instanceof MFXTableRow)
                .map(node -> (MFXTableRow<T>) node)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            return;
        }
        for (MFXTableRow<T> row : shownRows) {
            if (selectedItems.contains(row.getItem())) {
                tableView.getSelectionModel().select(row, null);
            }
        }
    }

    private void clearSort(MFXTableColumnCell<T> currColumn) {
        MFXTableView<T> tableView = getSkinnable();

        List<MFXTableColumnCell<T>> columns = tableView.getColumns();
        columns.forEach(column -> {
            if (column.getSortState() != SortState.UNSORTED && column != currColumn) {
                Node icon = column.getGraphic();
                icon.setVisible(false);
                icon.setRotate(0.0);
                column.setSortState(SortState.UNSORTED);
            }
        });
        sortedList.setComparator(null);
        buildRows();
    }

    private void animateSortIcon(Node node, SortState sortState) {
        Timeline animation = new Timeline();
        switch (sortState) {
            case DESCENDING: {
                animation = MFXAnimationFactory.FADE_OUT.build(node, 250);
                animation.setOnFinished(event -> {
                    node.setVisible(false);
                    node.setRotate(0.0);
                });
                break;
            }
            case UNSORTED: {
                node.setVisible(true);
                animation = MFXAnimationFactory.FADE_IN.build(node, 250);
                break;
            }
            case ASCENDING: {
                node.setVisible(true);
                KeyFrame kf = new KeyFrame(Duration.millis(150),
                        new KeyValue(node.rotateProperty(), 180)
                );
                animation = new Timeline(kf);
                break;
            }
        }
        animation.play();
    }

    protected void sortColumn(MFXTableColumnCell<T> column) {
        if (column.getComparator() == null) {
            throw new NullPointerException("Comparator has not been set for column: " + column.getColumnName());
        }

        clearSort(column);
        Node icon = column.getGraphic();
        switch (column.getSortState()) {
            // Goes UNSORTED
            case DESCENDING: {
                sortedList.setComparator(null);
                animateSortIcon(icon, SortState.DESCENDING);
                column.setSortState(SortState.UNSORTED);
                break;
            }
            // Goes ASCENDING
            case UNSORTED: {
                sortedList.setComparator(column.getComparator());
                animateSortIcon(icon, SortState.UNSORTED);
                column.setSortState(SortState.ASCENDING);
                break;
            }
            // Goes DESCENDING
            case ASCENDING:  {
                sortedList.setComparator(column.getComparator().reversed());
                animateSortIcon(icon, SortState.ASCENDING);
                column.setSortState(SortState.DESCENDING);
                break;
            }
        }
        buildRows();
    }

    private void filterTable() {
        tableFiltered.set(true);

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T item : sortedList) {
            boolean expr;

            if (item instanceof IFilterable) {
                IFilterable fItem = (IFilterable) item;
                expr = filterDialog.filter(fItem.toFilterString());
            } else {
                expr = filterDialog.filter(item.toString());
            }

            if (expr) {
                list.add(item);
            }
        }

        filteredList = new SortedList<>(list);
        filteredList.comparatorProperty().bind(sortedList.comparatorProperty());
        buildRows();
        filterDialog.close();
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        List<Node> rows = rowsContainer.getChildren();
        return topInset
                + rows.stream().mapToDouble(node -> node.getLayoutBounds().getHeight()).sum()
                + columnsContainer.getHeight()
                + paginationControls.getHeight()
                + bottomInset;
    }

    private boolean isRightAlignment(Pos alignment) {
        return alignment == Pos.BASELINE_RIGHT || alignment == Pos.BOTTOM_RIGHT ||
                alignment == Pos.CENTER_RIGHT || alignment == Pos.TOP_RIGHT;
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, leftInset, bottomInset, rightInset);
    }
}
