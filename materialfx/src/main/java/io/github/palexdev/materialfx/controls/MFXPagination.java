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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.controls.cell.MFXPage;
import io.github.palexdev.materialfx.skins.MFXPaginationSkin;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is the implementation of a smart, material pagination control in JavaFX.
 * <p>
 * There are three main properties:
 * <p> - the current page property that specifies the current selected page
 * <p> - the max page property that specifies the number of pages
 * <p> - the pages to show property that specifies how many pages can be shown at a time by the control
 * <p></p>
 * {@code MFXPagination} is highly customizable.
 * <p>
 * The {@link #indexesSupplierProperty()} allows you to specify how to build the page indexes. The default
 * algorithm, set by {@link #defaultIndexesSupplier()}, is a digg-style pagination.
 * <p>
 * The {@link #pageCellFactoryProperty()} allows you to specify how to build the pages' button.
 * <p>
 * The {@link #ellipseStringProperty()} allows you to specify the string shown on the truncated pages.
 * <p>
 * The {@link #showPopupForTruncatedPagesProperty()} allows you to decide whether to show a little popup
 * when clicking ona truncated page, which contains a list of the pages in between, for faster navigation.
 * The popup in created and handled by the default cells, {@link MFXPage}.
 * <p>
 * The {@link #orientationProperty()} allows you to specify the pagination orientation.
 */
public class MFXPagination extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-pagination";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXPagination.css");

	private final IntegerProperty currentPage = new SimpleIntegerProperty(1) {
		@Override
		public void set(int newValue) {
			int number = NumberUtils.clamp(newValue, 1, getMaxPage());
			super.set(number);
		}
	};
	private final IntegerProperty maxPage = new SimpleIntegerProperty();
	private final IntegerProperty pagesToShow = new SimpleIntegerProperty();
	private final SupplierProperty<List<Integer>> indexesSupplier = new SupplierProperty<>();
	private final FunctionProperty<Integer, MFXPage> pageCellFactory = new FunctionProperty<>(index -> new MFXPage(this, index));
	private final StringProperty ellipseString = new SimpleStringProperty("...");
	private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);
	private final BooleanProperty showPopupForTruncatedPages = new SimpleBooleanProperty(false);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPagination() {
		this(0);
	}

	public MFXPagination(int maxPage) {
		this(maxPage, 9);
	}

	public MFXPagination(int maxPage, int toShow) {
		setMaxPage(maxPage);
		setPagesToShow(toShow);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		defaultIndexesSupplier();
	}

	/**
	 * Sets the default indexing algorithm.
	 */
	public void defaultIndexesSupplier() {
		setIndexesSupplier(this::computePagesIndex);
	}

	/**
	 * This is the default algorithm used to build the pages.
	 * <p>
	 * This is a digg-style pagination algorithm.
	 * <p></p>
	 * There are some exception that won't make this work as expected.
	 * If the number of pages is lesser than the specified number of pages to show,
	 * or the specified number of pages to show is lesser than 5, all the indexes
	 * will be shown.
	 */
	protected List<Integer> computePagesIndex() {
		List<Integer> indexes = new ArrayList<>();

		int current = getCurrentPage();
		int nPages = getMaxPage();
		int toShow = getPagesToShow();

		if (nPages < toShow || toShow < 5) {
			for (int i = 1; i <= nPages; i++) {
				indexes.add(i);
			}
			return indexes;
		}

		int middle = (int) Math.ceil(toShow / 2.0);
		if (current < middle) {
			int end = NumberUtils.isEven(toShow) ? middle + 1 : middle;
			for (int i = 1; i < end; i++) {
				indexes.add(i);
			}
			indexes.add(-1);
			int remaining = toShow - indexes.size();
			for (int i = nPages - remaining + 1; i <= nPages; i++) {
				indexes.add(i);
			}
		} else if (current <= nPages - middle + 1) {
			indexes.add(1);
			indexes.add(-1);
			indexes.addAll(computeMiddleIndexes(current, toShow));
			indexes.add(-1);
			indexes.add(nPages);
		} else {
			for (int i = 1; i < middle; i++) {
				indexes.add(i);
			}
			indexes.add(-1);
			int remaining = toShow - indexes.size();
			for (int i = nPages - remaining + 1; i <= nPages; i++) {
				indexes.add(i);
			}
		}
		return indexes;
	}

	/**
	 * Helper method to {@link #computePagesIndex()} to properly compute
	 * the page indexes when the current page is in the middle.
	 */
	protected List<Integer> computeMiddleIndexes(int current, int toShow) {
		int remaining = toShow - 5;
		List<Integer> indexes = new LinkedList<>();
		indexes.add(current);

		int lastMin = current - 1;
		int lastMax = current + 1;
		boolean after = true;
		while (remaining > 0) {
			if (after) {
				indexes.add(lastMax);
				lastMax++;
				after = false;
			} else {
				indexes.add(0, lastMin);
				lastMin--;
				after = true;
			}
			remaining--;
		}
		return indexes;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXPaginationSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public int getCurrentPage() {
		return currentPage.get();
	}

	/**
	 * Specifies the current selected page.
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
	 * Specifies the max number of pages.
	 */
	public IntegerProperty maxPageProperty() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage.set(maxPage);
	}

	public int getPagesToShow() {
		return pagesToShow.get();
	}

	/**
	 * Specifies the max number of pages to show at a time.
	 */
	public IntegerProperty pagesToShowProperty() {
		return pagesToShow;
	}

	public void setPagesToShow(int pagesToShow) {
		this.pagesToShow.set(pagesToShow);
	}

	public Supplier<List<Integer>> getIndexesSupplier() {
		return indexesSupplier.get();
	}

	/**
	 * This supplier specifies the algorithm used to build the pages.
	 */
	public SupplierProperty<List<Integer>> indexesSupplierProperty() {
		return indexesSupplier;
	}

	public void setIndexesSupplier(Supplier<List<Integer>> indexesSupplier) {
		this.indexesSupplier.set(indexesSupplier);
	}

	public Function<Integer, MFXPage> getPageCellFactory() {
		return pageCellFactory.get();
	}

	/**
	 * This function specifies how to convert an index to a page.
	 */
	public FunctionProperty<Integer, MFXPage> pageCellFactoryProperty() {
		return pageCellFactory;
	}

	public void setPageCellFactory(Function<Integer, MFXPage> pageCellFactory) {
		this.pageCellFactory.set(pageCellFactory);
	}

	public String getEllipseString() {
		return ellipseString.get();
	}

	/**
	 * Specifies the string to show for truncated pages.
	 */
	public StringProperty ellipseStringProperty() {
		return ellipseString;
	}

	public void setEllipseString(String ellipseString) {
		this.ellipseString.set(ellipseString);
	}

	public Orientation getOrientation() {
		return orientation.get();
	}

	/**
	 * Specifies the control's orientation.
	 */
	public ObjectProperty<Orientation> orientationProperty() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}

	public boolean isShowPopupForTruncatedPages() {
		return showPopupForTruncatedPages.get();
	}

	/**
	 * Specifies whether truncated pages should show a popup
	 * containing the pages in between, on click.
	 */
	public BooleanProperty showPopupForTruncatedPagesProperty() {
		return showPopupForTruncatedPages;
	}

	public void setShowPopupForTruncatedPages(boolean showPopupForTruncatedPages) {
		this.showPopupForTruncatedPages.set(showPopupForTruncatedPages);
	}
}
