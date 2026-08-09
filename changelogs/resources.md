## 25.2.0 - 09/08/2026 - d871e30a

### Features

- [`a289b9ec`](https://github.com/palexdev/MaterialFX/commit/a289b9ec) Implement MFXSwitch
- [`1c249431`](https://github.com/palexdev/MaterialFX/commit/1c249431) Implement radio buttons!
- [`03e84b2c`](https://github.com/palexdev/MaterialFX/commit/03e84b2c) Add one more icon to MaterialFX icons pack

### Bug Fixes

- [`b124fd22`](https://github.com/palexdev/MaterialFX/commit/b124fd22) Fix surfaces color not updating when transitions are involved
- [`2445a301`](https://github.com/palexdev/MaterialFX/commit/2445a301) IconUtils: fix randomFAS() utility

### Refactoring

- [`0bc8e99c`](https://github.com/palexdev/MaterialFX/commit/0bc8e99c) Minor cleanup here and there
- [`5f3b1e10`](https://github.com/palexdev/MaterialFX/commit/5f3b1e10) Remake checkboxes layout/skin/CSS
- [`918c1e2c`](https://github.com/palexdev/MaterialFX/commit/918c1e2c) MFXSurface: improve shadow handling

### Style

- [`0aeb2b5a`](https://github.com/palexdev/MaterialFX/commit/0aeb2b5a) Update compiled CSS
- [`a58cd983`](https://github.com/palexdev/MaterialFX/commit/a58cd983) Update compiled CSS
- [`345e6e30`](https://github.com/palexdev/MaterialFX/commit/345e6e30) Allow elevation in button groups (elevated style)
- [`614383a1`](https://github.com/palexdev/MaterialFX/commit/614383a1) Update compiled CSS
- [`1fc85e4a`](https://github.com/palexdev/MaterialFX/commit/1fc85e4a) Update split buttons after remake
- [`7050f4d9`](https://github.com/palexdev/MaterialFX/commit/7050f4d9) Update FAB menu to spec
- [`3c5b8d0f`](https://github.com/palexdev/MaterialFX/commit/3c5b8d0f) Update icon buttons to spec
- [`c5bcced9`](https://github.com/palexdev/MaterialFX/commit/c5bcced9) Update buttons to spec and fix typo
- [`0d8d8e68`](https://github.com/palexdev/MaterialFX/commit/0d8d8e68) Proper style connected button groups
- [`b6c111ef`](https://github.com/palexdev/MaterialFX/commit/b6c111ef) Minor updates to FABs
- [`58eb2386`](https://github.com/palexdev/MaterialFX/commit/58eb2386) Add missing 'selected' state layer for text buttons
- [`60beaad0`](https://github.com/palexdev/MaterialFX/commit/60beaad0) Update state layer opacities



## 25.1.3 - 28/05/2026 - b024b5bf

### Features

- <54162178> Add a bunch of useful font icons to MaterialFX pack

### Style

- <0fe107a6> Minor CSS updates



## 25.1.2 - 09-10-2025 - 80d05dc1

### Bug Fixes

- <3092598f> Forgot to set the text color for checkboxes
- <7aed1258> Partially revert 99cb8f12: not including the bg color to the animated properties makes some components' look stuck when changing theme mode



## 25.1.1 - 08-10-2025 - e163a84b

### Bug Fixes

- <99cb8f12> Fix color flickering when disabling buttons due to md-motions interfering with surfaces animations

### Style

- <74af90af> Update/fix animations



## 24.3.0 - 07-09-2025 - eb8aea6d

### Features

- <59e250eb> Add MaterialFX font icons pack
- <66cc3675> IconUtils: add a bunch more utilities
- <81eb6fb4> Add styles for scrolling related controls and split buttons

### Refactoring

- <94e599cd> MFXIconWrapper: rework computeSize() to be a bit more stable and add public method to retrieve the size
- <6470f91e> MFXIconWrapper: throw an exception if animations are enabled but the provider is null, indicating the user the animation system is not properly set up

### Style

- <7c225ec5> Implemented checkboxes
- <5097c4a4> Update stylesheets
- <d4e724df> Update css output
- <4e3ebb95> Reorganize popups styles



## 24.2.1 - 31-07-2025 - 10f296ba

### Features

- <124ba29b> Add convenience method to load themes
- <31710355> Include fonts in material theme
- <9387b51e> Add styles for MFXMenus

### Refactoring

- <6f71db17> Do not hide scroll bars completely in menus

### Documentation

- <0cf99081> F u IntelliJ

### Style

- <f0c6843e> Update FAB styles



## 24.2.0 - 22-07-2025 - 91ae528e

### Features

- <e3712b7a> Completely review font icons system



## 24.1.0 - 03-07-2025 - cc69b56a

### Features

- <d60e7c51> Implemented FAB menu
- <6314d285> Re-implement MFXFab
- <f7dcf61b> Re-implement MFXSegmentedButton as MFXButtonsGroup
- <abb08674> Re-implement MFXIconButton styles and improve MFXButton ones
- <2846f9a2> Major review of MFXIconWrapper
- <c138dc0b> Re-implement Sass theming system

### Bug Fixes

- <5615c926> AnimationPresets: well this is awkward, JavaFX CSS gets confused by the word FADE wtf!

### Refactoring

- <21a8178b> Unify standard and icon buttons motion mixins
- <a67eff7f> IconProperty: do not reuse the current icon but create a new one when switching

### Style

- <5d825ac8> Add 'large-increased' to shapes vars
- <44f8ec16> Update assets
- <1f0c9625> Replace Comfortaa and OpenSans fonts with MontSerrat