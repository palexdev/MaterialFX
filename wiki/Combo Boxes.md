# Combo Boxes

## Preview

<img src="https://imgur.com/BO0twpA.gif" alt="Comboboxes">

## MFXComboBoxes

- Style Class: mfx-combo-box

- Default Stylesheet: MFXComboBox.css

- Default Skin: MFXComboBoxSkin.java

- Default Cell: MFXComboBoxCell.java

### Properties

| Property          | Description                                                                                                                             | Type                  |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ---------------------:|
| showing           | Specifies whether the popup is showing                                                                                                  | Boolean               |
| popupAlignment    | Specifies the popup's position                                                                                                          | Alignment             |
| popupOffsetX      | Specifies the popup's x offset, the amount of pixels to add to the computed x position (from popupAlignment)                            | Double                |
| popupOffsetY      | Specifies the popup's y offset, the amount of pixels to add to the computed y position (from popupAlignment)                            | Double                |
| animationProvider | Specifies the animation of the trailing icon used to open the popup                                                                     | BiFunction            |
| value             | Specifies the combo box's value, which does not necessarily coincides with the currently selected item                                  | T[Generic]            |
| converter         | Specifies the StringConverter used to convert a generic item to a String. It is used to set the combo box text when an item is selected | StringConverter       |
| items             | Specifies the combo box's items list                                                                                                    | ObservableList        |
| selectionModel    | The model holding the combo box's selection                                                                                             | ISingleSelectionModel |
| cellFactory       | Specifies the function used to create the items cells in the popup                                                                      | Function              |
| onCommit          | Specifies the action to perform when pressing the Enter button on the combo box                                                         | Consumer              |
| onCancel          | Specifies the action to perform when pressing the key combination Ctrl+Shift+Z on the combo box                                         | Consumer              |

### Styleable Properties

| Property     | Description                                                                              | CSS Property        | Type    | Default Value |
| ------------ | ---------------------------------------------------------------------------------------- | ------------------- | -------:| -------------:|
| scrollOnOpen | Specifies whether the combo box list should scroll to the current selected value on open | -mfx-scroll-on-open | Boolean | false         |

### CSS Selectors

- .mfx-combo-box

- .mfx-combo-box .caret (the icon to open/close the popup)

- .mfx-combo-box .combo-popup (to access the combo box's popup)

- .mfx-combo-box .combo-popup .virtual-flow (the combo box's cells container)

- .mfx-combo-box .combo-popup .virtual-flow .scrollbar

- .mfx-combo-box .combo-popup .virtual-flow .mfx-combo-box-cell

- .mfx-combo-box .combo-popup .virtual-flow .data-label (the cell's text)

  *There are other selectors, but they are the same as MFXTextField, refer to its wiki*

### PseudoClasses

| PseudoClass | Description                                                                                                                                 |
| ----------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| :popup      | Activated when the popup is shown and deactivated when the popup is hidden.<br/>Allows to customize the combo box when the popup is showing |

*Also inherits all new PseudoClasses from MFXTextField since it extends it*

## MFXFilterComboBoxes

- Style Class: mfx-filter-combo-box

- Default Stylesheet: MFXFilterComboBox.css

- Default Skin: MFXFilterComboBoxSkin.java

- Default Cell: MFXFilterComboBoxCell.java (important for selection to work properly)

### Properties

*In addition to the properties inherited by MFXComboBox:*

| Property           | Description                                                                                                                              | Type              |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- | -----------------:|
| searchText         | Specifies the text used to filter the items list.  By default this text is bound bidirectionally with the text-field's used in the popup | String            |
| filterList         | This is the list on which filtering and sorting are made. The original list remains untouched!                                           | TransformableList |
| filterFunction     | Specifies the function used to build a Predicate from the search text, the predicate is then used to filter the list                     | Function          |
| resetOnPopupHidden | Specifies whether to reset the filter state, such as the searchText when the popup is closed                                             | Boolean           |

### Styleable Properties

*This combo box has the same styleable properties as MFXComboBox*

### CSS Selectors

*This combo box has the same CSS selectors as MFXComboBox, the only difference is that the base style class is
.mfx-filter-combo-box*

### PseudoClasses

*This combo box has the same new PseudoClasses as MFXComboBox*
