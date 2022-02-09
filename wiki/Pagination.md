# Pagination

## Preview

<img src="https://imgur.com/nj6xhUT.gif" alt="Tableviews">

## MFXPaginations

- Style Class: mfx-pagination

- Default Stylesheet: MFXPagination.css

- Default Skin: MFXPaginationSkin.java

- Default Cell: MFXPage.java

### Properties

| Property                   | Description                                                                                     | Type              |
| -------------------------- | ----------------------------------------------------------------------------------------------- | -----------------:|
| currentPage                | Specifies the current selected page                                                             | Integer           |
| maxPage                    | Specifies the max number of pages                                                               | Integer           |
| pagesToShow                | Specifies the max number of pages to show at a time                                             | Integer           |
| indexesSupplier            | This supplier specifies the algorithm used to build the pages                                   | Supplier          |
| pageCellFactory            | This function specifies how to convert an index to a page                                       | Function          |
| ellipseString              | Specifies the string to show for truncated pages                                                | String            |
| orientation                | Specifies the control's orientation                                                             | Orientation[Enum] |
| showPopupForTruncatedPages | Specifies whether truncated pages should show a popup containing the pages in between, on click | Boolean           |

### CSS Selectors

- .mfx-pagination

- .mfx-pagination .mfx-icon-wrapper (left/right arrows container)

- .mfx-pagination .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-pagination .mfx-icon-wrapper .mfx-font-icon (the actual left/right icons)

- .mfx-pagination .pages-bar (the pages container)

- .mfx-pagination .pages-bar .mfx-page

<br/>

<br/>

## MFXPages

- Style Class: mfx-page

- Default Stylesheet: MFXPagination.css

### Properties

| Property | Description                                            | Type        |
| -------- | ------------------------------------------------------ | -----------:|
| index    | Specifies the page's index                             | Integer     |
| between  | The range of hidden pages, if truncated otherwise null | NumberRange |
| selected | Specifies the selection state of the page              | Boolean     |

### CSS Selectors

- .mfx-page

- .mfx-page .pages-popup

- .mfx-page .pages-popup .virtual-flow

- .mfx-page .pages-popup .mfx-list-view

- .mfx-page .pages-popup .virtual-flow .mfx-list-cell
