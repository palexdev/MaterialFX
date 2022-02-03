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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.cell.MFXPage;
import javafx.geometry.Orientation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class PaginationBuilder extends ControlBuilder<MFXPagination> {

	//================================================================================
	// Constructors
	//================================================================================
	public PaginationBuilder() {
		this(new MFXPagination());
	}

	public PaginationBuilder(MFXPagination control) {
		super(control);
	}

	public static PaginationBuilder pagination() {
		return new PaginationBuilder();
	}

	public static PaginationBuilder pagination(MFXPagination pagination) {
		return new PaginationBuilder(pagination);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public PaginationBuilder setCurrentPage(int currentPage) {
		node.setCurrentPage(currentPage);
		return this;
	}

	public PaginationBuilder setMaxPage(int maxPage) {
		node.setMaxPage(maxPage);
		return this;
	}

	public PaginationBuilder setPagesToShow(int pagesToShow) {
		node.setPagesToShow(pagesToShow);
		return this;
	}

	public PaginationBuilder setIndexesSupplier(Supplier<List<Integer>> indexesSupplier) {
		node.setIndexesSupplier(indexesSupplier);
		return this;
	}

	public PaginationBuilder setPageCellFactory(Function<Integer, MFXPage> pageCellFactory) {
		node.setPageCellFactory(pageCellFactory);
		return this;
	}

	public PaginationBuilder setEllipseString(String ellipseString) {
		node.setEllipseString(ellipseString);
		return this;
	}

	public PaginationBuilder setOrientation(Orientation orientation) {
		node.setOrientation(orientation);
		return this;
	}

	public PaginationBuilder setShowPopupForTruncatedPages(boolean showPopupForTruncatedPages) {
		node.setShowPopupForTruncatedPages(showPopupForTruncatedPages);
		return this;
	}
}
