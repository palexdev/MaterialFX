# Table Views

## Preview

<img src="https://imgur.com/nj6xhUT.gif" alt="Tableviews">

## MFXTableViews

- Style Class: mfx-table-view

- Default Stylesheet: MFXTableView.css

- Default Skin: MFXTableViewSkin.java

- Default Cell: MFXTableRow.java

- Default Columns: MFXTableColumn.java

### Properties

| Property               | Description                                                                                                                                                                                                                                                                                                                             | Type                     |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------:|
| virtualFlowInitialized | Useful property to inform that the table layout has been initialized/is ready.<br/>For example it is used by the autosizeColumnsOnInitialization() method to autosize the columns before the table is even laid out by using a listener.<br/>It is considered initialized as soon as the SimpleVirtualFlow retrieves the cells' height. | Boolean                  |
| items                  | Specifies the table's ObservableList containing the items.                                                                                                                                                                                                                                                                              | ObservableList           |
| selectionModel         | The model holding the table's selection                                                                                                                                                                                                                                                                                                 | IMultipleSelectionModel  |
| tableColumns           | The list containing the table's columns                                                                                                                                                                                                                                                                                                 | ObservableList           |
| tableRowFactory        | Specifies the Function used to generate the table rows                                                                                                                                                                                                                                                                                  | Function                 |
| transformableList      | This is the list on which filtering and sorting are made. The original list remains untouched!                                                                                                                                                                                                                                          | TransformableListWrapper |
| filters                | The list containing the filters' information used by the MFXFilterPane to filter the table                                                                                                                                                                                                                                              | ObservableList           |
| footerVisible          | Specifies whether the table's footer is visible                                                                                                                                                                                                                                                                                         | Boolean                  |

### CSS Selectors

- .mfx-table-view

- .mfx-table-view .columns-container

- .mfx-table-view .columns-container .mfx-table-column

- .mfx-table-view .virtual-flow

- .mfx-table-view .virtual-flow .scrollbar

- .mfx-table-view .virtual-flow .mfx-table-row

- .mfx-table-view .default-footer

- .mfx-table-view .default-footer .mfx-icon-wrapper

- .mfx-table-view .default-footer .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-table-view .default-footer .mfx-icon-wrapper .mfx-font-icon

## MFXPaginatedTableView

- Style Class: mfx-paginated-table-view

- Default Stylesheet: MFXTableView.css

- Default Skin: MFXPaginatedTableViewSkin.java

- Default Cell: MFXTableRow.java

- Default Columns: MFXTableColumn.java

### Properties

*In addition to the properties inherithed by MFXTableView:*

| Property    | Description                                                               | Type    |
| ----------- | ------------------------------------------------------------------------- | -------:|
| currentPage | Specifies the current shown page                                          | Integer |
| maxPage     | Specifies the last page index                                             | Integer |
| pagesToShow | Specifies how many pages can be shown at a time by the pagination control | Integer |
| rowsPerPage | Specifies how many rows the table can show per page                       | Integer |

### CSS Selectors

*In addition to the CSS selectors of MFXTableView:*

- .mfx-paginated-table-view .default-footer .mfx-pagination (to reach the pagination control)

<br/><br/>

## Cells/Sub-Components used by MFXTableViews

## MFXTableRow

- Style Class: mfx-table-row

- Default Stylesheet: MFXTableView.css

### Properties

| Property | Description                                        | Type           |
| -------- | -------------------------------------------------- | --------------:|
| cells    | The row's cells as an unmodifiable observable list | ObservableList |
| index    | Specifies the row's index in the SimpleVirtualFlow | Integer        |
| data     | Specifies the item represented by the row          | T[Generic]     |
| selected | Specifies the selection state of the row           | Boolean        |

### CSS Selectors

- .mfx-table-row

- .mfx-table-row .mfx-ripple-generator

- .mfx-table-row .mfx-table-row-cell

## MFXTableRowCell

- Style Class: mfx-table-row-cell

- Default Stylesheet: MFXTableView.css

- Default Skin: MFXTableRowCellSkin.java

### Properties

| Property        | Description                                                                                                                                                                                                                                                  | Type            |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------:|
| extractor       | The function used by the cell to extract from a generic table item T, the cell's data E.<br/>Example: the table contains a list of Cities, the second column represents the city's population, the function extracts from a City object the population field | Function        |
| converter       | The StringConverter used to convert the extracted E field to a String, which will then be the cell's text                                                                                                                                                    | StringConverter |
| leadingGraphic  | Specifies the cell's leading node                                                                                                                                                                                                                            | Node            |
| trailingGraphic | Specifies the cell's trailing node                                                                                                                                                                                                                           | Node            |

### CSS Selectors

- .mfx-table-row-cell

- .mfx-table-row-cell .label (not really needed since it's bound to the row cell)

## MFXTableColumn

- Style Class: mfx-table-column

- Default Stylesheet: MFXTableView.css

- Default Skin: MFXTableColumnSkin.java

### Properties

| Property        | Description                                                                    | Type            |
| --------------- | ------------------------------------------------------------------------------ | ---------------:|
| rowCellFactory  | Specifies the Function used to build the row's cells                           | Function        |
| sortState       | Specifies the sort state of the column, can be UNSORTED, ASCENDING, DESCENDING | SortState[Enum] |
| comparator      | Specifies the Comparator}used to sort the column                               | Comparator      |
| dragged         | Specifies whether the column is being dragged                                  | Boolean         |
| columnResozable | Specifies whether the column can be resized                                    | Boolean         |

### CSS Selectors

- .mfx-table-column

- .mfx-table-column .laber (not really needed since it's bound to the table column)

- .mfx-table-column .mfx-icon-wrapper (sort icon container)

- .mfx-table-column .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-table-column .mfx-icon-wrapper .mfx-font-icon (the actual sort icon)
