# Context Menus

## MFXContextMenus

- Style Class: mfx-context-menu

- Default Stylesheet: MFXContextMenu.css

- Default Skin: MFXContextMenuSkin.java

### Properties

| Property      | Description                                                                                                                                                       | Type           |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------:|
| items         | The list containing the context menu's items                                                                                                                      | ObservableList |
| owner         | The context menu's owner node                                                                                                                                     | Node           |
| disabled      | Enables/Disables the context menu                                                                                                                                 | Boolean        |
| showCondition | Specifies the function used to determine if a MouseEvent should trigger the showAction property.<br/>By default, checks if the SECONDARY mouse button was pressed | Function       |
| showAction    | Specifies the action to perform when a valid MouseEvent occurs.<br/> By default, calls shows the context menu at the MouseEvent' screen coordinates               | Consumer       |

### CSS Selectors

- .mfx-context-menu

- .mfx-context-menu .mfx-menu-item (to access the menu's items)

## MFXContextMenuItem

- Style Class: mfx-menu-item

- Default Stylesheet: MFXContextMenuItem.css

- Default Skin: MFXContextMenuItemSkin.java

### Properties

| Property        | Description                                                                                                         | Type         |
| --------------- | ------------------------------------------------------------------------------------------------------------------- | ------------:|
| accelerator     | Specifies the accelerator's text. Note that this is just the text, it's up to the user to setup the needed handlers | String       |
| tooltipSupplier | Specifies the Supplier used to build the item's tooltip                                                             | Supplier     |
| onAction        | Specifies the action to perform when clicked                                                                        | EventHandler |

### CSS Selectors

- .mfx-menu-item

- .mfx-menu-item .accelerator

- .mfx-menu-item .mfx-icon-wrapper (contains the icon)
