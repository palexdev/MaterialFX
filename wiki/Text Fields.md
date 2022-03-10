# Text Fields

## Preview

<img src="https://imgur.com/XT2iVU7.gif" alt="Fields">

## MFXTextFields

- Style Class: mfx-text-field

- Default Stylesheet: MFXTextField.css

- Default Skin: MFXTextFieldSkin.java

### Properties

| Property     | Description                                                                                                                                                                | Type    |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------:|
| selectable   | Specifies whether selection is allowed                                                                                                                                     | Boolean |
| leadingIcon  | Specifies the icon placed before the input field                                                                                                                           | Node    |
| trailingIcon | Specifies the icon placed after the input field                                                                                                                            | Node    |
| floatingText | Specifies the text of the floating text node                                                                                                                               | String  |
| floating     | Specifies if the floating text node is currently floating or not                                                                                                           | Boolean |
| measureUnit  | Specifies the unit of measure of the field.    <br/>This is useful of course when dealing with numeric fields that represent for example: weight, volume, length and so on... | String  |

### Styleable Properties

| Property        | Description                                                                                                                | CSS Property          | Type            | Default Value            |
| --------------- | -------------------------------------------------------------------------------------------------------------------------- | --------------------- | ---------------:| ------------------------:|
| allowEdit       | Specifies whether the field is editable                                                                                    | -mfx-editable         | Boolean         | true                     |
| animated        | Specifies whether the floating text positioning is animated                                                                | -mfx-animated         | Boolean         | true                     |
| borderGap       | For FloatMode.BORDER FloatMode.ABOVE modes, this specifies the distance from the control's x origin (padding not included) | -mfx-border-gap       | Double          | 10.0                     |
| caretVisible    | Specifies whether the caret should be visible                                                                              | -mfx-caret-visible    | Boolean         | true                     |
| floatMode       | Specifies how the floating text is positioned when floating.<br/>Can be: DISABLED, ABOVE, BORDER, INLINE                   | -mfx-float-mode       | FloatMode[Enum] | INLINE                   |
| floatingTextGap | For FloatMode.INLINE mode, this specifies the gap between the floating text node and the input field node                  | -mfx-gap              | Double          | 5.0                      |
| graphicTextGap  | Specifies the gap between the input field and the icons                                                                    | -fx-graphic-text-gap  | Double          | 10.0                     |
| measureUnitGap  | Specifies the gap between the field and the measure unit label                                                             | -mfx-measure-unit-gap | Double          | 5.0                      |
| scaleOnAbove    | Specifies whether the floating text node should be scaled or not when the float mode is set to FloatMode.ABOVE             | -mfx-scale-on-above   | Boolean         | false                    |
| textFill        | Specifies the text color                                                                                                   | -fx-text-fill         | Color           | Color.rgb(0, 0, 0, 0.87) |
| textLimit       | Specifies the maximum number of characters the field's text can have                                                       | -mfx-text-limit       | Integer         | -1(Unlimited)            |

### CSS Selectors

- .mfx-text-field

- .mfx-text-field .floating-text

- .mfx-text-field .text-field (to access the actual field node, should not be necessary)

## MFXPasswordFields

- Style Class: mfx-password-field

- Default Stylesheet: MFXPasswordField.css

- Default Skin: MFXTextFieldSkin.java

### Properties

*The password field has the same exact properties of MFXTextField*

### Styleable Properties

*In addition to the styleable properties inherited by MFXTextField:*

| Property      | Description                                                             | CSS Property        | Type                                                                               | Default Value                                         |
| ------------- | ----------------------------------------------------------------------- | ------------------- | ----------------------------------------------------------------------------------:| -----------------------------------------------------:|
| allowCopy     | Specifies if copying the password field text is allowed                 | -mfx-allow-copy     | Boolean                                                                            | false                                                 |
| allowCut      | Specifies if it's allowed to cut text from the password field           | -mfx-allow-cut      | Boolean                                                                            | false                                                 |
| allowPaste    | Specifies if it's allowed to paste text from the clipboard to the field | -mfx-allow-paste    | Boolean                                                                            | false                                                 |
| showPassword  | Specifies if the text should be un-masked to show the password          | -mfx-show-password  | Boolean                                                                            | false                                                 |
| hideCharacter | Specifies the character used to mask the text                           | -mfx-hide-character | String (can be only one character, the String will always be cut to one character) | BULLET (unicode, public constant of MFXPasswordField) |

### CSS Selectors

*In addition to the CSS selectors inherited by MFXTextField:*

- .mfx-password-field .mfx-icon-wrapper (eye icon container)

- .mfx-password-field .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-password-field .mfx-icon-wrapper .mfx-font-icon (the actual icon)
