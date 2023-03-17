package io.github.palexdev.materialfx.css.themes;

import io.github.palexdev.materialfx.MFXResourcesLoader;

/**
 * Enumerator implementing {@link Theme} that exposes all the single stylesheets part of MaterialFX relative to each single
 * control.
 * <p></p>
 * Makes use of the cache offered by {@link Theme}, subsequent loads of the same theme will be faster.
 */
public enum Stylesheets implements Theme {
	BUTTON("MFXButton.css"),
	CHECKBOX("MFXCheckBox.css"),
	CHECK_LIST_CELL("MFXCheckListCell.css"),
	CHECK_LIST_VIEW("MFXCheckListView.css"),
	CHECK_TREE_CELL("MFXCheckTreeCell.css"),
	CIRCLE_TOGGLE_NODE("MFXCircleToggleNode.css"),
	COMBO_BOX("MFXComboBox.css"),
	COMBO_BOX_CELL("MFXComboBoxCell.css"),
	CONTEXT_MENU("MFXContextMenu.css"),
	CONTEXT_MENU_ITEM("MFXContextMenuItem.css"),
	DATE_CELL("MFXDateCell.css"),
	DATE_PICKER("MFXDatePicker.css"),
	DIALOGS("MFXDialogs.css"),
	FILTER_COMBO_BOX("MFXFilterComboBox.css"),
	FILTER_DIALOG("MFXFilterDialog.css"),
	FILTER_PANE("MFXFilterPane.css"),
	LIST_CELL("MFXListCell.css"),
	LIST_VIEW("MFXListView.css"),
	MAGNIFIER("MFXMagnifier.css"),
	NOTIFICATION_CENTER("MFXNotificationCenter.css"),
	PAGINATION("MFXPagination.css"),
	PASSWORD_FIELD("MFXPasswordField.css"),
	PROGRESS_BAR("MFXProgressBar.css"),
	PROGRESS_SPINNER("MFXProgressSpinner.css"),
	RADIO_BUTTON("MFXRadioButton.css"),
	RECTANGLE_TOGGLE_NODE("MFXRectangleToggleNode.css"),
	SCROLL_PANE("MFXScrollPane.css"),
	SLIDER("MFXSlider.css"),
	SPINNER("MFXSpinner.css"),
	STEPPER("MFXStepper.css"),
	STEPPER_TOGGLE("MFXStepperToggle.css"),
	TABLE_VIEW("MFXTableView.css"),
	TEXT_FIELD("MFXTextField.css"),
	TOGGLE_BUTTON("MFXToggleButton.css"),
	TOOLTIP("MFXTooltip.css"),
	TREE_CELL("MFXTreeCell.css"),
	TREE_ITEM("MFXTreeItem.css"),
	TREE_VIEW("MFXTreeView.css"),
	;

	private final String stylesheet;

	Stylesheets(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	@Override
	public String getTheme() {
		return stylesheet;
	}

	@Override
	public String loadTheme() {
		if (Helper.isCached(this)) return Helper.getCachedTheme(this);
		return Helper.cacheTheme(this, MFXResourcesLoader.load(mfxBaseDir() + getTheme()));
	}
}
