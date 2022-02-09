/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.collections.TransformableListWrapper;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXTableColumn.MFXTableColumnEvent;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.SortState;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTableView}.
 * <p>
 * The top container is a {@link VBox} which contains the columns box, the virtual flow and the footer.
 * <p>
 * The {@link MFXTableView#getTableColumns()} are contained in a {@link HBox}
 * <p>
 * At the bottom of the table view there's a footer which by default has two icons to filter/clear filter. Can be changed by overriding
 * {@link #buildFooter()} or hidden(removed) by setting {@link MFXTableView#footerVisibleProperty()} to false.
 * <p></p>
 * The filter mechanism relies on the super flexible {@link MFXFilterPane} shown in a modal dialog.
 * <p></p>
 * Note: for sorting and the filtering a {@link TransformableList} is used, the original items list {@link MFXTableView#getItems()} remains untouched.
 * <p></p>
 * <b>N.B:</b> Although the layout and everything else is well organized and documented, especially with thew new implementation and considering the JavaFX's counterpart,
 * note that this control is quite complicated and "delicate" since there is a lot going on (bindings, listeners, various computation, layout adjustments, factories, nested cells, etc...).
 * So when extending this class or creating your table view based on this one, be careful and make sure you have fully understood how all of this works.
 */
public class MFXTableViewSkin<T> extends SkinBase<MFXTableView<T>> {
	//================================================================================
	// Properties
	//================================================================================
	protected final VBox container;
	protected final HBox columnsContainer;
	protected final SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow;
	protected final StackPane footer;

	private final MFXFilterPane<T> filterPane;
	private final MFXStageDialog filterDialog;
	private MFXTableColumn<T> sortedColumn;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableViewSkin(MFXTableView<T> tableView, SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow) {
		super(tableView);
		this.rowsFlow = rowsFlow;

		columnsContainer = new HBox();
		columnsContainer.getStyleClass().add("columns-container");
		Bindings.bindContent(columnsContainer.getChildren(), tableView.getTableColumns());

		filterPane = new MFXFilterPane<>();
		Bindings.bindContent(filterPane.getFilters(), tableView.getFilters());

		footer = buildFooter();

		container = new VBox(columnsContainer, rowsFlow);
		if (tableView.isFooterVisible()) {
			container.getChildren().add(footer);
		}

		filterDialog = MFXDialogs.filter(filterPane)
				.setShowMinimize(false)
				.toStageDialogBuilder()
				.setDraggable(true)
				.setOwnerNode(container)
				.setCenterInOwnerNode(true)
				.initOwner(tableView.getScene().getWindow())
				.initModality(Modality.APPLICATION_MODAL)
				.get();
		filterDialog.setOnShown(event -> filterDialog.toFront());

		getChildren().setAll(container);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Specifies the behavior for the following changes/events:
	 * <p> - Handles the focus on MOUSE_PRESSED
	 * <p> - Handles the sorting on {@link MFXTableColumnEvent#SORTING_EVENT}
	 * <p> - Re-builds the cell when columns change
	 * <p> - Handles the footer visibility
	 */
	@SuppressWarnings("unchecked")
	private void addListeners() {
		MFXTableView<T> tableView = getSkinnable();

		tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> tableView.requestFocus());
		tableView.addEventFilter(MFXTableColumnEvent.SORTING_EVENT, event -> {
			TransformableListWrapper<T> transformableList = tableView.getTransformableList();
			MFXTableColumn<T> column = event.getColumn();
			if (sortedColumn != null && sortedColumn != column) {
				sortedColumn.setSortState(SortState.UNSORTED);
			}
			switch (event.getSortState()) {
				case UNSORTED: {
					transformableList.setComparator(null, false);
					break;
				}
				case ASCENDING: {
					transformableList.setComparator(event.getComparator(), false);
					break;
				}
				case DESCENDING: {
					transformableList.setComparator(event.getComparator(), true);
					break;
				}
			}
			sortedColumn = column;
		});

		tableView.getTableColumns().addListener((InvalidationListener) invalidated -> {
			for (MFXTableRow<T> row : rowsFlow.getCells().values()) {
				row.buildCells();
			}
		});

		tableView.footerVisibleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				container.getChildren().add(footer);
			} else {
				container.getChildren().remove(footer);
			}
		});
	}

	/**
	 * Responsible for building the table's footer.
	 */
	protected StackPane buildFooter() {
		MFXTableView<T> tableView = getSkinnable();

		MFXIconWrapper filterIcon = new MFXIconWrapper("mfx-filter-alt", 16, 32).defaultRippleGeneratorBehavior();
		MFXIconWrapper clearFilterIcon = new MFXIconWrapper("mfx-filter-alt-clear", 16, 32).defaultRippleGeneratorBehavior();

		NodeUtils.makeRegionCircular(filterIcon);
		NodeUtils.makeRegionCircular(clearFilterIcon);

		filterIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			filterDialog.showDialog();
		});
		clearFilterIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			filterPane.getActiveFilters().clear();
			tableView.getTransformableList().setPredicate(null);
		});

		filterPane.setOnFilter(event -> {
			tableView.getTransformableList().setPredicate(filterPane.filter());
			filterDialog.close();
		});
		filterPane.setOnReset(event -> filterPane.getActiveFilters().clear());

		HBox container = new HBox(10, filterIcon, clearFilterIcon);
		StackPane.setAlignment(container, Pos.CENTER_LEFT);

		StackPane stackPane = new StackPane(container);
		stackPane.getStyleClass().add("default-footer");
		return stackPane;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
