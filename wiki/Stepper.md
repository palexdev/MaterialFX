# Stepper

## Preview

<img src="https://imgur.com/nEgV9F1.gif" alt="Stepper">

## MFXSteppers

- Style Class: mfx-stepper

- Default Stylesheet: MFXStepper.css

- Default Skin: MFXStepperSkin.java

### Properties

| Property                       | Description                                                                                                                                                      | Type           |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------:|
| stepperToggles                 | The stepper's toggles list, each toggle is a step                                                                                                                | ObservableList |
| animationDuration              | Specifies, in milliseconds, the duration of the progress bar animation                                                                                           | Duration       |
| progress                       | Specifies the stepper's progress, the number of COMPLETED toggles divided by the total number of toggles. The values go from 0.0 to 1.0                          | Double         |
| currentIndex                   | Specifies the selected toggle position in the toggles list.<br/>The index is updated by the next() and previous() methods                                        | Integer        |
| currentContent                 | Convenience property that holds the selected toggle content node.<br/>In case one of the toggles has a  null content the content pane's children list is cleared | Node           |
| lastToggle                     | Convenience property that specifies if the last toggle is selected                                                                                               | Boolean        |
| enableContentValidationOnError | Specifies if all the controls that implement the Validated interface should be validated when the next button is pressed and the toggle state is ERROR           | Boolean        |

### Styleable Properties

| Property                | Description                                                                      | CSS Property            | Type      | Default Value        |
| ----------------------- | -------------------------------------------------------------------------------- | ----------------------- | ---------:| --------------------:|
| spacing                 | Specifies the spacing between toggles                                            | -mfx-spacing            | Double    | 128.0                |
| extraSpacing            | Specifies the extra length (at the start and at the end) of the progress bar     | -mfx-extra-spacing      | Double    | 64.0                 |
| alignment               | Specifies the alignment of the toggles.<br/>Steppers are usually centered though | -mfx-alignment          | Pos[Enum] | Pos.CENTER           |
| baseColor               | Specifies the stepper's primary color                                            | -mfx-base-color         | Paint     | Color.web("7F0FFF")  |
| altColor                | Specifies the stepper's secondary color                                          | -mfx-alt-color          | Paint     | Color.web("BEBEBE")  |
| progressBarBorderRadius | Specifies the borders radius of the progress bar                                 | -mfx-bar-borders-radius | Double    | 7.0                  |
| progressBarBackground   | Specifies the progress bar track color                                           | -mfx-bar-background     | Paint     | Color.web("#F8F8FF") |
| progressColor           | Specifies the progress color                                                     | -mfx-progress-color     | Paint     | Color.web("#7F0FFF") |
| animated                | Specifies if the progress bar should be animated or not                          | -mfx-bar-animated       | Boolean   | true                 |

### CSS Selectors

- .mfx-stepper

- .mfx-stepper .track (progress bar's track)

- .mfx-stepper .bar (progress bar's progress)

- .mfx-stepper .mfx-stepper-toggle

- .mfx-stepper .content-pane (the pane containing the toggle's specified content)

- .mfx-stepper .buttons-box (the footer containing the previous and next buttons)

- .mfx-stepper .buttons-box .mfx-button

## MFXStepperToggles

- Style Class: mfx-stepper-toggle

- Default Stylesheet: MFXStepperToggle.css

- Default Skin: MFXStepperToggleSkin.java

### Properties

| Property      | Description                                                                                                                                             | Type                     |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------:|
| showErrorIcon | Specifies if a little error icon should be shown when the state is ERROR in the upper right corner of the toggle (default position defined in the skin) | Boolean                  |
| content       | The content to be shown in the stepper when selected                                                                                                    | Node                     |
| text          | Specifies the text to be shown above or below the toggle                                                                                                | String                   |
| icon          | Specifies the icon shown in the circle of the toggle                                                                                                    | Node                     |
| state         | Specifies the state of the toggle                                                                                                                       | StepperToggleState[Enum] |

### Styleable Properties

| Property     | Description                                                 | CSS Property        | Type               | Default Value       |
| ------------ | ----------------------------------------------------------- | ------------------- | ------------------:| -------------------:|
| labelTextGap | Specifies the gap between the toggle's circle and the label | -mfx-label-text-gap | Double             | 10.0                |
| textPosition | Specifies the position of the label                         | -mfx-text-position  | TextPosition[Enum] | TextPosition.BOTTOM |
| size         | Specifies the radius of the toggle's circle                 | -mfx-size           | Double             | 22.0                |
| strokeWidth  | Specifies the stroke width of the toggle's circle           | -mfx-stroke-width   | Double             | 2.0                 |

### CSS Selectors

- .mfx-stepper-toggle

- .mfx-stepper-toggle .mfx-text-field
