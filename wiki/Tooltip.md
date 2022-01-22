# Tooltip

## MFXTooltips

- Style Class: mfx-tooltip

- Default Stylesheet: MFXTooltip.css

- Default Skin: MFXTooltipSkin.java

### Properties

| Property      | Description                                                                                                                                                                                 | Type         |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------:|
| icon          | Specifies the tooltip's icon                                                                                                                                                                | Node         |
| text          | Specifies the tooltip's text                                                                                                                                                                | String       |
| owner         | The context menu's owner                                                                                                                                                                    | Node         |
| mousePosition | The context menu trakcs the mouse position on the owner node                                                                                                                                | PositionBean |
| showAction    | This Consumer allows the user to decide how to show the tooltip.<br/>The Consumer carries the tracked mouse position, see (mousePosition).<br/>By default, calls show(Node, double, double) | Consumer     |
| showDelay     | The amount of time after which the tooltip is shown                                                                                                                                         | Duration     |
| hideAfter     | The amount of time after which the tooltip is automatically hidden                                                                                                                          | Duration     |

### CSS Selectors

- .mfx-tooltip

- .mfx-tooltip .container (the top container, should not be necessary)

- .mfx-tooltip .container .label
