# Toggles

## Preview

<img src="https://imgur.com/ArUhH58.gif" alt="Checkboxes">

## MFXToggleButtons

- Style Class: mfx-toggle-button

- Default Stylesheet: MFXToggleButton.css

- Default Skin: MFXToggleButtonSkin.java

### Properties

| Property    | Description                                                              | Type         |
| ----------- | ------------------------------------------------------------------------ | ------------:|
| toggleGroup | Specifies the ToggleGroup to which this Toggle belongs                   | ToggleGroup  |
| selected    | Specifies the selection state for the Toggle                             | Boolean      |
| onAction    | Specifies the action to perform when the toggle' selection state changes | EventHandler |

### Styleable Properties

| Property           | Description                                                                                                                                                                                                                                                                                                                                                                                                            | CSS Property             | Type           | Default Value       |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ | --------------:| -------------------:|
| contentDisposition | Specifies how the control is positioned relative to its text                                                                                                                                                                                                                                                                                                                                                           | -mfx-content-disposition | ContentDisplay | ContentDisplay.LEFT |
| gap                | Specifies the spacing between the control and its text                                                                                                                                                                                                                                                                                                                                                                 | -mfx-gap                 | Double         | 8.0                 |
| length             | Specifies the length of the toggle button's line                                                                                                                                                                                                                                                                                                                                                                       | -mfx-length              | Double         | 36.0                |
| radius             | Specifies the radius of the toggle button's circle                                                                                                                                                                                                                                                                                                                                                                     | -mfx-radius              | Double         | 10.0                |
| textExpand         | When setting a specific size for the control (by using setPrefSize for example, and this is true for SceneBuilder too), this flag will tell the control's label to take all the space available. This allows, in combination with the contentDisposition to layout the control's content in many interesting ways. When the text is expanded (this property is true), use the alignment property to position the text. | -mfx-text-expand         | Boolean        | false               |

### CSS Selectors

- .mfx-toggle-button

- .mfx-toggle-button .label (not really needed as bound to the toggle)

- .mfx-toggle-button .mfx-ripple-generator

- .mfx-toggle-button .line (the toggle's line)

- .mfx-toggle-button .circle (the toggle's circle)

### PseudoClasses

| PseudoClass | Description                                       |
| ----------- | ------------------------------------------------- |
| ":selected" | Allows to customize the toggle when it's selected |

## MFXCircleToggleNodes

- Style Class: mfx-circle-toggle-node

- Default Stylesheet: MFXCircleToggleNode.css

- Default Skin: MFXCircleToggleNodeSkin.java

### Properties

| Property          | Description                         | Type |
| ----------------- | ----------------------------------- | ----:|
| labelLeadingIcon  | Specifies the label's leading icon  | Node |
| labelTrailingIcon | Specifies the label's trailing icon | Node |

### Styleable Properties

| Property     | Description                                                         | CSS Property       | Type               | Default Value       |
| ------------ | ------------------------------------------------------------------- | ------------------ | ------------------:| -------------------:|
| gap          | Specifies the gap between the toggle and its text                   | -mfx-gap           | Double             | 5.0                 |
| size         | Specifies the toggle's radius                                       | -mfx-size          | Double             | 32.0                |
| textPosition | Specifies the position of the label, above or underneath the toggle | -mfx-text-position | TextPosition[Enum] | TextPosition.BOTTOM |

### CSS Selectors

- .mfx-circle-toggle-node

- .mfx-circle-toggle-node .mfx-ripple-generator

- .mfx-circle-toggle-node .circle

- .mfx-circle-toggle-node .mfx-text-field (toggle's text, acts as a Label)

## MFXRectangleToggleNodes

- Style Class: mfx-rectangle-toggle-node

- Default Stylesheet: MFXRectangleToggleNode.css

- Default Skin: MFXRectangleToggleNodeSkin.java

### Properties

*This toggle has the same properties as MFXCircleToggleNode andbut no styleable properties*

### CSS Selectors

- .mfx-rectangle-toggle-node

- .mfx-rectangle-toggle-node .mfx-ripple-generator

- 

- .mfx-rectangle-toggle-node .mfx-text-field (toggle's text, acts as a Label)
