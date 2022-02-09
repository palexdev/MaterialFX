# Progress

## Preview

<img src="https://imgur.com/2E6X3uJ.gif" alt="Progress">

## MFXProgressBars

- Style Class: mfx-progress-bar

- Default Stylesheet: MFXProgressBar.css

- Default Skin: MFXProgressBarSkin.java

### Properties

| Property | Description               | Type           |
| -------- | ------------------------- | --------------:|
| ranges1  | The first list of ranges  | ObservableList |
| ranges2  | The second list of ranges | ObservableList |
| ranges3  | The third list of ranges  | ObservableList |

### Styleable Properties

| Property       | Description                                 | CSS Property         | Type   | Default Value |
| -------------- | ------------------------------------------- | -------------------- | ------:| -------------:|
| animationSpeed | Specifies the indeterminate animation speed | -mfx-animation-speed | Double | 1.0           |

### CSS Selectors

- .mfx-progress-bar

- .mfx-progress-bar .track

- .mfx-progress-bar .bar1

- .mfx-progress-bar .bar2

## MFXProgressSpinners

- Style Class: mfx-progress-spinner

- Default Stylesheet: MFXProgressSpinner.css

- Default Skin: MFXProgressSpinnerSkin.java

### Properties

*MFXProgressSpinner has the same properties of MFXProgressBar*

### Styleable Properties

| Property      | Description                                         | CSS Property        | Type   | Default Value                               |
| ------------- | --------------------------------------------------- | ------------------- | ------:| -------------------------------------------:|
| color1        | Specifies the first color of the spinner arc        | -mfx-color1         | Color  | Color.web("#4285f4")                        |
| color2        | Specifies the second color of the spinner arc       | -mfx-color2         | Color  | Color.web("#db4437")                        |
| color3        | Specifies the third color of the spinner arc        | -mfx-color3         | Color  | Color.web("#f4b400")                        |
| color4        | Specifies the fourth color of the spinner arc       | -mfx-color4         | Color  | Color.web("#0F9D58")                        |
| radius        | Specifies the radius of the progress spinner        | -mfx-radius         | Double | -1(will use the value of prefHeight(width)) |
| startingAngle | Specifies the angle at which the spinner will start | -mfx-starting-angle | Double | 360 - Math.random() * 720                   |

### CSS Selectors

- .mfx-progress-spinner

- .mfx-progress-spinner .track

- .mfx-progress-spinner .arc

- .mfx-progress-spinner .percentage
