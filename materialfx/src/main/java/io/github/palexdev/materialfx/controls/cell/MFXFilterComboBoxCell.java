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
