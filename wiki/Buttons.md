# Buttons

## Preview

<img src="https://imgur.com/jATdGFL.gif" alt="Buttons">

## MFXButtons

- Style Class: mfx-button

- Default Stylesheet: MFXButton.css

- Default Skin: MFXButtonSkin.java

### Properties

| Property                | Description                                                                                               | Type    |
| ----------------------- | --------------------------------------------------------------------------------------------------------- | -------:|
| computeRadiusMultiplier | Specifies if the rippleRadiusMultiplier property should be computed automatically by the ripple generator | Boolean |
| rippleAnimateBackground | Specifies if the button's background should also be animated                                              | Boolean |
| rippleAnimateShadow     | Specifies if the button's shadow should also be animated                                                  | Boolean |
| rippleAnimationSpeed    | Specifies the ripple generator's animations speed                                                         | Double  |
| rippleBackgroundOpacity | Specifies the opacity for the background animation. (if rippleAnimateBackground is true).                 | Double  |
| rippleColor             | Specifies the ripples color                                                                               | Color   |
| rippleRadius            | Specifies the radius of the ripples                                                                       | Double  |
| rippleRadiusMultiplier  | Specifies the number by which the ripples' radius will be multiplied                                      | Double  |

### Styleable Properties

| Property   | Description                                                                                             | CSS Property     | Type             | Default Value     |
| ---------- | ------------------------------------------------------------------------------------------------------- | ---------------- | ----------------:| -----------------:|
| depthLevel | Specifies how intense is the DropShadow effect applied to the button to make it appear 3D               | -mfx-depth-level | DepthLevel[Enum] | DepthLevel.LEVEL2 |
| buttonType | Specifies the appearance of the button. Either FLAT or RAISED. In the first mode, depthLevel is ignored | -mfx-button-type | ButtonType[Enum] | ButtonType.FLAT   |

### CSS Selectors

- .mfx-button

- .mfx-button .mfx-ripple-generator
