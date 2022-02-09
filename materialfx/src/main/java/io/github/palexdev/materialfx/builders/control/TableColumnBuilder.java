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

import io.github.palexdev.materialfx.builders.base.LabeledBuilder;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.SortState;

import java.util.Comparator;
import java.util.function.Function;

public class TableColumnBuilder<T> extends LabeledBuilder<MFXTableColumn<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public TableColumnBuilder() {
		this(new MFXTableColumn<>());
	}

	public TableColumnBuilder(MFXTableColumn<T> column) {
		super(column);
	}

	public static <T> TableColumnBuilder<T> tableColumn() {
		return new TableColumnBuilder<>();
	}

	public static <T> TableColumnBuilder<T> tableColumn(MFXTableColumn<T> column) {
		return new TableColumnBuilder<>(column);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public TableColumnBuilder<T> setRowCellFactory(Function<T, MFXTableRowCell<T, ?>> rowCellFactory) {
		node.setRowCellFactory(rowCellFactory);
		return this;
	}

	public TableColumnBuilder<T> setSortState(SortState sortState) {
		node.setSortState(sortState);
		return this;
	}

	public TableColumnBuilder<T> setComparator(Comparator<T> comparator) {
		node.setComparator(comparator);
		return this;
	}

	public TableColumnBuilder<T> setColumnResizable(boolean columnResizable) {
		node.setColumnResizable(columnResizable);
		return this;
	}
}
