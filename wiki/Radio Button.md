# Radio Button

## Preview

<img src="https://imgur.com/ArUhH58.gif" alt="Checkboxes">

## MFXRadioButtons

- Style Class: mfx-radio-button

- Default Stylesheet: MFXRadioButton.css

- Default Skin: MFXRadioButtonSkin.java

### Styleable Properties

| Property           | Description                                                                                                                                                                                                                                                                                                                                                                                                            | CSS Property             | Type                 | Default Value       |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ | --------------------:| -------------------:|
| contentDisposition | Specifies how the control is positioned relative to its text                                                                                                                                                                                                                                                                                                                                                           | -mfx-content-disposition | ContentDisplay[Enum] | ContentDisplay.LEFT |
| gap                | Specifies the spacing between the control and its text                                                                                                                                                                                                                                                                                                                                                                 | -mfx-gap                 | Double               | 8.0                 |
| radioGap           | Specifies the gap between the outer and the inner circles of the radio button                                                                                                                                                                                                                                                                                                                                          | -mfx-radio-gap           | Double               | 3.5                 |
| radius             | Specifies the radius of the radio button                                                                                                                                                                                                                                                                                                                                                                               | -mfx-radius              | Double               | 8.0                 |
| textExpand         | When setting a specific size for the control (by using setPrefSize for example, and this is true for SceneBuilder too), this flag will tell the control's label to take all the space available. This allows, in combination with the contentDisposition to layout the control's content in many interesting ways. When the text is expanded (this property is true), use the alignment property to position the text. | -mfx-text-expand         | Boolean              | false               |

### CSS Selectors

- .mfx-radio-button

- .mfx-radio-button .label (the radio's text, should not be necessary as bound to the radio button)

- .mfx-radio-button .mfx-ripple-generator

- .mfx-radio-button .radio

- .mfx-radio-button .dot
