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

## [Unreleased] - 14-03-2023

## Added

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

## Changed

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

## Removed

- MFXExtendedFab has been removed to avoid code duplication

## Fixed

- Fixed critical bug related to the initWidth and initHeight new styleable properties. For some reason they were causing
  the CSS to be reapplied continuously, causing memory leaks and a huge performance hit over time. Override
  invalidated() instead on set(...)

## [11.16.0] - 09-02-2023

## Added

- Introduced two new styleable properties to both MFXControl and MFXLabeled, to allow specifying the initial sizes of a
  component without relying on JavaFX's CSS properties since once they are set, they cannot be overwritten (bug or not
  it's a shitty behavior)
- Introduced a new method to both MFXControl and MFXLabeled to allow implementations to customize the SceneBuilder
  integration
- Introduced FABs and Extended FABs
- Added two new theming APIs to define components' variants

## Changed

- MFXButtonSkin: listener on the graphic property is not needed, let the BoundLabel handle it
- MFXButtonSkin: do not apply the listener on the content display property if the button is of type MFXFab

## Fixed

- MFXElevatedButton: fixed NullPointerException caused by the elevation property as in some occasions both the oldValue
  and newValue can be null

## [11.15.2] - 07-02-2023

## Changed

- Rename materialfx package to mfxcomponents, for consistency with other modules
- Make buttons use the new SceneBuilder integration API mentioned above

## [11.15.1] - 02-02-2023

## Added

- Implemented all variants of MFXButton

## Changed

- Implemented MFXButtonBehavior
- MFXButtonSkin: add ripple effect, adapt to changes made in SkinBase

## [11.15.0] - 26-01-2023

## Added

- Preliminary implementation of components hierarchy, the idea is to build the components from scratch making them
  extend either MFXControl or MFXLabeled
- Preliminary implementation of MFXButton and MFXElevatedButton variant

