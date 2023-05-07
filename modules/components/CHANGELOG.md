# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).  
(Date format is dd-MM-yyyy)

## Type of Changes

- **Added** for new features.
- **Changed** for changes in existing functionality.
- **Deprecated** for soon-to-be removed features.
- **Removed** for now removed features.
- **Fixed** for any bug fixes.

[//]: ##[Unreleased]

## [11.8.0] - 07-05-2023

### Added

- Implementing Popups and Tooltips
- Add MFXTooltip support to both MFXControl and MFXLabeled
- Define API for animated popups, IMFXPopupSkin
- MFXPopup now supports the new anchor based show mechanism for Window owners too
- Implemented the two types of popup contents shown by M3 guidelines

### Changed

- Moved MFXPopupSkin to Skins Package
- MFXPopupBase: position computation methods have been slightly refactored to support the above case too
- MFXPopupRoot: make sure the animated property is also being initialized on creation
- MFXTooltip: allow changing owner
- MFXPopupSkin: slightly improved open animation
- MFXTooltip: use MOUSE_MOVED handler instead of MOUSE_ENTER for a more reliable interaction

## [11.7.0] - 03-05-2023

### Added

- Implemented IconButtons
- Implemented base classes for the new Selection API from core module. To be used with components such as checks,
  radios, switches,...
- MFXButtonBehavior: added key events handling. By default now, by pressing ENTER if the button is focused, the behavior
  will trigger the ripple generation at the center of the button, as well as firing an ActionEvent just like for mouse
  clicks
- MFXFabBase: added delegate methods for the icon property
- Added PseudoClasses for selection
- Added theme files for the new IconButtons

### Changed

- Minor refactor and improvements to the ButtonsPlayground demo
- MFXButtonBehavior: change ripple generation method to not take the generator as a parameter. Rather, try to get the
  generator instance from the button' skin and then cache it
- MFXFabBehavior: Improved change icon animation for extended FABs
- MFXButtonSkin and MFXFabSkin: adapt to behavior changes mentioned above

## [11.16.4] - 12-04-2023

### Fixed

- Emergency fix for components, the behavior was initialized twice

## [11.16.3] - 11-04-2023

### Changed

- Adapt to new When API from MFXCore

### Fixed

- Fixed a bug that lead to the behavior not being initialized in specific occasions, also added unit test
- Fixed label positioning for buttons

## [11.16.2] - 06-04-2023

### Fixed

- Added missing exports

## [11.16.1] - 05-04-2023

### Added

- Added purple dark theme variant to MFXThemeManager (experimental, theme needs adjustments in MFXResources, M3
  guidelines changed recently)
- Added tests to check that FABs' label is always positioned correctly even when changing text or icon
- Added tests to check that init sizes are correctly applied to small and large FABs, honestly the tests pass, but there
  may still be issues (dunno why though, my guess is that it depends on the container)

### Changed

- MFXFabBehavior: make getLabelNode() return an Optional and adapt code accordingly
- MFXFabBehavior: improve the label displacement computation, the targetWidth is only useful for the animation. To
  compute the displacement we actually want the bound size of the FAB
- MFXTonalFilledButton: change style class to "filled-tonal"
- MFXFab, MFXFabBase: turns out that managinfg the "extended" state is much easier with a PseudoClass rather than
  changing the style classes (edge cases may happen, better not handle the mess and just adding a PseudoClass)
- Create a base skin for all MFXLabeled controls, make MFXButtonSkin extend it, and adapt MFXFabSkin accordingly
- ButtonsPlayground: implement theme switching (preliminary)
- ButtonsPlayground: add extended FAB to test icon change animation

### Fixed

- MFXFabSkin: fixed misplacement of the label when its width changed (text or icon changes), in such case the
  displacement must be re-computed

## [Unreleased] - 14-03-2023

### Added

- Added specific behavior for FABs as shown by the M3 guidelines
- MFXLabeled: added property that allows to control the text opacity (see recent changes to BoundLabel in core module)
- Implement specific skin for FABs
- Added EXTENDED variant for FABs
- Added preliminary implementation of MFXThemeManager (atm just to simplify dev)
- Added a new class that is capable of generating CSS stylesheets via code and it's super useful. It even allows you to
  use multiple selectors and pseudo classes
- New API to allow MaterialFX controls to easily change their sizing/layout strategy by simply setting a property
  instead of creating custom skins, custom components or overriding methods inline
- Added tests for the new LayoutStrategy and MFXResizable APIs

### Changed

- MFXButtonBehavior: fire an action only on PRIMARY mouse clicks by default
- Moved SkinBase from core module and renamed it to MFXSkinBase
- MFXSkinBase now doesn't keep the reference to the current behavior object, instead it is stored in the component base
  class (MFXControl or MFXLabeled)
- MFXControl and MFXLabeled will now automatically handle behavior creation and initialization
- For the above change, they now enforce the use of buildSkin() to create the default skin, and make createDefaultSkin()
  a final method, this is needed for a reliable initialization of the behavior
- MFXControl and MFXLabeled now override all the size methods to be public
- MFXFabBase now has a property which allows to setup the FAB as a standard one or an extended one
- MFXFabBase: since now FABs have their own specific behavior, to avoid code duplication a method has been added to
  retrieve the FAB behavior reference, it returns an Optional since the behavior could be null or could not be an
  instance of MFXFabBehavior
- MFXButtonSkin: bind text node opacity to the new property of MFXLabeled
- MFXButtonSkin: no need for the listener on the behaviorProvider property (good for memory and performance)
- Make both MFXControl and MFXLabeled implement the new MFXResizable API for integration with LayoutStrategy
- MFXSkinBase: make size computation methods public for integration with MFXResizable and LayoutStrategy
- Reworked and renamed applyInitSizes(...) to onInitSizesChanged(). Init sizes now are taken into account by the new
  LayoutStrategy API
- MFXFabSkin: do not take into account the init width as it is used by the LayoutStrategy now
- MFXFabBehavior: moved extended variant management in the component class, behaviors should not change the component
  state
- WithVariants: added method to remove variants from a component

### Removed

- MFXExtendedFab has been removed to avoid code duplication

### Fixed

- Fixed critical bug related to the initWidth and initHeight new styleable properties. For some reason they were causing
  the CSS to be reapplied continuously, causing memory leaks and a huge performance hit over time. Override
  invalidated() instead on set(...)

## [11.16.0] - 09-02-2023

### Added

- Introduced two new styleable properties to both MFXControl and MFXLabeled, to allow specifying the initial sizes of a
  component without relying on JavaFX's CSS properties since once they are set, they cannot be overwritten (bug or not
  it's a shitty behavior)
- Introduced a new method to both MFXControl and MFXLabeled to allow implementations to customize the SceneBuilder
  integration
- Introduced FABs and Extended FABs
- Added two new theming APIs to define components' variants

### Changed

- MFXButtonSkin: listener on the graphic property is not needed, let the BoundLabel handle it
- MFXButtonSkin: do not apply the listener on the content display property if the button is of type MFXFab

### Fixed

- MFXElevatedButton: fixed NullPointerException caused by the elevation property as in some occasions both the oldValue
  and newValue can be null

## [11.15.2] - 07-02-2023

### Changed

- Rename materialfx package to mfxcomponents, for consistency with other modules
- Make buttons use the new SceneBuilder integration API mentioned above

## [11.15.1] - 02-02-2023

### Added

- Implemented all variants of MFXButton

### Changed

- Implemented MFXButtonBehavior
- MFXButtonSkin: add ripple effect, adapt to changes made in SkinBase

## [11.15.0] - 26-01-2023

### Added

- Preliminary implementation of components hierarchy, the idea is to build the components from scratch making them
  extend either MFXControl or MFXLabeled
- Preliminary implementation of MFXButton and MFXElevatedButton variant

