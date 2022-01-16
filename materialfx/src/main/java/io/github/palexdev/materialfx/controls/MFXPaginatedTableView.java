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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.skins.MFXPaginatedTableViewSkin;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Skin;
import javafx.scene.input.ScrollEvent;

/**
 * This is the implementation of a paginated {@link MFXTableView}.
 * <p>
 * This table view introduces 4 new properties:
 * <p> - the current page
 * <p> - the total number of pages (max page)
 * <p> - The number of pages to show in the pagination control, {@link #pagesToShowProperty()}
 * <p> - The number of rows per page
 * <p></p>
 * This table view extends {@link MFXTableView} because it uses the same system (uses a virtual flow),
 * the page navigation is just a trick. The scroll bars are hidden and the scroll value is handled/updated
 * when changing the page.
 *
 * @param <T> The type of the data within the table.
 */
public class MFXPaginatedTableView<T> extends MFXTableView<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-paginated-table-view";
	private final IntegerProperty currentPage = new SimpleIntegerProperty(1) {
		@Override
		public void set(int newValue) {
			int number = NumberUtils.clamp(newValue, 1, getMaxPage());
			super.set(number);
		}
	};
	private final ReadOnlyIntegerWrapper maxPage = new ReadOnlyIntegerWrapper();
	private final IntegerProperty pagesToShow = new SimpleIntegerProperty(9);
	private final IntegerProperty rowsPerPage = new SimpleIntegerProperty(5);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPaginatedTableView() {
		this(FXCollections.observableArrayList());
	}

	public MFXPaginatedTableView(ObservableList<T> items) {
		super(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		rowsFlow.getVBar().visibleProperty().unbind();
		rowsFlow.getHBar().visibleProperty().unbind();
		rowsFlow.getVBar().setVisible(false);
		rowsFlow.getHBar().setVisible(false);
		addEventFilter(ScrollEvent.ANY, Event::consume);

		getTransformableList().addListener((InvalidationListener) invalidated -> updateMaxPages());
		getTransformableList().predicateProperty().addListener(invalidated -> {
			updateMaxPages();
			setCurrentPage(1);
			goToPage(1);
		});
		updateMaxPages();

		currentPageProperty().addListener(invalidated -> goToPage(getCurrentPage()));
	}

	/**
	 * Goes to the given page index.
	 * <p>
	 * The given integer is clamped between 1 and the max page index.
	 */
	public void goToPage(int index) {
		int page = NumberUtils.clamp(index, 1, getMaxPage());
		double pos = (page - 1) * getRowsPerPage() * rowsFlow.getCellHeight();
		rowsFlow.getVBar().setValue(pos);
	}

	/**
	 * Responsible for updating the max page index when needed.
	 */
	private void updateMaxPages() {
		int size = getTransformableList().size();
		int rowsPerPage = getRowsPerPage();
		int max = (int) Math.ceil((double) size / rowsPerPage);
		setMaxPage(max);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Unsupported by the paginated table view.
	 */
	@Override
	public void scrollBy(double pixels) {
		throw new UnsupportedOperationException("The paginated table view cannot scroll ny pixels");
	}

	/**
	 * Calls {@link #goToPage(int)}.
	 */
	@Override
	public void scrollTo(int index) {
		goToPage(index);
	}

	/**
	 * Goes to the first page.
	 */
	@Override
	public void scrollToFirst() {
		goToPage(1);
	}

	/**
	 * Goes to the last page.
	 */
	@Override
	public void scrollToLast() {
		goToPage(getMaxPage());
	}

	/**
	 * Unsupported by the paginated table view.
	 */
	@Override
	public void scrollToPixel(double pixel) {
		throw new UnsupportedOperationException("The paginated table view cannot scroll to pixel");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXPaginatedTableViewSkin<>(this, rowsFlow);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public int getCurrentPage() {
		return currentPage.get();
	}

	/**
	 * Specifies the current shown page.
	 */
	public IntegerProperty currentPageProperty() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage.set(currentPage);
	}

	public int getMaxPage() {
		return maxPage.get();
	}

	/**
	 * Specifies the last page index.
	 */
	public ReadOnlyIntegerProperty maxPageProperty() {
		return maxPage.getReadOnlyProperty();
	}

	protected void setMaxPage(int maxPage) {
		this.maxPage.set(maxPage);
	}

	public int getPagesToShow() {
		return pagesToShow.get();
	}

	/**
	 * Specifies how many pages can be shown at a time by the
	 * {@link MFXPagination} control used in the skin.
	 */
	public IntegerProperty pagesToShowProperty() {
		return pagesToShow;
	}

	public void setPagesToShow(int pagesToShow) {
		this.pagesToShow.set(pagesToShow);
	}

	public int getRowsPerPage() {
		return rowsPerPage.get();
	}

	/**
	 * Specifies how many rows the table can show per page.
	 */
	public IntegerProperty rowsPerPageProperty() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage.set(rowsPerPage);
	}
}
