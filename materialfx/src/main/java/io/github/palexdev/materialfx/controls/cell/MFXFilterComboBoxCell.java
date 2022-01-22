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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.base.MFXCombo;

/**
 * Extends {@link MFXComboBoxCell} to modify the {@link #updateIndex(int)} method.
 */
public class MFXFilterComboBoxCell<T> extends MFXComboBoxCell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final TransformableList<T> filterList;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterComboBoxCell(MFXCombo<T> combo, TransformableList<T> filterList, T data) {
		super(combo, data);
		this.filterList = filterList;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * A filter combo box uses a {@link TransformableList} to display the filtered items
	 * in the list. The thing is, when items are filtered their index changes as well. For
	 * selection to work properly the index must be converted using {@link TransformableList#viewToSource(int)}.
	 */
	@Override
	public void updateIndex(int index) {
		super.updateIndex(filterList.viewToSource(index));
	}
}
