# Lists

## Preview

<img src="https://imgur.com/4Ckdn5z.gif" alt="Listviews">

## MFXListViews

- Style Class: mfx-list-view

- Default Stylesheet: MFXListView.css

- Default Skin: MFXListViewSkin.java

- Default Cell: MFXListCell.java

### Properties

| Property        | Description                                                                                      | Type                    |
| --------------- | ------------------------------------------------------------------------------------------------ | -----------------------:|
| items           | The items list property                                                                          | ObservableList          |
| converter       | Specifies the StringConverter used to convert a generic item to a String, used by the list cells | StringConverter         |
| selectionModel  | The model holding the list's selection                                                           | IMultipleSelectionModel |
| cellFactory     | Specifies the function used to build the list's cells                                            | Function                |
| trackColor      | Specifies the color of the scrollbars' track                                                     | Paint                   |
| thumbColor      | Specifies the color of the scrollbars' thumb                                                     | Paint                   |
| thumbHoverColor | Specifies the color of the scrollbars' thumb when mouse hover                                    | Paint                   |
| hideAfter       | Specifies the time after which the scrollbars are hidden                                         | Duration                |

### Styleable Properties

| Property       | Description                                                                    | CSS Property         | Type             | Default Value     |
| -------------- | ------------------------------------------------------------------------------ | -------------------- | ----------------:| -----------------:|
| hideScrollBars | Specifies if the scrollbars should be hidden when the mouse is not on the list | -mfx-hide-scrollbars | Boolean          | false             |
| depthLevel     | Specifies the shadow strength around the control                               | -mfx-depth-level     | DepthLevel[Enum] | DepthLevel.LEVEL2 |

### CSS Selectors

- .mfx-list-view

- .mfx-list-view .virtual-flow

- .mfx-list-view .virtual-flow .scroll-bar

- .mfx-list-view .virtual-flow .mfx-list-cell

- .mfx-list-view .virtual-flow .mfx-list-cell .mfx-ripple-generator

- .mfx-list-view .virtual-flow .mfx-list-cell .data-label (cell's text)

## MFXCheckListViews

- Style Class: mfx-check-list-view

- Default Stylesheet: MFXCheckListView.css

- Default Skin: MFXListViewSkin.java

- Default Cell: MFXCheckListCell.java

### Properties and Styleable Properties

*This list view has the same exact properties and styleable properties as MFXListView.*

### CSS Selectors

- .mfx-check-list-view

- .mfx-check-list-view .virtual-flow

- .mfx-check-list-view .virtual-flow .scroll-bar

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .mfx-ripple-generator

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .data-label (cell's text)

- .mfx-check-list-view .virtual-flow .mfx-check-list-cell .mfx-checkbox
