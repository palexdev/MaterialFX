# Filter Pane

## MFXFilterPanes

- Style Class: mfx-filter-pane

- Default Stylesheet: MFXFilterPane.css

- Default Skin: MFXFilterPaneSkin.java

### Properties

| Property      | Description                                                                                              | Type           |
| ------------- | -------------------------------------------------------------------------------------------------------- | --------------:|
| headerText    | Specifies the text of the header                                                                         | String         |
| filters       | The list of AbstractFilters.<br/>Each of them represents an object's field on  which the filter operates | ObservableList |
| activeFilters | The list of built filters                                                                                | ObservableList |
| onFilter      | The action to perform when the filter icon is clicked                                                    | EventHandler   |
| onReset       | The action to perform when the reset icon is clicked                                                     | EventHandler   |

### CSS Selectors

- .mfx-filter-pane

- .mfx-filter-pane .header

- .mfx-filter-pane .header-label

- .mfx-filter-pane .header #filterIcon (includes ripple generator, actual icon is at .mfx-font-icon)

- .mfx-filter-pane .header #resetIcon (includes ripple generator, actual icon is at .mfx-font-icon)

- .mfx-filter-pane .filter-combo

- .mfx-filter-pane .predicates-combo

- .mfx-filter-pane .mfx-combo-box

- .mfx-filter-pane .mfx-text-field

- .mfx-filter-pane .mfx-button

- .mfx-filter-pane .mfx-scroll-pane

- .mfx-filter-pane .active-filter

- .mfx-filter-pane .active-filter .mfx-font-icon

- .mfx-filter-pane .active-filter .function-text

- .mfx-filter-pane .and-or-text
