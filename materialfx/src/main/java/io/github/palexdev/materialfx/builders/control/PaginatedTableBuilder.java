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

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;

public class PaginatedTableBuilder<T> extends TableBuilder<T, MFXPaginatedTableView<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public PaginatedTableBuilder() {
		this(new MFXPaginatedTableView<>());
	}

	public PaginatedTableBuilder(MFXPaginatedTableView<T> tableView) {
		super(tableView);
	}

	public static <T> PaginatedTableBuilder<T> paginatedTable() {
		return new PaginatedTableBuilder<>();
	}

	public static <T> PaginatedTableBuilder<T> paginatedTable(MFXPaginatedTableView<T> tableView) {
		return new PaginatedTableBuilder<>(tableView);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public PaginatedTableBuilder<T> goToPage(int index) {
		node.goToPage(index);
		return this;
	}

	public PaginatedTableBuilder<T> setCurrentPage(int currentPage) {
		node.setCurrentPage(currentPage);
		return this;
	}

	public PaginatedTableBuilder<T> setPagesToShow(int pagesToShow) {
		node.setPagesToShow(pagesToShow);
		return this;
	}

	public PaginatedTableBuilder<T> setRowsPerPage(int rowsPerPage) {
		node.setRowsPerPage(rowsPerPage);
		return this;
	}
}
