# Slider

## Preview

<img src="https://imgur.com/nOrsa1n.gif" alt="Sliders">

## MFXSliders

- Style Class: mfx-slider

- Default Stylesheet: MFXSlider.css

- Default Skin: MFXSliderSkin.java

### Properties

| Property         | Description                                                                                                                                 | Type           |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------- | --------------:|
| min              | Specifies the minimum value the slider can reach                                                                                            | Double         |
| max              | Specifies the maximum value the slider can reach                                                                                            | Double         |
| value            | Specifies the slider's actual value                                                                                                         | Double         |
| thumbSupplier    | Specifies the Supplier used to build the slider's thumb.<br/>Attempting to set or return a null value will fallback to the default supplier | Supplier       |
| popupSupplier    | Specifies the Supplier used to build the slider's popup.<br/>You can also set or return null to remove the popup                            | Supplier       |
| popupPadding     | Specifies the extra gap between the thumb and the popup                                                                                     | Double         |
| decimalPrecision | Specifies the number of decimal places for the slider's value                                                                               | Integer        |
| enableKeyboard   | Specifies if the value can be adjusted with the keyboard or not                                                                             | Boolean        |
| ranges1          | Returns the first list of ranges                                                                                                            | ObservableList |
| ranges2          | Returns the second list of ranges                                                                                                           | ObservableList |
| ranges3          | Returns the third list of ranges                                                                                                            | ObservableList |

### Styleable Properties

| Property                 | Description                                                                                                                                                                                                                                                                                                                        | CSS Property                    | Type                  | Default Value          |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------- | ---------------------:| ---------------------- |
| sliderMode               | Specifies the slider mode. Can be DEFAULT (freely adjust the thumb) or SNAP_TO_TICKS (the thumb will always snap to ticks)                                                                                                                                                                                                         | -mfx-slider-mode                | SlideMode[Enum]       | SliderMode.DEFAULT     |
| unitIncrement            | Specifies the value to add/subtract to the slider's value when an arrow key is pressed.<br/>The arrow keys depend on the slider orientation: HORIZONTAL: right, left; VERTICAL: up, down                                                                                                                                           | -mfx-unit-increment             | Double                | 10.0                   |
| alternativeUnitIncrement | Specifies the value to add/subtract to the slider's value when an arrow key and Shift or Ctrl are pressed                                                                                                                                                                                                                          | -mfx-alternative-unit-increment | Double                | 5.0                    |
| tickUnit                 | The value between each major tick mark in data units                                                                                                                                                                                                                                                                               | -mfx-tick-count                 | Double                | 25.0                   |
| showMajorTicks           | Specifies if the major ticks should be displayed or not                                                                                                                                                                                                                                                                            | -mfx-show-major-ticks           | Boolean               | false                  |
| showMinorTicks           | Specifies if the minor ticks should be displayed or not                                                                                                                                                                                                                                                                            | -mfx-show-minor-ticks           | Boolean               | false                  |
| showTicksAtEdges         | Specifies if the major ticks at the edge of the slider should be displayed or not.<br/>The ticks at the edge are those ticks which represent the min and max values                                                                                                                                                                | -mfx-show-ticks-at-edge         | Boolean               | true                   |
| minorTicksCount          | Specifies how many minor ticks should be added between two major ticks                                                                                                                                                                                                                                                             | -mfx-minor-ticks-count          | Integer               | 5                      |
| animateOnPress           | When pressing on the slider's track the value is adjusted according to the mouse event coordinates.<br/>This property specifies if the progress bar adjustment should be animated or not                                                                                                                                           | -mfx-animate-on-press           | Boolean               | true                   |
| bidirectional            | If the slider is set to be bidirectional the progress bar will always start from 0.<br/>When the value is negative the progress bar grows in the opposite direction to 0.<br/>This works only if min is negative and max is positive, otherwise this option in ignored during layout. See the warning in the control documentation | -mfx-bidirectional              | Boolean               | true                   |
| orientation              | Specifies the slider's orientation                                                                                                                                                                                                                                                                                                 | -mfx-orientation                | Orientation[Enum]     | Orientation.HORIZONTAL |
| popupSide                | Specifies the popup side.<br/>DEFAULT is above for horizontal orientation and left for vertical orientation.<br/>OTHER_SIDE is below for horizontal orientation and right for vertical orientation.                                                                                                                                | -mfx-popup-side                 | SliderPopupSide[Enum] | SlidePopupSide.DEFAULT |

### CSS Selectors

- .mfx-slider

- .mfx-slider .track

- .mfx-slider .axis (for the ticks)

- .mfx-slider .axis .axis-minor-tick-mark (for the minor ticks)

- .mfx-slider .tick-even (for even major ticks)

- .mfx-slider .tick-odd (for odd major ticks)

- .mfx-slider .bar

- .mfx-slider .thumb-container

- .mfx-slider .thumb-container .thumb (font icon)

- .mfx-slider .thumb-container .thumb-radius (font icon)

- .mfx-slider .thumb-container .mfx-ripple-generator
