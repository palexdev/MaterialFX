# Check Boxes

## Preview

<img src="https://imgur.com/ArUhH58.gif" alt="Checkboxes">

## MFXCheckBoxes

- Style Class: mfx-checkbox

- Default Stylesheet: MFXCheckBox.css

- Default Skin: MFXCheckboxSkin.java

### Styleable Properties

| Property           | Description                                                                                                                                                                                                                                                                                                                                                                                                            | CSS Property             | Type                 | Default Value       |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ | --------------------:| -------------------:|
| contentDisposition | Specifies how the control is positioned relative to its text                                                                                                                                                                                                                                                                                                                                                           | -mfx-content-disposition | ContentDisplay[Enum] | ContentDisplay.LEFT |
| gap                | Specifies the spacing between the control and its text                                                                                                                                                                                                                                                                                                                                                                 | -mfx-gap                 | Double               | 8.0                 |
| textExpand         | When setting a specific size for the control (by using setPrefSize for example, and this is true for SceneBuilder too), this flag will tell the control's label to take all the space available. This allows, in combination with the contentDisposition to layout the control's content in many interesting ways. When the text is expanded (this property is true), use the alignment property to position the text. | -mfx-text-expand         | Boolean              | false               |

### CSS Selectors

- .mfx-checkbox

- .mfx-checkbox .label (not really needed as bound to the checkbox)

- .mfx-checkbox .ripple-container (contains the ripple generator and the box)

- .mfx-checkbox .ripple-container .mfx-ripple-generator

- .mfx-checkbox .ripple-container .box (mark container)

- .mfx-checkbox .ripple-container .box .mark (the check mark icon)
  
  *Note that you should be able to reach the box and the mark without actually using the .ripple-container selector*
