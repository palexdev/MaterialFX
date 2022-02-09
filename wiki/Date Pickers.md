# Date Pickers

## Preview

<img src="https://imgur.com/J3v3i9w.gif" alt="Pickers">

## MFXDatePickers

- Style Class: mfx-date-picker

- Default Stylesheet: MFXDatePicker.css

- Default Skin: MFXDatePickerSkin.java

- Default Cell: MFXDateCell.java

### Properties

| Property                   | Description                                                                                                                                                              | Type        |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -----------:|
| showing                    | Specifies whether the popup is showing                                                                                                                                   | Boolean     |
| popupAlignment             | Specifies the popup's position                                                                                                                                           | Alignment   |
| popupOffsetX               | Specifies the popup's x offset, the amount of pixels to add to the computed x position (from popupAlignment)                                                             | Double      |
| popupOffsetY               | Specifies the popup's y offset, the amount of pixels to add to the computed y position (from popupAlignment)                                                             | Double      |
| value                      | Specifies the current selected date                                                                                                                                      | LocalDate   |
| converterSupplier          | Specifies the Supplier used to create a StringConverter capable of converting LocalDates to a String                                                                     | Supplier    |
| monthConverterSupplier     | Specifies the Supplier used to create a  StringConverter capable of converting Months to a String                                                                        | Supplier    |
| dayOfWeekConverterSupplier | Specifies the Supplier used to create a  StringConverter capable of converting DayOfWeeks to a String                                                                    | Supplier    |
| cellFactory                | Specifies the function used to create the day cells in the grid                                                                                                          | Function    |
| onCommit                   | Specifies the action to perform when pressing the Enter button on the combo box                                                                                          | Consumer    |
| onCancel                   | Specifies the action to perform when pressing the key combination Ctrl+Shift+Z on the combo box                                                                          | Consumer    |
| locale                     | Specifies the Locale used by the date picker.<br/>The Locale is mainly responsible for changing the language and the grid disposition (different week start for example) | Locale      |
| currentDate                | Specifies the current date                                                                                                                                               | LocalDate   |
| yearsRange                 | Specifies the years range of the date picker                                                                                                                             | NumberRange |
| gridAlgorithm              | Specifies the BiFunction used to generate the month grid which is a bi-dimensional array of integer values                                                               | BiFunction  |
| startingYearMonth          | The YearMonth at which the date picker will start.<br/> Note that this will be relevant only for the first initialization. Setting this afterwards won't take any effect | YearMonth   |
| closePopupOnChange         | Whether the popup should stay open on value change or close                                                                                                              | Boolean     |

### CSS Selectors

- .mfx-date-picker

- .mfx-date-picker .mfx-icon-wrapper (contains the icon)

- .mfx-date-picker .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-date-picker .mfx-icon-wrapper .mfx-font-icon (the actual icon)

- .mfx-date-picker .date-picker-popup (to access the popup)

- .mfx-date-picker .date-picker-popup .content (top container, should not be necessary, will be omitted in the following)

- .mfx-date-picker .date-picker-popup .left-arrow (icon container)

- .mfx-date-picker .date-picker-popup .left-arrow .mfx-ripple-generator

- .mfx-date-picker .date-picker-popup .left-arrow .mfx-font-icon (the actual icon)

- .mfx-date-picker .date-picker-popup .right-arrow (icon container)

- .mfx-date-picker .date-picker-popup .right-arrow .mfx-ripple-generator

- .mfx-date-picker .date-picker-popup .right-arrow .mfx-font-icon (the actual icon)

- .mfx-date-picker .date-picker-popup .months-combo

- .mfx-date-picker .date-picker-popup .years-combo

- .mfx-date-picker .date-picker-popup .week-day

- .mfx-date-picker .date-picker-popup .mfx-date-cell
