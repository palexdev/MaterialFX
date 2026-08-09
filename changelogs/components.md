## 25.1.0-EA4 - 09/08/2026 - d871e30a

### Features

- [`a289b9ec`](https://github.com/palexdev/MaterialFX/commit/a289b9ec) Implement MFXSwitch
- [`1c249431`](https://github.com/palexdev/MaterialFX/commit/1c249431) Implement radio buttons!
- [`9835d0db`](https://github.com/palexdev/MaterialFX/commit/9835d0db) Remake MFXSplitButton
- [`0e30e991`](https://github.com/palexdev/MaterialFX/commit/0e30e991) implement Theming API (part 2 of d6434ba8)

### Bug Fixes

- [`dfdfe0b3`](https://github.com/palexdev/MaterialFX/commit/dfdfe0b3) MFXCell: add itemProperty as a source of the selected binding

### Refactoring

- [`0bc8e99c`](https://github.com/palexdev/MaterialFX/commit/0bc8e99c) Minor cleanup here and there
- [`5f3b1e10`](https://github.com/palexdev/MaterialFX/commit/5f3b1e10) Remake checkboxes layout/skin/CSS
- [`c0ed3387`](https://github.com/palexdev/MaterialFX/commit/c0ed3387) MFXButtonsGroup: disallow text variant and fallback to filled
- [`65e117e3`](https://github.com/palexdev/MaterialFX/commit/65e117e3) FABVariants: add XS size
- [`918c1e2c`](https://github.com/palexdev/MaterialFX/commit/918c1e2c) MFXSurface: improve shadow handling
- [`d84b6973`](https://github.com/palexdev/MaterialFX/commit/d84b6973) MFXSurface: do not check for focusVisible for FOCUSED state
- [`891117d6`](https://github.com/palexdev/MaterialFX/commit/891117d6) MFXSurface: make fallback/default opacity CSS styleable
- [`ea07fe1b`](https://github.com/palexdev/MaterialFX/commit/ea07fe1b) MFXIconButton: allow null style (unset)
- [`1362a90d`](https://github.com/palexdev/MaterialFX/commit/1362a90d) MFXFabMenuSkin: minor improvements
- [`b2e45e47`](https://github.com/palexdev/MaterialFX/commit/b2e45e47) MFXFabSkin: improve animation and layout handling
- [`19b4618a`](https://github.com/palexdev/MaterialFX/commit/19b4618a) MFXFab: disallow direct write of minSizeProperty
- [`86d20e56`](https://github.com/palexdev/MaterialFX/commit/86d20e56) MFXButtonsGroupSkip: get rid of clip
- [`7ab0a398`](https://github.com/palexdev/MaterialFX/commit/7ab0a398) MFXButtonsGroup: allow groups of both standard and toggle buttons
- [`e1602543`](https://github.com/palexdev/MaterialFX/commit/e1602543) WithVariants: pull defaultVariants() up to the interface
- [`c6884ec1`](https://github.com/palexdev/MaterialFX/commit/c6884ec1) VariantsHandler: lazy instantiate and return the same instance of the unmodifiable variants map

### Tests

- [`c58db194`](https://github.com/palexdev/MaterialFX/commit/c58db194) Update test classes



## 25.1.0 - 28/05/2026 - b024b5bf

### Refactoring

- <58625458> Review, move and rename InsetsBuilder utility

### Misc

- <c03888a6> Update VirtualizedFX to v25.1.16



## 25.1.0 - 08-10-2025 - e163a84b

### Removed

- <cac70089> Nuke virtualized menu content
- <929c5bd4> Nuke components code, this hurts real bad

### Features

- <2e668564> Implemented checkboxes
- <dd51f1eb> MFXToggle: add hook for selection group property
- <ba759aa5> Major review of MaterialFX components hierarchy
- <98c0944b> Implement split buttons
- <78d12d79> Implement base cell for MaterialFX components
- <e8429f69> MFXVirtualMenuContentSkin: implement placeholder capabilities
- <f49855a1> Implement virtualized MFXMenuContent
- <e3326fe3> Implemented FAB menu as MFXFabMenu
- <63cf8d56> WithVariants: add default method to retrieve an applied enum variant
- <1188fc75> Re-implement MFXFab
- <e77196e8> Re-implement MFXSegmentedButton as MFXButtonsGroup
- <e03c666e> Re-implement MFXIconButton
- <5541f4ff> Re-implement MFXButton
- <d5602f8d> Reworked Variants API once again
- <2d7b5b2b> Re-implemented base classes for buttons, selectables and relative behaviors
- <6a3e73f8> Re-implemented and improved MaterialSurface as MFXSurface
- <90689fb0> Re-implemented and expanded the Variants API
- <5bbf6e4f> Re-implemented and improved PseudoClasses enum
- <a31056a1> Re-implemented base classes for all components

### Bug Fixes

- <bd7d171f> MFXFabSkin: do not subtract insets from clip's width, otherwise icons may end up cut
- <468cd83a> MFXLabeledSkin: fix potential NPE
- <235894c4> MFXSurface: handle null elevation level

### Refactoring

- <d2a19749> Adapt ExtendedPopoverConfig to changes in popups positioning API
- <3a79fc3d> MFXSurface: do not activate focused state when focus-visible is active
- <a248ad09> MFXToggle: do not toggle if disabled
- <c0698bdd> Add no-arg constructor defaults for some components
- <402f4002> MFXToggle: use MFXButtonBehavior as the default since the behavior is the same
- <f5dceed3> MFXFab: use new SizeProperty CSS handling
- <ce01cf47> MFXMenuCell: bind disable property
- <26c22bfb> Adapt to MFXResources changes
- <6cd9a99a> MFXFabSkin and MFXIconButtonSkin: bind icon bidirectionally to allow switch animations from CSS
- <620bf0c9> Adapt components to WithVariants changes
- <a0f34dee> Make WithVariants return an ObservableMap for the applied variants
- <f37b1498> MFXButtonSkin: generate ripple only on left mouse click
- <064d616e> Align MFXButtonBehaviorBase key handling behavior to MFXSelectableBehavior
- <5abd1a9f> MFXLabeledSkin: adapt to Label changes
- <0e31e52c> MFXLabeledSkin: properly dispose Label node
- <4a80435f> Adapt existing components to the new MFXSkinnable API
- <2bea3cf8> MFXSurface major changes:

### Documentation

- <eced040c> Forgot to document MFXIconButton
- <8078729b> Add missing documentation
- <33afa6d5> Add/update documentation
